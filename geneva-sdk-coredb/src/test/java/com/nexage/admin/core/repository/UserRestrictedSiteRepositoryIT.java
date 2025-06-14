package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/user-restricted-site-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class UserRestrictedSiteRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final long USER_1_PID = 1L;
  private static final long USER_2_PID = 2L;
  private static final long NOT_EXISTING_USER_PID = 3L;
  private static final long SITE_1_PID = 11L;
  private static final long SITE_2_PID = 22L;
  private static final long SITE_3_PID = 33L;
  private static final long NOT_EXISTING_SITE_PID = 44L;

  @Autowired protected UserRestrictedSiteRepository userRestrictedSiteRepository;

  @Test
  void shouldFindSitePidByUserIdAndSiteId() {
    assertEquals(
        SITE_1_PID,
        (long) userRestrictedSiteRepository.findPidByUserIdAndSiteId(USER_1_PID, SITE_1_PID).get());
    assertTrue(
        userRestrictedSiteRepository
            .findPidByUserIdAndSiteId(USER_1_PID, NOT_EXISTING_SITE_PID)
            .isEmpty());
  }

  @Test
  void shouldFindSitePidsByUserId() {
    assertEquals(List.of(SITE_1_PID), userRestrictedSiteRepository.findPidsByUserId(USER_1_PID));
    assertEquals(
        List.of(SITE_1_PID, SITE_2_PID, SITE_3_PID),
        userRestrictedSiteRepository.findPidsByUserId(USER_2_PID));
    assertTrue(userRestrictedSiteRepository.findPidsByUserId(NOT_EXISTING_USER_PID).isEmpty());
  }

  @Test
  void shouldDeleteByUserIdAndSiteId() {
    // when
    userRestrictedSiteRepository.deleteByPkUserIdAndPkSiteId(USER_1_PID, SITE_1_PID);

    // then
    assertTrue(
        userRestrictedSiteRepository.findPidByUserIdAndSiteId(USER_1_PID, SITE_1_PID).isEmpty());
  }

  @Test
  void shouldDeleteByUserId() {
    // when
    userRestrictedSiteRepository.deleteByPkUserId(USER_2_PID);

    // then
    assertTrue(
        userRestrictedSiteRepository.findPidByUserIdAndSiteId(USER_2_PID, SITE_1_PID).isEmpty());
    assertTrue(
        userRestrictedSiteRepository.findPidByUserIdAndSiteId(USER_2_PID, SITE_2_PID).isEmpty());
    assertTrue(
        userRestrictedSiteRepository.findPidByUserIdAndSiteId(USER_2_PID, SITE_3_PID).isEmpty());
  }
}
