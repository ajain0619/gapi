package com.ssp.geneva.common.security.filter.sso;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ssp.geneva.common.security.config.GenevaSecurityProperties;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.client.RestTemplate;

@Log4j2
@ExtendWith(MockitoExtension.class)
class SingleSignOnSessionFilterTest {

  @Mock private SysConfigUtil sysConfigUtil;
  @Mock private FilterConfig filterConfig;
  @Mock private HttpServletRequest request;
  @Mock private ServletResponse response;
  @Mock private FilterChain filterChain;
  @Mock private OAuth2Authentication oAuth2Authentication;
  @Mock private HttpSession httpSession;
  @Mock OAuth2RestTemplate oAuth2RestTemplate;
  @Mock OAuth2RefreshToken refreshToken;
  @Mock OAuth2ClientContext oAuth2ClientContext;
  @Mock OAuth2AccessToken oAuth2AccessToken;

  private static GenevaSecurityProperties genevaSecurityProperties;
  private static WireMockServer wm;

  private SingleSignOnSessionFilter singleSignOnSessionFilter;

  private static final String SSO_SESSION_PSUEDO_CREATION_TIME = "sso_session_psuedo_creation_time";

  @BeforeAll
  public static void init() {
    wm = new WireMockServer(18080);
    wm.start();
    genevaSecurityProperties =
        GenevaSecurityProperties.builder()
            .ssoOneIdBaseUrl("http://localhost:18080")
            .ssoOneIdRealm("test")
            .ssoOneIdBaseApplicationUri("http://localhost:28080")
            .springSessionMaxInactiveTimeout(1440)
            .build();
  }

  @AfterAll
  public static void tearDown() {
    if (wm.isRunning()) {
      wm.shutdown();
    }
  }

  @BeforeEach
  void setup() throws ServletException {
    when(sysConfigUtil.getSsoB2BTokenRefreshInterval()).thenReturn(11001L);
    singleSignOnSessionFilter =
        new SingleSignOnSessionFilter(
            oAuth2RestTemplate,
            new RestTemplate(),
            sysConfigUtil,
            genevaSecurityProperties,
            new HttpSessionOAuth2AuthorizedClientRepository());
    singleSignOnSessionFilter.init(filterConfig);
  }

  @Test
  void
      testSSOCreationTime_IsUnset_InHttpSession_WhenRefreshInterval_IsElapsed_And_Introspect_API_Returns_False() {
    wm.stubFor(
        post(urlPathMatching("/identity/oauth2/introspect.*"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{ \"active\": false}")));

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(oAuth2Authentication);
    SecurityContextHolder.setContext(securityContext);

    when(request.getRequestURI()).thenReturn("/geneva/");
    when(request.getSession()).thenReturn(httpSession);
    when(httpSession.getAttribute(SSO_SESSION_PSUEDO_CREATION_TIME)).thenReturn(null);
    when(httpSession.getAttribute(SSO_SESSION_PSUEDO_CREATION_TIME))
        .thenReturn((System.currentTimeMillis() - 15600L));
    when(oAuth2RestTemplate.getOAuth2ClientContext()).thenReturn(oAuth2ClientContext);
    when(oAuth2ClientContext.getAccessToken()).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(refreshToken);
    when(refreshToken.getValue()).thenReturn("3f94eb47-a295-4977-a124-e27bea5c828b");

    assertDoesNotThrow(() -> singleSignOnSessionFilter.doFilter(request, response, filterChain));
  }

  @Test
  void
      testSSOCreationTimeInHttpSession_WhenRefreshInterval_IsElapsed_And_Introspect_API_Returns_True() {
    wm.stubFor(
        post(urlPathMatching("/identity/oauth2/introspect.*"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{ \"active\": true}")));

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(oAuth2Authentication);
    SecurityContextHolder.setContext(securityContext);

    when(request.getRequestURI()).thenReturn("/geneva/");
    when(request.getSession()).thenReturn(httpSession);
    when(httpSession.getAttribute(SSO_SESSION_PSUEDO_CREATION_TIME)).thenReturn(null);
    when(httpSession.getAttribute(SSO_SESSION_PSUEDO_CREATION_TIME))
        .thenReturn((System.currentTimeMillis() - 15600L));
    when(oAuth2RestTemplate.getOAuth2ClientContext()).thenReturn(oAuth2ClientContext);
    when(oAuth2ClientContext.getAccessToken()).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(refreshToken);
    when(refreshToken.getValue()).thenReturn("3f94eb47-a295-4977-a124-e27bea5c828b");

    assertDoesNotThrow(() -> singleSignOnSessionFilter.doFilter(request, response, filterChain));
  }
}
