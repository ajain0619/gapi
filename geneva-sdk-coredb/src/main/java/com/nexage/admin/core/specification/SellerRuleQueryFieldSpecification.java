package com.nexage.admin.core.specification;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.CompanyRule_;
import com.nexage.admin.core.model.RuleDeployedCompany_;
import com.nexage.admin.core.model.RuleDeployedPosition_;
import com.nexage.admin.core.model.RuleDeployedSite_;
import com.nexage.admin.core.model.Rule_;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

/**
 * A {@link Specification} that is used to create SQL query used in searching for seller rules based
 * on different criteria from {@code qf} request parameter.
 */
@Builder
public class SellerRuleQueryFieldSpecification implements Specification<CompanyRule> {

  private static final long serialVersionUID = -3167603026861106477L;
  @Default private boolean isAndOperator = true;
  @NonNull private Long sellerPid;
  @Default private Optional<Set<Long>> rulePids = Optional.empty();
  @Default private Optional<String> ruleName = Optional.empty();
  @Default private Optional<Set<RuleType>> types = Optional.empty();
  @Default private Optional<Boolean> deployedForSeller = Optional.empty();
  @Default private Optional<Set<Long>> placementPids = Optional.empty();
  @Default private Optional<Set<Long>> sitePids = Optional.empty();
  @Default private Set<RuleType> typeLimitations = Set.of();

  @Override
  public Predicate toPredicate(
      Root<CompanyRule> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

    List<Specification<CompanyRule>> specs = new ArrayList<>();

    rulePids.ifPresent(pids -> specs.add(withRulePids(pids)));
    ruleName.ifPresent(name -> specs.add(withName(name)));
    types.ifPresent(ruleTypes -> specs.add(withTypes(ruleTypes)));
    deployedForSeller.ifPresent(
        v -> {
          if (v) {
            specs.add(withRulesOnlyDeployedForSeller());
          }
        });
    placementPids.ifPresent(pids -> specs.add(withPlacements(pids)));
    sitePids.ifPresent(pids -> specs.add(withSites(pids)));

    Specification<CompanyRule> forBasePredicates = Specification.where(withSeller());
    if (!typeLimitations.isEmpty()) {
      forBasePredicates = forBasePredicates.and(withTypes(typeLimitations));
    }

    if (specs.isEmpty() && forBasePredicates != null) {
      return forBasePredicates.toPredicate(root, query, criteriaBuilder);
    }

    Specification<CompanyRule> qfCriteria = Specification.where(null);
    for (int i = 0; i < specs.size(); i++) {
      qfCriteria = isAndOperator ? qfCriteria.and(specs.get(i)) : qfCriteria.or(specs.get(i));
    }
    query.distinct(true);
    root.join(Rule_.RULE_TARGETS, JoinType.LEFT);
    root.join(Rule_.RULE_INTENDED_ACTIONS, JoinType.LEFT);
    return forBasePredicates.and(qfCriteria).toPredicate(root, query, criteriaBuilder);
  }

  private Specification<CompanyRule> withSeller() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(CompanyRule_.ownerCompanyPid), sellerPid));
  }

  private Specification<CompanyRule> withSites(Set<Long> sitePids) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            root.join(Rule_.DEPLOYED_SITES).get(RuleDeployedSite_.PID).in(sitePids));
  }

  private Specification<CompanyRule> withPlacements(Set<Long> placementPids) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            root.join(Rule_.DEPLOYED_POSITIONS).get(RuleDeployedPosition_.PID).in(placementPids));
  }

  private Specification<CompanyRule> withRulesOnlyDeployedForSeller() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            root.join(Rule_.DEPLOYED_COMPANIES).get(RuleDeployedCompany_.PID).in(sellerPid));
  }

  private Specification<CompanyRule> withTypes(Set<RuleType> types) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(root.get(Rule_.ruleType).in(types));
  }

  private Specification<CompanyRule> withName(String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.like(root.get(Rule_.name), String.format("%%%s%%", name)));
  }

  private Specification<CompanyRule> withRulePids(Set<Long> rulePids) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      root.join(Rule_.DEPLOYED_COMPANIES, JoinType.LEFT);
      root.join(Rule_.DEPLOYED_POSITIONS, JoinType.LEFT);
      root.join(Rule_.DEPLOYED_SITES, JoinType.LEFT);
      return criteriaBuilder.and(root.get(Rule_.pid).in(rulePids));
    };
  }
}
