package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.SellerSeatRule;
import com.nexage.admin.core.model.SellerSeatRule_;
import com.nexage.admin.core.repository.SellerSeatRuleRepository;
import java.util.Collection;
import java.util.List;
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
@Sql(
    scripts = "/data/repository/seller-seat-rule-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class SellerSeatRuleSpecificationIT extends CoreDbSdkIntegrationTestBase {

  private static final String NAME = "Lancelot 123";
  private static final long PID = 1L;

  @Autowired private SellerSeatRuleRepository sellerSeatRuleRepository;

  @Test
  void shouldReturnSpecificationWithPid() {
    // given
    Specification<SellerSeatRule> spec = SellerSeatRuleSpecification.withPid(PID);

    // when
    List<SellerSeatRule> result = sellerSeatRuleRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(PID, result.get(0).getPid());
  }

  @Test
  void shouldReturnSpecificationWithSellerSeatPid() {
    // given
    Specification<SellerSeatRule> spec = SellerSeatRuleSpecification.withSellerSeat(PID);

    // when
    List<SellerSeatRule> result = sellerSeatRuleRepository.findAll(spec);

    // then
    assertEquals(2, result.size());
    assertEquals(
        Set.of(PID, 2L), result.stream().map(SellerSeatRule::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnSpecificationWithName() {
    // given
    Specification<SellerSeatRule> spec = SellerSeatRuleSpecification.withName(NAME);

    // when
    List<SellerSeatRule> result = sellerSeatRuleRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(PID, result.get(0).getPid());
  }

  @Test
  void shouldReturnSpecificationWithQueryName() {
    // given
    Specification<SellerSeatRule> spec =
        SellerSeatRuleSpecification.withQuery(Set.of(SellerSeatRule_.NAME), NAME);

    // when
    List<SellerSeatRule> result = sellerSeatRuleRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(PID, result.get(0).getPid());
  }

  @Test
  void shouldReturnSpecificationWithQuery() {
    // given
    Specification<SellerSeatRule> spec =
        SellerSeatRuleSpecification.withQuery(
            Set.of(SellerSeatRule_.PID, SellerSeatRule_.NAME), "2");

    // when
    List<SellerSeatRule> result = sellerSeatRuleRepository.findAll(spec);

    // then
    assertEquals(2, result.size());
    assertEquals(
        Set.of(1L, 2L), result.stream().map(SellerSeatRule::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnCorrectSpecificationWithRuleType() {
    // given
    Specification<SellerSeatRule> spec =
        SellerSeatRuleSpecification.withRuleType(Set.of(RuleType.BRAND_PROTECTION));

    // when
    Collection<SellerSeatRule> result = sellerSeatRuleRepository.findAll(spec);

    // then
    assertEquals(3, result.size());
    assertEquals(
        Set.of(1L, 2L, 3L),
        result.stream().map(SellerSeatRule::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnCorrectSpecificationWithStatus() {
    // given
    Specification<SellerSeatRule> spec =
        SellerSeatRuleSpecification.withStatus(Set.of(Status.ACTIVE));

    // when
    Collection<SellerSeatRule> result = sellerSeatRuleRepository.findAll(spec);

    // then
    assertEquals(3, result.size());
    assertEquals(
        Set.of(1L, 2L, 3L),
        result.stream().map(SellerSeatRule::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnSpecificationWithNameAsNull() {
    assertNotNull(SellerSeatRuleSpecification.withName(null));
  }

  @Test
  void shouldMakeSpecificationWithQueryFieldsAndQueryTermWhenAtLeastOneValueIsEmptyOrNull() {
    assertNotNull(SellerSeatRuleSpecification.withQuery(Set.of(), ""));
    assertNotNull(SellerSeatRuleSpecification.withQuery(null, null));
    assertNotNull(SellerSeatRuleSpecification.withQuery(Set.of(NAME), ""));
    assertNotNull(SellerSeatRuleSpecification.withQuery(Set.of(), NAME));
  }
}
