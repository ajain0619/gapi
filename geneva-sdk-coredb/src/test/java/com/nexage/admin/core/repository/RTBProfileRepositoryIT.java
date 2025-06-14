package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.RTBProfileView;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/default-rtb-profile-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class RTBProfileRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired RTBProfileRepository rtbProfileRepository;

  @Test
  void when_default_profiles_get_All() {
    final Long companyPid = 10000L;
    Pageable pageable = PageRequest.of(0, 6);
    List<RTBProfileView> profiles =
        rtbProfileRepository
            .findByDefaultRtbProfileOwnerCompanyPid(companyPid, pageable)
            .getContent();
    assertEquals(6, profiles.size());
  }

  @Test
  void when_default_profiles_find_by_name() {
    final Long companyPid = 10000L;
    Pageable pageable = PageRequest.of(0, 6);
    List<RTBProfileView> profiles =
        rtbProfileRepository
            .findByDefaultRtbProfileOwnerCompanyPidAndNameLike(companyPid, "%My%", pageable)
            .getContent();
    assertEquals(5, profiles.size());
    assertEquals(60000L, profiles.get(0).getPid().longValue());
  }

  @Test
  void when_default_profiles_get_by_seller_pid() {
    Long profileId = rtbProfileRepository.getDefaultRTBProfileBySellerPid(1L);
    assertNotNull(profileId);
    assertEquals(60000L, profileId.longValue());
  }
}
