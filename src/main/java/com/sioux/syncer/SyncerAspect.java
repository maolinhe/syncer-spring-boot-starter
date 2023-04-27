// ==========================
// Copyright (c) 2023-04-23 Sioux
// All rights reserved.
// ==========================
package com.sioux.syncer;

import cn.hutool.core.util.StrUtil;
import com.sioux.syncer.annotation.Delete;
import com.sioux.syncer.annotation.DeleteById;
import com.sioux.syncer.annotation.ElasticSyncer;
import com.sioux.syncer.annotation.Match;
import com.sioux.syncer.annotation.Push;
import com.sioux.syncer.annotation.Update;
import com.sioux.syncer.mapper.BaseEsMapper;
import com.sioux.syncer.service.DocumentService;
import com.sioux.syncer.service.ThreadService;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

@Data
@Slf4j
@Aspect
public class SyncerAspect {

  private final DocumentService documentService;

  private final ThreadService threadService;

  public SyncerAspect(DocumentService documentService, ThreadService threadService) {
    this.documentService = documentService;
    this.threadService = threadService;
  }

  @Pointcut("@annotation(com.sioux.syncer.annotation.ElasticSyncer)")
  public void elasticSyncerPointCut() {
  }

  @Pointcut("@annotation(com.sioux.syncer.annotation.Match)")
  public void matchPointCut() {
  }

  @Pointcut("@annotation(com.sioux.syncer.annotation.Push)")
  public void pushPointCut() {
  }

  @Around("matchPointCut()")
  public Object aroundMatch(ProceedingJoinPoint joinPoint) throws Throwable {
    return around(joinPoint);
  }

  @Around("pushPointCut()")
  public Object aroundPush(ProceedingJoinPoint joinPoint) throws Throwable {
    return around(joinPoint);
  }

  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    Class<?> mapperClazz = joinPoint.getTarget().getClass();
    Type superType = mapperClazz.getGenericSuperclass();
    if (superType == null) {
      return joinPoint.proceed();
    }

    Type[] typeArguments = ((ParameterizedType) mapperClazz.getGenericSuperclass()).getActualTypeArguments();
    if (typeArguments == null || typeArguments.length != 1) {
      return joinPoint.proceed();
    }
    Class<?> modelClazz = (Class<?>) typeArguments[0];

    Class<?> superclass = mapperClazz.getSuperclass();
    if (superclass == BaseEsMapper.class) {
      String index = StrUtil.toUnderlineCase(modelClazz.getSimpleName());
      ElasticSyncer syncer = mapperClazz.getAnnotation(ElasticSyncer.class);
      if (syncer != null) {
        index = StringUtils.hasText(syncer.index()) ? syncer.index() : index;
      }

      Object[] args = joinPoint.getArgs();
      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      Method method = methodSignature.getMethod();
      if (method.isAnnotationPresent(Match.class)) {
        return doElasticMatch(index, args, modelClazz);
      } else if (method.isAnnotationPresent(Push.class)) {
        documentService.upsert(index, args[0]);
        return null;
      }

    }

    return joinPoint.proceed();
  }

  private Object doElasticMatch(String index, Object[] args, Class<?> clazz)
      throws InvocationTargetException, IllegalAccessException {
    Class<? extends DocumentService> documentServiceClass = documentService.getClass();
    Object[] allParams = new Object[args.length + 2];
    System.arraycopy(args, 0, allParams, 1, args.length);

    allParams[0] = index;
    allParams[args.length + 1] = clazz;
    Method[] methods = documentServiceClass.getMethods();
    for (Method method : methods) {
      int paramCount = method.getParameterCount();
      if (paramCount == allParams.length) {
        return method.invoke(documentService, allParams);
      }
    }

    return null;
  }

  @After("elasticSyncerPointCut()")
  public void after(JoinPoint joinPoint)
      throws IOException, NoSuchFieldException, IllegalAccessException {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method method = methodSignature.getMethod();
    Object[] args = joinPoint.getArgs();

    ElasticSyncer syncer = method.getAnnotation(ElasticSyncer.class);
    Parameter[] params = method.getParameters();
    for (int i = 0; i < params.length; i++) {
      Parameter param = params[i];
      if (param.isAnnotationPresent(Push.class) ||
          param.isAnnotationPresent(Delete.class) ||
          param.isAnnotationPresent(DeleteById.class) ||
          param.isAnnotationPresent(Update.class)) {
        doElasticSync(param, syncer.index(), args[i], syncer.async());
      }
    }
  }

  private void doElasticSync(Parameter param, String index, Object arg, boolean async)
      throws IOException, NoSuchFieldException, IllegalAccessException {
    checkIndex(index);
    if (!async) {
      execute(param, index, arg);
    } else {
      threadService.submit(() -> {
        try {
          execute(param, index, arg);
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  private void execute(Parameter param, String index, Object arg)
      throws IOException, NoSuchFieldException, IllegalAccessException {
    if (param.isAnnotationPresent(Push.class)) {
      documentService.upsert(index, arg);
    } else if (param.isAnnotationPresent(Delete.class)) {
      documentService.delete(index, arg);
    } else if (param.isAnnotationPresent(DeleteById.class)) {
      documentService.deleteById(index, String.valueOf(arg));
    } else if (param.isAnnotationPresent(Update.class)) {
      documentService.update(index, arg);
    }
  }

  private void checkIndex(String index) {
    if (!StringUtils.hasText(index)) {
      throw new IllegalArgumentException("Illegal elastic index");
    }
  }

}
