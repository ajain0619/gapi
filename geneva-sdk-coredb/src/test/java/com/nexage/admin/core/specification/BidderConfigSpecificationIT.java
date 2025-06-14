package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.BidderConfig_;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
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
    scripts = "/data/repository/bidder-config-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BidderConfigSpecificationIT extends CoreDbSdkIntegrationTestBase {
  private static final long COMPANY_PID = 300L;
  private static Set<String> VALID_QF = Set.of(BidderConfig_.NAME);
  @Autowired private BidderConfigRepository bidderConfigRepository;

  @Test
  void shouldFindSpecificationWithGivenCompanyPid() {
    // given
    Specification<BidderConfig> spec = BidderConfigSpecification.withCompanyPid(COMPANY_PID);
    // when
    List<BidderConfig> result = bidderConfigRepository.findAll(spec);
    // then
    assertEquals(2, result.size());
    assertEquals(
        Set.of(100L, 101L), result.stream().map(BidderConfig::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindSpecificationWithGivenCompanyPidAndSearchCriteria() {
    // given
    final String qt = "DEFAULTNAME";
    Specification<BidderConfig> spec =
        BidderConfigSpecification.withCompanyPidAndSearchCriteria(COMPANY_PID, VALID_QF, qt);
    // when
    List<BidderConfig> result = bidderConfigRepository.findAll(spec);
    // then
    assertEquals(2, result.size());
    assertEquals(
        Set.of(100L, 101L), result.stream().map(BidderConfig::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindSpecificationWithGivenCompanyPidAndNoSearchCriteria() {
    // given
    final String qt = "DEFAULTNAME";
    Specification<BidderConfig> spec =
        BidderConfigSpecification.withCompanyPidAndSearchCriteria(COMPANY_PID, null, null);
    // when
    List<BidderConfig> result = bidderConfigRepository.findAll(spec);
    // then
    assertEquals(2, result.size());
    assertEquals(
        Set.of(100L, 101L), result.stream().map(BidderConfig::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindEmptySpecificationWithGivenCompanyPidAndUnmatchedSearchCriteria() {
    // given
    final String qt = "NOT_MATCHED_QT";
    Specification<BidderConfig> spec =
        BidderConfigSpecification.withCompanyPidAndSearchCriteria(COMPANY_PID, VALID_QF, qt);
    // when
    List<BidderConfig> result = bidderConfigRepository.findAll(spec);
    // then
    assertEquals(0, result.size());
  }

  @Test
  void shouldThrowExceptionWhenCompanyPidIsNull() {
    assertThrows(
        GenevaValidationException.class, () -> BidderConfigSpecification.withCompanyPid(null));
  }

  @Test
  void shouldThrowExceptionWhenValidQfAndNullQt() {
    assertThrows(
        GenevaValidationException.class,
        () -> BidderConfigSpecification.withCompanyPidAndSearchCriteria(1L, VALID_QF, null));
  }

  @Test
  void shouldThrowExceptionWhenValidQfAndBlankQt() {
    assertThrows(
        GenevaValidationException.class,
        () -> BidderConfigSpecification.withCompanyPidAndSearchCriteria(1L, VALID_QF, " "));
  }

  @Test
  void shouldThrowExceptionWhenInValidQfAndValidQt() {
    Set<String> qf_invalid = Set.of(BidderConfig_.ID);
    String qt = "test";
    assertThrows(
        GenevaValidationException.class,
        () -> BidderConfigSpecification.withCompanyPidAndSearchCriteria(1L, qf_invalid, qt));
  }
}
