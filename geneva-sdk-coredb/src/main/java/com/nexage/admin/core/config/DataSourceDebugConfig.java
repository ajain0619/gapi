package com.nexage.admin.core.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Log4j2
@Profile("debug")
@Configuration
@ComponentScan("com.github.gavlyukovskiy.boot")
public class DataSourceDebugConfig {

  public DataSourceDebugConfig() {
    log.info("geneva.auto-config:DataSourceDebugConfig");
  }
}
