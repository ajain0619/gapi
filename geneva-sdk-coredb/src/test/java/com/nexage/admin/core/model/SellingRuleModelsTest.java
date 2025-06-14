package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import org.junit.jupiter.api.Test;

class SellingRuleModelsTest {
  @Test
  void ruleModelTest() {
    CompanyRule rule1 = new CompanyRule();
    rule1.setPid(1L);
    rule1.setName("rule1");
    rule1.setStatus(Status.ACTIVE);
    rule1.setRuleType(RuleType.BRAND_PROTECTION);
    rule1.setOwnerCompanyPid(2L);

    CompanyRule rule2 = new CompanyRule();
    rule2.setPid(1L);
    rule2.setName("rule1");
    rule2.setStatus(Status.ACTIVE);
    rule2.setRuleType(RuleType.BRAND_PROTECTION);
    rule2.setOwnerCompanyPid(2L);
    assertEquals(rule1, rule2);
    assertEquals(rule1.hashCode(), rule2.hashCode());

    rule2.setDescription("adf");
    assertNotEquals(rule1, rule2);
    assertNotEquals(rule1.hashCode(), rule2.hashCode());
  }

  @Test
  void ruleTargetModelTest() {
    RuleTarget target1 = new RuleTarget();
    target1.setPid(1L);
    target1.setData("Target Data");
    target1.setStatus(Status.ACTIVE);
    target1.setRuleTargetType(RuleTargetType.AD_SIZE);
    target1.setMatchType(MatchType.INCLUDE_LIST);

    RuleTarget target2 = new RuleTarget();
    target2.setPid(1L);
    target2.setData("Target Data");
    target2.setStatus(Status.ACTIVE);
    target2.setRuleTargetType(RuleTargetType.AD_SIZE);
    target2.setMatchType(MatchType.INCLUDE_LIST);
    assertEquals(target1, target2);
    assertEquals(target1.hashCode(), target2.hashCode());

    target2.setMatchType(MatchType.EXCLUDE_LIST);
    assertNotEquals(target1, target2);
    assertNotEquals(target1.hashCode(), target2.hashCode());
  }
}
