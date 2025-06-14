package com.nexage.admin.core.model.placementformula.formula.impl;

import static java.util.stream.Collectors.toList;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PlacementFormulaPredicateBuilder {
  private Group<RuleFormulaPositionView> root;
  private GroupOperator betweenGroupItems = GroupOperator.AND;
  private List<Collection<? extends PredicateBuilder<RuleFormulaPositionView>>> groups =
      new ArrayList<>();

  private PlacementFormulaPredicateBuilder() {}

  private PlacementFormulaPredicateBuilder(Group<RuleFormulaPositionView> root) {
    this.root = root;
  }

  public static PlacementFormulaPredicateBuilder betweenGroups(GroupOperator groupOperator) {
    return new PlacementFormulaPredicateBuilder(new Group<>(groupOperator));
  }

  public PlacementFormulaPredicateBuilder betweenGroupItems(GroupOperator groupOperator) {
    betweenGroupItems = groupOperator;
    return this;
  }

  public PlacementFormulaPredicateBuilder addGroup(
      Collection<? extends PredicateBuilder<RuleFormulaPositionView>> groupItems) {
    groups.add(groupItems);
    return this;
  }

  @SafeVarargs
  public final <E extends PredicateBuilder<RuleFormulaPositionView>>
      PlacementFormulaPredicateBuilder addGroup(E... groupItems) {
    addGroup(Arrays.asList(groupItems));
    return this;
  }

  public Group<RuleFormulaPositionView> build() {
    root.addAll(
        groups.stream()
            .map(
                sourceGroup -> {
                  Group<RuleFormulaPositionView> resultGroup = new Group<>(this.betweenGroupItems);
                  sourceGroup.forEach(resultGroup::add);
                  return resultGroup;
                })
            .collect(toList()));
    return root;
  }
}
