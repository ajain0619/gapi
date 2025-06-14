package com.ssp.geneva.sdk.onecentral.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath;
import java.util.Map;
import org.junit.jupiter.api.Test;

class BuildUriTest {

  @Test
  void shouldFailOnInvalidUrl() {
    final String host = "https:/oneapi.com#";
    String path = OneCentralSdkResourcePath.CREATE_USER_URL_PATH.getResourcePath();
    Map<String, String> pathVars = Map.of("username", "testUser");
    RuntimeException runtimeException =
        assertThrows(RuntimeException.class, () -> BuildUri.build(host, path, pathVars));
    assertNotNull(runtimeException);
  }

  @Test
  void shouldBuild() {
    final String host = "https://oneapi.com";
    Map<String, String> pathVars = Map.of("type", "user", "id", "userName1", "roleId", "1234");
    Map<String, String> pathVars2 = Map.of("username", "testUser");

    String path = OneCentralSdkResourcePath.CREATE_SSP_USER_URL_PATH.getResourcePath();
    String url = BuildUri.build(host, path);
    assertEquals(url, host + "/one-central/user-migration/v1/users");

    path = OneCentralSdkResourcePath.CREATE_USER_URL_PATH.getResourcePath();
    url = BuildUri.build(host, path);
    assertEquals(url, host + "/one-central/user-management/v6/users/");

    path = OneCentralSdkResourcePath.UPDATE_USER_URL_PATH.getResourcePath();
    url = BuildUri.build(host, path, pathVars2);
    assertEquals(url, host + "/one-central/user-management/v6/users/testUser");

    path = OneCentralSdkResourcePath.RESET_PASSWORD_URL_PATH.getResourcePath();
    url = BuildUri.build(host, path, pathVars2);
    assertEquals(url, host + "/one-central/user-management/v6/users/testUser/password");

    path = OneCentralSdkResourcePath.ROLE_ASSIGN_URL_PATH.getResourcePath();
    url = BuildUri.build(host, path, pathVars);
    assertEquals(
        url, host + "/one-central/authorization-management/v2/entities/user/userName1/roles/1234");
  }
}
