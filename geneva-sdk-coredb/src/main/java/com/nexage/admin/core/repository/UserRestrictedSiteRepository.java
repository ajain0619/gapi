package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.UserRestrictedSite;
import com.nexage.admin.core.model.UserRestrictedSitePK;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRestrictedSiteRepository
    extends JpaRepository<UserRestrictedSite, UserRestrictedSitePK>,
        JpaSpecificationExecutor<UserRestrictedSite> {

  /**
   * Find userRestrictedSite pid by userId and siteId
   *
   * @param userId user pid.
   * @return {@link Long} site pid.
   */
  @Query(
      value =
          "SELECT u.pk.siteId FROM UserRestrictedSite u WHERE u.pk.userId = :userId AND u.pk.siteId = :siteId")
  Optional<Long> findPidByUserIdAndSiteId(
      @Param("userId") Long userId, @Param("siteId") Long siteId);

  @Query(value = "SELECT u.pk.siteId FROM UserRestrictedSite u WHERE u.pk.userId = :userId")
  List<Long> findPidsByUserId(@Param("userId") Long userId);

  void deleteByPkUserIdAndPkSiteId(Long userId, Long siteId);

  @Modifying
  @Query(value = "DELETE FROM UserRestrictedSite u WHERE u.pk.userId = :userId")
  void deleteByPkUserId(@Param("userId") Long userId);
}
