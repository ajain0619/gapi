package com.nexage.app;

import com.nexage.app.config.GenevaServerConfig;
import com.nexage.app.config.GenevaServletContextConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(
    scanBasePackageClasses = {GenevaServerConfig.class, GenevaServletContextConfig.class})
@EnableConfigurationProperties
public class GenevaServer {

  public static void main(String[] args) {
    SpringApplication.run(GenevaServer.class, args);
  }
}
