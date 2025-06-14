package com.nexage.admin.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CipherUtilTest {

  private static MockedStatic<Cipher> mockedSettings;

  @BeforeEach
  void init() {
    mockedSettings = mockStatic(Cipher.class);
  }

  @AfterEach
  void close() {
    mockedSettings.close();
  }

  @Test
  void testCipherUtil() throws Exception {
    var input = "input1";
    var encryptedByte = input.getBytes();
    var cipher = mock(Cipher.class);
    when(Cipher.getInstance("AES/CBC/PKCS5Padding")).thenReturn(cipher);
    when(cipher.doFinal(input.getBytes())).thenReturn(encryptedByte);
    String encrypted = CipherUtil.encrypt(input);
    String decrypted = CipherUtil.decrypt(encrypted);
    assertEquals(input, decrypted);
  }

  @Test
  void shouldThrowCryptoExceptionOnCipherNoSuchAlgorithmException() throws Exception {
    when(Cipher.getInstance("AES/CBC/PKCS5Padding")).thenThrow(new NoSuchAlgorithmException());

    GenevaAppRuntimeException exception =
        assertThrows(
            GenevaAppRuntimeException.class, () -> CipherUtil.getCipher(Cipher.ENCRYPT_MODE));

    assertEquals(CommonErrorCodes.COMMON_CRYPTO_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldThrowCryptoExceptionOnCipherNoSuchPaddingException() throws Exception {
    when(Cipher.getInstance("AES/CBC/PKCS5Padding")).thenThrow(new NoSuchPaddingException());

    GenevaAppRuntimeException exception =
        assertThrows(
            GenevaAppRuntimeException.class, () -> CipherUtil.getCipher(Cipher.ENCRYPT_MODE));

    assertEquals(CommonErrorCodes.COMMON_CRYPTO_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldThrowCryptoExceptionOnCipherInvalidKeyException() throws Exception {
    var cipher = mock(Cipher.class);

    when(Cipher.getInstance("AES/CBC/PKCS5Padding")).thenReturn(cipher);
    doThrow(new InvalidKeyException())
        .when(cipher)
        .init(anyInt(), any(Key.class), any(AlgorithmParameterSpec.class));

    GenevaAppRuntimeException exception =
        assertThrows(
            GenevaAppRuntimeException.class, () -> CipherUtil.getCipher(Cipher.ENCRYPT_MODE));

    assertEquals(CommonErrorCodes.COMMON_CRYPTO_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldThrowCryptoExceptionOnCipherInvalidAlgorithmParameterException() throws Exception {
    var cipher = mock(Cipher.class);
    when(Cipher.getInstance("AES/CBC/PKCS5Padding")).thenReturn(cipher);
    doThrow(new InvalidAlgorithmParameterException())
        .when(cipher)
        .init(anyInt(), any(Key.class), any(AlgorithmParameterSpec.class));

    GenevaAppRuntimeException exception =
        assertThrows(
            GenevaAppRuntimeException.class, () -> CipherUtil.getCipher(Cipher.ENCRYPT_MODE));

    assertEquals(CommonErrorCodes.COMMON_CRYPTO_ERROR, exception.getErrorCode());
  }
}
