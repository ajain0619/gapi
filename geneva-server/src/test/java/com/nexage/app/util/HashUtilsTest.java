package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class HashUtilsTest {

  MessageDigest messageDigest;

  HashUtils hashUtils = new HashUtils();

  @Test
  void shouldGetSha1() {
    String key = "whatever";
    String response = hashUtils.sha1(key);
    assertNotNull(response);
    assertEquals("d869db7fe62fb07c25a0403ecaea55031744b5fb", response);
  }

  @Test
  void shouldGetMd5() {
    String key = "whatever";
    String response = hashUtils.md5(key);
    assertNotNull(response);
    assertEquals("008c5926ca861023c1d2a36653fd88e2", response);
  }

  @Test
  void shouldCalculateHash() {
    int hashcode = 0;
    String response = hashUtils.calculateHash(hashcode);
    assertNotNull(response);
    assertEquals("cfcd208495d565ef66e7dff9f98764da", response);
  }

  @Test
  @SneakyThrows
  void noSuchAlgorithmExceptionCaught() {

    NoSuchAlgorithmException exception =
        assertThrows(
            NoSuchAlgorithmException.class, () -> messageDigest.getInstance("not-an-algorithm"));

    assertEquals("not-an-algorithm MessageDigest not available", exception.getMessage());
  }
}
