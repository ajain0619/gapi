package com.nexage.admin.core.bidder.support.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CountryLetterCodesValidatorTest {

  @Test
  void testEmpty() throws Exception {
    CountryLetterCodesValidator validator = new CountryLetterCodesValidator();
    validator.initialize(null);

    assertFalse(validator.isValid("", null));
    assertTrue(validator.isValid(null, null));
    assertFalse(validator.isValid("111,111", null));
    assertTrue(validator.isValid("AFG", null));
    assertFalse(validator.isValid("AFG,", null));
    assertTrue(validator.isValid("AFG,USA,BLR", null));
    assertFalse(validator.isValid("AFG,USA,BLR,", null));
    assertFalse(validator.isValid("AF", null));
    assertFalse(validator.isValid("USA,111", null));
    assertFalse(validator.isValid("1,11", null));
  }
}
