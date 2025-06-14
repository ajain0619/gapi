package com.ssp.geneva.common.security.handler.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.Error;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class LoginAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
  private final ObjectMapper jsonMapper;
  private final MessageHandler msgHandler;
  private final SysConfigUtil sysConfig;

  public LoginAuthenticationEntryPoint(
      ObjectMapper jsonMapper, MessageHandler msgHandler, SysConfigUtil sysConfig) {
    super("/login");
    this.jsonMapper = jsonMapper;
    this.msgHandler = msgHandler;
    this.sysConfig = sysConfig;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    if (authException instanceof AuthenticationCredentialsNotFoundException) {
      SecurityErrorCodes errorCode = SecurityErrorCodes.SECURITY_SESSION_EXPIRED;
      Error error =
          new Error(
              errorCode.getHttpStatus(),
              errorCode.getCode(),
              msgHandler.getMessage(errorCode.toString()),
              authException,
              sysConfig.getErrorTraceEnabled());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(jsonMapper.writeValueAsString(error));
      response.getWriter().flush();
      response.getWriter().close();
    } else {
      super.commence(request, response, authException);
    }
  }
}
