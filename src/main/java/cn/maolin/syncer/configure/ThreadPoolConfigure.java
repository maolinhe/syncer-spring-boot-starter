package cn.maolin.syncer.configure;

import cn.maolin.syncer.service.ThreadService;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "syncer-executors")
public class ThreadPoolConfigure {

  private int coreSize = 1;

  @Bean
  @ConditionalOnMissingBean
  public ThreadService threadService() {
    return new ThreadService(coreSize);
  }
}
