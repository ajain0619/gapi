package com.ssp.geneva.server.deals.config;

import com.nexage.admin.core.config.CoreDbSdkConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@AutoConfigureAfter({CoreDbSdkConfig.class})
@ComponentScan(basePackages = {"com.ssp.geneva.server.deals"})
public class GenevaServerDealsConfig {

  public GenevaServerDealsConfig() {
    log.info("geneva.auto-config:GenevaServerDealsConfig");
  }
}
