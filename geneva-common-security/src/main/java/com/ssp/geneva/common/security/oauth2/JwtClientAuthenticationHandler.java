package com.ssp.geneva.common.security.oauth2;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import lombok.extern.log4j.Log4j2;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.auth.ClientAuthenticationHandler;
import org.springframework.util.MultiValueMap;

@Log4j2
public class JwtClientAuthenticationHandler implements ClientAuthenticationHandler {

  @Override
  public void authenticateTokenRequest(
      OAuth2ProtectedResourceDetails resource,
      MultiValueMap<String, String> form,
      HttpHeaders headers) {
    if (resource.isAuthenticationRequired()) {

      String audience = resource.getAccessTokenUri();
      try {
        String jwt =
            generateJsonWebToken(resource.getClientId(), resource.getClientSecret(), audience);
        log.debug("jwt has been created: {}", jwt);
        form.set("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        form.set("client_assertion", jwt);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
  }

  protected static String generateJsonWebToken(
      final String clientId, final String secret, final String audience) throws JoseException {

    JwtClaims claims = new JwtClaims();
    claims.setIssuedAt(NumericDate.now());
    claims.setExpirationTimeMinutesInTheFuture(10);
    claims.setSubject(clientId);
    claims.setIssuer(clientId);
    claims.setAudience(audience);
    claims.setGeneratedJwtId();

    Key key = new HmacKey(secret.getBytes(StandardCharsets.UTF_8));

    JsonWebSignature jws = new JsonWebSignature();
    jws.setPayload(claims.toJson());
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
    jws.setKey(key);
    jws.setDoKeyValidation(false);

    return jws.getCompactSerialization();
  }
}
