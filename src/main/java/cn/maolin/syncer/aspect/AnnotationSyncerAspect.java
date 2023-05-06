package cn.maolin.syncer.aspect;

import cn.hutool.core.util.StrUtil;
import cn.maolin.syncer.annotation.Delete;
import cn.maolin.syncer.annotation.DeleteById;
import cn.maolin.syncer.annotation.ElasticSyncer;
import cn.maolin.syncer.annotation.Push;
import cn.maolin.syncer.annotation.Update;
import cn.maolin.syncer.service.DocumentService;
import cn.maolin.syncer.service.ThreadService;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

@Data
@Slf4j
@Aspect
public class AnnotationSyncerAspect {

  private final DocumentService documentService;

  private final ThreadService threadService;

  public AnnotationSyncerAspect(DocumentService documentService, ThreadService threadService) {
    this.documentService = documentService;
    this.threadService = threadService;
  }

  @Pointcut("@annotation(cn.maolin.syncer.annotation.ElasticSyncer)")
  public void elasticSyncerPointCut() {
  }

  @AfterReturning("elasticSyncerPointCut()")
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
        Object arg = args[i];
        String index = syncer.index();
        index = StringUtils.hasText(index) ? index
            : StrUtil.toUnderlineCase(arg.getClass().getSimpleName());

        doElasticSync(param, index, args[i], syncer.async());
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
