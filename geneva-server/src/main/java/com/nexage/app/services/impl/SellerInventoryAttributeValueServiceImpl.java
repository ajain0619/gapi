package com.nexage.app.services.impl;

import com.nexage.admin.core.model.InventoryAttributeValue;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.admin.core.repository.InventoryAttributeValueRepository;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.InventoryAttributeValueDTOMapper;
import com.nexage.app.services.SellerInventoryAttributeValueService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class SellerInventoryAttributeValueServiceImpl
    implements SellerInventoryAttributeValueService {

  private static final InventoryAttributeValueDTOMapper MAPPER =
      InventoryAttributeValueDTOMapper.MAPPER;
  private final InventoryAttributeValueRepository inventoryAttributeValueRepository;
  private final InventoryAttributeRepository inventoryAttributeRepository;
  private final CompanyRepository companyRepository;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller())"
          + " and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  public InventoryAttributeValueDTO updateInventoryAttributeValue(
      Long sellerPid,
      Long inventoryAttributePid,
      Long inventoryAttributevaluePid,
      InventoryAttributeValueDTO inventoryAttributeValueDto) {

    var out = doUpdate(inventoryAttributeValueDto, inventoryAttributePid);
    return MAPPER.map(out);
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()"
          + "or @loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()"
          + "or @loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller())"
          + " and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Override
  public Page<InventoryAttributeValueDTO> getAllValuesForInventoryAttribute(
      Long sellerPid, Long inventoryAttributePid, Pageable pageable) {
    var company =
        companyRepository
            .findById(sellerPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));
    return inventoryAttributeValueRepository
        .findAllValuesForAttribute(company, inventoryAttributePid, pageable)
        .map(MAPPER::map);
  }

  private InventoryAttributeValue doUpdate(
      InventoryAttributeValueDTO inventoryAttributeValueDTO, Long inventoryAttributePid) {
    var currentValue =
        inventoryAttributeValueRepository
            .findById(inventoryAttributeValueDTO.getPid())
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_DOES_NOT_EXIST));
    currentValue.setEnabled(inventoryAttributeValueDTO.isEnabled());
    currentValue.setName(inventoryAttributeValueDTO.getValue());

    var inventoryAttribute =
        inventoryAttributeRepository
            .findById(inventoryAttributePid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST));
    currentValue.setAttribute(inventoryAttribute);
    return inventoryAttributeValueRepository.save(currentValue);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller())"
          + " and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  public InventoryAttributeValueDTO createInventoryAttributeValue(
      Long sellerPid,
      Long inventoryAttributePid,
      InventoryAttributeValueDTO inventoryAttributeValueDto) {
    var inventoryAttribute =
        inventoryAttributeRepository
            .findById(inventoryAttributePid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST));
    var attributeValue = MAPPER.map(inventoryAttributeValueDto);
    attributeValue.setAttribute(inventoryAttribute);

    return MAPPER.map(inventoryAttributeValueRepository.save(attributeValue));
  }
}
