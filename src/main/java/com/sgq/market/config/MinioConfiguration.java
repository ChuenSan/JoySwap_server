package com.sgq.market.config;

import com.sgq.market.constants.MinioProp;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {
  @Autowired
  private MinioProp minioProp;
  
  @Bean
  public MinioClient minioClient() throws Exception {
    MinioClient client = MinioClient.builder()
      .endpoint(minioProp.getEndpoint())
      .credentials(minioProp.getAccesskey(), minioProp.getSecretKey())
      .build();
    return client;
  }
  
}
