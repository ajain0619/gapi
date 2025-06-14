package com.nexage.admin.core.repository;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.io.Resources.getResource;
import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE;
import static com.nexage.admin.core.enums.RuleType.BRAND_PROTECTION;
import static com.nexage.admin.core.model.placementformula.formula.impl.FormulaAttributeInfo.LONG_FORM;
import static com.nexage.admin.core.model.placementformula.formula.impl.FormulaAttributeInfo.PLACEMENT_NAME;
import static com.nexage.admin.core.model.placementformula.formula.impl.FormulaAttributeInfo.PLACEMENT_TYPE;
import static com.nexage.admin.core.model.placementformula.formula.impl.FormulaAttributeInfo.SITE_IAB_CATEGORY;
import static com.nexage.admin.core.model.placementformula.formula.impl.FormulaAttributeInfo.SITE_NAME;
import static com.nexage.admin.core.model.placementformula.formula.impl.Operator.CONTAINS;
import static com.nexage.admin.core.model.placementformula.formula.impl.Operator.EQUALS;
import static com.nexage.admin.core.model.placementformula.formula.impl.Operator.MEMBER_OF;
import static com.nexage.admin.core.model.placementformula.formula.impl.Operator.NOT_EQUALS;
import static com.nexage.admin.core.model.placementformula.formula.impl.Operator.NOT_MEMBER_OF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleActionType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleFormula;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.RuleIntendedAction;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.model.placementformula.formula.impl.FormulaAttributeInfo;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import com.nexage.admin.core.model.placementformula.formula.impl.GroupOperator;
import com.nexage.admin.core.model.placementformula.formula.impl.PlacementFormulaPredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.impl.SimpleAttribute;
import com.nexage.admin.core.specification.RuleSpecification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Sql(scripts = "/data/repository/rule-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class RuleRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final String RULE_NAME_ONE = "Test Rule One";
  private static final String RULE_NAME_TWO = "Test Rule Two";
  private static final String TARGET_DATA = "Test Data One";

  @Autowired private RuleRepository ruleRepository;
  @Autowired private RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository;

  @Autowired private RuleDeployedCompanyRepository ruleDeployedCompanyRepository;

  @Test
  void test_createSellingRule() throws IOException {
    CompanyRule rule = getRule(Status.ACTIVE);
    rule = ruleRepository.save(rule);
    RuleTarget target = rule.getRuleTargets().stream().findFirst().orElse(null);
    assertEquals(RULE_NAME_ONE, rule.getName());
    assertEquals(Status.ACTIVE, rule.getStatus());
    assertEquals(BRAND_PROTECTION, rule.getRuleType());

    assertNotNull(rule.getRuleIntendedActions());
    assertEquals(1, rule.getRuleIntendedActions().size());
    RuleIntendedAction action = rule.getRuleIntendedActions().iterator().next();
    assertEquals(RuleActionType.FLOOR, action.getActionType());
    assertEquals("1.0", action.getActionData());

    assertNotNull(target);
    assertEquals(MatchType.INCLUDE_LIST, target.getMatchType());
    assertEquals(RuleTargetType.AD_SIZE, target.getRuleTargetType());
    assertEquals(TARGET_DATA, target.getData());
    assertEquals(Status.ACTIVE, target.getStatus());
    assertTrue(rule.getRuleFormula().isAutoUpdate(), "getAutoUpdate is false");
    assertEquals(getFormula(), rule.getRuleFormula().getFormula());
  }

  @Test
  void test_findSellingRule() throws IOException {
    CompanyRule rule = ruleRepository.findActualByPid(1L).orElse(null);

    RuleTarget target =
        Objects.requireNonNull(rule).getRuleTargets().stream().findFirst().orElse(null);
    assertEquals(RULE_NAME_TWO, rule.getName());
    assertEquals(Status.ACTIVE, rule.getStatus());
    assertEquals(BRAND_PROTECTION, rule.getRuleType());

    assertNotNull(target);
    assertEquals(MatchType.INCLUDE_LIST, target.getMatchType());
    assertEquals(RuleTargetType.COUNTRY, target.getRuleTargetType());
    assertEquals(TARGET_DATA, target.getData());
    assertEquals(Status.ACTIVE, target.getStatus());
    assertTrue(rule.getRuleFormula().isAutoUpdate(), "getAutoUpdate is false");
    assertEquals(getFormula(), rule.getRuleFormula().getFormula());
  }

  @Test
  void test_updateSellingRule_withCompanyDeployment() {
    CompanyRule rule = ruleRepository.findActualByPid(1L).orElse(null);
    Objects.requireNonNull(rule).getDeployedCompanies().add(getTestDeployedCompany(1L));
    rule = ruleRepository.save(rule);

    assertNotNull(rule.getDeployedCompanies());
    assertTrue(rule.getDeployedCompanies().size() > 0);
  }

  @Test
  void test_deleteSellingRule() {
    ruleRepository.deleteById(1L);
    assertEquals(Optional.empty(), ruleRepository.findActualByPid(1L));
  }

  @Test
  void test_findRulesUpdateableWithNewlyApplicablePlacements() {
    List<Long> list = ruleRepository.findRulesUpdateableWithNewlyApplicablePlacements();
    assertEquals(1, list.size());
    assertNotNull(list.get(0));
    assertEquals(1L, (long) list.get(0));
  }

  @Test
  void test_findRulePositions_Attribute_site_name_Operator_CONTAINS() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_NAME, CONTAINS, "me."))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals("me.com", resultList.get(0).getSite().getName());
    assertEquals(Type.MOBILE_WEB, resultList.get(0).getSite().getType());
  }

  @Test
  void test_findRulePositions_Attribute_site_name_Assert_2_placements() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR).addGroup().build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(2, resultList.size());
    assertEquals("position1", resultList.get(0).getName());
    assertEquals(NATIVE, resultList.get(0).getType()); // NATIVE(3)
    assertEquals(Integer.valueOf(200), resultList.get(0).getHeight());
    assertEquals(Integer.valueOf(300), resultList.get(0).getWidth());
    assertEquals("position2", resultList.get(1).getName());
    assertEquals(INSTREAM_VIDEO, resultList.get(1).getType()); // INSTREAM_VIDEO(4)
    assertEquals(Integer.valueOf(400), resultList.get(1).getHeight());
    assertEquals(Integer.valueOf(800), resultList.get(1).getWidth());
  }

  @ParameterizedTest
  @CsvSource({"1, me.", "99999, me.", " , me."})
  void test_findRulePositions_company_pid_negative(ArgumentsAccessor arguments) {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_NAME, CONTAINS, arguments.getString(1)))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(
                Collections.singleton(arguments.get(0, Long.class)), placementFormula));
    assertEquals(0, resultList.size()); // site's correct company_pid is 2L
  }

  @Test
  void test_findRulePositionsForDeals_Companies() {
    List<Long> twoCompanies = new ArrayList<>();
    twoCompanies.add(1L);
    twoCompanies.add(2L);

    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_NAME, CONTAINS, "me."))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(twoCompanies, placementFormula));
    assertEquals(2, resultList.size());

    List<Long> threeCompanies = new ArrayList<>();
    threeCompanies.add(1L);
    threeCompanies.add(2L);
    threeCompanies.add(3L);

    List<RuleFormulaPositionView> resultList1 =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(threeCompanies, placementFormula));
    assertEquals(4, resultList1.size());

    List<Long> oneCompany = new ArrayList<>();
    oneCompany.add(1L);

    List<RuleFormulaPositionView> resultList2 =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(oneCompany, placementFormula));
    assertEquals(0, resultList2.size());

    List<Long> emptyCompaniesList = new ArrayList<>();

    List<RuleFormulaPositionView> resultList3 =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(emptyCompaniesList, placementFormula));
    assertEquals(4, resultList3.size());
  }

  @Test
  void test_findRulePositionsForDeals_Attribute_site_name_Operator_CONTAINS_negative() {
    List<Long> threeCompanies = new ArrayList<>();
    threeCompanies.add(1L);
    threeCompanies.add(2L);
    threeCompanies.add(3L);

    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_NAME, CONTAINS, "mexx."))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(threeCompanies, placementFormula));
    assertEquals(0, resultList.size());
  }

  @Test
  void test_findRulePositions_Attribute_site_name_Operator_CONTAINS_negative() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_NAME, CONTAINS, "mexx."))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(0, resultList.size());
  }

  @Test
  void test_findRulePositions_Attribute_placement_name_Operator_EQUALS() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(PLACEMENT_NAME, EQUALS, "position1"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(1, resultList.size());
    assertEquals("position1", resultList.get(0).getName());
  }

  @Test
  void test_findRulePositions_Attribute_placement_name_Operator_EQUALS_negative() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(PLACEMENT_NAME, EQUALS, "positionXX"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(0, resultList.size());
  }

  @Test
  void test_findRulePositions_Attribute_site_type_Operator_NOT_EQUALS() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(
                new SimpleAttribute(FormulaAttributeInfo.SITE_TYPE, NOT_EQUALS, "APPLICATION"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(2, resultList.size());
    assertEquals("me.com", resultList.get(0).getSite().getName());
    assertEquals("me.com", resultList.get(1).getSite().getName());
    assertEquals("position1", resultList.get(0).getName());
    assertEquals("position2", resultList.get(1).getName());
  }

  @Test
  void test_findRulePositions_Attribute_site_type_Operator_NOT_EQUALS_negative() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(FormulaAttributeInfo.SITE_TYPE, NOT_EQUALS, "MOBILE_WEB"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(0, resultList.size());
  }

  @Test
  void shouldReturnAllSitesWithAndroidPlatform() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(FormulaAttributeInfo.SITE_TYPE, EQUALS, "ANDROID"))
            .build();

    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(3L), placementFormula));

    assertEquals(3, resultList.size());
    assertEquals("android_phonetab_site_banner", resultList.get(0).getName());
    assertEquals("android_site_banner", resultList.get(1).getName());
    assertEquals("android_tab_site_banner", resultList.get(2).getName());
  }

  @Test
  void shouldReturnAllSitesWithIOSPlatform() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(FormulaAttributeInfo.SITE_TYPE, EQUALS, "IOS"))
            .build();

    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(3L), placementFormula));

    assertEquals(3, resultList.size());
    assertEquals("ipad_iphone_site_banner", resultList.get(0).getName());
    assertEquals("iphone_site_banner", resultList.get(1).getName());
    assertEquals("ipad_site_banner", resultList.get(2).getName());
  }

  @Test
  void shouldReturnAllSitesWithCTVPlatform() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(FormulaAttributeInfo.SITE_TYPE, EQUALS, "CTV_OTT"))
            .build();

    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(3L), placementFormula));

    assertEquals(1, resultList.size());
    assertEquals("ctv_site_banner", resultList.get(0).getName());
  }

  @Test
  void shouldReturnAllSitesWithApplicationType() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(FormulaAttributeInfo.SITE_TYPE, EQUALS, "APPLICATION"))
            .build();

    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(3L), placementFormula));

    assertEquals(7, resultList.size());
    assertEquals("android_phonetab_site_banner", resultList.get(0).getName());
    assertEquals("android_site_banner", resultList.get(1).getName());
    assertEquals("android_tab_site_banner", resultList.get(2).getName());
    assertEquals("ipad_iphone_site_banner", resultList.get(3).getName());
    assertEquals("iphone_site_banner", resultList.get(4).getName());
    assertEquals("ipad_site_banner", resultList.get(5).getName());
    assertEquals("ctv_site_banner", resultList.get(6).getName());
  }

  @Test
  void test_findRulePositions_Attribute_site_type_Operator_NOT_EQUALS_invalid_enum_type() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(
                new SimpleAttribute(
                    FormulaAttributeInfo.SITE_TYPE, NOT_EQUALS, "INVALID_NONEXISTENT_TYPE"))
            .build();

    Specification<RuleFormulaPositionView> resultSpecification =
        RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula);

    // method under test
    assertThrows(
        InvalidDataAccessApiUsageException.class,
        () -> ruleFormulaPositionViewRepository.findAll(resultSpecification));
  }

  @Test
  void test_findRulePositions_Attribute_placement_type_Operator_EQUALS() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(PLACEMENT_TYPE, EQUALS, "INSTREAM_VIDEO"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(1, resultList.size());
    assertEquals("position2", resultList.get(0).getName());
  }

  @Test
  void test_findRulePositions_Attribute_placement_type_Operator_EQUALS_negative() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(PLACEMENT_TYPE, EQUALS, "BANNER"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(0, resultList.size());
  }

  @Test
  void test_findRulePositions_Attribute_placement_type_Operator_EQUALS_invalid_enum_type() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(PLACEMENT_TYPE, EQUALS, "XXINSTREAM_VIDEO"))
            .build();

    Specification<RuleFormulaPositionView> resultSpecification =
        RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula);

    // method under test
    assertThrows(
        InvalidDataAccessApiUsageException.class,
        () -> ruleFormulaPositionViewRepository.findAll(resultSpecification));
  }

  @Test
  void test_findRulePositions_2_Rules() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(
                new SimpleAttribute(PLACEMENT_TYPE, EQUALS, "INSTREAM_VIDEO"),
                new SimpleAttribute(FormulaAttributeInfo.SITE_TYPE, EQUALS, "MOBILE_WEB"))
            .addGroup(new SimpleAttribute(PLACEMENT_TYPE, EQUALS, "INSTREAM_VIDEO"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(1, resultList.size());
    assertEquals("position2", resultList.get(0).getName());
  }

  @Test
  void test_findRulePositions_2_Rules_negative() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(
                new SimpleAttribute(PLACEMENT_TYPE, EQUALS, "INSTREAM_VIDEO"),
                new SimpleAttribute(PLACEMENT_NAME, EQUALS, "positionXX"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(0, resultList.size());
  }

  @Test
  void test_findRulePositions_2_OR_Groups() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(PLACEMENT_TYPE, EQUALS, "INSTREAM_VIDEO"))
            .addGroup(new SimpleAttribute(PLACEMENT_NAME, EQUALS, "position1"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(2, resultList.size());
    assertEquals("position1", resultList.get(0).getName());
    assertEquals("position2", resultList.get(1).getName());
    assertEquals(INSTREAM_VIDEO, resultList.get(1).getType());
  }

  @Test
  void test_findRulePositions_2_OR_Groups_negative() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(PLACEMENT_TYPE, EQUALS, "BANNER"))
            .addGroup(new SimpleAttribute(PLACEMENT_NAME, EQUALS, "positionXx"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(0, resultList.size());
  }

  @Test
  void test_findRulePositions_Attribute_2_placement_type_Operator_EQUALS() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(PLACEMENT_TYPE, EQUALS, "INSTREAM_VIDEO,NATIVE"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(2, resultList.size());
    assertEquals(NATIVE, resultList.get(0).getType());
    assertEquals(INSTREAM_VIDEO, resultList.get(1).getType());
  }

  @Test
  void test_findRulePositions_Attribute_2_placement_type_Operator_NOT_EQUALS() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(
                new SimpleAttribute(
                    PLACEMENT_TYPE,
                    NOT_EQUALS,
                    "NATIVE,BANNER")) // NOTE: (not equals NATIVE) or (not equals BANNER))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(1, resultList.size());
    assertEquals(INSTREAM_VIDEO, resultList.get(0).getType()); // selected because not equals NATIVE
  }

  @Test
  void test_findRulePositions_Attribute_2_placement_type_Operator_NOT_EQUALS_contrived_case() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(
                new SimpleAttribute(
                    PLACEMENT_TYPE,
                    NOT_EQUALS,
                    "NATIVE,NATIVE")) // NOTE: (not equals NATIVE) or (not equals NATIVE)
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(1, resultList.size());
    assertEquals(INSTREAM_VIDEO, resultList.get(0).getType()); // selected because not equals NATIVE
  }

  @Test
  void test_findRulePositions_Attribute_2_placement_name_Operator_EQUALS() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(PLACEMENT_NAME, EQUALS, "position1, position2"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(2, resultList.size());
    assertEquals("position1", resultList.get(0).getName());
    assertEquals("position2", resultList.get(1).getName());
  }

  @Test
  void test_findRulePositions_Attribute_2_placement_name_Operator_NOT_EQUALS() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(
                new SimpleAttribute(
                    PLACEMENT_NAME,
                    NOT_EQUALS,
                    "position1,position2")) // NOTE: (not equals position1) or (not equals
            // position2)
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertTrue(resultList.isEmpty());
  }

  @Test
  void test_findRulePositions_Attribute_2_placement_name_Operator_NOT_EQUALS_contrived_case() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(
                new SimpleAttribute(
                    PLACEMENT_NAME,
                    NOT_EQUALS,
                    "position1,position1")) // NOTE: (not equals position1) or (not equals
            // position1)
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(1, resultList.size());
    assertEquals("position2", resultList.get(0).getName()); // selected because not equals position1
  }

  @Test
  void test_findRulePositions_iabCategories() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_IAB_CATEGORY, MEMBER_OF, "IAB1"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(2, resultList.size());
    assertEquals(1L, resultList.get(0).getSite().getPid());
    assertEquals(1L, resultList.get(1).getSite().getPid());
  }

  @Test
  void shouldReturnOneResultForLongFormTrue() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(LONG_FORM, EQUALS, "true"))
            .build();

    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(1, resultList.size());
    assertEquals(1L, resultList.get(0).getPid());
    assertTrue(resultList.get(0).getPlacementVideoView().isLongform());
  }

  @Test
  void shouldReturnOneResultForLongFormFalse() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(LONG_FORM, EQUALS, "false"))
            .build();

    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(1, resultList.size());
    assertEquals(2L, resultList.get(0).getPid());
    assertFalse(resultList.get(0).getPlacementVideoView().isLongform());
  }

  @Test
  void test_findRulePositions_iabCategories_multiple() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_IAB_CATEGORY, MEMBER_OF, "IAB1,IAB3"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(2, resultList.size());
    assertEquals(1L, resultList.get(0).getSite().getPid());
    assertEquals(1L, resultList.get(1).getSite().getPid());
  }

  @Test
  void test_findRulePositions_iabCategories_empty() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_IAB_CATEGORY, MEMBER_OF, "IAB4,IAB3"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(0, resultList.size());
  }

  @Test
  void test_findRulePositions_iabCategories_notContains() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_IAB_CATEGORY, NOT_MEMBER_OF, "IAB1"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(0, resultList.size());
  }

  @Test
  void test_findRulePositions_iabCategories_notContains_empty() {
    Group<RuleFormulaPositionView> placementFormula =
        PlacementFormulaPredicateBuilder.betweenGroups(GroupOperator.OR)
            .addGroup(new SimpleAttribute(SITE_IAB_CATEGORY, NOT_MEMBER_OF, "IAB4,IAB5"))
            .build();

    // method under test
    List<RuleFormulaPositionView> resultList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(Collections.singleton(2L), placementFormula));
    assertEquals(2, resultList.size());
    assertEquals(1L, resultList.get(0).getSite().getPid());
    assertEquals(1L, resultList.get(1).getSite().getPid());
  }

  @Test
  void shouldReturnCompanyRuleWhenFindActualByPidAndRuleType() {
    CompanyRule companyRule =
        ruleRepository.findByPidAndRuleType(1L, BRAND_PROTECTION).orElse(null);

    assertNotNull(companyRule);
    assertEquals(BRAND_PROTECTION, companyRule.getRuleType());
  }

  @Test
  void shouldReturnCompanyRuleListWhenFindAllActiveDealRulesAssosiatedWithDeal() {
    List<CompanyRule> companyRules = ruleRepository.findAllActiveDealRulesAssosiatedWithDeal(1L);

    assertNotNull(companyRules);
    assertEquals(1, companyRules.size());
  }

  private CompanyRule getRule(Status status) throws IOException {
    CompanyRule rule = new CompanyRule();
    rule.setStatus(status);
    rule.setRuleIntendedActions(
        newHashSet(new RuleIntendedAction(rule, RuleActionType.FLOOR, "1.0")));
    rule.setName(RULE_NAME_ONE);
    rule.setRuleTargets(getRuleTarget(rule));
    rule.setOwnerCompanyPid(1L);
    rule.setRuleType(BRAND_PROTECTION);
    rule.setRuleFormula(getRuleFormula(rule));
    return rule;
  }

  private RuleFormula getRuleFormula(CompanyRule rule) throws IOException {
    RuleFormula formula = new RuleFormula();
    formula.setAutoUpdate(true);
    formula.setRule(rule);
    formula.setFormula(getFormula());
    return formula;
  }

  private String getFormula() throws IOException {
    return Resources.toString(getResource("placementFormula.json"), Charsets.UTF_8);
  }

  private Set<RuleTarget> getRuleTarget(CompanyRule rule) {
    RuleTarget target = new RuleTarget();
    target.setData(TARGET_DATA);
    target.setStatus(Status.ACTIVE);
    target.setRuleTargetType(RuleTargetType.AD_SIZE);
    target.setMatchType(MatchType.INCLUDE_LIST);
    target.setRule(rule);
    return Sets.newHashSet(target);
  }

  private RuleDeployedCompany getTestDeployedCompany(Long pid) {
    return ruleDeployedCompanyRepository.findById(pid).orElse(null);
  }
}
