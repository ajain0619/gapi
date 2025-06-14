package com.nexage.admin.core.repository;

import static com.nexage.admin.core.specification.SellerSeatRuleSpecification.withName;
import static com.nexage.admin.core.specification.SellerSeatRuleSpecification.withPid;
import static com.nexage.admin.core.specification.SellerSeatRuleSpecification.withQuery;
import static com.nexage.admin.core.specification.SellerSeatRuleSpecification.withSellerSeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.Rule;
import com.nexage.admin.core.model.SellerSeatRule;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/seller-seat-rule-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class SellerSeatRuleRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final long EXPECTED_PID_1 = 1L;
  private static final long EXPECTED_PID_2 = 2L;
  private static final long EXPECTED_PID_3 = 3L;
  private static final String EXPECTED_STRING = "Lancelot 123";
  private static final String NAME = "name";
  private static final String PID = "pid";

  @Autowired SellerSeatRuleRepository sellerSeatRuleRepository;

  @Test
  void shouldGetAllNotDeletedSellerSeatRules() {
    List<SellerSeatRule> rules = sellerSeatRuleRepository.findAll();
    assertEquals(3, rules.size());
    Set<Long> expected = ImmutableSet.of(EXPECTED_PID_1, EXPECTED_PID_2, EXPECTED_PID_3);
    assertTrue(rules.stream().map(Rule::getPid).allMatch(expected::contains));
  }

  @Test
  void shouldGetOneSellerSeatRule() {
    Optional<SellerSeatRule> rule = sellerSeatRuleRepository.findById(EXPECTED_PID_1);
    if (rule.isPresent()) {
      assertEquals(EXPECTED_STRING, rule.get().getName());
      assertEquals(EXPECTED_PID_1, (long) rule.get().getSellerSeatPid());
    }
  }

  @Test
  void shouldGetSellerSeatRuleByPid() {
    Specification<SellerSeatRule> withPid =
        withSellerSeat(EXPECTED_PID_1).and(withPid(EXPECTED_PID_1));

    Optional<SellerSeatRule> rule = sellerSeatRuleRepository.findOne(withPid);

    assertTrue(rule.isPresent());
    assertEquals(EXPECTED_STRING, rule.get().getName());
    assertEquals(EXPECTED_PID_1, (long) rule.get().getSellerSeatPid());
    assertEquals(EXPECTED_PID_1, (long) rule.get().getPid());
  }

  @Test
  void shouldGetSellerSeatRuleByPidUsingQueryTerm() {
    Specification<SellerSeatRule> withPid =
        withSellerSeat(EXPECTED_PID_1).and(withQuery(Sets.newHashSet(PID), "1"));

    Optional<SellerSeatRule> rule = sellerSeatRuleRepository.findOne(withPid);

    assertTrue(rule.isPresent());
    assertEquals(EXPECTED_STRING, rule.get().getName());
    assertEquals(EXPECTED_PID_1, (long) rule.get().getSellerSeatPid());
    assertEquals(EXPECTED_PID_1, (long) rule.get().getPid());
  }

  @Test
  void shouldNotGetAnySellerSeatRuleForWrongSellerSeatPid() {
    Specification<SellerSeatRule> withPid = withSellerSeat(4L).and(withPid(EXPECTED_PID_1));

    Optional<SellerSeatRule> rule = sellerSeatRuleRepository.findOne(withPid);

    assertTrue(rule.isEmpty());
  }

  @Test
  void shouldNotGetAnySellerSeatRuleForWrongRulePid() {
    Specification<SellerSeatRule> withPid = withSellerSeat(EXPECTED_PID_1).and(withPid(6L));

    Optional<SellerSeatRule> rule = sellerSeatRuleRepository.findOne(withPid);

    assertTrue(rule.isEmpty());
  }

  @Test
  void shouldFindRuleWithName() {
    Specification<SellerSeatRule> withName = withName(EXPECTED_STRING);
    List<SellerSeatRule> rules = sellerSeatRuleRepository.findAll(withName);

    assertNotNull(rules);
    assertEquals(1, rules.size());
    SellerSeatRule rule = rules.iterator().next();
    assertEquals(EXPECTED_STRING, rule.getName());
    assertEquals(EXPECTED_PID_1, (long) rule.getPid());
  }

  @Test
  void shouldFindRulesBySellerSeatPid() {
    Specification<SellerSeatRule> withSellerSeatPid = withSellerSeat(EXPECTED_PID_1);
    List<SellerSeatRule> rules = sellerSeatRuleRepository.findAll(withSellerSeatPid);

    assertNotNull(rules);
    assertEquals(2, rules.size());
    assertEquals(EXPECTED_PID_1, (long) rules.get(0).getPid());
    assertEquals(EXPECTED_PID_2, (long) rules.get(1).getPid());
  }

  @Test
  void shouldFindRulesBySellerSeatPidAndEmptyQueryTerm() {
    Specification<SellerSeatRule> withSellerSeatPidAndQuery =
        withSellerSeat(EXPECTED_PID_1).and(withQuery(Sets.newHashSet(NAME, PID), ""));
    List<SellerSeatRule> rules = sellerSeatRuleRepository.findAll(withSellerSeatPidAndQuery);

    assertNotNull(rules);
    assertEquals(2, rules.size());
    assertEquals(EXPECTED_PID_1, (long) rules.get(0).getPid());
    assertEquals(EXPECTED_PID_2, (long) rules.get(1).getPid());
  }

  @Test
  void shouldFindRulesBySellerSeatPidAndEmptyQueryFields() {
    Specification<SellerSeatRule> withSellerSeatPidAndQuery =
        withSellerSeat(EXPECTED_PID_1).and(withQuery(Sets.newHashSet(), "whatever"));
    List<SellerSeatRule> rules = sellerSeatRuleRepository.findAll(withSellerSeatPidAndQuery);

    assertNotNull(rules);
    assertEquals(2, rules.size());
    assertEquals(EXPECTED_PID_1, (long) rules.get(0).getPid());
    assertEquals(EXPECTED_PID_2, (long) rules.get(1).getPid());
  }

  @Test
  void shouldFindRulesBySellerSeatPidAndQueryTermOnOneField() {
    Specification<SellerSeatRule> withSellerSeatPidAndQuery =
        withSellerSeat(EXPECTED_PID_1).and(withQuery(Sets.newHashSet(NAME), "2"));
    List<SellerSeatRule> rules = sellerSeatRuleRepository.findAll(withSellerSeatPidAndQuery);

    assertNotNull(rules);
    assertEquals(1, rules.size());
    assertEquals(EXPECTED_PID_1, (long) rules.get(0).getPid());
  }

  @Test
  void shouldFindRulesBySellerSeatPidAndQueryTermOnTwoFields() {
    Specification<SellerSeatRule> withSellerSeatPidAndQuery =
        withSellerSeat(EXPECTED_PID_1).and(withQuery(Sets.newHashSet(NAME, PID), "2"));
    List<SellerSeatRule> rules = sellerSeatRuleRepository.findAll(withSellerSeatPidAndQuery);

    assertNotNull(rules);
    assertEquals(2, rules.size());
    assertEquals(EXPECTED_PID_1, (long) rules.get(0).getPid());
    assertEquals(EXPECTED_PID_2, (long) rules.get(1).getPid());
  }

  @Test
  void shouldFindRulesBySellerSeatPidAndQueryTermOnTwoFieldsUsingNonNumericTerm() {
    Specification<SellerSeatRule> withSellerSeatPidAndQuery =
        withSellerSeat(EXPECTED_PID_1).and(withQuery(Sets.newHashSet(NAME, PID), "Lancelot"));
    List<SellerSeatRule> rules = sellerSeatRuleRepository.findAll(withSellerSeatPidAndQuery);

    assertNotNull(rules);
    assertEquals(1, rules.size());
    assertEquals(EXPECTED_STRING, rules.get(0).getName());
  }

  @Test
  void shouldFindRulesBySellerSeatPidAndQueryTermOnTwoFieldsAndPaging() {
    Specification<SellerSeatRule> withSellerSeatPidAndQuery =
        withSellerSeat(EXPECTED_PID_1).and(withQuery(Sets.newHashSet(NAME, PID), "2"));
    Page<SellerSeatRule> rules =
        sellerSeatRuleRepository.findAll(withSellerSeatPidAndQuery, PageRequest.of(0, 10));

    assertNotNull(rules);
    assertEquals(2, rules.getTotalElements());
    assertEquals(EXPECTED_PID_1, (long) rules.getContent().get(0).getPid());
    assertEquals(EXPECTED_PID_2, (long) rules.getContent().get(1).getPid());
  }

  @Test
  void shouldFindRuleWithStatusOtherThanDeletedAndSellerSeatPid() {
    Specification<SellerSeatRule> withSellerSeatPidAndStatus = withSellerSeat(EXPECTED_PID_2);
    List<SellerSeatRule> rules = sellerSeatRuleRepository.findAll(withSellerSeatPidAndStatus);

    assertNotNull(rules);
    assertEquals(1, rules.size());
    SellerSeatRule rule = rules.iterator().next();
    assertEquals("Rude soldier", rule.getName());
    assertEquals(EXPECTED_PID_3, (long) rule.getPid());
  }

  @Test
  void shouldMarkRuleAsDeleted() {
    sellerSeatRuleRepository.delete(4L);
    assertTrue(sellerSeatRuleRepository.findById(4L).isEmpty());
  }
}
