package com.nexage.app.dto;

import static com.nexage.app.util.validator.ValidationTestUtil.assertViolationsContains;
import static com.nexage.app.util.validator.ValidationTestUtil.assertViolationsNotContain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aol.crs.model.v1.ContentType;
import java.util.List;
import java.util.Set;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreativeRegistrationDTOValidationTest {
  private CreativeRegistrationDTO creativeRegistrationDTO;
  private Validator validator;

  @BeforeEach
  void setup() {
    creativeRegistrationDTO = createValidCreativeRegistrationDTO();
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void shouldPassValidationWhenCreativeRegistrationDTOIsValid() {
    var violations = validator.validate(creativeRegistrationDTO);
    assertTrue(violations.isEmpty());
    assertEquals(List.of("1", "2"), creativeRegistrationDTO.getDealIds());
  }

  @Test
  void shouldPassValidationWhenSellerIdsIsNull() {
    creativeRegistrationDTO.setSellerIds(null);
    var violations = validator.validate(creativeRegistrationDTO);
    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldPassValidationWhenDealIdsIsNull() {
    creativeRegistrationDTO.setDealIds(null);
    var violations = validator.validate(creativeRegistrationDTO);
    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenContentMarkupIsNull() {
    creativeRegistrationDTO.setContentMarkup(null);
    var violations = validator.validate(creativeRegistrationDTO);
    assertViolationsContains(violations, "contentMarkup", "Required field");
  }

  @Test
  void shouldFailValidationWhenContentTypeIsNull() {
    creativeRegistrationDTO.setContentType(null);
    var violations = validator.validate(creativeRegistrationDTO);
    assertViolationsContains(violations, "contentType", "Value should not be empty");
  }

  @Test
  void shouldFailValidationWhenAdomainIsNull() {
    creativeRegistrationDTO.setAdomain(null);
    var violations = validator.validate(creativeRegistrationDTO);
    assertViolationsContains(violations, "adomain", "Required field");
  }

  @Test
  void shouldNotFailValidationWhenIurlIsNull() {
    creativeRegistrationDTO.setIurl(null);
    var violations = validator.validate(creativeRegistrationDTO);
    assertViolationsNotContain(violations, "iurl", "Value should not be empty");
    assertViolationsNotContain(violations, "iurl", "Required field");
  }

  @Test
  void shouldFailValidationWhenDisplayIsNull() {
    creativeRegistrationDTO.setDisplay(null);
    var violations = validator.validate(creativeRegistrationDTO);
    assertViolationsContains(violations, "display", "Value should not be empty");
  }

  @Test
  void shouldFailValidationWhenCountryCodeIsNull() {
    creativeRegistrationDTO.setCountryCode(null);
    var violations = validator.validate(creativeRegistrationDTO);
    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldPassValidationWhenSeatIdIsNull() {
    creativeRegistrationDTO.setSeatId(null);
    assertEquals(Set.of(), validator.validate(creativeRegistrationDTO));
  }

  @Test
  void shouldFailValidationWhenSeatIdIsTooLong() {
    creativeRegistrationDTO.setSeatId("long-buyer-seat-id-" + "0".repeat(255));
    assertViolationsContains(
        validator.validate(creativeRegistrationDTO), "seatId", "Entered value is too long");
  }

  private static CreativeRegistrationDTO createValidCreativeRegistrationDTO() {
    return CreativeRegistrationDTO.builder()
        .seatId("buyer-seat-id")
        .sellerIds(List.of(1234L, 4567L))
        .contentMarkup("content")
        .contentType(ContentType.DISPLAY_MARKUP)
        .adomain("testing.com")
        .iurl("testing.com/image.png")
        .display(new Display(1, 2))
        .countryCode("US")
        .dealIds(List.of("1", "2"))
        .build();
  }
}
