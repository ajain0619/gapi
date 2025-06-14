package com.nexage.app.mapper.site;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.PlacementDooh;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.dto.seller.RTBProfileDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import java.math.BigDecimal;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;

class PlacementDTOMapperTest {

  @Test
  void shouldMapPlacementDTOToPosition() {

    Type siteType = Type.DESKTOP;
    Long sitePid = RandomUtils.nextLong();
    Long placementPid = RandomUtils.nextLong();
    PlacementCategory placementCategory = PlacementCategory.BANNER;

    SiteDTO site = new SiteDTO();
    site.setType(siteType);
    site.setPid(sitePid);
    site.setStatus(Status.ACTIVE);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPid(placementPid);
    placementDTO.setSite(site);
    placementDTO.setPlacementCategory(placementCategory);

    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    assertEquals(sitePid, position.getSite().getPid());
    assertEquals(siteType, position.getSite().getType());
    assertEquals(placementCategory, position.getPlacementCategory());
    assertEquals(placementPid, position.getPid());
    assertEquals(Status.ACTIVE, position.getSite().getStatus());
  }

  @Test
  void shouldMapPlacementDTOWithDoohToPosition() {

    Type siteType = Type.DOOH;
    Long sitePid = RandomUtils.nextLong();
    Long placementPid = RandomUtils.nextLong();
    PlacementCategory placementCategory = PlacementCategory.BANNER;

    SiteDTO site = new SiteDTO();
    site.setType(siteType);
    site.setPid(sitePid);
    site.setStatus(Status.ACTIVE);

    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPid(placementPid);
    placementDTO.setSite(site);
    placementDTO.setPlacementCategory(placementCategory);

    PlacementDoohDTO placementDoohDTO = new PlacementDoohDTO();
    placementDoohDTO.setDefaultImpressionMultiplier(BigDecimal.ONE);
    placementDTO.setDooh(placementDoohDTO);

    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    assertEquals(sitePid, position.getSite().getPid());
    assertEquals(siteType, position.getSite().getType());
    assertEquals(placementCategory, position.getPlacementCategory());
    assertEquals(placementPid, position.getPid());
    assertEquals(
        placementDoohDTO.getDefaultImpressionMultiplier(),
        position.getPlacementDooh().getDefaultImpressionMultiplier());
    assertEquals(Status.ACTIVE, position.getSite().getStatus());
  }

  @Test
  void shouldMapPositionWithDoohToPlacementDTO() {

    Type siteType = Type.DOOH;
    Long sitePid = RandomUtils.nextLong();
    Long placementPid = RandomUtils.nextLong();
    PlacementCategory placementCategory = PlacementCategory.BANNER;

    Site site = new Site();
    site.setType(siteType);
    site.setStatus(Status.ACTIVE);
    site.setPid(sitePid);

    Position position = new Position();
    position.setPid(placementPid);
    position.setSite(site);
    position.setPlacementCategory(placementCategory);

    PlacementDooh placementDooh = new PlacementDooh();
    placementDooh.setDefaultImpressionMultiplier(BigDecimal.ONE);
    position.setPlacementDooh(placementDooh);

    PlacementDTO placementDTO = PlacementDTOMapper.MAPPER.map(position);
    assertEquals(placementCategory, placementDTO.getPlacementCategory());
    assertEquals(
        position.getPlacementDooh().getDefaultImpressionMultiplier(),
        placementDTO.getDooh().getDefaultImpressionMultiplier());
    assertEquals(placementPid, placementDTO.getPid());
  }

  @Test
  void shouldMapInterstitialTrueWhenMappingPosition() {
    Position position = TestObjectsFactory.createPositions(1).get(0);
    position.setIsInterstitial(true);
    PlacementDTO placementDTO = PlacementDTOMapper.MAPPER.map(position);
    assertTrue(placementDTO.getInterstitial());
  }

