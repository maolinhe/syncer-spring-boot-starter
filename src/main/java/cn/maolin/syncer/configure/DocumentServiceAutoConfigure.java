// ==========================
// Copyright (c) 2023-04-23 Sioux
// All rights reserved.
// ==========================
package cn.maolin.syncer.configure;

import cn.maolin.syncer.service.DocumentService;
import cn.maolin.syncer.service.DocumentServiceImpl;
import lombok.Data;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConditionalOnClass(value = {DocumentService.class, DocumentServiceImpl.class})
public class DocumentServiceAutoConfigure {

  private final RestHighLevelClient restHighLevelClient;

  public DocumentServiceAutoConfigure(RestHighLevelClient restHighLevelClient) {
    this.restHighLevelClient = restHighLevelClient;
  }

  @Bean
  @ConditionalOnMissingBean
  DocumentService documentService() {
    return new DocumentServiceImpl(restHighLevelClient);
  }

}
