package com.nexage.admin.core.util;

import static org.apache.commons.lang.StringUtils.isBlank;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptionUtil {

  /**
   * @param encrypted encrypted string.
   * @return decrypted string
   */
  public static String decrypt(String encrypted) {
    if (!isBlank(encrypted)) {
      try {
        SecretKey key = new SecretKeySpec(new byte[16], "AES");
        final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedByte = cipher.doFinal(Base64.decodeBase64(encrypted));
        return new String(decryptedByte);
      } catch (Exception e) {
        log.error("Unable to decrypt", e);
        return null;
      }
    }
    return encrypted;
  }
}
