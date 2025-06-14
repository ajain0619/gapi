package com.nexage.app.util.validator.deals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.repository.DealInventoryRepository;
import com.nexage.app.dto.deals.DealInventoriesDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class DealPlacementFormulaAttributesValidator {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final DealInventoryRepository dealInventoryRepository;

  public DealPlacementFormulaAttributesValidator(DealInventoryRepository dealInventoryRepository) {
    this.dealInventoryRepository = dealInventoryRepository;
  }

  public void validateDealPlacementFormulaAttributes(PlacementFormulaDTO placementFormulaDTO) {
    Set<String> listTypeAttributes = Set.of("DOMAIN", "APP_ALIAS", "APP_BUNDLE");
    Map<String, String> attributeToOperator = new HashMap<>();

    if (null == placementFormulaDTO) return;

    for (FormulaGroupDTO formulaGroupDTO : placementFormulaDTO.getFormulaGroups()) {
      for (FormulaRuleDTO formulaRuleDTO : formulaGroupDTO.getFormulaRules()) {
        if (formulaRuleDTO.getAttribute() == null
            || formulaRuleDTO.getOperator() == null
            || formulaRuleDTO.getRuleData() == null)
          throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_ATTRIBUTE_DATA);
        if (formulaRuleDTO.isDomainAppAttribute()) validateDomainAppAttribute(formulaRuleDTO);

        if (listTypeAttributes.contains(formulaRuleDTO.getAttribute().name())) {
          String existingOperator = attributeToOperator.get(formulaRuleDTO.getAttribute().name());
          if (existingOperator != null
              && !existingOperator.equals(formulaRuleDTO.getOperator().name())) {
            throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_INVENTORY_LIST_EXISTS);
          }
          attributeToOperator.put(
              formulaRuleDTO.getAttribute().name(), formulaRuleDTO.getOperator().name());
        }
      }
    }
  }

  private void validateDomainAppAttribute(FormulaRuleDTO formulaRuleDTO) {
    DealInventoriesDTO dealInventoriesDTO;
    try {
      dealInventoriesDTO =
          objectMapper.readValue(formulaRuleDTO.getRuleData(), DealInventoriesDTO.class);
    } catch (IOException e) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_RULE_DATA);
    }
    if (!dealInventoryRepository.existsByPidAndFileName(
        dealInventoriesDTO.getPid(), dealInventoriesDTO.getFileName()))
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PID_FILENAME_MISMATCH_IN_RULE_DATA);

    if (!formulaRuleDTO
        .getAttribute()
        .toString()
        .matches(dealInventoriesDTO.getFileType().toString()))
      throw new GenevaValidationException(ServerErrorCodes.SERVER_ATTRIBUTE_MISMATCH_IN_RULE_DATA);
  }
}
