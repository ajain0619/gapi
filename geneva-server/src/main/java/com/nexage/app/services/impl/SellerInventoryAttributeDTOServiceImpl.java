package com.nexage.app.services.impl;

import static com.nexage.app.mapper.InventoryAttributeDTOMapper.MAPPER;

import com.nexage.admin.core.model.InventoryAttribute;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.SellerInventoryAttributeDTOService;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("inventoryAttributeDTOService")
public class SellerInventoryAttributeDTOServiceImpl implements SellerInventoryAttributeDTOService {

  private final InventoryAttributeRepository inventoryAttributeRepository;
  private final CompanyRepository companyRepository;
  private final BeanValidationService beanValidationService;

  public SellerInventoryAttributeDTOServiceImpl(
      InventoryAttributeRepository inventoryAttributeRepository,
      CompanyRepository companyRepository,
      BeanValidationService beanValidationService) {
    this.inventoryAttributeRepository = inventoryAttributeRepository;
    this.companyRepository = companyRepository;
    this.beanValidationService = beanValidationService;
  }

  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + " or @loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerseller()")
  @Override
  public InventoryAttributeDTO createInventoryAttribute(
      InventoryAttributeDTO inventoryAttributeDTO) {
    beanValidationService.validate(inventoryAttributeDTO, CreateGroup.class);
    var company =
        companyRepository
            .findById(inventoryAttributeDTO.getSellerPid())
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));
    var entity = MAPPER.mapToEntity(inventoryAttributeDTO);
    entity.setOwnerCompany(company);
    entity.setCompanyPid(company.getPid());

    return MAPPER.map(inventoryAttributeRepository.save(entity));
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()"
          + "or @loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()"
          + "or @loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller())"
          + " and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Override
  public InventoryAttributeDTO getInventoryAttribute(Long sellerPid, Long attributePid) {
    var attributeOptional =
        inventoryAttributeRepository.findByCompanyPidAndPid(sellerPid, attributePid);
    var attribute =
        attributeOptional.orElseThrow(
            () ->
                new GenevaValidationException(
                    ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST));
    return MAPPER.map(attribute);
  }

  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller())"
          + " and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Override
  public InventoryAttributeDTO updateInventoryAttribute(
      Long sellerPid, InventoryAttributeDTO inventoryAttributeDTO) {
    beanValidationService.validate(inventoryAttributeDTO, UpdateGroup.class);
    var currentOpt = inventoryAttributeRepository.findById(inventoryAttributeDTO.getPid());
    var currentValue =
        currentOpt.orElseThrow(
            () ->
                new GenevaValidationException(
                    ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST));
    currentValue.setStatus(inventoryAttributeDTO.getStatus());
    currentValue.setRequired(inventoryAttributeDTO.isRequired());
    currentValue.setPrefix(inventoryAttributeDTO.getPrefix());
    currentValue.setName(inventoryAttributeDTO.getName());
    currentValue.setInternal(inventoryAttributeDTO.isInternalOnly());
    currentValue.setHasGlobalVisibility(inventoryAttributeDTO.isHasGlobalVisibility());
    currentValue.setDescription(inventoryAttributeDTO.getDescription());

    return MAPPER.map(inventoryAttributeRepository.save(currentValue));
  }

  private void validateSearchParamRequest(Set<String> qf) {
    if (!SearchRequestParamValidator.isValid(qf, (Class<?>) InventoryAttribute.class)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }
}
