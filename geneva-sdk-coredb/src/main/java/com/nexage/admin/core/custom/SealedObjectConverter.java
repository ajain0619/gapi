package com.nexage.admin.core.custom;

import com.nexage.admin.core.util.CipherUtil;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.persistence.AttributeConverter;
import lombok.extern.log4j.Log4j2;

/** JPA converter between {@code SealedObject} and {@code String}. */
@Log4j2
public class SealedObjectConverter implements AttributeConverter<SealedObject, String> {

  /**
   * Converts {@code SealedObject} to string for storage to DB.
   *
   * @param sealedObject sealed object to convert
   * @return decrypted string
   */
  @Override
  public String convertToDatabaseColumn(SealedObject sealedObject) {
    try {
      return (String) sealedObject.getObject(CipherUtil.getCipher(Cipher.DECRYPT_MODE));
    } catch (Exception ex) {
      log.error("Failed to get extract value from sealed object", ex);
      return null;
    }
  }

  /**
   * Encrypts input string and stores it inside {@code SealedObject} so it is not exposed in-memory.
   *
   * @param value string value to encrypt
   * @return {@code SealedObject} class containing input string
   */
  @Override
  public SealedObject convertToEntityAttribute(String value) {
    try {
      return new SealedObject(value, CipherUtil.getCipher(Cipher.ENCRYPT_MODE));
    } catch (Exception ex) {
      log.error("Failed to set password", ex);
      return null;
    }
  }
}
