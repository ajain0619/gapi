package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.BuyerSeat;
import com.nexage.admin.core.repository.BuyerSeatRepository;
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
    scripts = "/data/specification/buyer-seat-specification.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BuyerSeatSpecificationIT extends CoreDbSdkIntegrationTestBase {

  @Autowired BuyerSeatRepository buyerSeatRepository;

  @Test
  void shouldFindAllWithNameAndCompanyPid() {
    // given
    var spec =
        BuyerSeatSpecification.withCompanyPidAndQueryFieldsAndSearchTerm(
            2L, "name_3", Set.of("name"), "name_3");

    // when
    var result = buyerSeatRepository.findAll(spec);

    // then
    assertEquals(Set.of(3L), result.stream().map(BuyerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindAllWithName() {
    // given
    var spec = BuyerSeatSpecification.withName("name_3");

    // when
    var result = buyerSeatRepository.findAll(spec);

    // then
    assertEquals(Set.of(3L), result.stream().map(BuyerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindAllWithCompanyPid() {
    // given
    var spec = BuyerSeatSpecification.withCompanyPid(1L);

    // when
    var result = buyerSeatRepository.findAll(spec);

    // then
    assertEquals(
        Set.of(1L, 2L), result.stream().map(BuyerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindAllWithQueryFieldAndSearchTerm() {
    // given
    var spec = BuyerSeatSpecification.withQueryFieldsAndSearchTerm(Set.of("name"), "name_3");

    // when
    var result = buyerSeatRepository.findAll(spec);

    // then
    assertEquals(Set.of(3L), result.stream().map(BuyerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnBuyersByCompanyPidNameAndEmptyQfAndQt() {
    // given
    var spec =
        BuyerSeatSpecification.withCompanyPidAndQueryFieldsAndSearchTerm(
            2L, "name_4", Set.of(), "");

    // when
    var result = buyerSeatRepository.findAll(spec);

    // then
    assertEquals(Set.of(4L), result.stream().map(BuyerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnBuyersByCompanyPidNameAndNullQfAndQt() {
    // given
    var spec =
        BuyerSeatSpecification.withCompanyPidAndQueryFieldsAndSearchTerm(1L, "name_2", null, null);

    // when
    var result = buyerSeatRepository.findAll(spec);

    // then
    assertEquals(Set.of(2L), result.stream().map(BuyerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnBuyersByCompanyPidNameQfAndEmptyQt() {
    // given
    var spec =
        BuyerSeatSpecification.withCompanyPidAndQueryFieldsAndSearchTerm(
            1L, "name_1", Set.of("name"), "");

    // when
    var result = buyerSeatRepository.findAll(spec);

    // then
    assertEquals(Set.of(1L), result.stream().map(BuyerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnNullWhenQuerFieldSearchTermAndCompanyPidAreNull() {
    assertNull(
        BuyerSeatSpecification.withCompanyPidAndQueryFieldsAndSearchTerm(null, null, null, null));
  }
}
