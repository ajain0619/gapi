package com.ssp.geneva.common.security.oauth2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.common.security.service.Oauth2UserProvider;
import com.ssp.geneva.common.security.service.OneCentralUserDetailsService;
import com.ssp.geneva.common.security.service.UserAuthorizationService;
import com.ssp.geneva.common.security.util.UserTestData;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.jetty.http.HttpHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestOperations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class BearerTokenIntrospectorTest {

  private BearerTokenIntrospector bearerTokenIntrospector;

  @Mock private UserAuthorizationService userAuthorizationService;

  @Mock private OneCentralUserDetailsService userDetailsService;

  @Mock private RestOperations restOperations;

  @InjectMocks private Oauth2UserProvider userProvider;

  @BeforeEach
  void SetUp() {
    bearerTokenIntrospector =
        new BearerTokenIntrospector("http://localhost:5544", restOperations, userProvider);
    bearerTokenIntrospector.setRequestEntityConverter(
        a -> RequestEntity.post("http://localhost:5544").body(Collections.emptyMap()));

    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    request.addHeader(HttpHeader.AUTHORIZATION.name(), "bearer 3453");
  }

  @Test
  void shouldIntrospectReturnValidOAuth2AuthenticatedPrincipal() {
    Map<String, String> body = new HashMap<>();
    body.put("active", "true");
    var oneCentralUserAuthResponse = new OneCentralUserAuthResponse();
    oneCentralUserAuthResponse.setStatus("ACTIVE");
    var user = UserTestData.createUser();
    user.setUserName("testUserName");
    user.setMigratedOneCentral(true);
    var userAuth = new UserAuth(user, List.of());
    when(restOperations.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .thenReturn(ResponseEntity.of(Optional.of(body)));
    when(userAuthorizationService.getUserAuthorization(any()))
        .thenReturn(ResponseEntity.of(Optional.of(oneCentralUserAuthResponse)));
    when(userDetailsService.loadUserBy1CUsername(any(), anyBoolean())).thenReturn(userAuth);
    var oAuth2AuthenticatedPrincipal = bearerTokenIntrospector.introspect("test123");
    assertEquals("testUserName", oAuth2AuthenticatedPrincipal.getName());
    assertTrue(
        oAuth2AuthenticatedPrincipal.getAttribute("springUserDetails")
            instanceof SpringUserDetails);
  }
}
