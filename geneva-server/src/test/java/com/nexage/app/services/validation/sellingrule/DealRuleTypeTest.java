package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DealRuleTypeTest {

  private static final String AD_SIZE_TARGET_DATA = "200x50";
  private static final String JSON_DATA = "[{\"tag_id\":1},{\"tag_id\":2}]";

  private RuleType dealRuleType = RuleType.DEAL;

  @Test
  void whenDealRuleHasActions_thenValidationFails() {
    IntendedActionDTO intendedActionDto = IntendedActionDTO.builder().build();
    Set<IntendedActionDTO> intendedActions = new HashSet<>();
    intendedActions.add(intendedActionDto);
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_INTENDED_ACTION_IS_NOT_EMPTY,
        () -> dealRuleType.validateIntendedActions(intendedActions));
  }

  @Test
  void whenDealRuleHasNoActions_thenValidationPasses() {
    dealRuleType.validateIntendedActions(new HashSet<>());
  }

  @Test
  void whenDealRuleHasSupplyTargets_thenValidationPasses() {
    Set<RuleTargetDTO> ruleTargets = new HashSet<>();
    ruleTargets.add(RuleTargetDTO.builder().targetType(RuleTargetType.COUNTRY).build());
    ruleTargets.add(RuleTargetDTO.builder().targetType(RuleTargetType.REGION).build());
    ruleTargets.add(RuleTargetDTO.builder().targetType(RuleTargetType.STATE).build());
    dealRuleType.validateTargets(ruleTargets);
  }

  @Test
  void whenDealRuleHasBuyerTargets_thenValidationPasses() {
    Set<RuleTargetDTO> ruleTargets = new HashSet<>();
    ruleTargets.add(RuleTargetDTO.builder().targetType(RuleTargetType.BUYER_SEATS).build());
    ruleTargets.add(RuleTargetDTO.builder().targetType(RuleTargetType.MATCHED_USER_ID).build());
    dealRuleType.validateTargets(ruleTargets);
  }

  @Test
  void whenDealRuleHasBidTargets_thenValidationPasses() {
    Set<RuleTargetDTO> ruleTargets = new HashSet<>();
    ruleTargets.add(RuleTargetDTO.builder().targetType(RuleTargetType.IAB_CATEGORY).build());

    ruleTargets.add(
        RuleTargetDTO.builder()
            .targetType(RuleTargetType.CREATIVE_LANGUAGE)
            .data(JSON_DATA)
            .build());

    dealRuleType.validateTargets(ruleTargets);
  }

  @Test
  void whenDealRuleHasMixedTargets_thenValidationPasses() {
    Set<RuleTargetDTO> ruleTargets = new HashSet<>();
    ruleTargets.add(RuleTargetDTO.builder().targetType(RuleTargetType.COUNTRY).build());
    ruleTargets.add(RuleTargetDTO.builder().targetType(RuleTargetType.BUYER_SEATS).build());

    dealRuleType.validateTargets(ruleTargets);
  }

  @Test
  void whenDealRuleHasMultiAdSizeTargetWithIncludeMatchType_thenValidationPasses() {
    Set<RuleTargetDTO> ruleTargets = new HashSet<>();
    ruleTargets.add(
        RuleTargetDTO.builder()
            .targetType(RuleTargetType.MULTI_AD_SIZE)
            .matchType(MatchType.INCLUDE_LIST)
            .data(AD_SIZE_TARGET_DATA)
            .build());

    dealRuleType.validateTargets(ruleTargets);
  }

  @Test
  void whenDealRuleHasMultiAdSizeTargetWithExcludeMatchType_thenValidationPasses() {
    Set<RuleTargetDTO> ruleTargets = new HashSet<>();
    ruleTargets.add(
        RuleTargetDTO.builder()
            .targetType(RuleTargetType.MULTI_AD_SIZE)
            .matchType(MatchType.EXCLUDE_LIST)
            .data(AD_SIZE_TARGET_DATA)
            .build());

    dealRuleType.validateTargets(ruleTargets);
  }

  @Test
  void whenDealRuleHasAdSizeTarget_thenValidationFails() {
    Set<RuleTargetDTO> ruleTargets = new HashSet<>();
    ruleTargets.add(
        RuleTargetDTO.builder()
            .targetType(RuleTargetType.AD_SIZE)
            .matchType(MatchType.INCLUDE_LIST)
            .data(AD_SIZE_TARGET_DATA)
            .build());

    expectGenevaValidationException(
        ServerErrorCodes.SERVER_AD_SIZE_TARGET_WITH_DEAL_RULE_TYPE,
        () -> dealRuleType.validateTargets(ruleTargets));
  }

  private void expectGenevaValidationException(
      ServerErrorCodes expectedErrorMessage, Runnable runnable) {
    GenevaValidationException gve =
        assertThrows(
            GenevaValidationException.class,
            runnable::run,
            "GenevaValidationException expected but not thrown");
    assertEquals(expectedErrorMessage, gve.getErrorCode());
  }
}
