package com.nexage.admin.core.model.placementformula.formula.impl;

import static com.nexage.admin.core.enums.site.PublisherSiteType.*;
import static com.nexage.admin.core.enums.site.PublisherSiteType.IOS;

import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.PublisherSiteType;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.Attribute;
import com.nexage.admin.core.model.placementformula.formula.AttributeInfo;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.RootWrapper;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public class SimpleAttribute implements PredicateBuilder<RuleFormulaPositionView>, Attribute {
  private AttributeInfo attribute;
  private Operator operator;
  private String value;

  public SimpleAttribute(AttributeInfo attribute, Operator operator, String value) {
    this.attribute = attribute;
    this.operator = operator;
    this.value = value;
  }

  public Operator getOperator() {
    return operator;
  }

  @Override
  public Predicate build(
      CriteriaBuilder builder,
      RootWrapper<RuleFormulaPositionView> rootWrapper,
      Predicate... predicates) {
    return operator.build(builder, rootWrapper, this);
  }

  @Override
  public String getValue() {
    if (FormulaAttributeInfo.SITE_TYPE.equals(attribute)) {
      return Stream.of(value.split(","))
          .map(String::trim)
          .map(
              siteType -> {
                PublisherSiteType publisherSiteType = fromString(siteType);
                if (IOS.equals(publisherSiteType)
                    || ANDROID.equals(publisherSiteType)
                    || CTV_OTT.equals(publisherSiteType)) {
                  return platformsFromSiteType(publisherSiteType)._2().stream()
                      .map(Platform::name)
                      .collect(Collectors.joining(","));
                }
                return siteType;
              })
          .collect(Collectors.joining(","));
    }
    return value;
  }

  @Override
  public AttributeInfo getAttributeInfo() {
    return attribute;
  }
}
