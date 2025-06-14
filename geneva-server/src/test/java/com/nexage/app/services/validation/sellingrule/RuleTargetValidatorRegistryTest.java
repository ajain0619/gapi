package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RuleTargetValidatorRegistryTest {
  private static final String AD_SIZES = "200x100,100x30";
  private static final String PID = "1234";
  private static final String PIDS = "1,2,3,4";

  @BeforeAll
  static void beforeClass() {
    RuleTargetValidatorRegistry.clearValidators();
    RuleTargetValidatorRegistry.registerValidator(
        RuleTargetType.COUNTRY, RuleTargetValidatorRegistry.COMMA_SEPARATED_NUMBERS_VALIDATOR);
  }

  @AfterAll
  static void afterClass() {
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.COUNTRY, null);
  }

  @Test
  void whenNoValidatorIsRegisteredForAdSize_thenDefaultNotEmptyValidatorFailsNullData() {
    RuleTargetDTO ruleTargetDTO = generateAdSizeTarget(null);
    assertThrows(
        GenevaValidationException.class, () -> RuleTargetValidatorRegistry.validate(ruleTargetDTO));
  }

  @Test
  void whenNoValidatorIsRegisteredForAdSize_thenDefaultNotEmptyValidatorFailsBlankData() {
    RuleTargetDTO ruleTargetDTO = generateAdSizeTarget("    ");
    assertThrows(
        GenevaValidationException.class, () -> RuleTargetValidatorRegistry.validate(ruleTargetDTO));
  }

  @Test
  void whenNoValidatorIsRegisteredForAdSize_thenDefaultNotEmptyValidatorDoesNotFailAdSizes() {
    RuleTargetValidatorRegistry.validate(generateAdSizeTarget(AD_SIZES));
  }

  @Test
  void whenCommaSeparatedNumberValidatorIsRegistered_thenNullDataFailsToPass() {
    RuleTargetDTO ruleTargetDTO = generateCountryTarget(null);
    assertThrows(
        GenevaValidationException.class, () -> RuleTargetValidatorRegistry.validate(ruleTargetDTO));
  }

  @Test
  void whenCommaSeparatedNumberValidatorIsRegistered_thenBlankDataFailsToPass() {
    RuleTargetDTO ruleTargetDTO = generateCountryTarget("    ");
    assertThrows(
        GenevaValidationException.class, () -> RuleTargetValidatorRegistry.validate(ruleTargetDTO));
  }

  @Test
  void whenCommaSeparatedNumberValidatorIsRegistered_thenAdSizesFailToPass() {
    RuleTargetDTO ruleTargetDTO = generateCountryTarget(AD_SIZES);
    assertThrows(
        GenevaValidationException.class, () -> RuleTargetValidatorRegistry.validate(ruleTargetDTO));
  }

  @Test
  void whenCommaSeparatedNumberValidatorIsRegistered_thenPidPasses() {
    RuleTargetValidatorRegistry.validate(generateCountryTarget(PID));
  }

  @Test
  void whenCommaSeparatedNumberValidatorIsRegistered_thenPidsPass() {
    RuleTargetValidatorRegistry.validate(generateCountryTarget(PIDS));
  }

  private RuleTargetDTO generateAdSizeTarget(String data) {
    return RuleTargetDTO.builder()
        .pid(1L)
        .version(1)
        .status(Status.ACTIVE)
        .targetType(RuleTargetType.AD_SIZE)
        .matchType(MatchType.INCLUDE_LIST)
        .data(data)
        .build();
  }

  private RuleTargetDTO generateCountryTarget(String data) {
    return RuleTargetDTO.builder()
        .pid(1L)
        .version(1)
        .status(Status.ACTIVE)
        .targetType(RuleTargetType.COUNTRY)
        .matchType(MatchType.INCLUDE_LIST)
        .data(data)
        .build();
  }
}
