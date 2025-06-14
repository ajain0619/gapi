package com.ssp.geneva.server.bidinspector.config;

import com.ssp.geneva.sdk.dwdb.config.DwDbSdkConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@AutoConfigureAfter({DwDbSdkConfig.class})
@ComponentScan(basePackages = {"com.ssp.geneva.server.bidinspector"})
public class GenevaServerBidInspectorConfig {

  public GenevaServerBidInspectorConfig() {
    log.info("geneva.auto-config:GenevaServerBidInspectorConfig");
  }
}
