package com.ssp.geneva.common.error.exception.logger;

import static com.ssp.geneva.common.base.settings.SystemConfigurable.PredefinedParameterKey.ERROR_TRACE_ENABLED_KEY;
import static java.util.Objects.nonNull;

import com.ssp.geneva.common.base.settings.SystemConfigurable;
import java.util.Map.Entry;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class ExceptionLogger {

  private static final String DISABLED_MARK = "false";
  private static final String ENABLED_MARK = "true";
  private final SystemConfigurable sysConfigUtil;
  private final Properties properties;

  public ExceptionLogger(SystemConfigurable sysConfigUtil, Properties properties) {
    this.sysConfigUtil = sysConfigUtil;
    this.properties = properties;
  }

  public void logException(Throwable throwable) {
    if (nonNull(sysConfigUtil)) {
      boolean isEnabled =
          (Boolean) sysConfigUtil.getParameter(ERROR_TRACE_ENABLED_KEY.getParameterKey());
      if (isEnabled) {
        log.error("", throwable);
      } else {
        boolean isLogThrowable = true;
        for (Entry<Object, Object> entry : properties.entrySet()) {
          log.debug("checking exception to log: {} ", entry.getKey().toString());
          if (throwable.getClass().getCanonicalName().matches(entry.getKey().toString())
              && (DISABLED_MARK.equalsIgnoreCase(
                  StringUtils.defaultIfBlank(entry.getValue().toString(), ENABLED_MARK)))) {
            isLogThrowable = false;
            break;
          }
        }
        if (isLogThrowable) log.error("", throwable);
      }
    }
  }
}
