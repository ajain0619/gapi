package com.nexage.admin.core.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.Rule;
import com.nexage.admin.core.specification.CompanyRuleSpecification;
import com.nexage.admin.core.specification.SellerRuleQueryFieldSpecification;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(scripts = "/data/repository/rule-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class CompanyRuleRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final String RULE_3_NAME = "rule name 3";
  private static final Long RULE_3_OWNER_COMPANY_PID = 3L;
  private static final Long RULE_4_OWNER_COMPANY_PID = 4L;
  private static final Long RULE_3_PID = 3L;
  private static final Long RULE_6_PID = 6L;
  private static final Long RULE_7_PID = 7L;
  private static final Long RULE_13_PID = 13L;
  private static final Long RULE_6_OWNER_COMPANY_PID = 6L;
  private static final String PID = "pid";
  private static final String NAME = "name";
  private static final long RULE_4_PID = 4L;
  private static final long PLACEMENT_PID_11 = 11L;
  private static final long LIST_PID = 12L;

  @Autowired CompanyRuleRepository companyRuleRepository;

  @Test
  void shouldGetAllPublisherRules() {
    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll();

    // then
    assertEquals(10, rules.size());
    assertThat(
        extractRulePids(rules),
        containsInAnyOrder(1L, RULE_3_PID, RULE_4_PID, 7L, 8L, 9L, 10L, 11L, 12L, 13L));
  }

  @Test
  void shouldGetOnePublisherRule() {
    // when
    Optional<CompanyRule> rule = companyRuleRepository.findById(RULE_3_PID);

    // then
    assertTrue(rule.isPresent());
    assertEquals(RULE_3_NAME, rule.get().getName());
  }

  @Test
  void shouldFindRulesUsingSpecificationNameSearch() {
    // given
    Specification<CompanyRule> spec =
        CompanyRuleSpecification.withQuery(
            RULE_4_PID, EnumSet.of(RuleType.BRAND_PROTECTION), null, Set.of(NAME), "name 3");

    // when
    List<CompanyRule> rules = companyRuleRepository.findAll(spec);

    // then
    assertEquals(1, rules.size());
    assertEquals(RULE_3_NAME, rules.get(0).getName());
  }

  @Test
  void shouldFindRulesUsingSpecificationPidSearch() {
    // given
    Specification<CompanyRule> spec =
        CompanyRuleSpecification.withQuery(
            RULE_4_PID, EnumSet.of(RuleType.BRAND_PROTECTION), null, Set.of(PID), "3");

    // when
    List<CompanyRule> rules = companyRuleRepository.findAll(spec);

    // then
    assertEquals(1, rules.size());
    assertEquals(RULE_3_PID, rules.get(0).getPid());
  }

  @Test
  void shouldFindRulesUsingSpecificationPidNameSearchAndNumericTerm() {
    // given
    Specification<CompanyRule> spec =
        CompanyRuleSpecification.withQuery(
            RULE_4_PID,
            EnumSet.of(RuleType.BRAND_PROTECTION),
            null,
            Set.of(PID, NAME),
            "rule name 3");

    // when
    List<CompanyRule> rules = companyRuleRepository.findAll(spec);

    // then
    assertEquals(1, rules.size());
    assertEquals(RULE_3_PID, rules.get(0).getPid());
  }

  @Test
  void shouldFindRulesUsingSpecificationPidNameSearchAndStringTerm() {
    // given
    Specification<CompanyRule> spec =
        CompanyRuleSpecification.withQuery(
            RULE_4_PID, EnumSet.of(RuleType.BRAND_PROTECTION), null, Set.of(PID, NAME), "name 3");

    // when
    List<CompanyRule> rules = companyRuleRepository.findAll(spec);

    // then
    assertEquals(1, rules.size());
    assertEquals(RULE_3_PID, rules.get(0).getPid());
  }

  @Test
  void shouldFindRulesUsingSpecificationPidNameSearchAndEmptyTerm() {
    // given
    Specification<CompanyRule> spec =
        CompanyRuleSpecification.withQuery(
            RULE_4_PID, EnumSet.of(RuleType.BRAND_PROTECTION), null, Set.of(PID, NAME), "");

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(spec);

    // then
    assertEquals(4, rules.size());
    assertEquals(
        Set.of(RULE_3_PID, RULE_4_PID, RULE_7_PID, RULE_13_PID),
        rules.stream().map(CompanyRule::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindRuleByNameAndOwnerCompanyPid() {
    // when
    CompanyRule rule =
        companyRuleRepository.findByNameAndOwnerCompanyPid(RULE_3_NAME, RULE_4_OWNER_COMPANY_PID);

    // then
    assertEquals(RULE_3_PID, rule.getPid());
    assertEquals(RULE_3_NAME, rule.getName());
    assertEquals(RULE_4_OWNER_COMPANY_PID, rule.getOwnerCompanyPid());
  }

  @Test
  void shouldFindRuleByRulePidAndCompanyId() {
    // when
    CompanyRule rule =
        companyRuleRepository.findByPidAndOwnerCompanyPid(RULE_3_PID, RULE_4_OWNER_COMPANY_PID);

    // then
    assertEquals(RULE_3_NAME, rule.getName());
  }

  @Test
  void shouldFindNothingByRulePidAndCompanyId() {
    // when
    CompanyRule rule =
        companyRuleRepository.findByPidAndOwnerCompanyPid(RULE_3_PID, RULE_3_OWNER_COMPANY_PID);

    // then
    assertNull(rule);
  }

  @Test
  void shouldNotFindRuleWithDeletedStatus() {
    // when
    CompanyRule rule =
        companyRuleRepository.findByPidAndOwnerCompanyPid(4L, RULE_6_OWNER_COMPANY_PID);

    // then
    assertNull(rule);
  }

  @Test
  void shouldMarkRuleAsDeleted() {
    // when
    companyRuleRepository.delete(RULE_3_PID);

    // then
    assertTrue(companyRuleRepository.findById(RULE_3_PID).isEmpty());
  }

  @Test
  void shouldRetrieveRuleByPidOwnerCompanyPidAndRuleType() {
    // when
    CompanyRule rule =
        companyRuleRepository.findByPidAndOwnerCompanyPidAndRuleTypeIn(
            1L, 1L, Set.of(RuleType.BRAND_PROTECTION));

    // then
    assertEquals(1L, rule.getPid());
  }

  @Test
  void shouldFailToRetrieveRuleByPidOwnerCompanyPidAndRuleTypeWhenRuleTypeIsWrong() {
    // when
    CompanyRule rule =
        companyRuleRepository.findByPidAndOwnerCompanyPidAndRuleTypeIn(
            1L, 1L, Set.of(RuleType.EXPERIMENTATION));

    // then
    assertNull(rule);
  }

  @Test
  void shouldFindRulesByQueryFieldName() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .ruleName(Optional.of(RULE_3_NAME));

    // when
    List<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(1, rules.size());
    assertEquals(RULE_3_PID, rules.get(0).getPid());
    assertEquals(RULE_3_NAME, rules.get(0).getName());
    assertEquals(RULE_4_OWNER_COMPANY_PID, rules.get(0).getOwnerCompanyPid());
  }

  @Test
  void shouldNotFindRulesByQueryFieldWithNotExitingName() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .ruleName(Optional.of("some_unknown_name"));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertTrue(rules.isEmpty());
  }

  @Test
  void shouldFindRulesByQueryFieldPidsAndIgnoreDeletedOnes() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .rulePids(Optional.of(Set.of(RULE_3_PID, 4L, RULE_6_PID)));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(2, rules.size()); // rule with PID_4 is deleted (status = -1)
    assertThat(extractRulePids(rules), containsInAnyOrder(RULE_3_PID, RULE_4_PID));
  }

  @Test
  void shouldFindRulesByQueryFieldTypesAndIgnoreDeletedOnes() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .types(Optional.of(Set.of(RuleType.BRAND_PROTECTION)));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(4, rules.size()); // rule with PID_4 is deleted (status = -1)
    assertThat(
        extractRulePids(rules),
        containsInAnyOrder(RULE_3_PID, RULE_4_PID, RULE_7_PID, RULE_13_PID));
  }

  @Test
  void shouldNotFindRulesByQueryFieldWithUnknownType() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .types(Optional.of(Set.of(RuleType.DEAL)));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertTrue(rules.isEmpty());
  }

  @Test
  void shouldFindRulesByQueryFieldWithNameAndPids() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .types(Optional.of(Set.of(RuleType.BRAND_PROTECTION)))
            .rulePids(Optional.of(Set.of(RULE_3_PID, 4L, RULE_6_PID)));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(2, rules.size()); // rule with PID_4 is deleted (status = -1)
    assertThat(extractRulePids(rules), containsInAnyOrder(RULE_3_PID, RULE_4_PID));
  }

  @Test
  void shouldNotFindRulesByQueryFieldWithNotMatchingNameAndType() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .types(Optional.of(Set.of(RuleType.EXPERIMENTATION)))
            .ruleName(Optional.of(RULE_3_NAME));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertTrue(rules.isEmpty());
  }

  @Test
  void shouldFindRulesByQueryFieldWithMatchingNameOrType() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .isAndOperator(false)
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .types(Optional.of(Set.of(RuleType.BRAND_PROTECTION)))
            .ruleName(Optional.of(RULE_3_NAME));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(4, rules.size());
    assertThat(
        extractRulePids(rules),
        containsInAnyOrder(RULE_3_PID, RULE_4_PID, RULE_7_PID, RULE_13_PID));
  }

  @Test
  void shouldFindRulesByQueryFieldSitePids() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .sitePids(Optional.of(Set.of(3L)));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(2, rules.size());
    assertThat(extractRulePids(rules), containsInAnyOrder(RULE_3_PID, RULE_4_PID));
  }

  @Test
  void shouldNotFindRulesByQueryFieldSitePids() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_3_OWNER_COMPANY_PID)
            .sitePids(Optional.of(Set.of(4L)));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertTrue(rules.isEmpty());
  }

  @Test
  void shouldFindDistinctRulesByQueryFieldPlacementPids() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .placementPids(Optional.of(Set.of(PLACEMENT_PID_11, 12L)));

    // when
    List<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(1, rules.size());
    assertEquals(RULE_3_PID, rules.get(0).getPid());
  }

  @Test
  void shouldFindRulesByQueryFieldPlacementPidsOmitOrphans() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .placementPids(Optional.of(Set.of(PLACEMENT_PID_11, 12L, 13L)));

    // when
    List<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(1, rules.size()); // RULE_3 is an orphan - does not have company assigned
    assertEquals(RULE_3_PID, rules.get(0).getPid());
  }

  @Test
  void shouldFindRulesByQueryFieldPlacementPids() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .placementPids(Optional.of(Set.of(PLACEMENT_PID_11, 12L, 14L)));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(2, rules.size());
    assertThat(extractRulePids(rules), containsInAnyOrder(RULE_3_PID, 7L));
  }

  @Test
  void shouldFindRulesByQueryFieldTypeAndSiteAndPosition() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .sitePids(Optional.of(Set.of(3L)))
            .placementPids(Optional.of(Set.of(PLACEMENT_PID_11, 12L, 14L)));

    // when
    List<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(1, rules.size());
    assertEquals(RULE_3_PID, rules.get(0).getPid());
  }

  @Test
  void shouldFindRulesByQueryFieldTypeAndSiteAndPositionAndName() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(RULE_4_OWNER_COMPANY_PID)
            .ruleName(Optional.of("7"))
            .sitePids(Optional.of(Set.of(1L)))
            .placementPids(Optional.of(Set.of(PLACEMENT_PID_11, 12L, 14L)));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertTrue(rules.isEmpty());
  }

  @Test
  void shouldNotFindRulesByQueryFieldDeployedForSellerOnly() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(2L)
            .deployedForSeller(Optional.of(Boolean.TRUE));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertTrue(rules.isEmpty());
  }

  @Test
  void shouldFindRulesForSellerWhenQueryFieldIsMissing() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder().sellerPid(5L);

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(4, rules.size());
    assertThat(extractRulePids(rules), containsInAnyOrder(8L, 9L, 10L, 11L));
  }

  @Test
  void shouldFindAllSellerRulesByQueryFieldWithAllAvailableTypesDefined() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .isAndOperator(false)
            .sellerPid(5L)
            .types(Optional.of(Set.of(RuleType.BRAND_PROTECTION)));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(4, rules.size());
    assertThat(extractRulePids(rules), containsInAnyOrder(8L, 9L, 10L, 11L));
  }

  @Test
  void shouldAlwaysFindRulesThatBelongsToAGivenSellerWhenOperatorIsAnd() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(2L)
            .ruleName(Optional.of("unknown_name"));

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertTrue(rules.isEmpty());
  }

  @Test
  void shouldAlwaysFindRulesByQueryFieldWithNameThatBelongsToAGivenSellerWhenOperatorIsOr() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(3L)
            .ruleName(Optional.of(RULE_3_NAME))
            .isAndOperator(false);

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertTrue(rules.isEmpty());
  }

  @Test
  void shouldNotFindRulesForDifferentSellerByQueryFieldWithNameAndTypeWhenOperatorIsOr() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(2L)
            .ruleName(Optional.of(RULE_3_NAME))
            .types(Optional.of(Set.of(RuleType.BRAND_PROTECTION)))
            .isAndOperator(false);

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertTrue(rules.isEmpty());
  }

  @Test
  void shouldFindRulesForGivenSellerByQueryFieldWithNameAndTypeWhenOperatorIsOr() {
    // given
    SellerRuleQueryFieldSpecification.SellerRuleQueryFieldSpecificationBuilder builder =
        SellerRuleQueryFieldSpecification.builder()
            .sellerPid(4L)
            .ruleName(Optional.of("name"))
            .types(Optional.of(Set.of(RuleType.BRAND_PROTECTION)))
            .isAndOperator(false);

    // when
    Collection<CompanyRule> rules = companyRuleRepository.findAll(builder.build());

    // then
    assertEquals(4, rules.size());
    assertThat(extractRulePids(rules), containsInAnyOrder(3L, 4L, 7L, 13L));
  }

  @Test
  void shouldFindAllWithPublisherPidAndName() {
    // given
    Specification<CompanyRule> spec =
        CompanyRuleSpecification.withPublisherPidAndName(1L, "Test Rule Two");

    // when
    Collection<CompanyRule> result = companyRuleRepository.findAll(spec);

    // then
    assertEquals(Set.of(1L), result.stream().map(CompanyRule::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWithQueryFieldPidAndQueryTermString() {
    // given
    long publisherPid = 1L;
    Set<RuleType> ruleSet = EnumSet.of(RuleType.BRAND_PROTECTION);
    Set<Status> statusSet = EnumSet.of(Status.ACTIVE, Status.INACTIVE);
    Set<String> queryFields = Set.of("pid");
    String queryTerm = "foo";

    // when & then
    assertThrows(
        IllegalArgumentException.class,
        () ->
            CompanyRuleSpecification.withQuery(
                publisherPid, ruleSet, statusSet, queryFields, queryTerm));
  }

  @Test
  void shouldGetAllActiveRules() {
    // given
    Specification<CompanyRule> specification =
        CompanyRuleSpecification.withQuery(4L, null, EnumSet.of(Status.ACTIVE), null, null);

    // when
    Collection<CompanyRule> result = companyRuleRepository.findAll(specification);

    // then
    assertEquals(
        Set.of(3L, 4L, 7L), result.stream().map(CompanyRule::getPid).collect(Collectors.toSet()));
    assertEquals(
        Set.of(Status.ACTIVE),
        result.stream().map(CompanyRule::getStatus).collect(Collectors.toSet()));
  }

  @Test
  void shouldGetAllInactiveRules() {
    // given
    Specification<CompanyRule> specification =
        CompanyRuleSpecification.withQuery(4L, null, EnumSet.of(Status.INACTIVE), null, null);

    // when
    Collection<CompanyRule> result = companyRuleRepository.findAll(specification);

    // then
    assertEquals(Set.of(13L), result.stream().map(CompanyRule::getPid).collect(Collectors.toSet()));
    assertEquals(
        Set.of(Status.INACTIVE),
        result.stream().map(CompanyRule::getStatus).collect(Collectors.toSet()));
  }

  @Test
  void shouldNotGetExistingDeletedRules() {
    // given
    Set<Status> disallowedStatus = EnumSet.of(Status.DELETED);

    // when & then
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> CompanyRuleSpecification.withQuery(4L, null, disallowedStatus, null, null));
    assertEquals("Cannot retrieve deleted rules.", exception.getMessage());
  }

  @Test
  void shouldSetWhenEmptySetPassedAsArgument() {
    // given
    Specification<CompanyRule> specification =
        CompanyRuleSpecification.withQuery(
            4L, EnumSet.noneOf(RuleType.class), EnumSet.noneOf(Status.class), null, null);

    // when
    Collection<CompanyRule> result = companyRuleRepository.findAll(specification);

    // then
    assertEquals(
        Set.of(3L, 4L, 7L, 13L),
        result.stream().map(CompanyRule::getPid).collect(Collectors.toSet()));
  }

  private Collection<Long> extractRulePids(Collection<CompanyRule> rules) {
    return rules.stream().map(Rule::getPid).collect(Collectors.toSet());
  }
}
