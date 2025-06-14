package com.ssp.geneva.common.security.filter.sso;

import com.ssp.geneva.common.security.config.GenevaSecurityProperties;
import com.ssp.geneva.common.security.matcher.BearerAuthenticationRequestMatcher;
import com.ssp.geneva.common.security.model.B2BIntrospectResponseDTO;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class SingleSignOnSessionFilter implements Filter {

  private static final Long DEFAULT_TIMEOUT = 5000L; // in milliseconds
  private static final String SESSION_LAST_CHECK_TIME = "sso_session_psuedo_creation_time";
  private Long refreshTokenTimeoutInterval = null; // in milliseconds
  private final String b2bUri;
  private final String realm;
  private final SysConfigUtil sysConfigUtil;
  private final Integer sessionTimeout;
  private final OAuth2RestTemplate oidcRestTemplate; // the template used for OIDC login
  private final RestTemplate simpleRestTemplate;
  private final BearerAuthenticationRequestMatcher bearerAuthenticationRequestMatcher =
      new BearerAuthenticationRequestMatcher();
  private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

  public SingleSignOnSessionFilter(
      OAuth2RestTemplate oAuth2RestTemplate,
      RestTemplate simpleRestTemplate,
      SysConfigUtil sysConfigUtil,
      GenevaSecurityProperties properties,
      OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
    this.oidcRestTemplate = oAuth2RestTemplate;
    this.simpleRestTemplate = simpleRestTemplate;
    this.b2bUri = properties.getSsoOneIdBaseUrl();
    this.realm = properties.getSsoOneIdRealm();
    this.sysConfigUtil = sysConfigUtil;
    this.sessionTimeout = properties.getSpringSessionMaxInactiveTimeout();
    this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
  }

  @Override
  public void destroy() {
    log.debug("SingleSignOnSessionFilter is taken out of service by container");
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    log.debug("SingleSignOnSessionFilter instantiated by container");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpSession httpSession = httpRequest.getSession();
    httpSession.setMaxInactiveInterval(sessionTimeout);
    String path = httpRequest.getRequestURI();
    log.debug("Inside doFilter(), URI path is : {}", path);
    if (!bearerAuthenticationRequestMatcher.matches(httpRequest)) {
      log.debug(
          "Inside doFilter(), value of refreshTokenTimeoutInterval: {}",
          refreshTokenTimeoutInterval);
      refreshTokenTimeoutInterval =
          (sysConfigUtil.getSsoB2BTokenRefreshInterval() != null)
              ? sysConfigUtil.getSsoB2BTokenRefreshInterval()
              : DEFAULT_TIMEOUT;
      var authentication = SecurityContextHolder.getContext().getAuthentication();

      if ((authentication instanceof OAuth2Authentication)
          || (authentication instanceof OAuth2AuthenticationToken)) {
        if (httpSession.getAttribute(SESSION_LAST_CHECK_TIME) == null) {
          httpSession.setAttribute(SESSION_LAST_CHECK_TIME, System.currentTimeMillis());
        }
        long timeElapsed =
            System.currentTimeMillis()
                - Long.parseLong((httpSession.getAttribute(SESSION_LAST_CHECK_TIME).toString()));
        if ((refreshTokenTimeoutInterval > 0) && (timeElapsed >= refreshTokenTimeoutInterval)) {
          log.debug(
              "Polling to see if the B2B session is still valid (time since last check is :{} in millisecs)",
              timeElapsed);
          httpSession = updateSSOSessionTime(httpSession, request);
          if (httpSession.getAttribute(SESSION_LAST_CHECK_TIME) == null) {
            oAuth2AuthorizedClientRepository.removeAuthorizedClient(
                "b2b", null, (HttpServletRequest) request, (HttpServletResponse) response);
            logout();
          }
        }
      }
    }
    chain.doFilter(request, response);
  }

  void logout() {
    // set the now invalid access token in Oauth2ClientContext
    // to null
    if ((SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2Authentication)) {

      oidcRestTemplate.getOAuth2ClientContext().setAccessToken(null);
    }

    // clear the Spring Security Context so that all further
    // requests result in 401
    SecurityContextHolder.getContext().setAuthentication(null);
    SecurityContextHolder.clearContext();
  }

  HttpSession updateSSOSessionTime(HttpSession httpSession, ServletRequest servletRequest) {

    var authorizedClient =
        oAuth2AuthorizedClientRepository.loadAuthorizedClient(
            "b2b", null, (HttpServletRequest) servletRequest);
    String refreshTokenValue = null;
    OAuth2RefreshToken token;
    if (authorizedClient == null) {
      refreshTokenValue =
          oidcRestTemplate.getOAuth2ClientContext().getAccessToken().getRefreshToken().getValue();

    } else if ((token = authorizedClient.getRefreshToken()) != null) {
      refreshTokenValue = token.getTokenValue();
    }

    if (getTokenStatus(refreshTokenValue)) {
      httpSession.setAttribute(SESSION_LAST_CHECK_TIME, System.currentTimeMillis());
      log.debug("B2B session still active");
    } else {
      httpSession.setAttribute(SESSION_LAST_CHECK_TIME, null);
      log.debug("B2B session is inactive, user must now be logged out.");
    }
    return httpSession;
  }

  private boolean getTokenStatus(String refreshToken) {
    boolean status = true; // assume current session is valid until told otherwise
    log.info("Getting refresh token status from B2B for token:{}", refreshToken);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    List<MediaType> accept = new ArrayList<>();
    accept.add(MediaType.APPLICATION_JSON);
    headers.setAccept(accept);
    StringBuilder body =
        new StringBuilder("realm=").append(realm).append("&token=").append(refreshToken);
    HttpEntity<?> entity = new HttpEntity<>(body.toString(), headers);
    try {
      String introspectAPI = "identity/oauth2/introspect";
      ResponseEntity<B2BIntrospectResponseDTO> response =
          simpleRestTemplate.exchange(
              getB2bUrl(introspectAPI), HttpMethod.POST, entity, B2BIntrospectResponseDTO.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        status = response.getBody().isActive();
        log.debug("Status for refresh token: {}, in B2B platform is: {} ", refreshToken, status);
      }
    } catch (Exception ex) {
      log.info(
          "Could not talk to B2B to check refresh token status (assuming session is still active) {},{}",
          ex.getClass(),
          ex.getMessage());
    }
    return status;
  }

  protected String getB2bUrl(String api) {
    if (b2bUri != null) {
      return StringUtils.endsWith(b2bUri, "/") ? b2bUri + api : b2bUri + "/" + api;
    }
    return null;
  }
}
