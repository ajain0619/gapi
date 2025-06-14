package com.ssp.geneva.common.error.exception.logger;

import static com.ssp.geneva.common.base.settings.SystemConfigurable.PredefinedParameterKey.ERROR_TRACE_ENABLED_KEY;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.base.settings.SystemConfigurable;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExceptionLoggerTest {

  @Mock private SystemConfigurable config;
  @Mock private Properties properties;

  private ExceptionLogger exceptionLogger;

  @BeforeEach
  void setUp() {
    exceptionLogger = new ExceptionLogger(config, properties);
  }

  @Test
  void shouldLogUnfilteredExceptionWithErrorTraceEnabledShouldLog() {
    when(config.getParameter(ERROR_TRACE_ENABLED_KEY.getParameterKey())).thenReturn(true);
    exceptionLogger.logException(new NullPointerException("Test"));
    verify(config).getParameter(ERROR_TRACE_ENABLED_KEY.getParameterKey());
    verify(properties, never()).getProperty(ERROR_TRACE_ENABLED_KEY.getParameterKey());
  }

  @Test
  void shouldLogUnfilteredExceptionWithErrorTraceDisabledShouldLog() {
    when(config.getParameter(ERROR_TRACE_ENABLED_KEY.getParameterKey())).thenReturn(false);
    exceptionLogger.logException(new NullPointerException("Test"));
    verify(config).getParameter(ERROR_TRACE_ENABLED_KEY.getParameterKey());
  }

  @Test
  void shouldLogFilteredExceptionWithErrorTraceDisabledShouldNotLog() {
    when(config.getParameter(ERROR_TRACE_ENABLED_KEY.getParameterKey())).thenReturn(false);
    exceptionLogger.logException(
        new GenevaAppRuntimeException(CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR));
    verify(config).getParameter(ERROR_TRACE_ENABLED_KEY.getParameterKey());
  }

  @Test
  void shouldLogFilteredExceptionWithErrorTraceEnabledShouldLog() {
    when(config.getParameter(ERROR_TRACE_ENABLED_KEY.getParameterKey())).thenReturn(true);
    exceptionLogger.logException(
        new GenevaAppRuntimeException(CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR));
    verify(config).getParameter(ERROR_TRACE_ENABLED_KEY.getParameterKey());
    verify(properties, never()).getProperty(ERROR_TRACE_ENABLED_KEY.getParameterKey());
  }
}
