package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.LoginUserContext;
import com.ssp.geneva.common.error.handler.MessageHandler;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

class SellerInventoryAttributeDTOValidatorTest extends BaseValidatorTest {

  private static final long SELLER_PID = 100L;
  private static Properties messages;
  @Mock private SellerInventoryAttributeDTOConstraint constraint;
  @Mock private CompanyRepository companyRepository;
  @Mock private InventoryAttributeRepository inventoryAttributeRepository;
  @Mock private MessageSource messageSource;
  @Mock private LoginUserContext userContext;

  @InjectMocks private MessageHandler messageHandler;

  @InjectMocks private SellerInventoryAttributeDTOValidator validator;

  @BeforeAll
  public static void initClass() {
    messages = new Properties();
    try (var in =
        SellerInventoryAttributeDTOValidatorTest.class.getResourceAsStream(
            "/messages_en.properties")) {
      messages.load(in);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to load messages");
    }
  }

  @Test
  void shouldValidationFailWhenSellerPidIsNull() {
    var attribute = new InventoryAttributeDTO();
    mockErrorMessage(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_PUBLISHER_PID_IS_NULL);
    assertFalse(validator.isValid(attribute, ctx));
    verifyValidationMessage(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_PUBLISHER_PID_IS_NULL);
  }

  @Test
  void shouldValidationFailWhenSellerNotFound() {
    var attribute = new InventoryAttributeDTO();
    attribute.setSellerPid(SELLER_PID);
    when(companyRepository.findById(SELLER_PID)).thenReturn(Optional.empty());
    mockErrorMessage(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    assertFalse(validator.isValid(attribute, ctx));
    verifyValidationMessage(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
  }

  @Test
  void shouldValidationFailsWhenAttributeExists() {
    var attribute =
        InventoryAttributeDTO.builder()
            .name("attribute")
            .prefix("prefix")
            .sellerPid(SELLER_PID)
            .build();
    when(inventoryAttributeRepository.existsByNameAndPrefix(
            attribute.getName(), attribute.getPrefix()))
        .thenReturn(true);
    when(companyRepository.findById(SELLER_PID)).thenReturn(Optional.of(mock(Company.class)));
    mockErrorMessage(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_ATTRIBUTE_ALREADY_EXIST_FOR_THIS_PUBLISHER);
    assertFalse(validator.isValid(attribute, ctx));
    verifyValidationMessage(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_ATTRIBUTE_ALREADY_EXIST_FOR_THIS_PUBLISHER);
  }

  @Test
  void shouldValidationFailsWhenWrongSeller() {
    var attribute = InventoryAttributeDTO.builder().createdBy(200L).sellerPid(SELLER_PID).build();
    when(companyRepository.findById(SELLER_PID)).thenReturn(Optional.of(mock(Company.class)));
    mockErrorMessage(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_PUBLISHER_PIDS_ARENT_SAME);
    assertFalse(validator.isValid(attribute, ctx));
    verifyValidationMessage(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_PUBLISHER_PIDS_ARENT_SAME);
  }

  @Test
  void shouldValidationFailsOnGlobalVisibility() {
    var attribute =
        InventoryAttributeDTO.builder()
            .createdBy(SELLER_PID)
            .hasGlobalVisibility(true)
            .sellerPid(SELLER_PID)
            .build();
    when(userContext.isNexageAdminOrManager()).thenReturn(false);
    when(companyRepository.findById(SELLER_PID)).thenReturn(Optional.of(mock(Company.class)));
    mockErrorMessage(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_ILLEGAL_VALUE_OF_GLOBAL_VISIBILITY);
    assertFalse(validator.isValid(attribute, ctx));
    verifyValidationMessage(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_ILLEGAL_VALUE_OF_GLOBAL_VISIBILITY);
  }

  @Test
  void shouldValidationFailsOnInternalOnly() {
    var attribute =
        InventoryAttributeDTO.builder()
            .createdBy(SELLER_PID)
            .isInternalOnly(true)
            .sellerPid(SELLER_PID)
            .build();
    when(userContext.isNexageAdminOrManager()).thenReturn(false);
    when(companyRepository.findById(SELLER_PID)).thenReturn(Optional.of(mock(Company.class)));
    mockErrorMessage(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_ILLEGAL_VALUE_OF_INTERNAL_ONLY);
    assertFalse(validator.isValid(attribute, ctx));
    verifyValidationMessage(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_ILLEGAL_VALUE_OF_INTERNAL_ONLY);
  }

  @Override
  protected void initializeConstraint() {
    ReflectionTestUtils.setField(validator, "messageHandler", messageHandler);
  }

  @Override
  protected void initializeContext() {
    super.initializeContext();
  }

  private void verifyValidationMessage(ServerErrorCodes msg) {
    verify(ctx).buildConstraintViolationWithTemplate(messages.getProperty(msg.name()));
  }

  private void mockErrorMessage(ServerErrorCodes msg) {
    when(messageSource.getMessage(eq(msg.name()), eq(null), any(Locale.class)))
        .thenReturn(messages.getProperty(msg.name()));
  }
}
