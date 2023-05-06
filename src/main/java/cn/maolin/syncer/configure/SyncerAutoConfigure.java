package cn.maolin.syncer.configure;

import cn.maolin.syncer.aspect.AnnotationSyncerAspect;
import cn.maolin.syncer.aspect.MapperSyncerAspect;
import cn.maolin.syncer.service.DocumentService;
import cn.maolin.syncer.service.ThreadService;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConditionalOnClass(value = {AnnotationSyncerAspect.class})
public class SyncerAutoConfigure {

  private final DocumentService documentService;

  private final ThreadService threadService;

  public SyncerAutoConfigure(DocumentService documentService, ThreadService threadService) {
    this.documentService = documentService;
    this.threadService = threadService;
  }

  @Bean
  @ConditionalOnMissingBean
  AnnotationSyncerAspect annotationSyncerAspect() {
    return new AnnotationSyncerAspect(documentService, threadService);
  }

  @Bean
  @ConditionalOnMissingBean
  MapperSyncerAspect mapperSyncerAspect() {
    return new MapperSyncerAspect(documentService, threadService);
  }
}
