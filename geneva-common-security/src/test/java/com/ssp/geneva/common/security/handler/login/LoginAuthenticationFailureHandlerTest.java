package com.ssp.geneva.common.security.handler.login;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class LoginAuthenticationFailureHandlerTest {

  @Mock private ObjectMapper objectMapper;
  @Mock private MessageHandler msgHandler;
  @Mock private SysConfigUtil sysConfig;
  @Mock private Environment environment;

  @InjectMocks private LoginAuthenticationFailureHandler loginFailureHandler;

  @Test
  void shouldVerifyOnAuthenticationFailureWhenPropertyExist() throws IOException {
    // given
    when(environment.getProperty("geneva.server.login")).thenReturn("sso");
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter printWriter = mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(printWriter);
    AuthenticationException exception = mock(AuthenticationException.class);

    // when
    loginFailureHandler.onAuthenticationFailure(request, response, exception);

    // then
    verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(response, times(2)).getWriter();
  }

  @Test
  void shouldVerifyOnAuthenticationFailureWhenPropertyDoesNotExist() throws IOException {
    // given
    when(environment.getProperty("geneva.server.login")).thenReturn("non-sso");
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter printWriter = mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(printWriter);
    AuthenticationException exception = mock(AuthenticationException.class);
    when(sysConfig.getErrorTraceEnabled()).thenReturn(false);
    when(objectMapper.writeValueAsString(any())).thenReturn("whatever");

    // when
    loginFailureHandler.onAuthenticationFailure(request, response, exception);

    // then
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response, times(3)).getWriter();
    verify(objectMapper).writeValueAsString(any());
    verify(printWriter).write("whatever");
    verify(printWriter).flush();
    verify(printWriter).close();
  }
}
