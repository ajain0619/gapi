package com.nexage.admin.core.specification;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.DirectDeal_;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.model.RuleTarget_;
import com.nexage.admin.core.model.Rule_;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.admin.core.sparta.jpa.model.DealRule_;
import org.springframework.data.jpa.domain.Specification;

public class RuleTargetDealsSpecification {

  private RuleTargetDealsSpecification() {}

  public static Specification<RuleTarget> withDealPidAndTargetType(
      Long dealPid, RuleTargetType ruleTargetType) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      var subQuery = criteriaQuery.subquery(Long.class);
      var dealRule = subQuery.from(DealRule.class);
      subQuery.select(dealRule.get(DealRule_.rulePid));
      subQuery.where(
          criteriaBuilder.equal(dealRule.get(DealRule_.deal).get(DirectDeal_.pid), dealPid));
      var rule = criteriaBuilder.in(root.get(RuleTarget_.rule).get(Rule_.pid)).value(subQuery);
      var buyerSeatRule =
          criteriaBuilder.equal(root.get(RuleTarget_.RULE_TARGET_TYPE), ruleTargetType.asInt());

      return criteriaBuilder.and(rule, buyerSeatRule);
    };
  }
}
