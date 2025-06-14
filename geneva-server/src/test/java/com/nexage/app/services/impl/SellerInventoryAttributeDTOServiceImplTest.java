package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.InventoryAttribute;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.InventoryAttributeDTOMapper;
import com.nexage.app.services.BeanValidationService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerInventoryAttributeDTOServiceImplTest {

  @Mock private InventoryAttributeRepository inventoryAttributeRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private BeanValidationService beanValidationService;

  @InjectMocks SellerInventoryAttributeDTOServiceImpl inventoryAttributeDTOService;

  @Test
  void createAttributeSuccess() {
    long sellerPid = 1L;
    var attributeDto =
        InventoryAttributeDTO.builder()
            .assignedLevel(Set.of(1, 2, 3))
            .name("test name")
            .prefix("test_")
            .isRequired(true)
            .hasGlobalVisibility(true)
            .sellerPid(sellerPid)
            .build();
    var company = new Company();
    company.setPid(sellerPid);
    when(companyRepository.findById(sellerPid)).thenReturn(Optional.of(company));
    var testEntity = testEntity(attributeDto);
    testEntity.setPid(5L);
    when(inventoryAttributeRepository.save(any(InventoryAttribute.class))).thenReturn(testEntity);
    var out = inventoryAttributeDTOService.createInventoryAttribute(attributeDto);
    assertNotNull(out);
    assertEquals(testEntity.getPid(), out.getPid());
    assertEquals(attributeDto.getName(), out.getName());
    assertEquals(attributeDto.getPrefix(), out.getPrefix());
    assertTrue(out.isRequired());
    assertTrue(out.isHasGlobalVisibility());
  }

  @Test
  void getSingleAttributeSuccess() {
    Long sellerPid = 1L;
    Long attributePid = 10L;
    var attribute = new InventoryAttribute();
    attribute.setPid(attributePid);
    attribute.setCompanyPid(sellerPid);
    when(inventoryAttributeRepository.findByCompanyPidAndPid(sellerPid, attributePid))
        .thenReturn(Optional.of(attribute));

    var out = inventoryAttributeDTOService.getInventoryAttribute(sellerPid, attributePid);
    assertEquals(attributePid, out.getPid());
    assertEquals(sellerPid, out.getSellerPid());
  }

  @Test
  void attributeNotFound() {
    Long sellerPid = 1L;
    Long attributePid = 10L;
    when(inventoryAttributeRepository.findByCompanyPidAndPid(sellerPid, attributePid))
        .thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> inventoryAttributeDTOService.getInventoryAttribute(sellerPid, attributePid));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST, exception.getErrorCode());
  }

  @Test
  void updateAttributeSuccess() {
    long sellerPid = 1L;
    long attributePid = 2L;
    var attributeDto =
        InventoryAttributeDTO.builder()
            .pid(attributePid)
            .assignedLevel(Set.of(1, 2, 3))
            .name("test name")
            .prefix("test_")
            .isRequired(true)
            .hasGlobalVisibility(true)
            .sellerPid(sellerPid)
            .version(1)
            .build();
    var testEntity = testEntity(attributeDto);
    testEntity.setName("old name");
    when(inventoryAttributeRepository.findById(attributePid)).thenReturn(Optional.of(testEntity));
    when(inventoryAttributeRepository.save(any(InventoryAttribute.class))).thenReturn(testEntity);
    var out = inventoryAttributeDTOService.updateInventoryAttribute(sellerPid, attributeDto);
    assertNotNull(out);
    assertNotNull(testEntity.getName(), out.getName());
  }

  @Test
  void updateAttributeNotFound() {
    long sellerPid = 1L;
    long attributePid = 2L;
    var attributeDto =
        InventoryAttributeDTO.builder()
            .pid(attributePid)
            .assignedLevel(Set.of(1, 2, 3))
            .name("test name")
            .prefix("test_")
            .isRequired(true)
            .hasGlobalVisibility(true)
            .sellerPid(sellerPid)
            .version(1)
            .build();

    when(inventoryAttributeRepository.findById(attributePid)).thenReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> inventoryAttributeDTOService.updateInventoryAttribute(sellerPid, attributeDto));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST, exception.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundWhenCompanyDoesNotExist() {
    // when
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());
    var attributeDto = InventoryAttributeDTO.builder().sellerPid(1L).build();

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> inventoryAttributeDTOService.createInventoryAttribute(attributeDto));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  private InventoryAttribute testEntity(InventoryAttributeDTO attributeDto) {
    return InventoryAttributeDTOMapper.MAPPER.mapToEntity(attributeDto);
  }
}
