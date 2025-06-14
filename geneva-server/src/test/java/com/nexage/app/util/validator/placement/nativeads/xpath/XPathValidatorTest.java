package com.nexage.app.util.validator.placement.nativeads.xpath;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.app.dto.seller.nativeads.validators.BaseTestWebNativeExtention;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class XPathValidatorTest extends BaseTestWebNativeExtention {

  XPathValidator validator = new XPathValidator();

  @SneakyThrows
  @Test
  void verifyNotValid() {
    boolean valid = validator.isValid(INVALID_XPATH, context);
    assertFalse(valid);
  }

  @Test
  void verifyValid() {
    boolean valid = validator.isValid(VALID_XPATH, context);
    assertTrue(valid);
  }
}
