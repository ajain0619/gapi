package com.nexage.app.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.app.dto.publisher.PublisherAdSourceDefaultsDTO;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PublisherAdSourceDefaultsTest {

  private static final Set<String> fields =
      Set.of(
          "pid",
          "version",
          "sellerPid",
          "adSourcePid",
          "username",
          "password",
          "apiToken",
          "apiKey");

  @Test
  void testObjectInitialization() {
    var password = "password";
    var adSourceDefaults =
        PublisherAdSourceDefaultsDTO.newBuilder()
            .withPid(100L, fields)
            .withVersion(2, fields)
            .withSellerPid(200L, fields)
            .withAdSourcePid(300L, fields)
            .withUserName("user", fields)
            .withPassword(password, fields)
            .withApiToken("token", fields)
            .withApiKey("key", fields)
            .build();

    assertEquals(100L, adSourceDefaults.getPid().longValue());
    assertEquals(2, adSourceDefaults.getVersion().intValue());
    assertEquals(200L, adSourceDefaults.getSellerPid().longValue());
    assertEquals(300L, adSourceDefaults.getAdSourcePid().longValue());
    assertEquals("user", adSourceDefaults.getUsername());
    assertEquals(password, adSourceDefaults.getPassword());
    assertEquals("token", adSourceDefaults.getApiToken());
    assertEquals("key", adSourceDefaults.getApiKey());
  }

  @Test
  void getPasswordWhenNotSet() {
    var adSource = PublisherAdSourceDefaultsDTO.newBuilder().build();
    assertNull(adSource.getPassword());
  }
}
