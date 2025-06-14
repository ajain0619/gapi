package com.ssp.geneva.common.security.handler.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.Error;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class LoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final ObjectMapper objectMapper;
  private final MessageHandler msgHandler;
  private final SysConfigUtil sysConfig;
  private final Environment environment;

  public LoginAuthenticationFailureHandler(
      ObjectMapper objectMapper,
      MessageHandler msgHandler,
      SysConfigUtil sysConfig,
      Environment environment) {
    this.objectMapper = objectMapper;
    this.msgHandler = msgHandler;
    this.sysConfig = sysConfig;
    this.environment = environment;
  }

  // in this case, you cannot simply throw exception and have ControllerExceptionHandler handle it.
  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {

    /*
     If authentication is done though sso, we can ignore all login processing.
     `/login` endpoint ( and this class ) will be removed permanently in the future.
    */
    if (Objects.equals(environment.getProperty("geneva.server.login"), "sso")) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().flush();
      response.getWriter().close();
      return;
    }

    Error error = null;
    String errorMessage = null;
    if (exception.getCause() instanceof NullPointerException
        || exception instanceof UsernameNotFoundException
        || exception instanceof BadCredentialsException) {
      error = new Error(SecurityErrorCodes.SECURITY_BAD_CREDENTIALS);
      errorMessage = msgHandler.getMessage(SecurityErrorCodes.SECURITY_BAD_CREDENTIALS.toString());
    } else if (exception.getCause() instanceof GenevaSecurityException) {
      GenevaSecurityException securityException = ((GenevaSecurityException) exception.getCause());
      error = securityException.getError();
      errorMessage =
          msgHandler.getMessage(msgHandler.getMessage(securityException.getErrorCode().toString()));
    } else {
      error = new Error(SecurityErrorCodes.SECURITY_UNKNOWN_FAILURE);
      errorMessage = exception.getLocalizedMessage();
    }

    error.setErrorMessage(errorMessage);
    if (Boolean.TRUE.equals(sysConfig.getErrorTraceEnabled())) {
      error.setErrorTrace(Throwables.getStackTraceAsString(exception));
    }

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(objectMapper.writeValueAsString(error));
    response.getWriter().flush();
    response.getWriter().close();
  }
}
