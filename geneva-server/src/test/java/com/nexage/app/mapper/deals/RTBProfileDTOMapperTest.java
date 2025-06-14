package com.nexage.app.mapper.deals;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.mapper.deal.RTBProfileDTOMapper;
import java.math.BigDecimal;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

class RTBProfileDTOMapperTest {

  @Test
  void shouldReturnACorrectlyMappedRTBProfileDTOWhenDealRTBProfileViewUsingFormulasObjectIsPassed()
      throws IllegalAccessException {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();

    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "pid", 113L, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "description", "test description", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "auctionType", 1, true);
    FieldUtils.writeField(
        dealRtbProfileViewUsingFormulas, "defaultReserve", new BigDecimal(1), true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "lowReserve", new BigDecimal(2), true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "pubAlias", 1L, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "pubPid", 111L, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "pubName", "testName", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "pubNameAlias", "testNameAlias", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "siteAlias", 1L, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "sitePid", 112L, true);
    FieldUtils.writeField(
        dealRtbProfileViewUsingFormulas, "siteNameAlias", "testSiteNameAlias", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "siteName", "testSiteName", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "includeSiteName", 1, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "tagPid", 1L, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "tagName", "testTagName", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "siteType", 'a', true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "categories", "testCategories", true);

    // Countries
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "target", "testTarget", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "countryPid", 1L, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "ruleType", "testRuleType", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "targetType", "testTargetType", true);

    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "platform", "ANDROID", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "videoSupport", 1, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "height", 5, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "width", 10, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "placementCategory", 2, true);
    FieldUtils.writeField(
        dealRtbProfileViewUsingFormulas, "placementName", "testPlacementName", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "placementPid", 10L, true);

    // when
    RTBProfileDTO rtbProfileDTO = RTBProfileDTOMapper.MAPPER.map(dealRtbProfileViewUsingFormulas);

    // then
    validatePublisherDetails(rtbProfileDTO, dealRtbProfileViewUsingFormulas);
    validateSiteDetails(rtbProfileDTO, dealRtbProfileViewUsingFormulas);
    validatePlacementDetails(rtbProfileDTO, dealRtbProfileViewUsingFormulas);

    assertEquals(rtbProfileDTO.getRtbProfilePid(), dealRtbProfileViewUsingFormulas.getPid());
    assertEquals(
        rtbProfileDTO.getAuctionType().longValue(),
        dealRtbProfileViewUsingFormulas.getAuctionType());
    assertEquals(
        rtbProfileDTO.getDefaultReserve(), dealRtbProfileViewUsingFormulas.getDefaultReserve());
    assertEquals(rtbProfileDTO.getIsRealName(), dealRtbProfileViewUsingFormulas.getIsRealName());
    assertEquals(rtbProfileDTO.getTagPid(), dealRtbProfileViewUsingFormulas.getTagPid());
    assertEquals(rtbProfileDTO.getTagName(), dealRtbProfileViewUsingFormulas.getTagName());

    assertEquals(rtbProfileDTO.getCategories(), dealRtbProfileViewUsingFormulas.getCategories());
    assertEquals(
        rtbProfileDTO.getCountries().toString(),
        dealRtbProfileViewUsingFormulas.getCountries().toString());
    assertEquals(
        rtbProfileDTO.getVideoSupport(), dealRtbProfileViewUsingFormulas.getVideoSupport());
    assertEquals(rtbProfileDTO.getHeight(), dealRtbProfileViewUsingFormulas.getHeight());
    assertEquals(rtbProfileDTO.getWidth(), dealRtbProfileViewUsingFormulas.getWidth());
    assertEquals(rtbProfileDTO.getDescription(), dealRtbProfileViewUsingFormulas.getDescription());
    assertEquals(rtbProfileDTO.getLowFloor(), dealRtbProfileViewUsingFormulas.getLowReserve());
  }

  private void validatePublisherDetails(
      RTBProfileDTO rtbProfileDTO,
      DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas) {
    assertEquals(rtbProfileDTO.getPubId(), dealRtbProfileViewUsingFormulas.getPubPid());
    assertEquals(rtbProfileDTO.getPubName(), dealRtbProfileViewUsingFormulas.getPubName());
    assertEquals(rtbProfileDTO.getPubAlias(), dealRtbProfileViewUsingFormulas.getPubAlias());
    assertEquals(
        rtbProfileDTO.getPubNameAlias(), dealRtbProfileViewUsingFormulas.getPubNameAlias());
  }

  private void validateSiteDetails(
      RTBProfileDTO rtbProfileDTO,
      DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas) {
    assertEquals(rtbProfileDTO.getSiteId(), dealRtbProfileViewUsingFormulas.getSitePid());
    assertEquals(rtbProfileDTO.getSiteName(), dealRtbProfileViewUsingFormulas.getSiteName());
    assertEquals(rtbProfileDTO.getSiteType(), dealRtbProfileViewUsingFormulas.getSiteType());
    assertEquals(rtbProfileDTO.getSiteAlias(), dealRtbProfileViewUsingFormulas.getSiteAlias());
    assertEquals(
        rtbProfileDTO.getSiteNameAlias(), dealRtbProfileViewUsingFormulas.getSiteNameAlias());
  }

  private void validatePlacementDetails(
      RTBProfileDTO rtbProfileDTO,
      DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas) {
    assertEquals(
        rtbProfileDTO.getPlacementPid(), dealRtbProfileViewUsingFormulas.getPlacementPid());
    assertEquals(
        rtbProfileDTO.getPlacementName(), dealRtbProfileViewUsingFormulas.getPlacementName());
    assertEquals(
        rtbProfileDTO.getPlacementType(), dealRtbProfileViewUsingFormulas.getPlacementType());
  }
}
