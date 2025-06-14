package com.nexage.geneva.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class CipherUtil {

  public static String encryptObiGuid(String guid) {
    if (guid != null) {
      try {
        SecretKey key = new SecretKeySpec(new byte[16], "AES");
        final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedByte = cipher.doFinal(guid.getBytes());
        return new String(Base64.encodeBase64(encryptedByte));
      } catch (Exception e) {
        throw new RuntimeException("Encryption process is failed" + e.getMessage());
      }
    }
    return guid;
  }

  public static String decryptObiGuid(String guid) {
    if (guid != null) {
      try {
        SecretKey key = new SecretKeySpec(new byte[16], "AES");
        final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedByte = cipher.doFinal(Base64.decodeBase64(guid));
        return new String(decryptedByte);
      } catch (Exception e) {
        throw new RuntimeException("Decryption process is failed" + e.getMessage());
      }
    }
    return guid;
  }
}
