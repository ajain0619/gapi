package com.nexage.admin.core.model.placementformula.formula.impl;

import static com.nexage.admin.core.model.placementformula.formula.impl.Operator.escapeForLike;
import static org.hibernate.query.criteria.internal.predicate.ComparisonPredicate.ComparisonOperator.EQUAL;
import static org.hibernate.query.criteria.internal.predicate.ComparisonPredicate.ComparisonOperator.NOT_EQUAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.RootWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
import org.hibernate.query.criteria.internal.predicate.LikePredicate;
import org.hibernate.query.criteria.internal.predicate.NegatedPredicateWrapper;
import org.hibernate.query.criteria.internal.predicate.NullnessPredicate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FormulaOperatorIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Test
  void shouldCreateEqualsPlacementOperatorForSiteTypeAttr() throws IllegalAccessException {

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          EntityManager entityManager = entityManagerFactory.createEntityManager();
          CriteriaBuilder builder = entityManager.getCriteriaBuilder();
          CriteriaQuery<RuleFormulaPositionView> criteriaQuery =
              builder.createQuery(RuleFormulaPositionView.class);
          Root<RuleFormulaPositionView> root = criteriaQuery.from(RuleFormulaPositionView.class);
          root.join("site", JoinType.LEFT);

          SimpleAttribute formulaRule =
              new SimpleAttribute(
                  FormulaAttributeInfo.SITE_TYPE, null, "Mobile%, %SDK, Java Script, APPLICATION");
          Predicate predicate =
              Operator.EQUALS.build(builder, new RootWrapper<>(root, criteriaQuery), formulaRule);
          Predicate.BooleanOperator op = predicate.getOperator();
          assertEquals(op, BooleanOperator.OR, "The expressions should be related by OR operator");

          List<Expression<Boolean>> expressions = predicate.getExpressions();
          assertEquals(4, expressions.size(), "There should be four expressions built");

          for (Expression<Boolean> e : expressions) {
            NegatedPredicateWrapper negatedPredicateWrapper = (NegatedPredicateWrapper) e;
            boolean isNegated = negatedPredicateWrapper.isNegated();
            Object privatePredicateField =
                FieldUtils.readField(negatedPredicateWrapper, "predicate", true);
            if (expressions.indexOf(e) == 0 || expressions.indexOf(e) == 1) {
              assertTrue(
                  privatePredicateField instanceof LikePredicate,
                  "The predicate should be Like Predicate");
            } else if (expressions.indexOf(e) == 2) {
              assertTrue(
                  privatePredicateField instanceof NullnessPredicate,
                  "The predicate should be Nullness Predicate");
              NullnessPredicate np = (NullnessPredicate) privatePredicateField;
              assertTrue(!np.isNegated(), "The predicate was not built with not-negated");
            } else if (expressions.indexOf(e) == 3) {
              assertTrue(
                  e instanceof ComparisonPredicate, "The predicate should be Comparison Predicate");
              ComparisonPredicate cp = (ComparisonPredicate) privatePredicateField;
              assertEquals(
                  EQUAL,
                  cp.getComparisonOperator(isNegated),
                  "The predicate was not built with Comparison Operator 'EQUAL'");
            }
          }
          assertNotNull(predicate.isNotNull());
        });
  }

  @Test
  void shouldCreateNotEqualsPlacementOperatorForPlacementNameAttr() throws IllegalAccessException {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RuleFormulaPositionView> criteriaQuery =
        builder.createQuery(RuleFormulaPositionView.class);
    Root<RuleFormulaPositionView> root = criteriaQuery.from(RuleFormulaPositionView.class);
    root.join("site", JoinType.LEFT);

    SimpleAttribute formulaRule =
        new SimpleAttribute(
            FormulaAttributeInfo.PLACEMENT_NAME,
            null,
            "Header, Body, Footer, header, body, footer, asftg%, %5_%");
    Predicate predicate =
        Operator.NOT_EQUALS.build(builder, new RootWrapper<>(root, criteriaQuery), formulaRule);
    Predicate.BooleanOperator op = predicate.getOperator();
    assertEquals(BooleanOperator.AND, op, "The expressions should be related by OR operator");

    List<Expression<Boolean>> expressions = predicate.getExpressions();
    assertEquals(8, expressions.size(), "There should be eight expressions built");

    for (Expression<Boolean> e : expressions) {
      NegatedPredicateWrapper negatedPredicateWrapper = (NegatedPredicateWrapper) e;
      boolean isNegated = negatedPredicateWrapper.isNegated();
      Object privatePredicateField =
          FieldUtils.readField(negatedPredicateWrapper, "predicate", true);

      if (expressions.indexOf(e) == 6 || expressions.indexOf(e) == 7) {
        assertTrue(
            privatePredicateField instanceof LikePredicate,
            "The predicate should be Like Predicate");
        LikePredicate lp = (LikePredicate) privatePredicateField;
        assertFalse(lp.isNegated(), "The predicate should be negated for NOT_EQUAL");
      } else {
        assertTrue(
            privatePredicateField instanceof ComparisonPredicate,
            "The predicate should be Comparison Predicate");
        ComparisonPredicate cp = (ComparisonPredicate) privatePredicateField;
        assertEquals(
            cp.getComparisonOperator(isNegated),
            NOT_EQUAL,
            "The predicate was not built with Comparison Operator 'NOT_EQUAL'");
      }
    }
    assertNotNull(predicate.isNotNull());
  }

  @Test
  void shouldCreateEqualsPlacementOperatorForPlacementTypeAttr() throws IllegalAccessException {

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          EntityManager entityManager = entityManagerFactory.createEntityManager();
          CriteriaBuilder builder = entityManager.getCriteriaBuilder();
          CriteriaQuery<RuleFormulaPositionView> criteriaQuery =
              builder.createQuery(RuleFormulaPositionView.class);
          Root<RuleFormulaPositionView> root = criteriaQuery.from(RuleFormulaPositionView.class);
          root.join("site", JoinType.LEFT);

          SimpleAttribute formulaRule =
              new SimpleAttribute(
                  FormulaAttributeInfo.PLACEMENT_TYPE,
                  null,
                  "Inter%,video, BANNER, NATIVE, Default");
          Predicate predicate =
              Operator.EQUALS.build(builder, new RootWrapper<>(root, criteriaQuery), formulaRule);
          Predicate.BooleanOperator op = predicate.getOperator();
          assertEquals(op, BooleanOperator.OR, "The expressions should be related by OR operator");

          List<Expression<Boolean>> expressions = predicate.getExpressions();
          assertEquals(5, expressions.size(), "There should be five expressions built");

          for (Expression<Boolean> e : expressions) {
            NegatedPredicateWrapper negatedPredicateWrapper = (NegatedPredicateWrapper) e;
            boolean isNegated = negatedPredicateWrapper.isNegated();
            Object privatePredicateField =
                FieldUtils.readField(negatedPredicateWrapper, "predicate", true);

            if (expressions.indexOf(e) == 0) {
              assertTrue(
                  privatePredicateField instanceof LikePredicate,
                  "The predicate should be Like Predicate");
            } else if (expressions.indexOf(e) == 1 || expressions.indexOf(e) == 4) {
              assertTrue(
                  privatePredicateField instanceof NullnessPredicate,
                  "The predicate should be Nullness Predicate");
              NullnessPredicate np = (NullnessPredicate) privatePredicateField;
              assertTrue(!np.isNegated(), "The predicate was not built with not-negated");
            } else if (expressions.indexOf(e) == 2 || expressions.indexOf(e) == 3) {
              assertTrue(
                  privatePredicateField instanceof ComparisonPredicate,
                  "The predicate should be Comparison Predicate");
              ComparisonPredicate cp = (ComparisonPredicate) privatePredicateField;
              assertEquals(
                  cp.getComparisonOperator(isNegated),
                  EQUAL,
                  "The predicate was not built with Comparison Operator 'EQUAL'");
            }
          }
          assertNotNull(predicate.isNotNull());
        });
  }

  @Test
  void shouldCreateNotEqualsPlacementOperatorForSiteNameAttr() throws IllegalAccessException {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RuleFormulaPositionView> criteriaQuery =
        builder.createQuery(RuleFormulaPositionView.class);
    Root<RuleFormulaPositionView> root = criteriaQuery.from(RuleFormulaPositionView.class);
    root.join("site", JoinType.LEFT);

    SimpleAttribute formulaRule =
        new SimpleAttribute(
            FormulaAttributeInfo.SITE_NAME,
            null,
            "Site with spaces,Site123-skaSJl, ashkaska aaslall%, %kskk_snasm");
    Predicate predicate =
        Operator.NOT_EQUALS.build(builder, new RootWrapper<>(root, criteriaQuery), formulaRule);
    Predicate.BooleanOperator op = predicate.getOperator();
    assertEquals(op, BooleanOperator.AND, "The expressions should be related by OR operator");

    List<Expression<Boolean>> expressions = predicate.getExpressions();
    assertEquals(4, expressions.size(), "There should be four expressions built");

    for (Expression<Boolean> e : expressions) {
      NegatedPredicateWrapper negatedPredicateWrapper = (NegatedPredicateWrapper) e;
      boolean isNegated = negatedPredicateWrapper.isNegated();
      Object privatePredicateField =
          FieldUtils.readField(negatedPredicateWrapper, "predicate", true);
      if (expressions.indexOf(e) == 2 || (expressions.indexOf(e) == 3)) {
        assertTrue(
            privatePredicateField instanceof LikePredicate,
            "The predicate should be Like Predicate");
        LikePredicate lp = (LikePredicate) privatePredicateField;
        assertFalse(lp.isNegated(), "The predicate should be negated for NOT_EQUAL");
      } else {
        assertTrue(
            privatePredicateField instanceof ComparisonPredicate,
            "The predicate should be Comparison Predicate");
        ComparisonPredicate cp = (ComparisonPredicate) privatePredicateField;
        assertEquals(
            cp.getComparisonOperator(isNegated),
            NOT_EQUAL,
            "The predicate was not built with Comparison Operator 'NOT_EQUAL'");
      }
    }
    assertNotNull(predicate.isNotNull());
  }

  @Test
  void shouldCreateContainsPlacementOperatorForSiteTypeAttr() {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RuleFormulaPositionView> criteriaQuery =
        builder.createQuery(RuleFormulaPositionView.class);
    Root<RuleFormulaPositionView> root = criteriaQuery.from(RuleFormulaPositionView.class);
    root.join("site", JoinType.LEFT);

    SimpleAttribute formulaRule =
        new SimpleAttribute(
            FormulaAttributeInfo.SITE_TYPE, null, "MOBILE_WEB, SDK, Java Script, Desktop");
    Predicate predicate =
        Operator.CONTAINS.build(builder, new RootWrapper<>(root, criteriaQuery), formulaRule);

    Predicate.BooleanOperator op = predicate.getOperator();
    assertEquals(BooleanOperator.OR, op, "The expressions should be related by OR operator");
    assertNotNull(predicate.isNotNull());

    List<Expression<Boolean>> expressions = predicate.getExpressions();
    assertEquals(4, expressions.size(), "size of expressions did not match");

    List<String> expected_Patterns = new ArrayList<>();
    expected_Patterns.add("%" + escapeForLike("MOBILE_WEB") + "%");
    expected_Patterns.add("%SDK%");
    expected_Patterns.add("%Java Script%");
    expected_Patterns.add("%Desktop%");

    for (int i = 0; i < expressions.size(); i++) {
      assertTrue(
          expressions.get(i) instanceof LikePredicate, "The predicate should be Like Predicate");
      LikePredicate lp = (LikePredicate) expressions.get(i);
      assertFalse(lp.isNegated(), "The predicate should not be negated for CONTAINS");
      LiteralExpression<String> patternExpression = (LiteralExpression<String>) lp.getPattern();
      assertTrue(
          patternExpression.getLiteral().startsWith("%"),
          "The Predicate expression for contains should start with %");
      assertTrue(
          patternExpression.getLiteral().endsWith("%"),
          "The Predicate expression for contains should end with %");
      assertEquals(
          expected_Patterns.get(i), patternExpression.getLiteral(), "pattern did not match");
    }
  }

  @Test
  void shouldCreateNotContainsPlacementOperatorForPlacementNameAttr()
      throws IllegalAccessException {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RuleFormulaPositionView> criteriaQuery =
        builder.createQuery(RuleFormulaPositionView.class);
    Root<RuleFormulaPositionView> root = criteriaQuery.from(RuleFormulaPositionView.class);
    root.join("site", JoinType.LEFT);

    SimpleAttribute formulaRule =
        new SimpleAttribute(
            FormulaAttributeInfo.PLACEMENT_NAME,
            null,
            "Header 2,Body,Footer, header_20, body, fo-oter");
    Predicate predicate =
        Operator.NOT_CONTAINS.build(builder, new RootWrapper<>(root, criteriaQuery), formulaRule);

    Predicate.BooleanOperator op = predicate.getOperator();
    assertEquals(BooleanOperator.AND, op, "The expressions should be related by OR operator");
    assertNotNull(predicate.isNotNull());

    List<Expression<Boolean>> expressions = predicate.getExpressions();
    assertEquals(6, expressions.size(), "size of expressions did not match");

    List<String> expected_Patterns = new ArrayList<>();
    expected_Patterns.add("%Header 2%");
    expected_Patterns.add("%Body%");
    expected_Patterns.add("%Footer%");
    expected_Patterns.add("%" + escapeForLike("header_20") + "%");
    expected_Patterns.add("%body%");
    expected_Patterns.add("%fo-oter%");

    for (int i = 0; i < expressions.size(); i++) {
      NegatedPredicateWrapper negatedPredicateWrapper =
          (NegatedPredicateWrapper) expressions.get(i);
      Object privatePredicateField =
          FieldUtils.readField(negatedPredicateWrapper, "predicate", true);
      assertTrue(
          privatePredicateField instanceof LikePredicate, "The predicate should be Like Predicate");
      assertTrue(
          negatedPredicateWrapper.isNegated(), "The predicate should be negated for NOT_CONTAINS");
      LikePredicate lp = (LikePredicate) privatePredicateField;
      LiteralExpression<String> patternExpression = (LiteralExpression<String>) lp.getPattern();
      assertTrue(
          patternExpression.getLiteral().startsWith("%"),
          "The Predicate expression for contains should start with %");
      assertTrue(
          patternExpression.getLiteral().endsWith("%"),
          "The Predicate expression for contains should end with %");
      assertEquals(
          expected_Patterns.get(i), patternExpression.getLiteral(), "pattern did not match");
    }
  }

  @Test
  void shouldCreateContainsPlacementOperatorForPlacementTypeAttr() {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RuleFormulaPositionView> criteriaQuery =
        builder.createQuery(RuleFormulaPositionView.class);
    Root<RuleFormulaPositionView> root = criteriaQuery.from(RuleFormulaPositionView.class);
    root.join("site", JoinType.LEFT);

    SimpleAttribute formulaRule =
        new SimpleAttribute(
            FormulaAttributeInfo.PLACEMENT_TYPE,
            null,
            "Interstitial,video, Banner , Native, Default");
    Predicate predicate =
        Operator.CONTAINS.build(builder, new RootWrapper<>(root, criteriaQuery), formulaRule);

    Predicate.BooleanOperator op = predicate.getOperator();
    assertEquals(BooleanOperator.OR, op, "The expressions should be related by OR operator");
    assertNotNull(predicate.isNotNull());

    List<Expression<Boolean>> expressions = predicate.getExpressions();
    assertEquals(5, expressions.size(), "size of expressions did not match");

    List<String> expected_Patterns = new ArrayList<>();
    expected_Patterns.add("%Interstitial%");
    expected_Patterns.add("%video%");
    expected_Patterns.add("%Banner%");
    expected_Patterns.add("%Native%");
    expected_Patterns.add("%Default%");

    for (int i = 0; i < expressions.size(); i++) {
      assertTrue(
          expressions.get(i) instanceof LikePredicate, "The predicate should be Like Predicate");
      LikePredicate lp = (LikePredicate) expressions.get(i);
      assertFalse(lp.isNegated(), "The predicate should not be negated for CONTAINS");
      LiteralExpression<String> patternExpression = (LiteralExpression<String>) lp.getPattern();
      assertTrue(
          patternExpression.getLiteral().startsWith("%"),
          "The Predicate expression for contains should start with %");
      assertTrue(
          patternExpression.getLiteral().endsWith("%"),
          "The Predicate expression for contains should end with %");
      assertEquals(
          expected_Patterns.get(i), patternExpression.getLiteral(), "pattern did not match");
    }
  }

  @Test
  void shouldCreateNotContainsPlacementOperatorSiteNameAttr() throws IllegalAccessException {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RuleFormulaPositionView> criteriaQuery =
        builder.createQuery(RuleFormulaPositionView.class);
    Root<RuleFormulaPositionView> root = criteriaQuery.from(RuleFormulaPositionView.class);
    root.join("site", JoinType.LEFT);

    SimpleAttribute formulaRule =
        new SimpleAttribute(
            FormulaAttributeInfo.SITE_NAME,
            null,
            "Site with spaces, Site123-skaSJl, ashk&$#@*!+-_aska aaslall79980, TGSJskskk_snasm,");
    Predicate predicate =
        Operator.NOT_CONTAINS.build(builder, new RootWrapper<>(root, criteriaQuery), formulaRule);

    Predicate.BooleanOperator op = predicate.getOperator();
    assertEquals(BooleanOperator.AND, op, "The expressions should be related by OR operator");
    assertNotNull(predicate.isNotNull());

    List<Expression<Boolean>> expressions = predicate.getExpressions();
    assertEquals(4, expressions.size(), "size of expressions did not match");

    List<String> expected_Patterns = new ArrayList<>();
    expected_Patterns.add("%Site with spaces%");
    expected_Patterns.add("%Site123-skaSJl%");
    expected_Patterns.add("%" + escapeForLike("ashk&$#@*!+-_aska aaslall79980") + "%");
    expected_Patterns.add("%" + escapeForLike("TGSJskskk_snasm") + "%");

    for (int i = 0; i < expressions.size(); i++) {
      NegatedPredicateWrapper negatedPredicateWrapper =
          (NegatedPredicateWrapper) expressions.get(i);
      Object privatePredicateField =
          FieldUtils.readField(negatedPredicateWrapper, "predicate", true);
      assertTrue(
          privatePredicateField instanceof LikePredicate, "The predicate should be Like Predicate");
      LikePredicate lp = (LikePredicate) privatePredicateField;
      assertTrue(
          negatedPredicateWrapper.isNegated(), "The predicate should be negated for NOT CONTAINS");
      LiteralExpression<String> patternExpression = (LiteralExpression<String>) lp.getPattern();
      assertTrue(
          patternExpression.getLiteral().startsWith("%"),
          "The Predicate expression for contains should start with %");
      assertTrue(
          patternExpression.getLiteral().endsWith("%"),
          "The Predicate expression for contains should end with %");
      assertEquals(
          expected_Patterns.get(i), patternExpression.getLiteral(), "pattern did not match");
    }
  }
}
