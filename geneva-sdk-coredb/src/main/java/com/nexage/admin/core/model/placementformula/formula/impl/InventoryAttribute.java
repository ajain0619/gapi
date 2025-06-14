package com.nexage.admin.core.model.placementformula.formula.impl;

import com.nexage.admin.core.model.RuleFormulaAttributeValueView;
import com.nexage.admin.core.model.RuleFormulaAttributeValueView_;
import com.nexage.admin.core.model.RuleFormulaCompanyView;
import com.nexage.admin.core.model.RuleFormulaCompanyView_;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.RuleFormulaPositionView_;
import com.nexage.admin.core.model.RuleFormulaSiteView;
import com.nexage.admin.core.model.RuleFormulaSiteView_;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.RootWrapper;
import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.springframework.util.Assert;

public class InventoryAttribute implements PredicateBuilder<RuleFormulaPositionView> {
  private Long pid;
  private String value;
  private Operator operator;

  public InventoryAttribute(Long pid, Operator operator, String value) {
    Assert.isTrue(
        operator == Operator.EQUALS || operator == Operator.NOT_EQUALS,
        "Only EQUALS and NOT_EQUALS are allowed as operators for inventory attributes.");
    this.pid = pid;
    this.value = value;
    this.operator = operator;
  }

  public Long getPid() {
    return pid;
  }

  public String getValue() {
    return value;
  }

  public Operator getOperator() {
    return operator;
  }

  @Override
  public Predicate build(
      CriteriaBuilder builder,
      RootWrapper<RuleFormulaPositionView> rootWrapper,
      Predicate... predicates) {
    return new SearchBuilder(builder, rootWrapper.getRootQuery(), rootWrapper.getRoot())
        .buildSearch();
  }

  private class SearchBuilder {
    private final CriteriaBuilder builder;
    private final AbstractQuery<RuleFormulaPositionView> rootQuery;
    private final Root<RuleFormulaPositionView> rootFrom;
    private final List<String> searchValues;

    private SearchBuilder(
        CriteriaBuilder builder,
        AbstractQuery<RuleFormulaPositionView> rootQuery,
        Root<RuleFormulaPositionView> rootFrom) {
      this.builder = builder;
      this.rootQuery = rootQuery;
      this.rootFrom = rootFrom;
      searchValues = Arrays.asList(Operator.CSV_PATTERN.split(value));
    }

    public Predicate buildSearch() {
      return operator.isPositiveCheck()
          ? hasSearchValuesAnywhere()
          : hasNoSearchValuesButOthersAnywhere();
    }

    private Predicate hasSearchValuesAnywhere() {
      return builder.or(
          hasSearchValuesInPositionAttributes(),
          builder.and(
              hasNoValuesInPositionAttributes(),
              builder.or(
                  hasSearchValuesInSiteAttributes(),
                  builder.and(
                      hasNoValuesInSiteAttributes(), hasSearchValuesInCompanyAttributes()))));
    }

    private Predicate hasNoSearchValuesButOthersAnywhere() {
      return builder.or(
          builder.and(hasAnyValuesInPositionAttributes(), hasNoSearchValuesInPositionAttributes()),
          builder.and(
              hasNoValuesInPositionAttributes(),
              builder.or(
                  builder.and(hasAnyValuesInSiteAttributes(), hasNoSearchValuesInSiteAttributes()),
                  builder.and(
                      hasNoValuesInSiteAttributes(),
                      builder.or(
                          builder.and(
                              hasAnyValuesInCompanyAttributes(),
                              hasNoSearchValuesInCompanyAttributes()),
                          hasNoValuesInCompanyAttributes())))));
    }

    private Predicate hasSearchValuesInPositionAttributes() {
      return hasValuesInPositionAttributes(true);
    }

    private Predicate hasNoSearchValuesInPositionAttributes() {
      return hasSearchValuesInPositionAttributes().not();
    }

    private Predicate hasAnyValuesInPositionAttributes() {
      return hasValuesInPositionAttributes(false);
    }

