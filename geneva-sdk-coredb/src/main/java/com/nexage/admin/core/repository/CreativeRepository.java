package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.Creative;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreativeRepository
    extends JpaRepository<Creative, Long>, JpaSpecificationExecutor<Creative> {

  @Query(
      "SELECT creative FROM Creative creative INNER JOIN CampaignCreative cc on cc.campaignCreativePk.creative.pid=creative.pid "
          + "where creative.status != 1 and cc.campaignCreativePk.campaign.pid = :campaignPid")
  List<Creative> findAllNonDeletedByCampaignPid(@Param("campaignPid") Long campaignPid);

  @Query(
      "SELECT COUNT(creative) FROM Creative creative INNER JOIN CampaignCreative cc on cc.campaignCreativePk.creative.pid=creative.pid "
          + "where creative.status != 1 and cc.campaignCreativePk.campaign.pid = :campaignPid")
  long countAllNonDeletedByCampaignPid(@Param("campaignPid") Long campaignPid);

  List<Creative> findAllBySellerId(Long sellerId);
}
