package com.ssp.geneva.server.report.config;

import com.nexage.admin.core.config.CoreDbSdkConfig;
import com.ssp.geneva.sdk.dwdb.config.DwDbSdkConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Log4j2
@Configuration
@AutoConfigureAfter({CoreDbSdkConfig.class, DwDbSdkConfig.class})
@ImportResource("classpath*:applicationContext-reports.xml")
@ComponentScan(basePackages = {"com.ssp.geneva.server.report"})
public class GenevaServerReportConfig {

  public GenevaServerReportConfig() {
    log.info("geneva.auto-config:GenevaServerReportConfig");
  }
}
