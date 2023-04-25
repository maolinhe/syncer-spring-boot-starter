// ==========================
// Copyright (c) 2023-04-23 Sioux
// All rights reserved.
// ==========================
package com.sioux.syncer.configure;

import com.sioux.syncer.SyncerAspect;
import com.sioux.syncer.service.DocumentService;
import com.sioux.syncer.service.ThreadService;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConditionalOnClass(value = {SyncerAspect.class})
public class SyncerAutoConfigure {

  private final DocumentService documentService;

  private final ThreadService threadService;

  public SyncerAutoConfigure(DocumentService documentService, ThreadService threadService) {
    this.documentService = documentService;
    this.threadService = threadService;
  }

  @Bean
  @ConditionalOnMissingBean
  SyncerAspect syncerAspect() {
    return new SyncerAspect(documentService, threadService);
  }
}
