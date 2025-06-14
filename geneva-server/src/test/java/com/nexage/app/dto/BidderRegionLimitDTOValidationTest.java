package com.nexage.app.dto;

import static com.nexage.app.util.validator.ValidationTestUtil.assertViolationsContains;
import static com.nexage.app.util.validator.ValidationTestUtil.stringOfLength;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BidderRegionLimitDTOValidationTest {

  private BidderRegionLimitDTO bidderRegionLimitDTO;
  private Validator validator;

  @BeforeEach
  void setup() {
    bidderRegionLimitDTO = createValidBidderRegionLimitDTO();
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  private static BidderRegionLimitDTO createValidBidderRegionLimitDTO() {
    BidderRegionLimitDTO bidderRegionLimitDTO = new BidderRegionLimitDTO();
    bidderRegionLimitDTO.setCountriesFilter("USA");
    bidderRegionLimitDTO.setRequestRate(5);
    return bidderRegionLimitDTO;
  }

  @Test
  void validBidderRegionLimitDTO() {
    var violations = validator.validate(bidderRegionLimitDTO);
    assertTrue(violations.isEmpty());
  }

  @Test
  void nameMinSizeOne() {
    bidderRegionLimitDTO.setName("");
    var violations = validator.validate(bidderRegionLimitDTO);
    assertViolationsContains(violations, "name", "size must be between 1 and 255");
  }

  @Test
  void nameMaxSize255() {
    bidderRegionLimitDTO.setName(stringOfLength(256));
    var violations = validator.validate(bidderRegionLimitDTO);
    assertViolationsContains(violations, "name", "size must be between 1 and 255");
  }

  @Test
  void countriesFilterNotNull() {
    bidderRegionLimitDTO.setCountriesFilter(null);
    var violations = validator.validate(bidderRegionLimitDTO);
    assertViolationsContains(violations, "countriesFilter", "must not be null");
  }

  @Test
  void countriesFilterThreeLetterCode() {
    bidderRegionLimitDTO.setCountriesFilter("US");
    var violations = validator.validate(bidderRegionLimitDTO);
    assertViolationsContains(
        violations,
        "countriesFilter",
        "You must use a comma separated list of three letter country codes");
  }

  @Test
  void countriesFilterMaxSize1000() {
    String[] countries = new String[256];
    Arrays.fill(countries, "ABC");
    bidderRegionLimitDTO.setCountriesFilter(String.join(",", countries));
    var violations = validator.validate(bidderRegionLimitDTO);
    assertViolationsContains(violations, "countriesFilter", "size must be between 1 and 1000");
  }

  @Test
  void requestRateNotNull() {
    bidderRegionLimitDTO.setRequestRate(null);
    var violations = validator.validate(bidderRegionLimitDTO);
    assertViolationsContains(violations, "requestRate", "must not be null");
  }

  @Test
  void requestRateMinOne() {
    bidderRegionLimitDTO.setRequestRate(0);
    var violations = validator.validate(bidderRegionLimitDTO);
    assertViolationsContains(violations, "requestRate", "must be greater than or equal to 1");
  }

  @Test
  void requestRateMax999_999() {
    bidderRegionLimitDTO.setRequestRate(1_000_000);
    var violations = validator.validate(bidderRegionLimitDTO);
    assertViolationsContains(violations, "requestRate", "must be less than or equal to 999999");
  }
}
