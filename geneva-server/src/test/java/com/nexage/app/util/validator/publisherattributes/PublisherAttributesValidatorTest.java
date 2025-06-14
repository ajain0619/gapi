package com.nexage.app.util.validator.publisherattributes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.ssp.geneva.common.error.handler.MessageHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PublisherAttributesValidatorTest extends BaseValidatorTest {

  @Mock private MessageHandler messageHandler;

  @InjectMocks private PublisherAttributesValidator validator;

  @Mock private PublisherAttributesConstraint constraint;

  private static final String MUTUALLY_EXCLUSIVE =
      "Fields 'hbThrottleEnabled' and 'smartQPSEnabled' cannot be both enabled";

  @Override
  protected void initializeConstraint() {
    validator.initialize(constraint);
  }

  @Test
  void shouldBeValidWhenHbThrottleAndSmartQPSDisabled() {
    PublisherAttributes publisherAttributes =
        PublisherAttributes.newBuilder()
            .withHbThrottleEnabled(false)
            .withSmartQPSEnabled(false)
            .build();

    assertTrue(validator.isValid(publisherAttributes, ctx));
    verifyNoInteractions(ctx);
  }

  @Test
  void shouldBeValidWhenHbThrottleEnabled() {
    PublisherAttributes publisherAttributes =
        PublisherAttributes.newBuilder().withHbThrottleEnabled(true).build();

    assertTrue(validator.isValid(publisherAttributes, ctx));
    verifyNoInteractions(ctx);
  }

  @Test
  void shouldBeValidWhenSmartQPSEnabled() {
    PublisherAttributes publisherAttributes =
        PublisherAttributes.newBuilder().withSmartQPSEnabled(true).build();

    assertTrue(validator.isValid(publisherAttributes, ctx));
    verifyNoInteractions(ctx);
  }

  @Test
  void shouldNotBeValidWhenHbThrottleAndSmartQPSEnabled() {
    PublisherAttributes publisherAttributes =
        PublisherAttributes.newBuilder()
            .withHbThrottleEnabled(true)
            .withSmartQPSEnabled(true)
            .build();
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_MUTUALLY_EXCLUSIVE.toString()))
        .thenReturn(MUTUALLY_EXCLUSIVE);
    assertFalse(validator.isValid(publisherAttributes, ctx));
    verify(ctx).buildConstraintViolationWithTemplate(MUTUALLY_EXCLUSIVE);

    verifyNoMoreInteractions(ctx);
  }
}
