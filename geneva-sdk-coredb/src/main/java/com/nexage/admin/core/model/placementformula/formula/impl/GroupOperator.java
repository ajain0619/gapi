package com.nexage.admin.core.model.placementformula.formula.impl;

import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.RootWrapper;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public enum GroupOperator implements PredicateBuilder {
  OR {
    @Override
    public Predicate build(
        CriteriaBuilder builder, RootWrapper rootWrapper, Predicate... predicates) {
      return builder.or(predicates);
    }
  },
  AND {
    @Override
    public Predicate build(
        CriteriaBuilder builder, RootWrapper rootWrapper, Predicate... predicates) {
      return builder.and(predicates);
    }
  };
}
