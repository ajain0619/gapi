package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LimitServiceImplTest {

  @Mock private GlobalConfigService globalConfigService;
  @InjectMocks private LimitServiceImpl limitService;

  @Test
  void shouldGetGlobalSiteLimit() {
    // given
    var value = 1234;
    when(globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_SITES_LIMIT))
        .thenReturn(value);

    // when
    Integer returnedValue = limitService.getGlobalSiteLimit();

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldGetGlobalPositionsPerSiteLimit() {
    // given
    var value = 1234;
    when(globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_POSITIONS_SITE_LIMIT))
        .thenReturn(value);

    // when
    Integer returnedValue = limitService.getGlobalPositionsPerSiteLimit();

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldGetGlobalTagsPerPositionLimit() {
    // given
    var value = 1234;
    when(globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_TAGS_POSITION_LIMIT))
        .thenReturn(value);

    // when
    Integer returnedValue = limitService.getGlobalTagsPerPositionLimit();

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldGetGlobalCampaignsLimit() {
    // given
    var value = 1234;
    when(globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_CAMPAIGNS_LIMIT))
        .thenReturn(value);

    // when
    Integer returnedValue = limitService.getGlobalCampaignsLimit();

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldGetGlobalCreativesPerCampaignsLimit() {
    // given
    var value = 1234;
    when(globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_CREATIVES_CAMPAIGN_LIMIT))
        .thenReturn(value);

    // when
    Integer returnedValue = limitService.getGlobalCreativesPerCampaignsLimit();

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldGetGlobalBidderLibrariesLimit() {
    // given
    var value = 1234;
    when(globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_BIDDER_LIBRARIES_LIMIT))
        .thenReturn(value);

    // when
    Integer returnedValue = limitService.getGlobalBidderLibrariesLimit();

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldGetGlobalBlockLibrariesLimit() {
    // given
    var value = 1234;
    when(globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_BLOCK_LIBRARIES_LIMIT))
        .thenReturn(value);

    // when
    Integer returnedValue = limitService.getGlobalBlockLibrariesLimit();

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldGetGlobalUsersLimit() {
    // given
    var value = 1234;
    when(globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_USERS_LIMIT))
        .thenReturn(value);

    // when
    Integer returnedValue = limitService.getGlobalUsersLimit();

    // then
    assertEquals(value, returnedValue);
  }
}
