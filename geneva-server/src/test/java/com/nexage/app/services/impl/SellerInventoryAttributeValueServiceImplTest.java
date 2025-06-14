package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.InventoryAttribute;
import com.nexage.admin.core.model.InventoryAttributeValue;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.admin.core.repository.InventoryAttributeValueRepository;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.InventoryAttributeValueDTOMapper;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class SellerInventoryAttributeValueServiceImplTest {

  private static final Long SELLER_ID = 123L;
  private static final Long ATTRIBUTE_ID = 20L;
  private static final Long ATTRIBUTE_VALUE_ID = 100L;

  @Mock private InventoryAttributeValueRepository inventoryAttributeValueRepository;
  @Mock private InventoryAttributeRepository inventoryAttributeRepository;
  @Mock private CompanyRepository companyRepository;

  @InjectMocks private SellerInventoryAttributeValueServiceImpl sellerInventoryAttributeService;

  private InventoryAttributeValueDTO attributeValueDto;

  @BeforeEach
  void setup() {
    attributeValueDto = new InventoryAttributeValueDTO();
    attributeValueDto.setPid(ATTRIBUTE_VALUE_ID);
    attributeValueDto.setValue("new value");
    attributeValueDto.setEnabled(false);
  }

  @Test
  void updateInventoryAttributeValueSuccess() {
    var value = createTestValue();
    when(inventoryAttributeValueRepository.findById(attributeValueDto.getPid()))
        .thenReturn(Optional.of(value));
    when(inventoryAttributeValueRepository.save(any(InventoryAttributeValue.class)))
        .thenReturn(InventoryAttributeValueDTOMapper.MAPPER.map(attributeValueDto));

    var attrib = new InventoryAttribute();
    attrib.setPid(ATTRIBUTE_ID);
    when(inventoryAttributeRepository.findById(ATTRIBUTE_ID)).thenReturn(Optional.of(attrib));

    var out =
        sellerInventoryAttributeService.updateInventoryAttributeValue(
            SELLER_ID, ATTRIBUTE_ID, ATTRIBUTE_VALUE_ID, attributeValueDto);
    assertEquals(attributeValueDto.getPid(), out.getPid());
    assertEquals(attributeValueDto.getValue(), out.getValue());
    assertEquals(attributeValueDto.isEnabled(), out.isEnabled());
  }

  @Test
  void getAttributeValuesSuccess() {
    var testValue = createTestValue();
    var owner = mock(Company.class);
    when(companyRepository.findById(SELLER_ID)).thenReturn(Optional.of(owner));

    var pageable = mock(Pageable.class);
    when(inventoryAttributeValueRepository.findAllValuesForAttribute(owner, ATTRIBUTE_ID, pageable))
        .thenReturn(new PageImpl<>(List.of(testValue)));
    var result =
        sellerInventoryAttributeService.getAllValuesForInventoryAttribute(
            SELLER_ID, ATTRIBUTE_ID, pageable);
    assertNotNull(result);
    assertEquals(1, result.getSize());
  }

  @Test
  void createAttributeValueSuccess() {
    var attrib = new InventoryAttribute();
    attrib.setPid(ATTRIBUTE_ID);
    when(inventoryAttributeRepository.findById(ATTRIBUTE_ID)).thenReturn(Optional.of(attrib));

    attributeValueDto.setPid(null);
    var testValue = createTestValue();
    testValue.setName(attributeValueDto.getValue());
    when(inventoryAttributeValueRepository.save(any(InventoryAttributeValue.class)))
        .thenReturn(testValue);
    var out =
        sellerInventoryAttributeService.createInventoryAttributeValue(
            SELLER_ID, ATTRIBUTE_ID, attributeValueDto);
    assertNotNull(out);
    assertEquals(attributeValueDto.getValue(), out.getValue());
    assertNotNull(out.getPid());
  }

  @Test
  void shouldThrowExceptionWhenGetAllValuesForInventoryAttributeWithInvalidCompanyId() {
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());
    var pageable = mock(Pageable.class);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                sellerInventoryAttributeService.getAllValuesForInventoryAttribute(
                    SELLER_ID, ATTRIBUTE_ID, pageable));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdateInventoryAttributeValueWithInvalidInventoryAttributeValueId() {
    when(inventoryAttributeValueRepository.findById(attributeValueDto.getPid()))
        .thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                sellerInventoryAttributeService.updateInventoryAttributeValue(
                    SELLER_ID, ATTRIBUTE_ID, ATTRIBUTE_VALUE_ID, attributeValueDto));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_DOES_NOT_EXIST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdateInventoryAttributeValueWithInvalidInventoryAttributeId() {
    var value = createTestValue();
    when(inventoryAttributeValueRepository.findById(attributeValueDto.getPid()))
        .thenReturn(Optional.of(value));

    var attrib = new InventoryAttribute();
    attrib.setPid(ATTRIBUTE_ID);
    when(inventoryAttributeRepository.findById(ATTRIBUTE_ID)).thenReturn(Optional.empty());

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                sellerInventoryAttributeService.updateInventoryAttributeValue(
                    SELLER_ID, ATTRIBUTE_ID, ATTRIBUTE_VALUE_ID, attributeValueDto));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenCreateInventoryAttributeValueWithInvalidAttributeId() {
    when(inventoryAttributeRepository.findById(anyLong())).thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                sellerInventoryAttributeService.createInventoryAttributeValue(
                    SELLER_ID, ATTRIBUTE_ID, attributeValueDto));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST, exception.getErrorCode());
  }

  private InventoryAttributeValue createTestValue() {
    var inventoryAttributeValue = new InventoryAttributeValue();
    inventoryAttributeValue.setPid(100L);
    inventoryAttributeValue.setName("some name");
    inventoryAttributeValue.setEnabled(true);
    return inventoryAttributeValue;
  }
}
