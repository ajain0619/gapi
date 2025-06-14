package com.ssp.geneva.server.screenmanagement.config;

import com.nexage.admin.core.config.CoreDbSdkConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@AutoConfigureAfter({CoreDbSdkConfig.class})
@ComponentScan(basePackages = {"com.ssp.geneva.server.screenmanagement"})
public class GenevaServerScreenManagementConfig {

  public GenevaServerScreenManagementConfig() {
    log.info("geneva.auto-config:GenevaServerScreenManagementConfig");
  }
}
