package com.nexage.app.services.impl;

import static com.nexage.app.dto.sellingrule.RuleType.BRAND_PROTECTION;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateRulePidAndVersionAreNull;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateTargetsAndIntendedActions;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.SellerSeatRule;
import com.nexage.admin.core.repository.SellerSeatRepository;
import com.nexage.admin.core.repository.SellerSeatRuleRepository;
import com.nexage.admin.core.specification.SellerSeatRuleSpecification;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.rule.RuleTargetDataConverter;
import com.nexage.app.mapper.rule.SellerSeatRuleDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerSeatRuleService;
import com.nexage.app.util.validator.RuleSearchRequestParamValidator;
import com.nexage.app.util.validator.RuleTypeStringValidator;
import com.nexage.app.util.validator.StatusStringValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.StaleStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Log4j2
public class SellerSeatRuleServiceImpl implements SellerSeatRuleService {

  private final SellerSeatRuleRepository sellerSeatRuleRepository;
  private final SellerSeatRepository sellerSeatRepository;
  private final RuleTargetDataConverter ruleTargetDataConverter;

  private static final Set<RuleType> allowedRuleTypes = Sets.immutableEnumSet(BRAND_PROTECTION);
  private static final SellerSeatRuleDTOMapper MAPPER = SellerSeatRuleDTOMapper.MAPPER;
  private final UserContext userContext;

  @Override
  @PreAuthorize("@loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(#sellerSeatPid)")
  @Transactional(readOnly = true)
  public Page<SellerSeatRuleDTO> findRulesInSellerSeat(
      Long sellerSeatPid,
      String ruleTypes,
      String statuses,
      Set<String> queryFields,
      String queryTerm,
      Pageable pageable) {
    if (!RuleSearchRequestParamValidator.isValid(queryFields, queryTerm)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }

    Specification<SellerSeatRule> spec;
    try {
      spec =
          getFindSpec(sellerSeatPid)
              .and(SellerSeatRuleSpecification.withQuery(queryFields, queryTerm));

      if (ruleTypes != null) {
        spec =
            spec.and(SellerSeatRuleSpecification.withRuleType(getAndValidateRuleTypes(ruleTypes)));
      }
      if (statuses != null) {
        spec = spec.and(SellerSeatRuleSpecification.withStatus(getAndValidateStatuses(statuses)));
      }
    } catch (IllegalArgumentException e) {
      log.warn(e.getMessage());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_ERROR_FETCHING_RULES);
    }

