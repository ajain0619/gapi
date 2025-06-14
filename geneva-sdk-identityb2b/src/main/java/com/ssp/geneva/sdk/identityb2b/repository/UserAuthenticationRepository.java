package com.ssp.geneva.sdk.identityb2b.repository;

import static com.ssp.geneva.sdk.identityb2b.model.IdentityB2bSdkResourcePath.USER_INFO_URL_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.identityb2b.config.IdentityB2bSdkConfigProperties;
import com.ssp.geneva.sdk.identityb2b.util.BuildUri;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class UserAuthenticationRepository extends BaseRestRepository {

  public UserAuthenticationRepository(
      final ObjectMapper identityB2bObjectMapper,
      final RestTemplate restTemplate,
      final IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties) {
    super(identityB2bObjectMapper, restTemplate, identityB2bSdkConfigProperties.getB2bHost());
  }

  public ResponseEntity<Map<String, Object>> getUserInfo(String accessToken) {
    var resourcePath = USER_INFO_URL_PATH.getResourcePath();

    String url = BuildUri.build(this.baseUrl, resourcePath);
    HttpEntity requestEntity = new HttpEntity<>(buildRequestHeaders(accessToken));
    log.debug("The url used get user info in b2b is : {}", url);

    return makeRequest(url, HttpMethod.GET, requestEntity, Map.class);
  }
}
