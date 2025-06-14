package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.aol.crs.model.v1.ContentType;
import com.nexage.app.dto.CreativeRegistrationDTO;
import com.nexage.app.dto.Display;
import com.ssp.geneva.common.error.handler.MessageHandler;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class CreativeRegistrationDTOValidatorTest extends BaseValidatorTest {

  @Mock private CreativeRegistrationConstraint constraint;
  @Mock private MessageHandler messageHandler;
  @InjectMocks private CreativeRegistrationDTOValidator creativeRegistrationDTOValidator;
  private static final String MESSAGE = "test message";

  @Test
  void shouldReturnTrueWhenValidCreativeRegistrationDTO() {
    assertTrue(creativeRegistrationDTOValidator.isValid(createValidCreativeRegistrationDTO(), ctx));
  }

  @Test
  void shouldReturnTrueWhenSellerIdsIsPresent() {
    CreativeRegistrationDTO dto = createValidCreativeRegistrationDTO();
    dto.setDealIds(Collections.emptyList());
    assertTrue(creativeRegistrationDTOValidator.isValid(createValidCreativeRegistrationDTO(), ctx));
  }

  @Test
  void shouldReturnTrueWhenDealsIdsIsPresent() {
    CreativeRegistrationDTO dto = createValidCreativeRegistrationDTO();
    dto.setSellerIds(Collections.emptyList());
    assertTrue(creativeRegistrationDTOValidator.isValid(createValidCreativeRegistrationDTO(), ctx));
  }

  @Test
  void shouldReturnFalseWhenBothSellerAndDealIdsAreNull() {
    when(messageHandler.getMessage(anyString())).thenReturn(MESSAGE);
    CreativeRegistrationDTO dto = createValidCreativeRegistrationDTO();
    dto.setSellerIds(null);
    dto.setDealIds(null);
    assertFalse(creativeRegistrationDTOValidator.isValid(dto, ctx));
  }

  @Test
  void shouldReturnFalseWhenBothSellerAndDealIdsAreEmpty() {
    when(messageHandler.getMessage(anyString())).thenReturn(MESSAGE);
    CreativeRegistrationDTO dto = createValidCreativeRegistrationDTO();
    dto.setSellerIds(Collections.emptyList());
    dto.setDealIds(Collections.emptyList());
    assertFalse(creativeRegistrationDTOValidator.isValid(dto, ctx));
  }

  @Override
  protected void initializeConstraint() {
    lenient().when(constraint.message()).thenReturn(MESSAGE);
  }

  private CreativeRegistrationDTO createValidCreativeRegistrationDTO() {
    return CreativeRegistrationDTO.builder()
        .sellerIds(List.of(1234L, 4567L))
        .contentMarkup("content")
        .contentType(ContentType.DISPLAY_MARKUP)
        .adomain("testing.com")
        .iurl("imageUrl")
        .display(new Display(1, 2))
        .countryCode("US")
        .build();
  }
}
