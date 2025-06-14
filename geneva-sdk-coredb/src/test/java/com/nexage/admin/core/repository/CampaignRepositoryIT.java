package com.nexage.admin.core.repository;

import static com.nexage.admin.core.util.TestUtil.TEST_PREFIX;
import static com.nexage.admin.core.util.TestUtil.getTestCampaign;
import static com.nexage.admin.core.util.TestUtil.getTestTarget;
import static com.nexage.admin.core.util.TestUtil.validateCampaign;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.Campaign;
import com.nexage.admin.core.model.Target;
import com.nexage.admin.core.specification.CampaignSpecification;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/campaign-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class CampaignRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private CampaignRepository campaignRepository;

  @Test
  void shouldCreateCampaign() {
    Campaign campaign = getTestCampaign();
    campaign.setPid(null);

    campaign = campaignRepository.save(campaign);
    validateCampaign(campaign, TEST_PREFIX);
  }

  @Test
  void shouldUpdateCampaignWithTarget() {
    // given
    Campaign campaign = getTestCampaign();
    campaign.setPid(null);
    campaign = campaignRepository.save(campaign);
    Target target = getTestTarget(campaign);
    Collection<Target> targets = new ArrayList<Target>();
    targets.add(target);
    campaign.setTargets(targets);

    // when
    campaign = campaignRepository.save(campaign);

    // then
    List<Campaign> campaignList =
        campaignRepository.findByTargets_TypeAndTargets_FilterLike(
            Target.TargetType.ZONE, "site_1/zone_1");
    assertEquals(1, campaign.getTargets().size());
    assertEquals(1, campaignList.size());
    for (Campaign iterCampaign : campaignList) {
      for (Target iterTarger : iterCampaign.getTargets()) {
        assertEquals(Target.TargetType.ZONE, iterTarger.getType());
      }
    }
  }

  @Test
  void shouldFindCampaignBySellerId() {
    long sellerId = 1L;

    List<Campaign> campaigns = campaignRepository.findBySellerId(sellerId);

    for (Campaign campaign : campaigns) {
      assertEquals(sellerId, campaign.getSellerId());
    }
  }

  @Test
  void shouldExistByPidAndSellerId() {
    assertTrue(campaignRepository.existsByPidAndSellerId(1L, 1L));
  }

  @Test
  void shouldNotExistByPidAndSellerId() {
    assertFalse(campaignRepository.existsByPidAndSellerId(1L, 2L));
  }

  @Test
  void shouldFindNotDeletedCampaigns() {
    // when
    List<Campaign> campaigns = campaignRepository.findAll(CampaignSpecification.isNotDeleted());

    // then
    assertEquals(1, campaigns.size());
    assertEquals(1, campaigns.get(0).getPid());
  }

  @Test
  void shouldFindCampaignsWithGivenCompanyPid() {
    // given
    var companyPid = 1L;

    // when
    List<Campaign> campaigns =
        campaignRepository.findAll(CampaignSpecification.hasCompanyPid(companyPid));

    // then
    assertEquals(2, campaigns.size());
    assertEquals(
        Set.of(1L, 2L), campaigns.stream().map(Campaign::getPid).collect(Collectors.toSet()));
  }
}
