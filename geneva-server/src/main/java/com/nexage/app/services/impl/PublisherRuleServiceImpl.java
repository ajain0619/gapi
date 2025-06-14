package com.nexage.app.services.impl;

import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateRulePidAndVersionAreNull;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateTargetsAndIntendedActions;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.RuleRepository;
import com.nexage.admin.core.specification.CompanyRuleSpecification;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.PublisherRuleService;
import com.nexage.app.util.assemblers.sellingrule.RuleAssembler;
import com.nexage.app.util.validator.RuleSearchRequestParamValidator;
import com.nexage.app.util.validator.RuleTypeStringValidator;
import com.nexage.app.util.validator.StatusStringValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.EnumSet;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
public class PublisherRuleServiceImpl implements PublisherRuleService {
  private final RuleRepository ruleRepository;
  private final RuleAssembler ruleAssembler;
  private final CompanyRuleRepository companyRuleRepository;

  @Autowired
  public PublisherRuleServiceImpl(
      RuleRepository ruleRepository,
      RuleAssembler ruleAssembler,
      CompanyRuleRepository companyRuleRepository) {
    this.ruleRepository = ruleRepository;
    this.ruleAssembler = ruleAssembler;
    this.companyRuleRepository = companyRuleRepository;
  }

  @Override
  @PreAuthorize(
      "!T(com.nexage.admin.core.model.User.Role).ROLE_USER.equals(@loginUserContext.getCurrentUser().getRole()) "
          + "and (@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller())")
  public SellerRuleDTO create(SellerRuleDTO dto) {
    validateDealRule(dto);
    validateRulePidAndVersionAreNull(dto);
    validateCommon(dto);

    CompanyRule entity = ruleAssembler.apply(new CompanyRule(), dto);
    validateRuleIsNotDeployed(dto, entity);
    CompanyRule rule = ruleRepository.save(entity);
    return ruleAssembler.make(rule);
  }

