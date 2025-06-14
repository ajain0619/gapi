package com.nexage.admin.core.repository;

import com.nexage.admin.core.dto.RtbProfileTagHierarchyDto;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RTBProfileView;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RTBProfileRepository
    extends JpaRepository<RTBProfile, Long>, JpaSpecificationExecutor<RTBProfile> {

  Optional<RTBProfile> findByTagPid(Long tagPid);

  Optional<RTBProfile> findByDefaultRtbProfileOwnerCompanyPidAndPid(
      Long publisherPid, Long rtbProfilePid);

  Page<RTBProfileView> findByDefaultRtbProfileOwnerCompanyPid(
      Long defaultRtbProfileOwnerCompanyPid, Pageable pageable);

  Page<RTBProfileView> findByDefaultRtbProfileOwnerCompanyPidAndNameLike(
      Long defaultRtbProfileOwnerCompanyPid, String name, Pageable pageable);

  List<RTBProfile> findByDefaultRtbProfileOwnerCompanyPid(Long defaultRtbProfileOwnerCompanyPid);

  List<RTBProfile> findByDefaultRtbProfileOwnerCompanyPidAndNameContains(
      Long publisherPid, String search);

  List<RTBProfile> findByDefaultRtbProfileOwnerCompanyPidAndName(Long publisherPid, String name);

  List<RTBProfile> findByPubAlias(Long pubAlias);

  List<RTBProfile> findBySitePidIn(Set<Long> sitePid);

  List<RTBProfile> findByExchangeSiteTagIdIn(List<String> tagId);

  @Query(
      nativeQuery = true,
      value =
          " SELECT pid AS pid FROM exchange_site_tag est "
              + " JOIN seller_attributes sa ON sa.rtb_profile = est.pid "
              + " WHERE sa.seller_pid = :sellerId")
  Long getDefaultRTBProfileBySellerPid(@Param("sellerId") Long sellerId);

  @Query(
      nativeQuery = true,
      value =
          " SELECT c.pid AS publisherPid, c.name AS publisherName,"
              + " s.pid AS sitePid, est.site_alias AS siteAlias,"
              + " est.site_name_alias AS siteNameAlias, est.site_type AS siteType,p.pid AS positionPid, IFNULL(p.memo,p.name) AS positionName,"
              + " p.placement_type AS placementType, p.traffic_type AS trafficType, s.name AS siteName, s.status AS status, s.live AS mode "
              + " FROM exchange_site_tag est "
              + " JOIN tag t ON est.tag_id = t.primary_id "
              + " JOIN site s ON s.pid = est.site_pid AND t.site_pid = s.pid "
              + " JOIN position p ON p.site_pid = s.pid "
              + " JOIN company c ON c.pid = :publisherPid AND c.pid = s.company_pid "
              + " WHERE t.position_pid = p.pid OR t.position_pid IS NOT NULL "
              + " ORDER by est.site_pid, est.VERSION DESC ")
  List<RtbProfileTagHierarchyDto> getAllTagHierarchy(@Param("publisherPid") Long publisherPid);
}
