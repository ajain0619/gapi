package com.nexage.app.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

@Log4j2
public class HashUtils {

  public String sha1(String data) {
    return DigestUtils.sha1Hex(data);
  }

  public String md5(String data) {
    return DigestUtils.md5Hex(data);
  }

  public String calculateHash(int hashCode) {
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.reset();
      messageDigest.update(String.valueOf(hashCode).getBytes(StandardCharsets.UTF_8));
      final byte[] resultByte = messageDigest.digest();
      return new String(Hex.encodeHex(resultByte));
    } catch (NoSuchAlgorithmException e) {
      log.error("Unable to generate hash.");
      return null;
    }
  }
}
