package com.nexage.app.dto;

import static com.nexage.app.util.validator.ValidationTestUtil.assertViolationsContains;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BidderDeviceTypeDTOValidationTest {

  private BidderDeviceTypeDTO bidderDeviceTypeDTO;
  private Validator validator;

  @BeforeEach
  void setup() {
    bidderDeviceTypeDTO = createValidBidderDeviceTypeDTO();
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  private static BidderDeviceTypeDTO createValidBidderDeviceTypeDTO() {
    BidderDeviceTypeDTO bidderDeviceTypeDTO = new BidderDeviceTypeDTO();
    bidderDeviceTypeDTO.setDeviceTypeId(1);
    return bidderDeviceTypeDTO;
  }

  @Test
  void valid() {
    var violations = validator.validate(bidderDeviceTypeDTO);
    assertTrue(violations.isEmpty());
  }

  @Test
  void deviceTypeIdNull() {
    bidderDeviceTypeDTO.setDeviceTypeId(null);
    var violations = validator.validate(bidderDeviceTypeDTO);
    assertViolationsContains(violations, "deviceTypeId", "must not be null");
  }
}
