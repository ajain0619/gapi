package com.nexage.geneva.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

public class MD5HashGenerator {

  public static String md5Hex(String accessKey, String secretKey) {
    Hex hex = new Hex();
    String data = accessKey + ":api.nexage.com:" + secretKey;
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("No MD5 algorithm available!");
    }

    return new String(hex.encode(digest.digest(data.getBytes())));
  }
}