    private Predicate hasNoValuesInPositionAttributes() {
      return hasAnyValuesInPositionAttributes().not();
    }

    private Predicate hasSearchValuesInSiteAttributes() {
      return hasValuesInSiteAttributes(true);
    }

    private Predicate hasNoSearchValuesInSiteAttributes() {
      return hasSearchValuesInSiteAttributes().not();
    }

    private Predicate hasAnyValuesInSiteAttributes() {
      return hasValuesInSiteAttributes(false);
    }

    private Predicate hasNoValuesInSiteAttributes() {
      return hasAnyValuesInSiteAttributes().not();
    }

    private Predicate hasNoValuesInCompanyAttributes() {
      return hasAnyValuesInCompanyAttributes().not();
    }

    private Predicate hasSearchValuesInCompanyAttributes() {
      return hasValuesInCompanyAttributes(true);
    }

    private Predicate hasNoSearchValuesInCompanyAttributes() {
      return hasSearchValuesInCompanyAttributes().not();
    }

    private Predicate hasAnyValuesInCompanyAttributes() {
      return hasValuesInCompanyAttributes(false);
    }

    // PSEUDO JPQL: exists (select a from pos.site.company.attributeValues a where a.attributePid =
    // pid [and a.name in ?])
    private Predicate hasValuesInCompanyAttributes(boolean searchValuesOnly) {
      Subquery<RuleFormulaAttributeValueView> subquery =
          rootQuery.subquery(RuleFormulaAttributeValueView.class);
      Join<RuleFormulaCompanyView, RuleFormulaAttributeValueView> values =
          subquery
              .correlate(rootFrom)
              .join(RuleFormulaPositionView_.site)
              .join(RuleFormulaSiteView_.company)
              .join(RuleFormulaCompanyView_.attributeValues);
      subquery.select(values).where(matchesAttributeValues(values, searchValuesOnly));
      return builder.exists(subquery);
    }

    // PSEUDO JPQL: exists (select a from pos.attributeValues a where a.attributePid = pid [and
    // a.name in ?])
    private Predicate hasValuesInPositionAttributes(boolean searchValuesOnly) {
      Subquery<RuleFormulaAttributeValueView> subquery =
          rootQuery.subquery(RuleFormulaAttributeValueView.class);
      Join<RuleFormulaPositionView, RuleFormulaAttributeValueView> values =
          subquery.correlate(rootFrom).join(RuleFormulaPositionView_.attributeValues);
      subquery.select(values).where(matchesAttributeValues(values, searchValuesOnly));
      return builder.exists(subquery);
    }

    // PSEUDO JPQL: exists (select a from pos.site.attributeValues a where a.attributePid = pid [and
    // a.name in ?])
    private Predicate hasValuesInSiteAttributes(boolean searchValuesOnly) {
      Subquery<RuleFormulaAttributeValueView> subquery =
          rootQuery.subquery(RuleFormulaAttributeValueView.class);
      Join<RuleFormulaSiteView, RuleFormulaAttributeValueView> values =
          subquery
              .correlate(rootFrom)
              .join(RuleFormulaPositionView_.site)
              .join(RuleFormulaSiteView_.attributeValues);
      subquery.select(values).where(matchesAttributeValues(values, searchValuesOnly));
      return builder.exists(subquery);
    }

    private Predicate matchesAttributeValues(
        Join<?, RuleFormulaAttributeValueView> values, boolean searchValuesOnly) {
      Predicate attributePidMatch =
          builder.equal(values.get(RuleFormulaAttributeValueView_.attributePid), pid);
      if (!searchValuesOnly) {
        return attributePidMatch;
      }
      Predicate searchValueMatch =
          matchesSearchValues(values.get(RuleFormulaAttributeValueView_.name));
      return builder.and(attributePidMatch, searchValueMatch);
    }

    private Predicate matchesSearchValues(Expression<String> expression) {
      return searchValues.size() > 1
          ? expression.in(searchValues)
          : builder.equal(expression, searchValues.get(0));
    }
  }
}
