package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.DealRtbProfileViewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/default-rtb-profile-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class DealRtbProfileViewSpecificationIT extends CoreDbSdkIntegrationTestBase {

  private static final int PID = 60006;
  @Autowired private DealRtbProfileViewRepository dealRtbProfileViewRepository;

  @Test
  void shouldFindAllNotDeleted() {
    // given
    var spec = DealRtbProfileViewSpecification.isNotDeleted();

    // when
    var result = dealRtbProfileViewRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(PID, result.get(0).getPid());
  }

  @Test
  void shouldFindAllWithNullDefaultRtbProfileOwnerCompanyPid() {
    // given
    var spec = DealRtbProfileViewSpecification.hasNullDefaultRtbProfileOwnerCompanyPid();

    // when
    var result = dealRtbProfileViewRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(PID, result.get(0).getPid());
  }
}
