package com.ssp.geneva.common.security.handler.login;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.User;
import com.ssp.geneva.common.security.util.login.LoginEntitlementCorrectionHandler;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class LoginAuthenticationSuccessHandlerTest {

  @Mock private LoginEntitlementCorrectionHandler entitlementCorrectionHandler;

  private LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;

  private final HttpServletRequest request = mock(HttpServletRequest.class);
  private final HttpServletResponse response = mock(HttpServletResponse.class);
  private final Authentication authentication = mock(Authentication.class);
  private final PrintWriter printWriter = mock(PrintWriter.class);

  private static final List<Entitlement> entitlements =
      Collections.singletonList(mock(Entitlement.class));

  @BeforeEach
  void setUp() {
    loginAuthenticationSuccessHandler =
        new LoginAuthenticationSuccessHandler(entitlementCorrectionHandler);
  }

  @Test
  void shouldSuccessfullyExecuteOnAuthenticationSuccess() throws IOException, ServletException {
    // given
    when(authentication.getName()).thenReturn("username");
    when(response.getWriter()).thenReturn(printWriter);
    when(response.isCommitted()).thenReturn(true);

    when(entitlementCorrectionHandler.getObjectMapper()).thenReturn(new ObjectMapper());

    // when
    loginAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

    // then
    verify(entitlementCorrectionHandler).correctEntitlements("username");
    verify(response, times(3)).getWriter();
  }

  @Test
  void shouldSuccessfullyExecuteOnAuthenticationSuccessForSellerSeatUser()
      throws IOException, ServletException {
    // given
    when(entitlementCorrectionHandler.getObjectMapper()).thenReturn(new ObjectMapper());

    User user = mock(User.class);
    user.setSellerSeat(mock(SellerSeat.class));

    when(authentication.getName()).thenReturn("username");
    when(response.getWriter()).thenReturn(printWriter);
    when(response.isCommitted()).thenReturn(false);

    // when
    loginAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

    // then
    verify(entitlementCorrectionHandler).correctEntitlements("username");
    verify(response, times(3)).getWriter();
  }

  @Test
  void shouldFailExecutionOnAuthenticationSuccessForBadPrincipal() {
    // given
    when(authentication.getName()).thenReturn(null);
    // then
    assertThrows(
        RuntimeException.class,
        () ->
            loginAuthenticationSuccessHandler.onAuthenticationSuccess(
                request, response, authentication));
  }

  @Test
  void shouldFailExecutionOnAuthenticationSuccessForUnknownUser() {
    // given
    when(authentication.getName()).thenReturn("username");
    // then
    assertThrows(
        RuntimeException.class,
        () ->
            loginAuthenticationSuccessHandler.onAuthenticationSuccess(
                request, response, authentication));
  }
}
