package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerLimitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NexageAwareSellerLimitServiceTest {

  @Mock private UserContext userContext;
  @Mock private SellerLimitService sellerLimitService;
  @Mock private RtbProfileLibrarySellerLimitServiceImpl rtbProfileLibrarySellerLimitService;
  @InjectMocks private NexageAwareSellerLimitService nexageSellerLimitService;

  private long publisher = 1L;
  private long campaignPid = 2L;
  private long site = 3L;
  private long position = 4L;

  @Test
  void nexageUserCanDoEverythingAndHasNoLimits() {
    given(userContext.isNexageUser()).willReturn(true);

    assertTrue(nexageSellerLimitService.canCreateBidderGroups(publisher));
    assertTrue(nexageSellerLimitService.canCreateBlockGroups(publisher));
    assertTrue(nexageSellerLimitService.canCreateCampaigns(publisher));
    assertTrue(nexageSellerLimitService.canCreateCreativesInCampaign(publisher, campaignPid));
    assertTrue(nexageSellerLimitService.canCreatePositionsInSite(publisher, site));
    assertTrue(nexageSellerLimitService.canCreateSites(publisher));
    assertTrue(nexageSellerLimitService.canCreateTagsInPosition(publisher, site, position));
    assertTrue(nexageSellerLimitService.canCreateUsers(publisher));
    assertFalse(nexageSellerLimitService.isLimitEnabled(publisher));
  }

  @Test
  void externalSellerDependsOnActualServiceResponse() {
    assertFalse(nexageSellerLimitService.canCreateBidderGroups(publisher));
    assertFalse(nexageSellerLimitService.canCreateBlockGroups(publisher));
    assertFalse(nexageSellerLimitService.canCreateCampaigns(publisher));
    assertFalse(nexageSellerLimitService.canCreateCreativesInCampaign(publisher, campaignPid));
    assertFalse(nexageSellerLimitService.canCreatePositionsInSite(publisher, site));
    assertFalse(nexageSellerLimitService.canCreateSites(publisher));
    assertFalse(nexageSellerLimitService.canCreateTagsInPosition(publisher, site, position));
    assertFalse(nexageSellerLimitService.canCreateUsers(publisher));
    assertFalse(nexageSellerLimitService.isLimitEnabled(publisher));

    verify(rtbProfileLibrarySellerLimitService).canCreateBidderGroups(publisher);
    verify(rtbProfileLibrarySellerLimitService).canCreateBlockGroups(publisher);
    verify(sellerLimitService).canCreateCampaigns(publisher);
    verify(sellerLimitService).canCreateCreativesInCampaign(publisher, campaignPid);
    verify(sellerLimitService).canCreatePositionsInSite(publisher, site);
    verify(sellerLimitService).canCreateSites(publisher);
    verify(sellerLimitService).canCreateTagsInPosition(publisher, site, position);
    verify(sellerLimitService).canCreateUsers(publisher);
    verify(sellerLimitService).isLimitEnabled(publisher);
  }

  @Test
  void allCheckMethodsReturnValuesFromActualService() {
    given(rtbProfileLibrarySellerLimitService.checkBidderLibrariesLimit(publisher)).willReturn(1);
    assertEquals(1, nexageSellerLimitService.checkBidderLibrariesLimit(publisher));

    given(rtbProfileLibrarySellerLimitService.checkBlockLibrariesLimit(publisher)).willReturn(2);
    assertEquals(2, nexageSellerLimitService.checkBlockLibrariesLimit(publisher));

    given(sellerLimitService.checkCampaignsLimit(publisher)).willReturn(3);
    assertEquals(3, nexageSellerLimitService.checkCampaignsLimit(publisher));

    given(sellerLimitService.checkCreativesInCampaignLimit(publisher, campaignPid)).willReturn(4);
    assertEquals(4, nexageSellerLimitService.checkCreativesInCampaignLimit(publisher, campaignPid));

    given(sellerLimitService.checkPositionsInSiteLimit(publisher, site)).willReturn(5);
    assertEquals(5, nexageSellerLimitService.checkPositionsInSiteLimit(publisher, site));

    given(sellerLimitService.checkSitesLimit(publisher)).willReturn(6);
    assertEquals(6, nexageSellerLimitService.checkSitesLimit(publisher));

    given(sellerLimitService.checkUsersLimit(publisher)).willReturn(7);
    assertEquals(7, nexageSellerLimitService.checkUsersLimit(publisher));

    given(sellerLimitService.checkTagsInPositionLimit(publisher, site, position)).willReturn(8);
    assertEquals(8, nexageSellerLimitService.checkTagsInPositionLimit(publisher, site, position));
  }
}
