package com.ssp.geneva.sdk.identityb2b.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ssp.geneva.sdk.identityb2b.model.IdentityB2bSdkResourcePath;
import org.junit.jupiter.api.Test;

class BuildUriTest {

  @Test
  void shouldFailOnInvalidUrl() {
    final String host = "ttps://id.b2b.yahooinc.com";
    String path = IdentityB2bSdkResourcePath.ACCESS_TOKEN_URL_PATH.getResourcePath();
    RuntimeException runtimeException =
        assertThrows(RuntimeException.class, () -> BuildUri.build(host, path));
    assertNotNull(runtimeException);
  }

  @Test
  void shouldBuild() {
    final String host = "https://id.b2b.yahooinc.com";

    String path = IdentityB2bSdkResourcePath.ACCESS_TOKEN_URL_PATH.getResourcePath();
    String url = BuildUri.build(host, path);
    assertEquals(url, host + "/identity/oauth2/access_token");
  }
}
