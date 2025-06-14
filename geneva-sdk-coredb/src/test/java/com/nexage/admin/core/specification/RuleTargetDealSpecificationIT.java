package com.nexage.admin.core.specification;

import static com.nexage.admin.core.specification.RuleTargetDealsSpecification.withDealPidAndTargetType;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.repository.RuleTargetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(
    scripts = {"/data/repository/rule-target-repository.sql", "/data/repository/deals-common.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class RuleTargetDealSpecificationIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private RuleTargetRepository ruleTargetRepository;

  @Test
  void shouldCorrectlySelectDealsWithDealPidAndTargetType() {
    var ruleTargetCount =
        ruleTargetRepository.count(withDealPidAndTargetType(1L, RuleTargetType.BUYER_SEATS));
    Assertions.assertEquals(1, ruleTargetCount);
  }
}
