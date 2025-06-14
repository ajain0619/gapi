package com.ssp.geneva.common.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@ConditionalOnProperty(prefix = "springdoc.swagger-ui", name = "enabled", havingValue = "true")
public class GenevaSwaggerAutoConfiguration {

  @Value("${geneva.common.swagger.title}")
  private String title;

  @Value("${geneva.common.swagger.description}")
  private String description;

  @Bean
  public OpenAPI genevaSwaggerAPI() {
    return new OpenAPI().info(new Info().title(title).description(description));
  }
}
