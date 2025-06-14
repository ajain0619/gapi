package com.nexage.admin.core.model.placementformula.formula.impl;

import com.nexage.admin.core.model.RuleFormulaSiteView;
import com.nexage.admin.core.model.RuleFormulaSiteView_;
import com.nexage.admin.core.model.placementformula.formula.Attribute;
import com.nexage.admin.core.model.placementformula.formula.RootWrapper;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.springframework.util.StringUtils;

public enum Operator {
  EQUALS {
    @Override
    public Predicate build(CriteriaBuilder builder, RootWrapper root, Attribute attr) {
      return buildPredicate(
          builder,
          attr,
          e -> {
            if (e.contains("%")) {
              return builder.like(attr.getAttributeInfo().getPath(root, e), e);
            }
            if (attr.getAttributeInfo().getValueAsObject(e) == null) {
              return builder.isNull(attr.getAttributeInfo().getPath(root, e));
            }
            return builder.equal(
                attr.getAttributeInfo().getPath(root, e),
                attr.getAttributeInfo().getValueAsObject(e));
          });
    }

    @Override
    public boolean isPositiveCheck() {
      return true;
    }
  },
  NOT_EQUALS {
    @Override
    public <T> Predicate build(CriteriaBuilder builder, RootWrapper<T> root, Attribute attr) {
      return EQUALS.build(builder, root, attr).not();
    }

    @Override
    public boolean isPositiveCheck() {
      return !EQUALS.isPositiveCheck();
    }
  },
  CONTAINS {
    @Override
    public <T> Predicate build(CriteriaBuilder builder, RootWrapper<T> root, Attribute attr) {
      return buildPredicate(
          builder,
          attr,
          e ->
              builder.like(
                  attr.getAttributeInfo().getPath(root, e),
                  '%' + escapeForLike(e) + '%',
                  ESCAPE_CHAR));
    }

    @Override
    public boolean isPositiveCheck() {
      return true;
    }
  },
  NOT_CONTAINS {
    @Override
    public <T> Predicate build(CriteriaBuilder builder, RootWrapper<T> root, Attribute attr) {
      return CONTAINS.build(builder, root, attr).not();
    }

    @Override
    public boolean isPositiveCheck() {
      return !CONTAINS.isPositiveCheck();
    }
  },
  MEMBER_OF {
    @Override
    public <T> Predicate build(CriteriaBuilder builder, RootWrapper<T> root, Attribute attr) {
      return buildPredicate(
          builder,
          attr,
          e ->
              builder
                  .in(attr.getAttributeInfo().getPath(root, e))
                  .value(Arrays.asList(e.split(","))));
    }

    @Override
    public boolean isPositiveCheck() {
      return true;
    }
  },
  NOT_MEMBER_OF {
    @Override
    public <T> Predicate build(CriteriaBuilder builder, RootWrapper<T> root, Attribute attr) {
      if (attr.getAttributeInfo() == FormulaAttributeInfo.SITE_IAB_CATEGORY) {
        return builder.exists(buildSubqueryForIabCategoriesNotMemberOf(root, attr)).not();
      }

      return MEMBER_OF.build(builder, root, attr).not();
    }

    @Override
    public boolean isPositiveCheck() {
      return !MEMBER_OF.isPositiveCheck();
    }
  };

  private static final char ESCAPE_CHAR = '¡';
  static final Pattern CSV_PATTERN = Pattern.compile(",");

  public abstract <T> Predicate build(CriteriaBuilder builder, RootWrapper<T> root, Attribute attr);

  public abstract boolean isPositiveCheck();

  public static String escapeForLike(String e) {
    return e.replaceAll("([%_])", ESCAPE_CHAR + "$1");
  }

  private static Predicate buildPredicate(
      CriteriaBuilder builder, Attribute attr, Function<String, Predicate> predicateBuildFunction) {
    return builder.or(
        Stream.of(CSV_PATTERN.split(attr.getValue()))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .map(predicateBuildFunction)
            .toArray(Predicate[]::new));
  }

  private static <T> Subquery buildSubqueryForIabCategoriesNotMemberOf(
      RootWrapper<T> root, Attribute attr) {
    CriteriaQuery<T> query = (CriteriaQuery<T>) root.getRootQuery();
    Subquery<RuleFormulaSiteView> subquery = query.subquery(RuleFormulaSiteView.class);
    Root<RuleFormulaSiteView> subRoot = subquery.from(RuleFormulaSiteView.class);

    return subquery
        .select(subRoot)
        .distinct(true)
        .where(
            subRoot
                .join(RuleFormulaSiteView_.IAB_CATEGORIES)
                .in(Arrays.asList(attr.getValue().split(","))));
  }
}
