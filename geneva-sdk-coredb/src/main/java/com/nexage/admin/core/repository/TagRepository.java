package com.nexage.admin.core.repository;

import com.nexage.admin.core.dto.TagHierarchyDto;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.TagView;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

  /**
   * Aggregate {@link Tag} and {@link com.nexage.admin.core.model.TagView} into a single projection,
   * based on request. This query search companies only by name due to aggregation restrictions in
   * this specific version of Hibernate & Spring.
   *
   * @param positionId positionId the tag is under.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link TagView} instances based on parameters.
   */
  @Query(
      "SELECT new com.nexage.admin.core.model.TagView("
          + "t.pid, t.name, a.name, t.status, t.positionPid, t.rtbFloor, t.sitePid, t.buyerPid, t.ecpmProvision) "
          + "FROM Tag AS t "
          + "LEFT JOIN AdSource as a ON t.buyerPid = a.pid "
          + "WHERE t.positionPid = :positionId and t.sitePid = :siteId ")
  Page<TagView> findTags(
      @Param("siteId") Long siteId, @Param("positionId") Long positionId, Pageable pageable);

  /**
   * Find all not deleted {@link Tag} for an ad source
   *
   * @param buyerPid buyer pid
   * @param publisherPid publisher pid
   * @return matching tags
   */
  @Query(
      "SELECT t FROM Tag t "
          + "WHERE t.status IN (1, 0)"
          + "AND t.buyerPid = :buyerPid "
          + "AND t.site.company.pid = :publisherPid "
          + "AND t.position IS NOT NULL")
  List<Tag> findForAdSource(
      @Param("buyerPid") long buyerPid, @Param("publisherPid") long publisherPid);

  /**
   * Find all active {@link Tag} with a specific position
   *
   * @param positionPid position pid
   * @return matching tags
   */
  @Query("SELECT t FROM Tag t WHERE status = 1 AND position.pid = :positionPid")
  List<Tag> findActiveByPositionPid(@Param("positionPid") long positionPid);

  /**
   * Find the primary IDs of tags identified by pids
   *
   * @param pids tag pids
   * @return primary IDs of tags
   */
  @Query("SELECT primaryId FROM Tag WHERE pid IN :pids")
  List<String> getPrimaryIdForPidIn(@Param("pids") List<Long> pids);

  /**
   * Check if a tag exists with given fields
   *
   * @param buyerPid buyer pid
   * @param primaryId primary ID
   * @param primaryName primary name
   * @param secondaryId secondary ID
   * @param secondaryName secondary name
   * @return whether the tag exists or not
   */
  boolean existsByBuyerPidAndPrimaryIdAndPrimaryNameAndSecondaryIdAndSecondaryName(
      long buyerPid,
      String primaryId,
      String primaryName,
      String secondaryId,
      String secondaryName);

  @Query(
      value =
          "SELECT t.pid AS tagPid,"
              + "t.name AS tagName,"
              + "tier.level AS tierLevel,"
              + "tier.pid AS tierPid,"
              + "tier.tier_type AS tierType,"
              + "CASE WHEN grpid IS NOT NULL THEN 1 ELSE 0 END AS belongsToRTBGroup,"
              + "est.filter_bidders_whitelist AS filterBiddersWhitelist,"
              + "est.filter_bidders_allowlist AS filterBiddersAllowlist,"
              + "est.use_default_block AS useDefaultBlock,"
              + "est.use_default_bidders AS useDefaultBidders "
              + "FROM tag t "
              + "JOIN exchange_site_tag est ON t.primary_id = est.tag_id "
              + "LEFT JOIN tier_tag tg ON tg.tag_pid = t.pid "
              + "LEFT JOIN tier tier ON tier.position_pid = t.position_pid AND tier.pid= tg.tier_pid "
              + "LEFT JOIN("
              + "SELECT assoc.rtb_profile_pid , gr.pid AS grpid "
              + "FROM rtb_profile_library_association assoc "
              + "JOIN rtb_profile_library li ON assoc.library_pid = li.pid AND li.publisher_pid = :publisher "
              + "JOIN rtb_profile_library_item it ON it.library_pid = li.pid "
              + "JOIN rtb_profile_group gr ON gr.pid = :rtbprofilegroup AND gr.pid = it.item_pid AND gr.publisher_pid=:publisher "
              + ") AS temp ON est.pid = temp.rtb_profile_pid "
              + "WHERE t.status = 1 AND t.position_pid = :positionPid",
      nativeQuery = true)
  Set<TagHierarchyDto> getTagHierarchy(
      @Param("publisher") long publisherPid,
      @Param("positionPid") long positionPid,
      @Param("rtbprofilegroup") long rtbProfileGroupPid);

  long countBySitePidAndPositionPidAndStatusNot(long sitePid, long positionPid, Status status);

  @Query("SELECT t FROM Site s JOIN s.tags t WHERE t.primaryId=:primaryId AND s.status = 1")
  List<Tag> findByPrimaryIdWithActiveSite(@Param("primaryId") String primaryId);

  @Query(
      nativeQuery = true,
      value =
          "select distinct(auction_type)"
              + "from exchange_site_tag where tag_pid IN (select pid from tag where site_pid IN (select site_pid from deal_site where deal_pid = :dealId))")
  List<Integer> findGranularAuctionTypeForDealId(@Param("dealId") String dealId);
}
