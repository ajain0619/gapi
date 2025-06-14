package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.app.util.BidInspectorQueryFieldParameter;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class BidInspectorQueryParameterValidatorTest extends BaseValidatorTest {
  @Mock private BidInspectorQueryFieldParameterConstraint parameterConstraint;
  @Captor ArgumentCaptor<String> captor;
  @InjectMocks private static BidInspectorQueryParameterValidator validator;
  @Mock private MessageHandler messageHandler;

  private static final String INVALID_QUERY_FIELD_PARAMETER = "Query field parameter is invalid";

  @Test
  void shouldReturnTrueWhenMissingOrEmptyQueryFieldMap() {
    boolean isValid = validator.isValid(prepareValidationMap(new String[0], new String[0]), ctx);
    assertTrue(isValid);
  }

  @Test
  void shouldReturnFalseWhenFieldIsUnknown() {
    when(messageHandler.getMessage(any())).thenReturn(INVALID_QUERY_FIELD_PARAMETER);
    BidInspectorQueryFieldParams queryFieldMap = prepareValidationMap("keyNotValid", "some_val");
    boolean isValid = validator.isValid(queryFieldMap, ctx);
    validateErrorResult(isValid, INVALID_QUERY_FIELD_PARAMETER);
  }

  @Test
  void shouldReturnTrueWhenBidderIdFieldHasOnlyNumericValues() {
    BidInspectorQueryFieldParams queryFieldMap =
        prepareValidationMap(BidInspectorQueryFieldParameter.BIDDER_ID.getName(), "12345");
    boolean isValid = validator.isValid(queryFieldMap, ctx);
    assertTrue(isValid);
  }

  @Test
  void shouldReturnFalseWhenBidderIdFieldHasAnAlphanumericValue() {
    when(messageHandler.getMessage(any())).thenReturn(INVALID_QUERY_FIELD_PARAMETER);
    BidInspectorQueryFieldParams queryFieldMap =
        prepareValidationMap(BidInspectorQueryFieldParameter.BIDDER_ID.getName(), "abc123");
    boolean isValid = validator.isValid(queryFieldMap, ctx);
    validateErrorResult(isValid, INVALID_QUERY_FIELD_PARAMETER);
  }

  @Test
  void shouldReturnTrueWhenSellerIdFieldHasOnlyNumericValues() {
    BidInspectorQueryFieldParams queryFieldMap =
        prepareValidationMap(BidInspectorQueryFieldParameter.SELLER_ID.getName(), "12345");
    boolean isValid = validator.isValid(queryFieldMap, ctx);
    assertTrue(isValid);
  }

  @Test
  void shouldReturnFalseWhenSellerIdFieldHasAnAlphanumericValue() {
    when(messageHandler.getMessage(any())).thenReturn(INVALID_QUERY_FIELD_PARAMETER);
    BidInspectorQueryFieldParams queryFieldMap =
        prepareValidationMap(BidInspectorQueryFieldParameter.SELLER_ID.getName(), "123abc");
    boolean isValid = validator.isValid(queryFieldMap, ctx);
    validateErrorResult(isValid, INVALID_QUERY_FIELD_PARAMETER);
  }

  @Test
  void shouldReturnFalseWhenFieldsValueIsNull() {
    when(messageHandler.getMessage(any())).thenReturn(INVALID_QUERY_FIELD_PARAMETER);
    BidInspectorQueryFieldParams inputParams =
        prepareValidationMap(BidInspectorQueryFieldParameter.DEAL_ID.getName(), null);
    boolean isValid = validator.isValid(inputParams, ctx);
    validateErrorResult(isValid, INVALID_QUERY_FIELD_PARAMETER);
  }

  @Test
  void shouldReturnFalseWhenNotAllFieldsHaveValues() {
    when(messageHandler.getMessage(any())).thenReturn(INVALID_QUERY_FIELD_PARAMETER);
    String[] keys = {
      BidInspectorQueryFieldParameter.BIDDER_ID.getName(),
      BidInspectorQueryFieldParameter.PLACEMENT_ID.getName(),
      BidInspectorQueryFieldParameter.SITE_ID.getName()
    };
    BidInspectorQueryFieldParams queryFieldMap =
        prepareValidationMap(keys, new String[] {"11", "12", null});
    boolean isValid = validator.isValid(queryFieldMap, ctx);
    validateErrorResult(isValid, INVALID_QUERY_FIELD_PARAMETER);
  }

  @Test
  void shouldReturnFalseWhenNotAllFieldsHaveValidValues() {
    when(messageHandler.getMessage(any())).thenReturn(INVALID_QUERY_FIELD_PARAMETER);
    String[] keys = {
      BidInspectorQueryFieldParameter.BIDDER_ID.getName(),
      BidInspectorQueryFieldParameter.PLACEMENT_ID.getName(),
      BidInspectorQueryFieldParameter.SITE_ID.getName()
    };
    BidInspectorQueryFieldParams queryFieldMap =
        prepareValidationMap(keys, new String[] {"11", "12", "xyz123"});
    boolean isValid = validator.isValid(queryFieldMap, ctx);
    validateErrorResult(isValid, INVALID_QUERY_FIELD_PARAMETER);
  }

  @Test
  void shouldReturnFalseEvenWhenOneFieldIsUnknown() {
    when(messageHandler.getMessage(any())).thenReturn(INVALID_QUERY_FIELD_PARAMETER);
    String[] keys = {
      BidInspectorQueryFieldParameter.BIDDER_ID.getName(),
      BidInspectorQueryFieldParameter.PLACEMENT_ID.getName(),
      "invalidKey"
    };
    BidInspectorQueryFieldParams queryFieldMap =
        prepareValidationMap(keys, new String[] {"11", "12", "13"});
    boolean isValid = validator.isValid(queryFieldMap, ctx);
    validateErrorResult(isValid, INVALID_QUERY_FIELD_PARAMETER);
  }

  @Test
  void shouldReturnTrueWhenAllFieldsAreValid() {
    String[] keys = {
      BidInspectorQueryFieldParameter.BIDDER_ID.getName(),
      BidInspectorQueryFieldParameter.PLACEMENT_ID.getName(),
      BidInspectorQueryFieldParameter.APP_BUNDLE_ID.getName()
    };
    BidInspectorQueryFieldParams queryFieldMap =
        prepareValidationMap(keys, new String[] {"11", "12", "13"});
    boolean isValid = validator.isValid(queryFieldMap, ctx);
    assertTrue(isValid);
  }

  private BidInspectorQueryFieldParams prepareValidationMap(String[] keys, String[] values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    for (int i = 0; i < keys.length; i++) {
      map.add(keys[i], values[i]);
    }
    return new BidInspectorQueryFieldParams(map, SearchQueryOperator.AND);
  }

  private BidInspectorQueryFieldParams prepareValidationMap(String key, String value) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(key, value);
    return new BidInspectorQueryFieldParams(map, SearchQueryOperator.AND);
  }

  @Override
  protected void initializeConstraint() {
    ReflectionTestUtils.setField(validator, "messageHandler", messageHandler);
    lenient().when(parameterConstraint.field()).thenReturn("Query Field");
    validator.initialize(parameterConstraint);
  }

  private void validateErrorResult(boolean valid, String expectedMessage) {
    verify(ctx, atLeastOnce()).buildConstraintViolationWithTemplate(captor.capture());
    assertFalse(valid);
    assertEquals(expectedMessage, captor.getValue());
  }
}
