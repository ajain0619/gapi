package com.nexage.admin.core.util;

import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;

/** Utility class for string encoding and decoding. */
@Log4j2
public class CipherUtil {

  private CipherUtil() {
    // private constructor to prevent instantiation
  }

  /**
   * Returns the {@code Cipher} used for cryptographic operation. This can be either encryption or
   * decryption, based on {@code mode} parameter.
   *
   * <p>Parameter mode can be either {@code Cipher.ENCRYPT_MODE} or {@code Cipher.DECRYPT_MODE}.
   *
   * @param mode operation mode (encryption or decryption)
   * @return cryptogrphic cipher
   * @throws Exception if an error occurs
   */
  public static Cipher getCipher(int mode) {
    try {
      SecretKey key = new SecretKeySpec(new byte[16], "AES");
      final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
      final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(mode, key, iv);
      return cipher;
    } catch (NoSuchAlgorithmException
        | NoSuchPaddingException
        | InvalidKeyException
        | InvalidAlgorithmParameterException ex) {
      throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_CRYPTO_ERROR);
    }
  }

  /**
   * Encrypts the input string and returns encrypted version encoded as Base64.
   *
   * @param input string to encrypt
   * @return Base64 encoded encrypted string
   */
  public static String encrypt(String input) {
    if (input != null) {
      try {
        final Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedByte = cipher.doFinal(input.getBytes());
        return new String(Base64.encodeBase64(encryptedByte));
      } catch (Exception e) {
        log.error("Encryption process is failed", e);
        throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_CRYPTO_ERROR);
      }
    }
    return input;
  }

  /**
   * Decrypts previously encrypted string. Input string is assumed to be Base64 encoded.
   *
   * @param input string to decrypt, Base64 encoded
   * @return decrypted string
   */
  public static String decrypt(String input) {
    if (input != null) {
      try {
        final Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] decryptedByte = cipher.doFinal(Base64.decodeBase64(input));
        return new String(decryptedByte);
      } catch (Exception e) {
        log.error("Decryption process is failed", e);
        throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_CRYPTO_ERROR);
      }
    }
    return input;
  }
}
