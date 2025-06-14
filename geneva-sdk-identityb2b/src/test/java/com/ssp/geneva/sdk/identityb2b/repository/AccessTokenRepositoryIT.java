package com.ssp.geneva.sdk.identityb2b.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.identityb2b.config.IdentityB2bSdkConfig;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bSdkException;
import com.ssp.geneva.sdk.identityb2b.model.B2bAccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class, IdentityB2bSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class AccessTokenRepositoryIT {

  @RegisterExtension
  static WireMockExtension wireMockRule = new WireMockExtension(options().port(9999));

  @Autowired private AccessTokenRepository accessTokenRepository;

  @Test
  void shouldComplainInvalidRequestRefreshToken() {
    // given
    wireMockRule.stubFor(
        post(urlPathMatching("/identity/oauth2/access_token"))
            .withHeader("Content-Type", matching("application/x-www-form-urlencoded;charset=UTF-8"))
            .willReturn(aResponse().withStatus(HttpStatus.UNAUTHORIZED.value())));

    // when
    var exception =
        assertThrows(
            IdentityB2bSdkException.class,
            () ->
                accessTokenRepository.getAccessTokenByRefreshToken(
                    "b89fba14-24ac-481b-a1e2-664c81af8888"));

    assertNotNull(exception.getErrorCode());
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getErrorCode().getHttpStatus());
  }

  @Test
  void shouldRespondSuccessfullyForRefreshToken() {
    // given
    wireMockRule.stubFor(
        post(urlPathMatching("/identity/oauth2/access_token"))
            .withHeader("Content-Type", matching("application/x-www-form-urlencoded;charset=UTF-8"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{\n"
                            + "  \"access_token\": \"52acc0ab-77ce-4641-8b26-95b2381375ac\",\n"
                            + "  \"scope\": \"openid\",\n"
                            + "  \"id_token\": \"eyAidHlwIjogIkpXVCIsICJraWQiOiAic3lsbGM2bmp0MWtncWt0ZDltdCswemNlcXN1IiwgImN0eSI6ICJKV1QiLCAiYWxnIjogIlJTMjU2IiB9.eyAiYXRfaGFzaCI6ICJFaW5WbzRUY0pHOFB6b0FZVS16Y2p3IiwgInN1YiI6ICI5MzY2Mjg1OC05YjY3LTQ4OGEtYWNmNy01ZjhhOTkwMTBlYjUiLCAiaXNzIjogImh0dHBzOi8vaWQtdWF0Mi5jb3JwLmFvbC5jb20vaWRlbnRpdHkiLCAidG9rZW5OYW1lIjogImlkX3Rva2VuIiwgImF1ZCI6IFsgImQ2MjRiYjgzLTczNWItNGY1My1iNTU2LTdhMTMwYzljMDFmMyIgXSwgIm9wcyI6ICI3NGFmNzVmMC0wYTE4LTQ0NzgtYmE3Yy01YzNiN2QxNjk3ZmUiLCAiYXpwIjogImQ2MjRiYjgzLTczNWItNGY1My1iNTU2LTdhMTMwYzljMDFmMyIsICJhdXRoX3RpbWUiOiAxNDc1MTYxMzY3LCAicmVhbG0iOiAiL2IyYiIsICJleHAiOiAxNDc1MTYxOTY3LCAidG9rZW5UeXBlIjogIkpXVFRva2VuIiwgImlhdCI6IDE0NzUxNjEzNjcgfQ.SabwGH9stGdOluNaQs-gatTUuRVt2X0UhCyFM-aBKokEwR4HD3mEgorT4dciEUh5LaQv9qLzDGupSr9e_IvxFwOudEoqHQGwCWiKHcpNFGTEKYV8z8JR_oVQl9M-xU_fNa_0bM5WmvDlmRZH6qaXZN4Hac0m-n_GbFQJRgNwKhI\",\n"
                            + "  \"token_type\": \"Bearer\",\n"
                            + "  \"expires_in\": \"599\"\n"
                            + "}")));

    // when
    ResponseEntity<B2bAccessToken> response =
        accessTokenRepository.getAccessTokenByRefreshToken("b89fba14-24ac-481b-a1e2-664c81af8888");

    // then
    assertNotNull(response);
  }
}
