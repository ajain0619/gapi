package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.RuleTargetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(
    scripts = "/data/repository/rule-target-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class RuleTargetRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired protected RuleTargetRepository ruleTargetRepository;

  @Test
  void shouldReturnTrueWithOneDsp() {
    Boolean dealHasOneDsp = ruleTargetRepository.hasOneDsp(1L, RuleTargetType.BUYER_SEATS);
    assertTrue(dealHasOneDsp);
  }

  @Test
  void shouldReturnFalseWithMultipleDsps() {
    Boolean dealHasOneDsp = ruleTargetRepository.hasOneDsp(2L, RuleTargetType.BUYER_SEATS);
    assertFalse(dealHasOneDsp);
  }

  @Test
  void shouldReturnTrueWithOneDspAndMultipleTargets() {
    Boolean dealHasOneDsp = ruleTargetRepository.hasOneDsp(3L, RuleTargetType.BUYER_SEATS);
    assertTrue(dealHasOneDsp);
  }

  @Test
  void shouldReturnTrueWhenRuleTargetExist() {
    Boolean hasRuleTarget = ruleTargetRepository.hasRuleTarget(1L);
    assertTrue(hasRuleTarget);
  }

  @Test
  void shouldReturnFalseWhenRuleTargetAbsent() {
    Boolean hasRuleTarget = ruleTargetRepository.hasRuleTarget(10L);
    assertNull(hasRuleTarget);
  }

  @Test
  void shouldReturnTrueWhenRuleTargetOtherThanBuyerSeat() {
    Boolean hasRuleTargetOtherThanProvided =
        ruleTargetRepository.hasRuleTargetOtherThanProvided(3L, RuleTargetType.BUYER_SEATS);
    assertTrue(hasRuleTargetOtherThanProvided);
  }

  @Test
  void shouldReturnFalseWhenRuleTargetOnlyBuyerSeat() {
    Boolean hasRuleTargetOtherThanProvided =
        ruleTargetRepository.hasRuleTargetOtherThanProvided(1L, RuleTargetType.BUYER_SEATS);
    assertFalse(hasRuleTargetOtherThanProvided);
  }
}
