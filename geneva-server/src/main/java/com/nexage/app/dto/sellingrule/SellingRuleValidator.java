package com.nexage.app.dto.sellingrule;

import static java.util.stream.Collectors.toSet;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetCategory;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.Rule;
import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.admin.core.model.RuleIntendedAction;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.validation.sellingrule.RuleTargetValidatorRegistry;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.StaleStateException;

/** Validation logic for the Selling Rules */
@NoArgsConstructor
public class SellingRuleValidator {

  public static void validateIntendedActionsIsEmpty(Set<IntendedActionDTO> intendedActions) {
    if (CollectionUtils.isNotEmpty(intendedActions)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INTENDED_ACTION_IS_NOT_EMPTY);
    }
  }

  public static void validateIntendedActionsAndActionType(
      Set<IntendedActionDTO> intendedActions, RuleActionType requiredRuleActionType) {
    IntendedActionDTO intendedActionDto =
        SellingRuleValidator.getSingletonIntendedAction(intendedActions);
    if (!requiredRuleActionType.equals(intendedActionDto.getActionType())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_RULE_WRONG_COMBINATION_OF_RULE_TYPE_AND_RULE_INTENDED_ACTION);
    }
    intendedActionDto.getActionType().validateData(intendedActionDto.getActionData());
  }

  public static void validateMaxTargetCategoryLevel(
      RuleTargetCategory maxTargetCategory, RuleTargetCategory maxTargetCategoryAllowed) {
    if (maxTargetCategory != null
        && maxTargetCategory.getOrder() > maxTargetCategoryAllowed.getOrder()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_DEPLOYED_TO_WRONG_TARGET);
    }
  }

  public static void validateMinTargetCategoryLevel(
      RuleTargetCategory maxTargetCategory, RuleTargetCategory requiredMinTargetCategory) {
    if (maxTargetCategory != null
        && maxTargetCategory.getOrder() < requiredMinTargetCategory.getOrder()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_DEPLOYED_TO_WRONG_TARGET);
    }
  }

  public static IntendedActionDTO getSingletonIntendedAction(
      Set<IntendedActionDTO> intendedActions) {
    if (CollectionUtils.isEmpty(intendedActions)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INTENDED_ACTION_IS_NULL_OR_EMPTY);
    }
    if (intendedActions.size() > 1) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_MORE_THAN_ONE_INTENDED_ACTION);
    }
    return intendedActions.iterator().next();
  }

  public static RuleTargetCategory getMaxTargetCategory(Set<RuleTargetDTO> targets) {
    int maxOrder = -1;
    if (targets != null) {
      for (RuleTargetDTO target : targets) {
        maxOrder = Math.max(maxOrder, target.getTargetType().getCategory().getOrder());
      }
    }
    return maxOrder == -1 ? null : RuleTargetCategory.fromOrder(maxOrder);
  }

  public static void validateTargetIsNotEmpty(Set<RuleTargetDTO> targets) {
    if (targets.isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_MUST_HAVE_TARGET);
    }
  }

  public static void validateBidManagementMultiAdSizeTarget(Set<RuleTargetDTO> targets) {
    RuleTargetDTO multiAdSizeTarget =
        targets.stream()
            .filter(target -> RuleTargetType.MULTI_AD_SIZE.equals(target.getTargetType()))
            .findFirst()
            .orElse(null);

    if (multiAdSizeTarget != null) {
      if (!MatchType.INCLUDE_LIST.equals(multiAdSizeTarget.getMatchType())) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_MULTI_AD_SIZE_TARGET_WITH_INVALID_MATCH_TYPE);
      }

      boolean adSizeTargetExists =
          targets.stream()
              .anyMatch(target -> RuleTargetType.AD_SIZE.equals(target.getTargetType()));
      if (adSizeTargetExists) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_MULTI_AD_SIZE_TARGET_ALONG_WITH_AD_SIZE_TARGET);
      }
    }
  }

  /**
   * Validates if passed rule targets don't contain wildcard advertiser domain target along with
   * exact match advertiser domain target
   *
   * @param targets the set of selling rule targets
   */
  public static void validateAdomainTargets(Set<RuleTargetDTO> targets) {
    boolean exactMatchAdomainTargetExists =
        targets.stream()
            .anyMatch(
                target -> RuleTargetType.WILDCARD_ADVERTISER_DOMAIN.equals(target.getTargetType()));
    boolean wildcardAdomainTargetExists =
        targets.stream()
            .anyMatch(
                target ->
                    RuleTargetType.EXACT_MATCH_ADVERTISER_DOMAIN.equals(target.getTargetType()));

    if (wildcardAdomainTargetExists && exactMatchAdomainTargetExists) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_WILDCARD_ADOMAIN_TARGET_ALONG_WITH_EXACT_MATCH_ADOMAIN_TARGET);
    }
  }

  public static void validateTargetData(Set<RuleTargetDTO> targets) {
    targets.forEach(
        t -> {
          try {
            t.getTargetType().validateTargetData(t.getData());
          } catch (Exception e) {
            throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_INVALID_TARGET_DATA);
          }
        });
  }

  public static void validateTargetsDoNotContainMultiSizeAdTarget(Set<RuleTargetDTO> targets) {
    boolean multiAdSizeTargetExists =
        targets.stream()
            .anyMatch(target -> RuleTargetType.MULTI_AD_SIZE.equals(target.getTargetType()));

    if (multiAdSizeTargetExists) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_MULTI_AD_SIZE_TARGET_WITH_INVALID_RULE_TYPE);
    }
  }

  public static void validateDealTargetsDoNotContainAdSizeTarget(Set<RuleTargetDTO> targets) {
    boolean adSizeTargetExists =
        targets.stream().anyMatch(target -> RuleTargetType.AD_SIZE.equals(target.getTargetType()));

    if (adSizeTargetExists) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_AD_SIZE_TARGET_WITH_DEAL_RULE_TYPE);
    }
  }

  /**
   * Validates if passed rule targets don't contain duplicates - only one target type per rule can
   * bbe defined
   *
   * @param targets the set of selling rule targets
   */
  public static void validateDuplicateTarget(Set<RuleTargetDTO> targets) {
    if (targets.size()
        > targets.stream().map(RuleTargetDTO::getTargetType).collect(toSet()).size()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_DUPLICATE_TARGET_TYPE);
    }
  }

  private static void validateNoPlaylistRenderingCapabilityTarget(Set<RuleTargetDTO> targets) {
    var isPresent =
        targets.stream()
            .map(RuleTargetDTO::getTargetType)
            .anyMatch(RuleTargetType.PLAYLIST_RENDERING_CAPABILITY::equals);

    if (isPresent) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_DEPLOYED_TO_WRONG_TARGET);
    }
  }

  /**
   * Validates if passed rule has properly defined targets and intended actions
   *
   * @param sellingRule the selling rule DTO to check
   */
  public static void validateTargetsAndIntendedActions(RuleDTO sellingRule) {
    validateDuplicateTarget(sellingRule.getTargets());
    validateNoPlaylistRenderingCapabilityTarget(sellingRule.getTargets());
    sellingRule.getType().validateTargets(sellingRule.getTargets());
    sellingRule.getType().validateIntendedActions(sellingRule.getIntendedActions());
    sellingRule.getTargets().forEach(RuleTargetValidatorRegistry::validate);
  }

  /**
   * Validates if passed rule DTO has nullable pid and version fields
   *
   * @param sellingRule the selling rule DTO to check
   */
  public static void validateRulePidAndVersionAreNull(RuleDTO sellingRule) {
    if (sellingRule.getPid() != null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_PID_IS_NOT_NULL);
    }

    if (sellingRule.getVersion() != null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_VERSION_IS_NOT_NULL);
    }
  }

  /**
   * Validates if passed rule DTO has same pid with the target pid
   *
   * @param sellingRule the selling rule DTO to check
   * @param rulePid the rulePid to compare with
   * @return rule_pids_arent_same Exception if Invalid
   */
  public static void validateRulePidsForUpdateAreSame(RuleDTO sellingRule, Long rulePid) {

    if (!sellingRule.getPid().equals(rulePid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_PIDS_ARENT_SAME);
    }
  }

  /**
   * Validate the Rule to update always sync the version with the record in database.
   *
   * @param companyRule This is the CompanyRule record from database
   * @param sellingRule This is the rule DTO as UPDATE BODY.
   * @return StaleStateException Exception if Invalid
   */
  public static void validateVersionsAreSame(CompanyRule companyRule, RuleDTO sellingRule) {
    if (!companyRule.getVersion().equals(sellingRule.getVersion())) {
      throw new StaleStateException(
          "Invalid version; expected:"
              + companyRule.getVersion()
              + "; actual:"
              + sellingRule.getVersion());
    }
  }

  /**
   * Validate the Rule to update is still available in database
   *
   * @param companyRule This is the CompanyRule from Database
   * @return StaleStateException Exception if Invalid
   */
  public static void validateRuleExistence(CompanyRule companyRule) {
    if (companyRule == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_DOESNT_EXIST);
    }
  }

  /**
   * Validates the sellerRule DTO has the correct assignments. Check the RuleDeployedCompany
   * assignment to the right company Check the RuleDeployedSite assignment to the right company
   * Check the RuleDeployedPosition assignment to the right company
   *
   * @param sellerRuleDTO DTO used the UPDATE Body
   * @param companyRule CompanyRule from database to compare with
   * @return rule_assignments_not_allowed Exception if Invalid
   */
  public static void validateDeloyedToCorrectAssignments(
      SellerRuleDTO sellerRuleDTO, CompanyRule companyRule) {
    final Long ownerCompanyPid = companyRule.getOwnerCompanyPid();
    if (!ownerCompanyPid.equals(sellerRuleDTO.getOwnerCompanyPid())
        || companyRule.getDeployedCompanies().stream()
            .map(RuleDeployedCompany::getPid)
            .filter(Objects::nonNull)
            .anyMatch(e -> !e.equals(ownerCompanyPid))
        || companyRule.getDeployedSites().stream()
            .map(RuleDeployedSite::getCompanyPid)
            .filter(Objects::nonNull)
            .anyMatch(e -> !e.equals(ownerCompanyPid))
        || companyRule.getDeployedPositions().stream()
            .map(e -> e.getSite().getCompanyPid())
            .filter(Objects::nonNull)
            .anyMatch(e -> !e.equals(ownerCompanyPid))) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_ASSIGNMENTS_NOT_ALLOWED);
    }
  }

  public static void validateUpdateAnotherRuleIntendedActions(Rule rule, RuleDTO ruleDTO) {
    Set<Long> ruleIntendedActionsPids =
        rule.getRuleIntendedActions().stream()
            .map(RuleIntendedAction::getPid)
            .collect(Collectors.toSet());

    boolean updatedAnotherRuleIntendedActions =
        ruleDTO.getIntendedActions().stream()
            .map(IntendedActionDTO::getPid)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet())
            .retainAll(ruleIntendedActionsPids);

    if (updatedAnotherRuleIntendedActions) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INTENDED_ACTION_FROM_ANOTHER_RULE);
    }
  }

  public static void validateUpdateAnotherRuleTargets(Rule rule, RuleDTO ruleDTO) {
    Set<Long> ruleTargetsPids =
        rule.getRuleTargets().stream().map(RuleTarget::getPid).collect(Collectors.toSet());

    boolean updatedAnotherRuleTargets =
        ruleDTO.getTargets().stream()
            .map(RuleTargetDTO::getPid)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet())
            .retainAll(ruleTargetsPids);

    if (updatedAnotherRuleTargets) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_TARGET_FROM_ANOTHER_RULE);
    }
  }
}
