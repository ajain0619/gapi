package com.nexage.app.services.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.HBPartnerResponseConfigConstraint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

class HbPartnerResponseConfigValidatorTest extends BaseValidatorTest {

  @Mock private HBPartnerResponseConfigConstraint annotation;

  @InjectMocks private HBPartnerResponseConfigValidator validator;

  @Spy private CustomObjectMapper objectMapper = new CustomObjectMapper();

  private static final String INVALID_RESPONSE_CONFIG = "Invalid Response Config";

  @BeforeEach
  void setup() throws Exception {
    validator.init();
    initializeContext();
    initializeConstraint();
  }

  @Test
  void validateNullResponseConfig() {
    assertTrue(validator.isValid(null, ctx));
    verifyNoInteractions(ctx);
  }

  @Test
  void when_data_is_empty_string_responseConfig_is_not_valid() {
    assertFalse(validator.isValid("", ctx));
    verify(ctx).buildConstraintViolationWithTemplate(INVALID_RESPONSE_CONFIG);
    verify(annotation).message();
  }

  @Test
  void when_data_is_empty_json_responseConfig_is_not_valid() {
    assertFalse(validator.isValid("{}", ctx));
    verify(ctx).buildConstraintViolationWithTemplate(INVALID_RESPONSE_CONFIG);
    verify(annotation).message();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"deliveryTrackingUrl\": \"bdm\"}", // wrong_values_for_deliveryTrackingUrl
        "{\"deliveryTrackingUrl\": [\"adm\", \"burl\"]}", // more_number_of_values_for_deliveryTrackingUrl
        "{\"deliveryTrackingUrl\": \"adm\", \"priceMacro\" : 123}", // wrong_dataType_for_priceMacro
        "{\"deliveryTrackingUrl\": \"adm\", \"priceMacro\" : \"anyString\", \"unknownKey\": \"unknownValue\"}", // unknown_property
      })
  void shouldReturnFalseWhenJsonIsValidButValuesAreNot(String responseConfig) {
    assertTrue(validJson(responseConfig));
    assertFalse(validator.isValid(responseConfig, ctx));
    verify(ctx).buildConstraintViolationWithTemplate(INVALID_RESPONSE_CONFIG);
    verify(annotation).message();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"deliveryTrackingUrl\": \"adm\", \"priceMacro\" : \"anyString\" }", // with_all_correct_values
        "{\"deliveryTrackingUrl\": \"adm\"}", // with_deliveryTrackingUrl_option1
        "{\"deliveryTrackingUrl\": \"nurl\"}", // with_deliveryTrackingUrl_option2
        "{\"deliveryTrackingUrl\": \"burl\"}", // with_deliveryTrackingUrl_option3
      })
  void shouldReturnTrueWhenBothJsonAndValuesAreValid(String responseConfig) {
    assertTrue(validJson(responseConfig));
    assertTrue(validator.isValid(responseConfig, ctx));
    verifyNoInteractions(ctx);
  }

  private boolean validJson(String data) {
    try {
      objectMapper.readTree(data);
    } catch (JsonProcessingException e) {
      return false;
    }
    return true;
  }

  @Override
  protected void initializeConstraint() {
    lenient().when(annotation.message()).thenReturn(INVALID_RESPONSE_CONFIG);
    ReflectionTestUtils.setField(validator, "annotation", annotation);
  }
}
