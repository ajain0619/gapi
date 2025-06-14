package com.ssp.geneva.sdk.identityb2b.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class JwtGeneratorTest {
  @Test
  void shouldGenerate() {
    final String host = "https://id.b2b.yahooinc.com";
    final String realm = "aolcorporate/aolexternals";
    final String clientId = "12345";
    final String clientSecret = "1111111";

    String Jwt = JwtGenerator.generate(host, realm, clientId, clientSecret);
    assertNotNull(Jwt);
    assertFalse(StringUtils.isEmpty(Jwt));
  }

  @Test
  void shouldFailOnGenerate() {
    final String host = "https://id.b2b.yahooinc.com";
    final String realm = "aolcorporate/aolexternals";
    final String clientId = "12345";
    String Jwt = JwtGenerator.generate(host, realm, clientId, null);
    assertTrue(StringUtils.isEmpty(Jwt));
  }
}
