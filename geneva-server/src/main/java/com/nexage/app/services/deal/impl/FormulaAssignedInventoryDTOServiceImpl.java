package com.nexage.app.services.deal.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.app.dto.deals.FormulaAssignedInventoryDTO;
import com.nexage.app.dto.deals.FormulaAssignedInventoryListDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.deal.DealFormulaAssignedInventoryService;
import com.nexage.app.services.support.DealServiceSupport;
import com.nexage.app.services.validation.SellerDealValidator;
import com.nexage.app.services.validation.sellingrule.ZeroCostDealValidator;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.validator.deals.DealPlacementFormulaAttributesValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Log4j2
@Transactional
public class FormulaAssignedInventoryDTOServiceImpl implements DealFormulaAssignedInventoryService {

  private final CustomObjectMapper objectMapper;
  private final DirectDealRepository dealRepository;
  private final CompanyRuleRepository companyRuleRepository;
  private final SellerDealValidator sellerDealValidator;
  private final ZeroCostDealValidator zeroCostDealValidator;
  private final DealServiceSupport dealServiceSupport;
  private final DealPlacementFormulaAttributesValidator dealPlacementFormulaAttributesValidator;

  @Autowired
  public FormulaAssignedInventoryDTOServiceImpl(
      CustomObjectMapper objectMapper,
      DirectDealRepository dealRepository,
      CompanyRuleRepository companyRuleRepository,
      ZeroCostDealValidator zeroCostDealValidator,
      SellerDealValidator sellerDealValidator,
      DealServiceSupport dealServiceSupport,
      DealPlacementFormulaAttributesValidator dealPlacementFormulaAttributesValidator) {
    this.objectMapper = objectMapper;
    this.dealRepository = dealRepository;
    this.companyRuleRepository = companyRuleRepository;
    this.zeroCostDealValidator = zeroCostDealValidator;
    this.sellerDealValidator = sellerDealValidator;
    this.dealServiceSupport = dealServiceSupport;
    this.dealPlacementFormulaAttributesValidator = dealPlacementFormulaAttributesValidator;
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or ((@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerYieldNexage()) "
          + "and @loginUserContext.isDealAdmin())")
  @Override
  public FormulaAssignedInventoryListDTO updateAssignedInventory(
      Long dealPid, FormulaAssignedInventoryDTO formulaAssignedInventoryDTO) {
    return updateInventory(dealPid, formulaAssignedInventoryDTO);
  }

  /** {@inheritDoc} */
  @PreAuthorize("@loginUserContext.isOcUserNexage() OR @loginUserContext.isOcUserSeller()")
  @Override
  public FormulaAssignedInventoryListDTO updateAssignedInventoryForSeller(
      Long sellerPid,
      Long dealPid,
      FormulaAssignedInventoryListDTO formulaAssignedInventoryListDTO) {
    var content = formulaAssignedInventoryListDTO.getContent();
    if (content.isEmpty()) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    var formulaAssignedInventoryDTO = content.iterator().next();
    sellerDealValidator.validateSellerFormulaRuleDTOs(
        formulaAssignedInventoryDTO.getPlacementFormula(), sellerPid);
    return updateInventory(dealPid, formulaAssignedInventoryDTO);
  }

  private FormulaAssignedInventoryListDTO updateInventory(
      Long dealPid, FormulaAssignedInventoryDTO formulaAssignedInventoryDTO) {
    var deal =
        dealRepository
            .findById(dealPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND));
    validateDealForFormulaAssignedInventory(deal, formulaAssignedInventoryDTO);

    dealPlacementFormulaAttributesValidator.validateDealPlacementFormulaAttributes(
        formulaAssignedInventoryDTO.getPlacementFormula());

    deal.setPlacementFormulaStatus(
        dealServiceSupport.updateDealPlacementFormulaStatus(
            dealPid,
            deal.getPlacementFormula(),
            formulaAssignedInventoryDTO.getPlacementFormula()));
    var placementFormula =
        convertFormulaToString(formulaAssignedInventoryDTO.getPlacementFormula());

    deal.setPlacementFormula(placementFormula);
    deal.setAutoUpdate(formulaAssignedInventoryDTO.getAutoUpdate());

    dealRepository.save(deal);
    var dtoList = new FormulaAssignedInventoryListDTO();
    dtoList.setContent(List.of(formulaAssignedInventoryDTO));
    return dtoList;
  }

  private void validateDealForFormulaAssignedInventory(
      DirectDeal coreDeal, FormulaAssignedInventoryDTO formulaAssignedInventoryDTO) {
    zeroCostDealValidator.validateZeroCostDeals(coreDeal, formulaAssignedInventoryDTO);
  }

  @Override
  public FormulaAssignedInventoryListDTO getAssignedInventory(Long dealPid) {
    var deal =
        dealRepository
            .findById(dealPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND));
    var formulaString = deal.getPlacementFormula();
    var formulaAssignedInventory = new FormulaAssignedInventoryDTO();
    formulaAssignedInventory.setPlacementFormula(convertStringToFormula(formulaString));
    formulaAssignedInventory.setAutoUpdate(deal.getAutoUpdate());
    formulaAssignedInventory.setDealPid(dealPid);
    var formulaList = new FormulaAssignedInventoryListDTO();
    formulaList.setContent(List.of(formulaAssignedInventory));
    return formulaList;
  }

  private String convertFormulaToString(PlacementFormulaDTO placementFormulaDTO) {
    if (placementFormulaDTO == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(placementFormulaDTO);
    } catch (JsonProcessingException e) {
      log.error("Failed to convert placement formula dto to string data : {}", placementFormulaDTO);
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR);
    }
  }

  private PlacementFormulaDTO convertStringToFormula(String formulaString) {
    if (formulaString == null) {
      return null;
    }
    try {
      return objectMapper.readValue(formulaString, PlacementFormulaDTO.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to convert placement formula string data to DTO : {}", formulaString);
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR);
    }
  }
}
