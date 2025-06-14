package com.nexage.admin.core.model.placementformula.formula.impl;

import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.RootWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public class Group<T> implements PredicateBuilder<T> {
  private GroupOperator operator;
  private List<PredicateBuilder<T>> items = new ArrayList<>();

  Group(GroupOperator operator) {
    this.operator = operator;
  }

  Group<T> add(PredicateBuilder<T> item) {
    items.add(item);
    return this;
  }

  Group<T> addAll(Collection<? extends PredicateBuilder<T>> items) {
    this.items.addAll(items);
    return this;
  }

  public GroupOperator getOperator() {
    return operator;
  }

  public List<? extends PredicateBuilder<T>> getItems() {
    return new ArrayList<>(items);
  }

  @Override
  public Predicate build(
      CriteriaBuilder builder, RootWrapper<T> rootWrapper, Predicate... predicates) {
    return operator.build(
        builder,
        rootWrapper,
        items.stream()
            .map(e -> e.build(builder, rootWrapper, predicates))
            .toArray(Predicate[]::new));
  }
}
