package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.model.Campaign;
import com.nexage.admin.core.model.CampaignCreative;
import com.nexage.admin.core.model.CampaignCreativePk;
import com.nexage.admin.core.model.Creative;
import com.nexage.admin.core.repository.AdserverConfigurationRepository;
import com.nexage.admin.core.repository.CampaignRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.CreativeRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.EnvironmentUtil;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CampaignCreativeServiceImplTest {

  private static final long SELLER_PID = 1L;
  private static final long CAMPAIGN_PID = 2L;

  @Mock private CompanyRepository companyRepository;
  @Mock private CampaignRepository campaignRepository;
  @Mock private UserContext userContext;
  @Mock private CreativeRepository creativeRepository;
  @Mock private EnvironmentUtil environmentUtil;
  @Mock private AdserverConfigurationRepository adserverConfigurationRepository;

  @InjectMocks private CampaignCreativeServiceImpl campaignCreativeService;

  @Test
  void shouldGetAllCreatives() {
    // given
    Campaign campaign = new Campaign();
    CampaignCreativePk campaignCreativePk = new CampaignCreativePk();
    campaignCreativePk.setCampaign(campaign);
    campaign.setSellerId(SELLER_PID);
    Creative creative = new Creative();
    List<Creative> creativeList = new ArrayList<>();
    List<CampaignCreative> campaignCreatives = new ArrayList<>();
    CampaignCreative campaignCreative = new CampaignCreative();
    campaignCreative.setCampaignCreativePk(campaignCreativePk);
    campaignCreatives.add(campaignCreative);
    creative.setCampaignCreatives(campaignCreatives);
    creativeList.add(creative);
    campaign.setCampaignCreatives(campaignCreatives);

    given(userContext.doSameOrNexageAffiliation(SELLER_PID)).willReturn(true);
    given(companyRepository.existsById(SELLER_PID)).willReturn(true);
    given(campaignRepository.existsByPidAndSellerId(CAMPAIGN_PID, SELLER_PID)).willReturn(true);
    given(creativeRepository.findAllNonDeletedByCampaignPid(CAMPAIGN_PID)).willReturn(creativeList);
    given(environmentUtil.isAwsEnvironment()).willReturn(true);
    // when
    Set<Creative> creativeSet = campaignCreativeService.getAllCreatives(SELLER_PID, CAMPAIGN_PID);

    // then
    assertEquals(1, creativeSet.size());
  }

  @Test
  void shouldThrowInvalidSellerError() {
    // given
    given(userContext.doSameOrNexageAffiliation(1L)).willReturn(true);
    given(companyRepository.existsById(1L)).willReturn(false);

    // when
    var errorMessage =
        assertThrows(
                GenevaValidationException.class,
                () -> campaignCreativeService.getAllCreatives(1L, 1L))
            .getErrorCode();

    // then
    assertEquals(ServerErrorCodes.SERVER_USER_RESTRICTION_INVALID_SELLER, errorMessage);
  }

  @Test
  void shouldThrowInvalidCampaignForSellerError() {
    // given
    given(userContext.doSameOrNexageAffiliation(1L)).willReturn(true);
    given(companyRepository.existsById(1L)).willReturn(true);
    given(campaignRepository.existsByPidAndSellerId(1L, 1L)).willReturn(false);

    // when
    var errorMessage =
        assertThrows(
                GenevaValidationException.class,
                () -> campaignCreativeService.getAllCreatives(1L, 1L))
            .getErrorCode();

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_CAMPAIGN_FOR_SELLER, errorMessage);
  }
}
