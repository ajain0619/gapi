package com.ssp.geneva.common.settings.util;

import static com.nexage.admin.core.enums.GlobalConfigProperty.GENEVA_ERROR_TRACE_ENABLED;
import static com.nexage.admin.core.enums.GlobalConfigProperty.SSO_B2B_TOKEN_REFRESH_INTERVAL;

import com.ssp.geneva.common.base.settings.SystemConfigurable;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
@Log4j2
@Getter
public class SysConfigUtil implements SystemConfigurable {

  private final GlobalConfigService globalConfigService;
  private final Environment environment;

  public SysConfigUtil(GlobalConfigService globalConfigService, Environment environment) {
    this.globalConfigService = globalConfigService;
    this.environment = environment;
  }

  @PostConstruct
  public void init() {
    var keyTag = GENEVA_ERROR_TRACE_ENABLED.getPropertyName();
    boolean errorTraceEnabled =
        Objects.requireNonNullElse(
            globalConfigService.getBooleanValue(GENEVA_ERROR_TRACE_ENABLED), false);
    log.info("System has [{}] param with value [{}]", keyTag, errorTraceEnabled);
    parameters.put(GENEVA_ERROR_TRACE_ENABLED.getPropertyName(), errorTraceEnabled);

    keyTag = SSO_B2B_TOKEN_REFRESH_INTERVAL.getPropertyName();
    long tokenRefreshInterval = Long.parseLong(environment.getProperty(keyTag, "900000"));
    log.info("System has [{}] param with value [{}]", keyTag, tokenRefreshInterval);
    parameters.put(keyTag, tokenRefreshInterval);
  }

  @ManagedAttribute
  public void setErrorTraceEnabled(Boolean errorTraceEnabled) {
    var keyTag = GENEVA_ERROR_TRACE_ENABLED.getPropertyName();
    parameters.put(keyTag, errorTraceEnabled);
  }

  @ManagedAttribute
  public void setSsoB2BTokenRefreshInterval(Long tokenRefreshInterval) {
    var keyTag = SSO_B2B_TOKEN_REFRESH_INTERVAL.getPropertyName();
    parameters.put(keyTag, tokenRefreshInterval);
  }

  @ManagedAttribute
  @Override
  public void restart() {
    this.init();
  }

  public Boolean getErrorTraceEnabled() {
    return (Boolean) parameters.get(GENEVA_ERROR_TRACE_ENABLED.getPropertyName());
  }

  public Long getSsoB2BTokenRefreshInterval() {
    return (Long) parameters.get(SSO_B2B_TOKEN_REFRESH_INTERVAL.getPropertyName());
  }
}
