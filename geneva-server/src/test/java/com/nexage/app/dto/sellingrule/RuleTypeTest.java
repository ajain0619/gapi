package com.nexage.app.dto.sellingrule;

import static com.nexage.app.dto.sellingrule.RuleType.BRAND_PROTECTION;
import static com.nexage.app.dto.sellingrule.RuleType.DEAL;
import static com.nexage.app.dto.sellingrule.RuleType.EXPERIMENTATION;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RuleTypeTest {

  @Test
  void shouldBeInvalidWhenBrandProtectionRuleHasMultiAdSizeTarget() {
    Set<RuleTargetDTO> ruleTargets =
        Set.of(
            RuleTargetDTO.builder()
                .targetType(RuleTargetType.MULTI_AD_SIZE)
                .data("200x50,600x200")
                .matchType(MatchType.INCLUDE_LIST)
                .build(),
            RuleTargetDTO.builder()
                .targetType(RuleTargetType.CREATIVE_ID)
                .data("200x50,600x200")
                .matchType(MatchType.INCLUDE_LIST)
                .build());

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> BRAND_PROTECTION.validateTargets(ruleTargets));

    assertEquals(
        ServerErrorCodes.SERVER_MULTI_AD_SIZE_TARGET_WITH_INVALID_RULE_TYPE,
        exception.getErrorCode());
  }

  @Test
  void shouldBeInvalidWhenBrandProtectionRuleHasWildcardAndExactMatchAdvertiserDomainTarget() {
    Set<RuleTargetDTO> ruleTargets =
        Set.of(
            RuleTargetDTO.builder()
                .targetType(RuleTargetType.WILDCARD_ADVERTISER_DOMAIN)
                .data("adomain.com")
                .matchType(MatchType.INCLUDE_LIST)
                .build(),
            RuleTargetDTO.builder()
                .targetType(RuleTargetType.EXACT_MATCH_ADVERTISER_DOMAIN)
                .data("adomain.com")
                .matchType(MatchType.INCLUDE_LIST)
                .build());

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> BRAND_PROTECTION.validateTargets(ruleTargets));

    assertEquals(
        ServerErrorCodes.SERVER_WILDCARD_ADOMAIN_TARGET_ALONG_WITH_EXACT_MATCH_ADOMAIN_TARGET,
        exception.getErrorCode());
  }

  @Test
  void shouldBeInvalidWhenDealRuleHasWildcardAndExactMatchAdvertiserDomainTarget() {
    Set<RuleTargetDTO> ruleTargets =
        Set.of(
            RuleTargetDTO.builder()
                .targetType(RuleTargetType.WILDCARD_ADVERTISER_DOMAIN)
                .data("adomain.com")
                .matchType(MatchType.INCLUDE_LIST)
                .build(),
            RuleTargetDTO.builder()
                .targetType(RuleTargetType.EXACT_MATCH_ADVERTISER_DOMAIN)
                .data("adomain.com")
                .matchType(MatchType.INCLUDE_LIST)
                .build());

    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> DEAL.validateTargets(ruleTargets));

    assertEquals(
        ServerErrorCodes.SERVER_WILDCARD_ADOMAIN_TARGET_ALONG_WITH_EXACT_MATCH_ADOMAIN_TARGET,
        exception.getErrorCode());
  }

  @Test
  void shouldBeValid_whenExperimentRuleHasCorrectActionType() {
    IntendedActionDTO actionDTO =
        IntendedActionDTO.builder()
            .actionType(RuleActionType.FILTER)
            .actionData("BLOCKLIST")
            .build();

    assertDoesNotThrow(() -> EXPERIMENTATION.validateIntendedActions(Set.of(actionDTO)));
  }

  @Test
  void shouldBeInvalid_whenExperimentRuleHasWrongActionType() {
    Set<IntendedActionDTO> actions =
        Set.of(IntendedActionDTO.builder().actionType(RuleActionType.FLOOR).build());
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> EXPERIMENTATION.validateIntendedActions(actions));
    assertEquals(
        ServerErrorCodes.SERVER_RULE_WRONG_COMBINATION_OF_RULE_TYPE_AND_RULE_INTENDED_ACTION,
        exception.getErrorCode());
  }

  @Test
  void shouldBeValid_whenExperimentRuleHasCorrectTargets() {
    RuleTargetDTO publisherTargetDTO =
        RuleTargetDTO.builder().targetType(RuleTargetType.PUBLISHER).build();
    RuleTargetDTO revgroupTargetDTO =
        RuleTargetDTO.builder().targetType(RuleTargetType.REVGROUP).build();
    Set<RuleTargetDTO> targets = Set.of(publisherTargetDTO, revgroupTargetDTO);

    assertDoesNotThrow(() -> EXPERIMENTATION.validateTargets(targets));
  }

  @Test
  void shouldBeInvalid_whenExperimentRuleHasEmptyTargets() {
    Set<RuleTargetDTO> targets = new HashSet<>();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> EXPERIMENTATION.validateTargets(targets));
    assertEquals(ServerErrorCodes.SERVER_RULE_MUST_HAVE_TARGET, exception.getErrorCode());
  }

  @Test
  void shouldBeInvalid_whenExperimentRuleHasWrongTarget() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder().targetType(RuleTargetType.PUBLISHER).build();
    RuleTargetDTO wrongRuleTargetDTO =
        RuleTargetDTO.builder().targetType(RuleTargetType.DEVICE_TYPE).build();
    Set<RuleTargetDTO> targets = Set.of(ruleTargetDTO, wrongRuleTargetDTO);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> EXPERIMENTATION.validateTargets(targets));
    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_TYPE_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void shouldBeValidWhenBrandProtectionRuleHasIncludeAdFormatType() {
    Set<RuleTargetDTO> ruleTargets =
        Set.of(
            RuleTargetDTO.builder()
                .targetType(RuleTargetType.AD_FORMAT_TYPE)
                .data("BANNER")
                .matchType(MatchType.INCLUDE_LIST)
                .build());

    assertDoesNotThrow(() -> BRAND_PROTECTION.validateTargets(ruleTargets));
  }

  @Test
  void shouldBeValidWhenBrandProtectionRuleHasExcludeAdFormatType() {
    Set<RuleTargetDTO> ruleTargets =
        Set.of(
            RuleTargetDTO.builder()
                .targetType(RuleTargetType.AD_FORMAT_TYPE)
                .data("VIDEO")
                .matchType(MatchType.EXCLUDE_LIST)
                .build());

    assertDoesNotThrow(() -> BRAND_PROTECTION.validateTargets(ruleTargets));
  }

  @Test
  void shouldBeValidWhenDealTargetContainsOnlyAdFormatTypeTarget() {
    RuleTargetDTO adFormatTypeTarget =
        RuleTargetDTO.builder()
            .targetType(RuleTargetType.AD_FORMAT_TYPE)
            .data("VIDEO")
            .matchType(MatchType.INCLUDE_LIST)
            .build();

    Set<RuleTargetDTO> ruleTargets = Set.of(adFormatTypeTarget);

    assertDoesNotThrow(() -> DEAL.validateTargets(ruleTargets));
  }
}
