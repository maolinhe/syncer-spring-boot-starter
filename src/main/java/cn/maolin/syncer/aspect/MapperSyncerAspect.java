package cn.maolin.syncer.aspect;

import cn.hutool.core.util.StrUtil;
import cn.maolin.syncer.annotation.IndexName;
import cn.maolin.syncer.annotation.Match;
import cn.maolin.syncer.annotation.Push;
import cn.maolin.syncer.mapper.BaseEsMapper;
import cn.maolin.syncer.service.DocumentService;
import cn.maolin.syncer.service.ThreadService;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

@Data
@Slf4j
@Aspect
public class MapperSyncerAspect {

  private final DocumentService documentService;

  private final ThreadService threadService;

  public MapperSyncerAspect(DocumentService documentService, ThreadService threadService) {
    this.documentService = documentService;
    this.threadService = threadService;
  }

  @Pointcut("@annotation(cn.maolin.syncer.annotation.Match)")
  public void matchPointCut() {
  }

  @Pointcut("@annotation(cn.maolin.syncer.annotation.Push)")
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

      if (mapperClazz.isAnnotationPresent(IndexName.class)) {
        IndexName indexName = mapperClazz.getAnnotation(IndexName.class);
        if (!StringUtils.hasText(indexName.value())) {
          throw new IllegalArgumentException("ElasticSearch index name should not be empty");
        }
        index = indexName.value();
      } else if (modelClazz.isAnnotationPresent(IndexName.class)) {
        IndexName indexName = modelClazz.getAnnotation(IndexName.class);
        if (!StringUtils.hasText(indexName.value())) {
          throw new IllegalArgumentException("ElasticSearch index name should not be empty");
        }
        index = indexName.value();
      }

      Object[] args = joinPoint.getArgs();
      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      Method method = methodSignature.getMethod();
      if (method.isAnnotationPresent(Match.class)) {
        return doElasticMatch(index, args, modelClazz);
      } else if (method.isAnnotationPresent(Push.class)) {
        documentService.upsert(index, args[0]);
        return joinPoint.proceed();
      }
    }

    return joinPoint.proceed();
  }

  private Object doElasticMatch(String index, Object[] args, Class<?> clazz)
      throws InvocationTargetException, IllegalAccessException {
    Object[] allArgs = new Object[args.length + 2];
    System.arraycopy(args, 0, allArgs, 1, args.length);
    allArgs[0] = index;
    allArgs[args.length + 1] = clazz;

    Method[] methods = documentService.getClass().getMethods();
    for (Method method : methods) {
      if (!"match".equals(method.getName())) {
        continue;
      }

      if (!matchMethod(allArgs, method.getParameterTypes())) {
        continue;
      }

      return method.invoke(documentService, allArgs);
    }

    return null;
  }

  private boolean matchMethod(Object[] args, Class<?>[] clazz) {
    if (args.length != clazz.length) {
      return false;
    }

    for (int i = 0; i < args.length; i++) {
      if (!clazz[i].isAssignableFrom(args[i].getClass())) {
        return false;
      }
    }

    return true;
  }

}
