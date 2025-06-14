package com.ssp.geneva.sdk.identityb2b.util;

import static com.ssp.geneva.sdk.identityb2b.model.IdentityB2bSdkResourcePath.ACCESS_TOKEN_URL_PATH;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.keys.HmacKey;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtGenerator {

  public static String generate(
      String b2bHost, String realm, String clientId, String clientSecret) {

    String audience = b2bHost + ACCESS_TOKEN_URL_PATH.getResourcePath() + "?realm=" + realm;
    JwtClaims claims = new JwtClaims();
    claims.setIssuedAt(NumericDate.now());
    claims.setExpirationTimeMinutesInTheFuture(10);
    claims.setSubject(clientId);
    claims.setIssuer(clientId);
    claims.setAudience(audience);
    claims.setGeneratedJwtId();

    try {
      Key key = new HmacKey(clientSecret.getBytes(StandardCharsets.UTF_8));

      JsonWebSignature jws = new JsonWebSignature();
      jws.setPayload(claims.toJson());
      jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
      jws.setKey(key);
      jws.setDoKeyValidation(false);

      return jws.getCompactSerialization();

    } catch (Exception e) {
      log.error("JWT Generation failed");
      return "";
    }
  }
}