  @Test
  void shouldMapInterstitialTrueWhenMappingPlacementDTO() {
    PlacementDTO placementDTO = TestObjectsFactory.createPlacements(1).get(0);
    placementDTO.setInterstitial(true);
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    assertTrue(position.getIsInterstitial());
  }

  @Test
  void shouldMapPlacementDTOToPositionWithNullSiteStatus() {
    Type siteType = Type.DESKTOP;
    Long sitePid = RandomUtils.nextLong();
    Long placementPid = RandomUtils.nextLong();
    PlacementCategory placementCategory = PlacementCategory.BANNER;

    SiteDTO site = new SiteDTO();
    site.setType(siteType);
    site.setPid(sitePid);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPid(placementPid);
    placementDTO.setSite(site);
    placementDTO.setPlacementCategory(placementCategory);

    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    assertEquals(sitePid, position.getSite().getPid());
    assertEquals(siteType, position.getSite().getType());
    assertEquals(placementCategory, position.getPlacementCategory());
    assertEquals(placementPid, position.getPid());
  }

  @Test
  void shouldMapDefaultValueForPosition() {
    PlacementDTO placementDTO = new PlacementDTO();
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    assertNotNull(position.getIsDefault());
    assertFalse(position.getIsDefault());
  }

  @Test
  void shouldMapWhenOnlyBiddersFilterWhitelistSet() {
    PlacementDTO placementDTO = TestObjectsFactory.createPlacements(1).get(0);
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(true);
    placementDTO.setDefaultRtbProfile(rtbProfileDTO);
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    assertTrue(position.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void shouldMapWhenOnlyBiddersFilterAllowlistSet() {
    PlacementDTO placementDTO = TestObjectsFactory.createPlacements(1).get(0);
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterAllowlist(false);
    placementDTO.setDefaultRtbProfile(rtbProfileDTO);
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    assertFalse(position.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void shouldMapWhenBothBiddersFilterWhitelistBiddersFilterAllowlistSet() {
    PlacementDTO placementDTO = TestObjectsFactory.createPlacements(1).get(0);
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(false);
    rtbProfileDTO.setBiddersFilterAllowlist(true);
    placementDTO.setDefaultRtbProfile(rtbProfileDTO);
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    // preference will be given to the inclusive term
    assertTrue(position.getDefaultRtbProfile().getBiddersFilterWhitelist());
    rtbProfileDTO.setBiddersFilterWhitelist(true);
    rtbProfileDTO.setBiddersFilterAllowlist(false);
    placementDTO.setDefaultRtbProfile(rtbProfileDTO);
    position = PlacementDTOMapper.MAPPER.map(placementDTO);
    // preference will be given to the inclusive term
    assertFalse(position.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void shouldMapBiddersFilterAllowlistWhenMappingPosition() {
    Position position = TestObjectsFactory.createPositions(1).get(0);
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterAllowlist(true);
    position.setDefaultRtbProfile(rtbProfile);
    PlacementDTO placementDTO = PlacementDTOMapper.MAPPER.map(position);
    assertTrue(placementDTO.getDefaultRtbProfile().getBiddersFilterAllowlist());
    assertTrue(placementDTO.getDefaultRtbProfile().getBiddersFilterWhitelist());
    rtbProfile.setBiddersFilterAllowlist(false);
    position.setDefaultRtbProfile(rtbProfile);
    placementDTO = PlacementDTOMapper.MAPPER.map(position);
    assertFalse(placementDTO.getDefaultRtbProfile().getBiddersFilterAllowlist());
    assertFalse(placementDTO.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void shouldMapPositionViewtoPlamentDTO() {
    PositionView positionView = TestObjectsFactory.createPositionView();
    PlacementDTO placementDTO = PlacementDTOMapper.MAPPER.map(positionView);
    assertAll(
        () -> assertEquals(positionView.getPid(), placementDTO.getPid()),
        () -> assertEquals(positionView.getName(), placementDTO.getName()));
  }
}
