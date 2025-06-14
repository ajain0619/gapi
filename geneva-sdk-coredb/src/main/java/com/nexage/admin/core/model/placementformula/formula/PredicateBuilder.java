package com.nexage.admin.core.model.placementformula.formula;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public interface PredicateBuilder<T> {
  Predicate build(CriteriaBuilder builder, RootWrapper<T> rootWrapper, Predicate... predicates);
}
