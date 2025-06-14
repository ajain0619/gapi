package com.nexage.admin.core.specification;

import static com.nexage.admin.core.specification.HbPartnerSpecification.withSellerPid;
import static com.nexage.admin.core.specification.HbPartnerSpecification.withSitePid;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.repository.HbPartnerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/hb-partner-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class HbPartnerSpecificationIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private HbPartnerRepository hbPartnerRepository;

  @Test
  void shouldFindSpecWithSitePid() {
    // given
    Specification<HbPartner> spec = withSitePid(37L);

    // when
    long countSpec = hbPartnerRepository.count(spec);

    // then
    assertEquals(1, countSpec);
  }

  @Test
  void shouldFindSpecWithSellerPid() {
    // given
    Specification<HbPartner> spec = withSellerPid(1L);

    // when
    long countSpec = hbPartnerRepository.count(spec);

    // then
    assertEquals(1, countSpec);
  }
}
