package com.nexage.admin.core.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.util.CipherUtil;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SealedObjectConverterTest {

  private static final String TEST_INPUT = "test input string";
  private SealedObjectConverter converter;

  @BeforeEach
  public void setup() {
    converter = new SealedObjectConverter();
  }

  @Test
  void convertToString() throws Exception {
    var sealedObject = new SealedObject(TEST_INPUT, CipherUtil.getCipher(Cipher.ENCRYPT_MODE));

    var out = converter.convertToDatabaseColumn(sealedObject);
    assertEquals(TEST_INPUT, out);
  }

  @Test
  void convertToSealedObject() throws Exception {
    var out = converter.convertToEntityAttribute(TEST_INPUT);
    assertEquals(TEST_INPUT, out.getObject(CipherUtil.getCipher(Cipher.DECRYPT_MODE)));
  }

  @Test
  void convertToStringWithError() {
    var mockObject = mock(SealedObject.class);

    var out = converter.convertToDatabaseColumn(mockObject);
    assertNull(out);
  }
}
