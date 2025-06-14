package com.ssp.geneva.common.settings.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
class SysConfigUtilTest {

  @Mock private GlobalConfigService configService;
  @Mock private Environment environment;

  private SysConfigUtil sysConfigUtil;

  @BeforeEach
  void setUp() {
    sysConfigUtil = new SysConfigUtil(configService, environment);
  }

  @Test
  void shouldVerify() {

    // given
    var whateverRefreshInterval = "1";
    when(configService.getBooleanValue(GlobalConfigProperty.GENEVA_ERROR_TRACE_ENABLED))
        .thenReturn(true);
    when(environment.getProperty(
            GlobalConfigProperty.SSO_B2B_TOKEN_REFRESH_INTERVAL.getPropertyName(), "900000"))
        .thenReturn(whateverRefreshInterval);

    // when
    sysConfigUtil.init();

    // then
    verify(configService).getBooleanValue(GlobalConfigProperty.GENEVA_ERROR_TRACE_ENABLED);
    verify(environment)
        .getProperty(
            GlobalConfigProperty.SSO_B2B_TOKEN_REFRESH_INTERVAL.getPropertyName(), "900000");
  }

  @Test
  void shouldVerifyDefaultValues() {

    // given
    var whateverRefreshInterval = "1";
    when(configService.getBooleanValue(GlobalConfigProperty.GENEVA_ERROR_TRACE_ENABLED))
        .thenReturn(null);
    when(environment.getProperty(
            GlobalConfigProperty.SSO_B2B_TOKEN_REFRESH_INTERVAL.getPropertyName(), "900000"))
        .thenReturn(whateverRefreshInterval);

    // when
    sysConfigUtil.init();

    // then
    verify(configService).getBooleanValue(GlobalConfigProperty.GENEVA_ERROR_TRACE_ENABLED);
    verify(environment)
        .getProperty(
            GlobalConfigProperty.SSO_B2B_TOKEN_REFRESH_INTERVAL.getPropertyName(), "900000");
    var errorTraceEnabled = sysConfigUtil.getParameter("geneva.errortrace.enable");
    assertNotNull(errorTraceEnabled);
    assertFalse((Boolean) errorTraceEnabled);
  }
}