    return sellerSeatRuleRepository
        .findAll(spec, pageable)
        .map(ssr -> MAPPER.map(ssr, ruleTargetDataConverter));
  }

  @Override
  @PreAuthorize("@loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(#sellerSeatPid)")
  @Transactional(readOnly = true)
  public SellerSeatRuleDTO findSellerSeatRule(Long sellerSeatPid, Long sellerSeatRulePid) {
    Optional<SellerSeatRule> rule = findOneByPid(sellerSeatPid, sellerSeatRulePid);
    if (rule.isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_NOT_FOUND);
    }
    return MAPPER.map(rule.get(), ruleTargetDataConverter);
  }

  @Override
  @PreAuthorize("@loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(#sellerSeatPid)")
  public SellerSeatRuleDTO save(Long sellerSeatPid, SellerSeatRuleDTO sellerSeatRule) {
    validateWriteAccess();
    validateCreate(sellerSeatPid, sellerSeatRule);
    Optional<SellerSeat> sellerSeat = sellerSeatRepository.findById(sellerSeatPid);
    if (sellerSeat.isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND);
    }
    SellerSeatRule rule = MAPPER.map(sellerSeatRule);
    setForeignRuleKeys(rule);
    sellerSeatRuleRepository.save(rule);
    return MAPPER.map(rule, ruleTargetDataConverter);
  }

  protected void validateWriteAccess() {
    if (userContext.isOcAdminNexage()
        || userContext.isOcManagerNexage()
        || userContext.isOcManagerYieldNexage()
        || userContext.isOcManagerSmartexNexage()) {
      return;
    }
    if (!(userContext.isOcAdminSeller() || userContext.isOcManagerSeller())) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(#sellerSeatPid)")
  @Transactional
  public SellerSeatRuleDTO update(
      Long sellerSeatPid, Long sellerSeatRulePid, SellerSeatRuleDTO sellerSeatRule) {
    validateWriteAccess();
    validateUpdate(sellerSeatRule, sellerSeatPid, sellerSeatRulePid);

    if (!sellerSeatRepository.existsById(sellerSeatPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND);
    }

    Optional<SellerSeatRule> optionalSellerSeatRule =
        findOneByPid(sellerSeatPid, sellerSeatRulePid);
    if (optionalSellerSeatRule.isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_NOT_FOUND);
    }
    SellerSeatRule rule = optionalSellerSeatRule.get();
    if (!rule.getVersion().equals(sellerSeatRule.getVersion())) {
      throw new StaleStateException(
          "Invalid version; expected:"
              + rule.getVersion()
              + "; actual:"
              + sellerSeatRule.getVersion());
    }
    if (!rule.getSellerSeatPid().equals(sellerSeatPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_FORBIDDEN_TO_EDIT);
    }
    SellerSeatRule mappedSellerSeatRule = MAPPER.update(sellerSeatRule);
    setForeignRuleKeys(mappedSellerSeatRule);
    sellerSeatRuleRepository.saveAndFlush(mappedSellerSeatRule);
    return MAPPER.map(mappedSellerSeatRule, ruleTargetDataConverter);
  }

  @Override
  @PreAuthorize("@loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(#sellerSeatPid)")
  @Transactional
  public SellerSeatRuleDTO delete(Long sellerSeatPid, Long sellerSeatRulePid) {
    Optional<SellerSeatRule> optionalSellerSeatRule =
        findOneByPid(sellerSeatPid, sellerSeatRulePid);
    if (optionalSellerSeatRule.isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_NOT_FOUND);
    }
    SellerSeatRule rule = optionalSellerSeatRule.get();
    validateWriteAccess();
    sellerSeatRuleRepository.delete(rule);
    return SellerSeatRuleDTO.builder().pid(sellerSeatRulePid).build();
  }

  private void validateCreate(Long sellerSeatPid, SellerSeatRuleDTO dto) {
    if (!sellerSeatPid.equals(dto.getSellerSeatPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_MISMATCH);
    }
    if (!allowedRuleTypes.contains(dto.getType())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_SELLER_SEAT_RULE_TYPE_NOT_ALLOWED);
    }

    validateRulePidAndVersionAreNull(dto);
    validateTargetsAndIntendedActions(dto);
    validateDuplicateName(dto);
  }

  private void validateDuplicateName(SellerSeatRuleDTO dto) {
    Specification<SellerSeatRule> spec =
        SellerSeatRuleSpecification.withSellerSeat(dto.getSellerSeatPid())
            .and(SellerSeatRuleSpecification.withName(dto.getName()));
    Optional<SellerSeatRule> optionalSellerSeatRule = sellerSeatRuleRepository.findOne(spec);
    if (optionalSellerSeatRule.isEmpty()) {
      return;
    }
    SellerSeatRule rule = optionalSellerSeatRule.get();
    if (dto.getPid() == null || !rule.getPid().equals(dto.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_DUPLICATE_NAME);
    }
  }

  private void validateUpdate(SellerSeatRuleDTO dto, Long sellerSeatPid, Long rulePid) {
    if (dto.getPid() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_PID_IS_NULL);
    }
    if (!dto.getPid().equals(rulePid) || !dto.getSellerSeatPid().equals(sellerSeatPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_MISMATCH);
    }
    validateTargetsAndIntendedActions(dto);
    validateDuplicateName(dto);
  }

  private void setForeignRuleKeys(SellerSeatRule rule) {
    rule.getRuleTargets().forEach(t -> t.setRule(rule));
    rule.getRuleIntendedActions().forEach(i -> i.setRule(rule));
  }

  private Specification<SellerSeatRule> getFindSpec(Long sellerSeatPid) {
    return SellerSeatRuleSpecification.withSellerSeat(sellerSeatPid);
  }

  private Optional<SellerSeatRule> findOneByPid(Long sellerSeatPid, Long sellerSeatRulePid) {
    Specification<SellerSeatRule> spec =
        getFindSpec(sellerSeatPid).and(SellerSeatRuleSpecification.withPid(sellerSeatRulePid));
    return sellerSeatRuleRepository.findOne(spec);
  }

  private Set<com.nexage.admin.core.enums.RuleType> getAndValidateRuleTypes(String ruleTypes) {
    Set<com.nexage.admin.core.enums.RuleType> ruleTypeSet =
        EnumSet.noneOf(com.nexage.admin.core.enums.RuleType.class);

    if (!RuleTypeStringValidator.isValid(ruleTypes)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    } else {
      for (String ruleType : ruleTypes.split(",", -1)) {
        ruleTypeSet.add(com.nexage.admin.core.enums.RuleType.valueOf(ruleType.trim()));
      }
    }

    return ruleTypeSet;
  }

  private Set<Status> getAndValidateStatuses(String statuses) {
    Set<Status> statusSet = EnumSet.noneOf(Status.class);

    if (!StatusStringValidator.isValid(statuses) || statuses.contains("DELETED")) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    } else {
      for (String status : statuses.split(",", -1)) {
        statusSet.add(Status.valueOf(status.trim()));
      }
    }

    return statusSet;
  }
}
