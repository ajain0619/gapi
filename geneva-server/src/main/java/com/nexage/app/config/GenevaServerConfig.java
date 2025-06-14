package com.nexage.app.config;

import com.nexage.admin.core.config.CoreDbSdkConfig;
import com.ssp.geneva.common.error.config.GenevaErrorAutoConfiguration;
import com.ssp.geneva.common.settings.config.GenevaSettingsAutoConfiguration;
import com.ssp.geneva.common.swagger.GenevaSwaggerAutoConfiguration;
import com.ssp.geneva.sdk.dwdb.config.DwDbSdkConfig;
import com.ssp.geneva.server.bidinspector.config.GenevaServerBidInspectorConfig;
import com.ssp.geneva.server.report.config.GenevaServerReportConfig;
import com.ssp.geneva.server.screenmanagement.config.GenevaServerScreenManagementConfig;
import java.net.MalformedURLException;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.UrlResource;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.security.core.session.SessionRegistryImpl;

/**
 * This is the entrypoint for Geneva Server configuration as long as we have a mixed Xml and Java
 * configuration mechanism. Xml goes first, as long as there is not a directive before.
 *
 * <p>This class is a pre-step before we can move forward a full spring-boot app.
 *
 * <p>{@link Import annotation} will act before {@link ImportResource annotation} due to the order
 * listed in this class. On a full spring-boot application we won't use those imports methods, but
 * auto-configure capabilities.
 */
@Log4j2
@Configuration
@Import({
  DwDbSdkConfig.class,
  CoreDbSdkConfig.class,
  GenevaSettingsAutoConfiguration.class,
  GenevaErrorAutoConfiguration.class,
  GenevaServerReportConfig.class,
  GenevaServerMetricsServletConfig.class,
  GenevaSwaggerAutoConfiguration.class,
  GenevaServerBidInspectorConfig.class,
  GenevaServerScreenManagementConfig.class
})
@AutoConfigureAfter({
  DwDbSdkConfig.class,
  CoreDbSdkConfig.class,
  GenevaSettingsAutoConfiguration.class,
  GenevaErrorAutoConfiguration.class,
  GenevaServerReportConfig.class,
  GenevaServerMetricsServletConfig.class,
  GenevaSwaggerAutoConfiguration.class,
  GenevaServerBidInspectorConfig.class,
  GenevaServerScreenManagementConfig.class
})
@ComponentScan(
    basePackages = {"com.ssp.geneva.sdk", "com.nexage.app"},
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = {"com.nexage.app.web"}))
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class GenevaServerConfig {

  public GenevaServerConfig() {
    log.info("geneva.auto-config:GenevaServerConfig");
  }

  @Bean("sessionRegistry")
  public SessionRegistryImpl sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean("featureVisibilityResource")
  public UrlResource featureVisibilityResource() throws MalformedURLException {
    return new UrlResource(System.getProperty("geneva.features"));
  }

  @Bean
  public InstrumentationLoadTimeWeaver loadTimeWeaver() {
    return new InstrumentationLoadTimeWeaver();
  }
}
