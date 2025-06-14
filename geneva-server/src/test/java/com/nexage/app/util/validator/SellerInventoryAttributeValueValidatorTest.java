package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.InventoryAttribute;
import com.nexage.admin.core.model.InventoryAttributeValue;
import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.admin.core.repository.InventoryAttributeValueRepository;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerInventoryAttributeValueValidatorTest {

  private static final long ATTRIBUTE_PID = 100L;
  private static final long ATTRIBUTE_VALUE_PID = 200L;
  private static final long SELLER_PID = 300L;

  @Mock private InventoryAttributeRepository inventoryAttributeRepository;
  @Mock private InventoryAttributeValueRepository inventoryAttributeValueRepository;

  @InjectMocks private SellerInventoryAttributeValuesValidatorImpl validator;

  private InventoryAttributeValueDTO inventoryAttributeValueDTO;

  @BeforeEach
  void setup() {
    inventoryAttributeValueDTO = new InventoryAttributeValueDTO();
    inventoryAttributeValueDTO.setPid(ATTRIBUTE_VALUE_PID);
    inventoryAttributeValueDTO.setVersion(2);
    inventoryAttributeValueDTO.setEnabled(true);
    inventoryAttributeValueDTO.setValue("value");
  }

  @Test
  void validateSucces() {
    when(inventoryAttributeRepository.findById(ATTRIBUTE_PID))
        .thenReturn(Optional.of(mock(InventoryAttribute.class)));
    when(inventoryAttributeValueRepository.findById(ATTRIBUTE_VALUE_PID))
        .thenReturn(Optional.of(mock(InventoryAttributeValue.class)));
    validator.validateForUpdate(ATTRIBUTE_PID, ATTRIBUTE_VALUE_PID, inventoryAttributeValueDTO);
  }

  @Test
  void validateShouldFailPidMismatch() {
    long invalidPid = 1L;
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                validator.validateForUpdate(ATTRIBUTE_PID, invalidPid, inventoryAttributeValueDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_UPDATE_PID_MISMATCH,
        exception.getErrorCode());
  }

  @Test
  void validateShouldFailAttributeMissing() {
    long invalidPid = 1L;
    when(inventoryAttributeRepository.findById(invalidPid)).thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                validator.validateForUpdate(
                    invalidPid, ATTRIBUTE_VALUE_PID, inventoryAttributeValueDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST, exception.getErrorCode());
  }

  @Test
  void validateShouldFailValueMissing() {
    long invalidPid = 1L;
    inventoryAttributeValueDTO.setPid(invalidPid);
    when(inventoryAttributeRepository.findById(ATTRIBUTE_PID))
        .thenReturn(Optional.of(mock(InventoryAttribute.class)));
    when(inventoryAttributeValueRepository.findById(invalidPid)).thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                validator.validateForUpdate(ATTRIBUTE_PID, invalidPid, inventoryAttributeValueDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_DOES_NOT_EXIST, exception.getErrorCode());
  }

  @Test
  void validateShouldFailWhenPidIsNull() {
    inventoryAttributeValueDTO.setPid(null);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                validator.validateForUpdate(
                    ATTRIBUTE_PID, ATTRIBUTE_VALUE_PID, inventoryAttributeValueDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_PID_IS_NULL, exception.getErrorCode());
  }

  @Test
  void validateShouldFailGetAttributeMissing() {
    long invalidPid = 1L;
    when(inventoryAttributeRepository.findById(invalidPid)).thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateForFetch(SELLER_PID, invalidPid));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST, exception.getErrorCode());
  }

  @Test
  void validateShouldFailSellerOwnerMismatch() {
    long invalidPid = 1L;
    var seller = mock(Company.class);
    when(seller.getPid()).thenReturn(SELLER_PID);

    var attribute = mock(InventoryAttribute.class);
    when(attribute.getOwnerCompany()).thenReturn(seller);
    when(inventoryAttributeRepository.findById(ATTRIBUTE_PID)).thenReturn(Optional.of(attribute));

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> validator.validateForFetch(invalidPid, ATTRIBUTE_PID));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_OWNER_PID_MISMATCH, exception.getErrorCode());
  }

  @Test
  void validationShouldPassForCreate() {
    var attribute = mock(InventoryAttribute.class);
    when(inventoryAttributeRepository.findById(ATTRIBUTE_PID)).thenReturn(Optional.of(attribute));

    var company = mock(Company.class);
    when(attribute.getOwnerCompany()).thenReturn(company);
    when(company.getPid()).thenReturn(SELLER_PID);

    inventoryAttributeValueDTO.setPid(null);
    validator.validateForCreate(SELLER_PID, ATTRIBUTE_PID, inventoryAttributeValueDTO);
  }

  @Test
  void validationShouldFailOnCreateWhenAttributeMissing() {
    inventoryAttributeValueDTO.setPid(null);
    when(inventoryAttributeRepository.findById(ATTRIBUTE_PID)).thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                validator.validateForCreate(SELLER_PID, ATTRIBUTE_PID, inventoryAttributeValueDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST, exception.getErrorCode());
  }

  @Test
  void validationShouldFailOnCreateOwnerMismatch() {
    long invalidPid = 1L;
    var seller = mock(Company.class);
    when(seller.getPid()).thenReturn(SELLER_PID);
    inventoryAttributeValueDTO.setPid(null);

    var attribute = mock(InventoryAttribute.class);
    when(attribute.getOwnerCompany()).thenReturn(seller);
    when(inventoryAttributeRepository.findById(ATTRIBUTE_PID)).thenReturn(Optional.of(attribute));

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                validator.validateForCreate(invalidPid, ATTRIBUTE_PID, inventoryAttributeValueDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_OWNER_PID_MISMATCH, exception.getErrorCode());
  }

  @Test
  void validationShouldFailOnCraeteAttributePidMissing() {
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                validator.validateForCreate(SELLER_PID, ATTRIBUTE_PID, inventoryAttributeValueDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_PID_IS_NOT_NULL,
        exception.getErrorCode());
  }
}