  @Override
  @PreAuthorize(
      "!T(com.nexage.admin.core.model.User.Role).ROLE_USER.equals(@loginUserContext.getCurrentUser().getRole()) "
          + "and (@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller())")
  public SellerRuleDTO update(Long rulePid, SellerRuleDTO dto) {
    validateDealRule(dto);
    validateRulePids(dto, rulePid);

    CompanyRule entity =
        ruleAssembler.apply(
            checkVersion(
                checkExists(ruleRepository.findActualByPid(dto.getPid()).orElse(null)), dto),
            dto);

    validateRuleIsNotDeployed(dto, entity);

    return ruleAssembler.make(ruleRepository.saveAndFlush(entity));
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public Page<SellerRuleDTO> findRulesByPidAndTypeAndStatusWithPagination(
      Long publisherPid,
      String types,
      String statuses,
      Pageable pageable,
      Set<String> qf,
      String qt) {
    validateRuleTypeString(types);
    validateStatusString(statuses);
    validateRuleSearchParamRequest(qf, qt);

    Specification<CompanyRule> spec;
    try {
      Set<RuleType> typeSet = EnumSet.noneOf(RuleType.class);
      if (types != null) {
        for (String type : types.split(",", -1)) {
          typeSet.add(RuleType.valueOf(type.trim()));
        }
      }

      Set<Status> statusSet = EnumSet.noneOf(Status.class);
      if (statuses != null) {
        for (String status : statuses.split(",", -1)) {
          statusSet.add(Status.valueOf(status.trim()));
        }
      }

      spec = CompanyRuleSpecification.withQuery(publisherPid, typeSet, statusSet, qf, qt);
    } catch (IllegalArgumentException e) {
      log.warn(e.getMessage());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_ERROR_FETCHING_RULES);
    }

    return companyRuleRepository.findAll(spec, pageable).map(ruleAssembler::make);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()) "
          + "or (@ruleRepository.findActualByPid(#rulePid).isEmpty()) or (@ruleRepository.findActualByPid(#rulePid).get().getRuleType().name() == 'DEAL')")
  public SellerRuleDTO find(Long rulePid) {
    return ruleAssembler.make(checkExists(ruleRepository.findActualByPid(rulePid).orElse(null)));
  }

  @Override
  @PreAuthorize(
      "!T(com.nexage.admin.core.model.User.Role).ROLE_USER.equals(@loginUserContext.getCurrentUser().getRole()) "
          + "and (@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage() "
          + "or (@ruleRepository.findActualByPid(#rulePid).isEmpty() "
          + "or @loginUserContext.doSameOrNexageAffiliation(@ruleRepository.findActualByPid(#rulePid).get().getRuleType().name() == 'DEAL')))")
  public void delete(Long rulePid) {
    CompanyRule rule =
        ruleRepository
            .findByPidAndRuleType(rulePid, RuleType.DEAL)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));
    rule.setStatus(Status.DELETED);
    ruleRepository.save(rule);
  }

  private void validateDealRule(SellerRuleDTO dto) {
    if (!(com.nexage.app.dto.sellingrule.RuleType.DEAL.equals(dto.getType()))) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_TYPE_IS_NOT_DEAL);
    }
  }

  private void validateRulePids(SellerRuleDTO dto, Long rulePid) {
    validateNotNull(dto.getPid(), ServerErrorCodes.SERVER_RULE_PID_IS_NULL);

    if (!dto.getPid().equals(rulePid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_PIDS_ARENT_SAME);
    }

    validateCommon(dto);
  }

  private void validateCommon(SellerRuleDTO dto) {
    validateNotNulls(dto);
    validateTargetsAndIntendedActions(dto);

    if (dto.getType() == com.nexage.app.dto.sellingrule.RuleType.DEAL) {
      if (dto.getRuleFormula() != null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_DEAL_RULE_SHOULD_NOT_HAVE_FORMULA);
      }
      if (dto.getAssignments() != null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_DEAL_RULE_SHOULD_NOT_HAVE_ASSIGNMENTS);
      }
    } else if (dto.getRuleFormula() != null) {
      if (dto.getAssignments() != null) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_ASSIGNMENTS_NOT_ALLOWED);
      }
    }
  }

  private void validateNotNulls(SellerRuleDTO dto) {
    if (!(com.nexage.app.dto.sellingrule.RuleType.DEAL.equals(dto.getType()))) {
      validateNotNull(
          dto.getOwnerCompanyPid(), ServerErrorCodes.SERVER_RULE_OWNER_COMPANY_PID_IS_NULL);
    }
    validateNotNull(dto.getStatus(), ServerErrorCodes.SERVER_RULE_STATUS_IS_NULL);
    validateNotNull(dto.getName(), ServerErrorCodes.SERVER_RULE_NAME_IS_NULL);
    validateNotNull(dto.getType(), ServerErrorCodes.SERVER_RULE_TYPE_IS_NULL);
  }

  private void validateRuleIsNotDeployed(SellerRuleDTO dto, CompanyRule entity) {
    if (dto.getOwnerCompanyPid() != null) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_RULE_OWNER_COMPANY_PID_IS_NOT_NULL);
    }
    if (!(entity.getDeployedCompanies().isEmpty()
        && entity.getDeployedSites().isEmpty()
        && entity.getDeployedPositions().isEmpty())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_HAS_DEPLOYED_OBJECTS);
    }
  }

  private void validateRuleSearchParamRequest(Set<String> qf, String qt) {
    if (!RuleSearchRequestParamValidator.isValid(qf, qt)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  private void validateRuleTypeString(String ruleType) {
    if (ruleType != null && !RuleTypeStringValidator.isValid(ruleType)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  private void validateStatusString(String status) {
    if (status != null && !StatusStringValidator.isValid(status)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  private CompanyRule checkExists(CompanyRule entity) {
    if (entity == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_DOESNT_EXIST);
    }
    return entity;
  }

  private CompanyRule checkVersion(CompanyRule entity, SellerRuleDTO dto) {
    if (!entity.getVersion().equals(dto.getVersion())) {
      throw new StaleStateException(
          "Invalid version; expected:" + entity.getVersion() + "; actual:" + dto.getVersion());
    }
    return entity;
  }

  private void validateNotNull(Object value, ServerErrorCodes errorMessage) {
    if (value == null) {
      throw new GenevaValidationException(errorMessage);
    }
  }
}
