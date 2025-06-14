package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.specification.SellerSeatSpecification;
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

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/seller-seat-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class SellerSeatSpecificationIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private SellerSeatRepository sellerSeatRepository;

  @Test
  void shouldMakeRepositoryGetAllSellerSeatsWithPidNotIn() {
    // given
    Specification<SellerSeat> spec = SellerSeatSpecification.withPidNotIn(1L, 4L, 5L);

    // when
    List<SellerSeat> result = sellerSeatRepository.findAll(spec);

    // then
    assertEquals(3, result.size());
    assertEquals(
        Set.of(2L, 3L, 12345L),
        result.stream().map(SellerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldMakeRepositoryGetAllSellerSeatsWithStatus() {
    // given
    Specification<SellerSeat> spec = SellerSeatSpecification.withStatus(Boolean.TRUE);

    // when
    List<SellerSeat> result = sellerSeatRepository.findAll(spec);

    // then
    assertEquals(2, result.size());
    assertEquals(
        Set.of(2L, 12345L), result.stream().map(SellerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldMakeRepositoryGetAllSellerSeatsWithNonEmptySellers() {
    // given
    Specification<SellerSeat> spec = SellerSeatSpecification.withNonEmptySellers();

    // when
    List<SellerSeat> result = sellerSeatRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(
        Set.of(12345L), result.stream().map(SellerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldMakeRepositoryGetAllSellerSeatsWithNameLike() {
    // given
    Specification<SellerSeat> spec = SellerSeatSpecification.withNameLike("Foo");

    // when
    List<SellerSeat> result = sellerSeatRepository.findAll(spec);

    // then
    assertEquals(3, result.size());
    assertEquals(
        Set.of(2L, 3L, 12345L),
        result.stream().map(SellerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldMakeRepositoryGetAllSellerSeatsWithNameLikeAndStatusDisabled() {
    // given
    Specification<SellerSeat> spec = SellerSeatSpecification.withNameLike("Testseat");

    // when
    List<SellerSeat> result = sellerSeatRepository.findAll(spec);

    // then
    assertEquals(2, result.size());
    assertEquals(
        Set.of(4L, 5L), result.stream().map(SellerSeat::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldMakeRepositoryGetAllSellerSeatsWithExactNameAndStatusDisabled() {
    // given
    Specification<SellerSeat> spec = SellerSeatSpecification.withNameLike("Testseat2");

    // when
    List<SellerSeat> result = sellerSeatRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(Set.of(5L), result.stream().map(SellerSeat::getPid).collect(Collectors.toSet()));
  }
}
