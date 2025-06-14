package com.nexage.app.services.sellingrule.impl;

import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateUpdateAnotherRuleIntendedActions;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateUpdateAnotherRuleTargets;
import static java.util.Objects.isNull;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.specification.SellerRuleQueryFieldSpecification;
import com.nexage.admin.core.specification.SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.rule.RuleMapper;
import com.nexage.app.services.sellingrule.formula.RuleFormulaService;
import com.nexage.app.services.validation.sellingrule.SellerRuleValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Log4j2
abstract class BaseSellerRuleService {

  @Autowired protected CompanyRuleRepository companyRuleRepository;
  @Autowired protected RuleFormulaService formulaService;
  @Autowired protected SellerRuleValidator sellerRuleValidator;

  protected RuleMapper mapper = RuleMapper.MAPPER;

  protected CompanyRule findRule(Long rulePid, Long sellerPid) {
    return companyRuleRepository.findByPidAndOwnerCompanyPid(rulePid, sellerPid);
  }

  Page<CompanyRule> findRules(
      Long sellerPid, SellerRuleQueryFieldParameter queryFieldParameter, Pageable pageable) {
    boolean isAndOperator = SearchQueryOperator.AND.equals(queryFieldParameter.getOperator());
    SellerRuleQueryFieldSpecificationBuilder builder = SellerRuleQueryFieldSpecification.builder();
    builder
        .sellerPid(sellerPid)
        .isAndOperator(isAndOperator)
        .ruleName(queryFieldParameter.getRuleName())
        .types(queryFieldParameter.getTypes())
        .rulePids(queryFieldParameter.getRulePids())
        .sitePids(queryFieldParameter.getSitePids())
        .placementPids(queryFieldParameter.getPlacementPids())
        .deployedForSeller(queryFieldParameter.getOnlyRulesDeployedForSeller())
        .typeLimitations(getTypeLimitations());
    return companyRuleRepository.findAll(builder.build(), pageable);
  }

  void deleteRule(Long rulePid, Long sellerPid) {
    CompanyRule rule = findRule(rulePid, sellerPid);
    if (Objects.isNull(rule)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_NOT_FOUND);
    }
    companyRuleRepository.delete(rulePid);
  }

  protected SellerRuleDTO map(CompanyRule rule) {
    return mapper.map(rule);
  }

  protected CompanyRule create(SellerRuleDTO inputRule, Long sellerPid) {
    sellerRuleValidator.validateCommonPartForCreateAndUpdate(sellerPid, inputRule);
    processRuleFormula(inputRule, sellerPid);
    CompanyRule companyRule = mapper.map(inputRule);
    sellerRuleValidator.validateDeployedTargetsAndUpdateRule(companyRule);
    sellerRuleValidator.validateDuplicateName(companyRule);
    companyRuleRepository.save(companyRule);
    return companyRule;
  }

  protected CompanyRule updateRule(Long sellerPid, SellerRuleDTO source) {
    return updateRule(sellerPid, source, targetRule -> {});
  }

  protected CompanyRule updateRule(
      Long sellerPid, SellerRuleDTO source, Consumer<CompanyRule> validateTargetRule) {
    sellerRuleValidator.validateCommonPartForCreateAndUpdate(sellerPid, source);
    processRuleFormula(source, sellerPid);
    CompanyRule target = findRule(source.getPid(), sellerPid);
    if (isNull(target)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_NOT_FOUND);
    }
    validateTargetRule.accept(target);
    validateUpdateAnotherRuleIntendedActions(target, source);
    validateUpdateAnotherRuleTargets(target, source);
    CompanyRule mappedSource = RuleMapper.MAPPER.map(source);
    sellerRuleValidator.validateDeployedTargetsAndUpdateRule(mappedSource);
    sellerRuleValidator.validateDuplicateName(mappedSource);
    return companyRuleRepository.saveAndFlush(mappedSource);
  }

  protected void processRuleFormula(SellerRuleDTO inputRule, Long sellerPid) {
    InventoryAssignmentsDTO inventoryAssignments =
        formulaService.processFormula(inputRule.getRuleFormula(), sellerPid);
    if (inventoryAssignments != null) {
      inputRule.setAssignments(inventoryAssignments);
    }
  }

  protected abstract Set<RuleType> getTypeLimitations();
}
