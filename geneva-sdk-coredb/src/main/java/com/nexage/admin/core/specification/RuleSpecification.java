package com.nexage.admin.core.specification;

import static com.nexage.admin.core.model.RuleFormulaCompanyView_.DEFAULT_RTB_PROFILES_ENABLED;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.RuleFormulaCompanyView;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.RuleFormulaPositionView_;
import com.nexage.admin.core.model.RuleFormulaSiteView;
import com.nexage.admin.core.model.RuleFormulaSiteView_;
import com.nexage.admin.core.model.placementformula.formula.RootWrapper;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import java.util.Collection;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class RuleSpecification {

  private static final String STATUS = "status";
  private static final String PID = "pid";

  private RuleSpecification() {
    // private constructor because all fields and methods are static
  }

  public static Specification<RuleFormulaPositionView> withDefaultRtbProfiles(
      Collection<Long> companies, Group<RuleFormulaPositionView> placementFormula) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Join<RuleFormulaPositionView, RuleFormulaSiteView> siteJoin =
          root.join(RuleFormulaPositionView_.SITE, JoinType.LEFT);
      Join<RuleFormulaSiteView, RuleFormulaCompanyView> companyJoin =
          siteJoin.join(RuleFormulaSiteView_.COMPANY, JoinType.LEFT);

      criteriaQuery.distinct(true);
      criteriaBuilder.asc(root.get(PID));

      if (criteriaQuery.getResultType() != Long.class
          && criteriaQuery.getResultType() != long.class) {
        root.fetch(RuleFormulaPositionView_.PLACEMENT_VIDEO_VIEW, JoinType.LEFT);

        var siteFetch = root.fetch(RuleFormulaPositionView_.SITE, JoinType.LEFT);
        siteFetch.fetch(RuleFormulaSiteView_.COMPANY, JoinType.LEFT);
        siteFetch.fetch(RuleFormulaSiteView_.IAB_CATEGORIES, JoinType.LEFT);
      }
      Predicate formulaPredicate =
          placementFormula.build(
              criteriaBuilder,
              new RootWrapper<>(root, (CriteriaQuery<RuleFormulaPositionView>) criteriaQuery));

      return (companies == null || companies.isEmpty()
          ? criteriaBuilder.and(
              criteriaBuilder.isTrue(companyJoin.get(DEFAULT_RTB_PROFILES_ENABLED)),
              criteriaBuilder.equal(root.get(STATUS), Status.ACTIVE),
              criteriaBuilder.equal(siteJoin.get(STATUS), Status.ACTIVE),
              criteriaBuilder.equal(companyJoin.get(STATUS), Status.ACTIVE),
              formulaPredicate)
          : criteriaBuilder.and(
              criteriaBuilder.isTrue(companyJoin.get(DEFAULT_RTB_PROFILES_ENABLED)),
              criteriaBuilder.in(companyJoin.get(PID)).value(companies),
              criteriaBuilder.equal(root.get(STATUS), Status.ACTIVE),
              criteriaBuilder.equal(siteJoin.get(STATUS), Status.ACTIVE),
              criteriaBuilder.equal(companyJoin.get(STATUS), Status.ACTIVE),
              formulaPredicate));
    };
  }
}
