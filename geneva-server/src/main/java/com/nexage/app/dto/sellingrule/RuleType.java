package com.nexage.app.dto.sellingrule;

import com.nexage.admin.core.enums.RuleTargetCategory;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Set;

public enum RuleType {
  BRAND_PROTECTION {
    @Override
    public void validateIntendedActions(Set<IntendedActionDTO> intendedActions) {
      SellingRuleValidator.validateIntendedActionsAndActionType(
          intendedActions, RuleActionType.FILTER);
    }

    @Override
    public void validateTargets(Set<RuleTargetDTO> targets) {
      SellingRuleValidator.validateTargetIsNotEmpty(targets);
      SellingRuleValidator.validateMinTargetCategoryLevel(
          SellingRuleValidator.getMaxTargetCategory(targets), RuleTargetCategory.BUYER);
      SellingRuleValidator.validateTargetData(targets);
      SellingRuleValidator.validateTargetsDoNotContainMultiSizeAdTarget(targets);
      SellingRuleValidator.validateAdomainTargets(targets);
    }
  },
  DEAL {
    @Override
    public void validateIntendedActions(Set<IntendedActionDTO> intendedActions) {
      SellingRuleValidator.validateIntendedActionsIsEmpty(intendedActions);
    }

    @Override
    public void validateTargets(Set<RuleTargetDTO> targets) {
      SellingRuleValidator.validateMaxTargetCategoryLevel(
          SellingRuleValidator.getMaxTargetCategory(targets), RuleTargetCategory.BID);
      SellingRuleValidator.validateTargetData(targets);
      SellingRuleValidator.validateDealTargetsDoNotContainAdSizeTarget(targets);
      SellingRuleValidator.validateAdomainTargets(targets);
    }
  },
  EXPERIMENTATION {
    @Override
    public void validateIntendedActions(Set<IntendedActionDTO> intendedActions) {
      SellingRuleValidator.validateIntendedActionsAndActionType(
          intendedActions, RuleActionType.FILTER);
    }

    @Override
    public void validateTargets(Set<RuleTargetDTO> targets) {
      SellingRuleValidator.validateTargetIsNotEmpty(targets);
      Set<RuleTargetType> acceptedTargets =
          Set.of(RuleTargetType.PUBLISHER, RuleTargetType.REVGROUP, RuleTargetType.COUNTRY);
      boolean invalid =
          targets.stream().anyMatch(target -> !acceptedTargets.contains(target.getTargetType()));
      if (invalid) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_TARGET_TYPE_NOT_ALLOWED);
      }
    }
  };

  private static final Set<RuleTargetType> AD_SOURCE_MANAGEMENT_ALLOWED_TARGET_TYPES =
      Set.of(RuleTargetType.PLAYLIST_RENDERING_CAPABILITY);

  public abstract void validateIntendedActions(Set<IntendedActionDTO> intendedActions);

  public abstract void validateTargets(Set<RuleTargetDTO> targets);
}
