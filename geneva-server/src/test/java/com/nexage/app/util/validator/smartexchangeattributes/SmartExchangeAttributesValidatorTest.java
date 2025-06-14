package com.nexage.app.util.validator.smartexchangeattributes;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class SmartExchangeAttributesValidatorTest extends BaseValidatorTest {

  private static final String MIN_REV_SHARE_MISSING =
      "Field 'smartMarginMinRevShare' must be present";
  private static final String MAX_REV_SHARE_MISSING =
      "Field 'smartMarginMaxRevShare' must be present";
  private static final String STARTING_REV_SHARE_MISSING =
      "Field 'smartMarginStartRevShare' must be present";
  private static final String MIN_REV_SHARE_BETWEEN =
      "Field 'smartMarginMinRevShare' must have a value between 0 and 1";
  private static final String MAX_REV_SHARE_BETWEEN =
      "Field 'smartMarginMaxRevShare' must have a value between 0 and 1";
  private static final String MAX_REV_SHARE_GREATER =
      "Field 'smartMarginMaxRevShare' must be greater than 'smartMarginMinRevShare'";
  private static final String STARTING_REV_SHARE_BETWEEN =
      "Field 'smartMarginStartRevShare' must have a value between 0.2 and 0.4";

  private final SmartExchangeAttributesValidator validator = new SmartExchangeAttributesValidator();
  private SmartExchangeAttributesDTO smartExchangeAttributes;

  @Mock private SmartExchangeAttributesConstraint constraint;

  @Override
  protected void initializeConstraint() {
    validator.initialize(constraint);
  }

  @BeforeEach
  public void init() {
    super.init();
    smartExchangeAttributes =
        SmartExchangeAttributesDTO.newBuilder().withSmartMarginEnabled(true).build();
  }

  @Test
  void shouldBeValid() {
    assertTrue(validator.isValid(smartExchangeAttributes, ctx));
    verifyNoInteractions(ctx);
  }

  @Test
  void shouldBeValidWhenSmartMarginDisabled() {
    smartExchangeAttributes.setSmartMarginEnabled(false);

    assertTrue(validator.isValid(smartExchangeAttributes, ctx));
    verifyNoInteractions(ctx);
  }
}
