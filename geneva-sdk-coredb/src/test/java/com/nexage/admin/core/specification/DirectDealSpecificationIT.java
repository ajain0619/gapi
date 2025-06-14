package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.DirectDealRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/deals-common.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class DirectDealSpecificationIT extends CoreDbSdkIntegrationTestBase {

  @Autowired DirectDealRepository directDealRepository;

  @Test
  void shouldFindAllWithDealId() {
    // given
    var spec = DirectDealSpecification.withDealId("5");

    // when
    var result = directDealRepository.findAll(spec);

    // then
    assertEquals(Set.of(1L), result.stream().map(DirectDeal::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindAllWithDescription() {
    // given
    var spec = DirectDealSpecification.withDescription("Test-deal");

    // when
    var result = directDealRepository.findAll(spec);

    // then
    assertEquals(Set.of(2L), result.stream().map(DirectDeal::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindAllWithTierDealCategory() {
    // given
    var spec = DirectDealSpecification.withTierDealCategoryType(List.of("SSP"));

    // when
    var result = directDealRepository.findAll(spec);

    // then
    assertEquals(Set.of(1L), result.stream().map(DirectDeal::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindAllWithRules() {
    // given
    var spec = DirectDealSpecification.withRules();

    // when
    var result = directDealRepository.findAll(spec);

    // then
    assertEquals(
        Set.of(1L, 2L), result.stream().map(DirectDeal::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindAllWithoutRules() {
    // given
    var spec = DirectDealSpecification.withoutRules();

    // when
    var result = directDealRepository.findAll(spec);

    // then
    assertEquals(
        Set.of(110L, 111L, 112L),
        result.stream().map(DirectDeal::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindAllWithCurrency() {
    // given
    var spec = DirectDealSpecification.of(Set.of("currency"), "USD");

    // when
    var result = directDealRepository.findAll(spec);

    // then
    assertEquals(
        Set.of(1L, 2L), result.stream().map(DirectDeal::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindAllWithPlacementFormula() {
    // given
    var spec = DirectDealSpecification.of(Map.of("placementFormula", List.of("formula 1")));

    // when
    var result = directDealRepository.findAll(spec);

    // then
    assertEquals(Set.of(2L), result.stream().map(DirectDeal::getPid).collect(Collectors.toSet()));
  }
}
