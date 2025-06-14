package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.RTBProfileDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.PlacementsService;
import com.nexage.app.services.SellerLimitService;
import com.nexage.app.web.placement.PlacementDTOController;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlacementDTOControllerTest {

  private Long siteId;
  private Long sellerId;
  @Mock private PlacementsService placementsService;
  @Mock private UserContext userContext;
  @Mock private SellerLimitService sellerLimitService;
  @InjectMocks private PlacementDTOController placementDTOController;

  @BeforeEach
  public void setUp() {
    siteId = RandomUtils.nextLong();
    sellerId = RandomUtils.nextLong();
  }

  @Test
  void shouldCreatePlacementDTO() {
    PlacementDTO placementDTO = createValidPlacementDTO();
    when(userContext.isNexageUser()).thenReturn(true);
    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    assertNotNull(
        placementDTOController.createPlacementDTO(sellerId, siteId, placementDTO).getBody());
  }

  @Test
  void shouldThrowExceptionWhenServiceLimitIsReached() {
    PlacementDTO placementDTO = createValidPlacementDTO();
    when(userContext.isNexageUser()).thenReturn(false);
    when(sellerLimitService.isLimitEnabled(sellerId)).thenReturn(true);
    when(sellerLimitService.canCreatePositionsInSite(sellerId, siteId)).thenReturn(false);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> placementDTOController.createPlacementDTO(sellerId, siteId, placementDTO));
    assertEquals(ServerErrorCodes.SERVER_LIMIT_REACHED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenSitePidInSiteNotEqualToSitePidArgInCreatePlacementDTO() {
    when(userContext.isNexageUser()).thenReturn(true);
    PlacementDTO placementDTO = createValidPlacementDTO();
    placementDTO.getSite().setPid(siteId + 1);

    assertThrows(
        GenevaValidationException.class,
        () -> placementDTOController.createPlacementDTO(sellerId, siteId, placementDTO));
  }

  @Test
  void shouldUpdatePlacementDTO() {
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createValidPlacementDTO();
    placementDTO.setPid(placementId);
    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    assertNotNull(
        placementDTOController
            .updatePlacementDTO(sellerId, siteId, placementId, placementDTO)
            .getBody());
  }

  @Test
  void shouldThrowExceptionWhenSitePidInSiteNotEqualToSitePidArgInUpdatePlacementDTO() {
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createValidPlacementDTO();
    placementDTO.setPid(placementId);
    placementDTO.getSite().setPid(siteId + 1);
    assertThrows(
        GenevaValidationException.class,
        () ->
            placementDTOController.updatePlacementDTO(sellerId, siteId, placementId, placementDTO));
  }

  @Test
  void shouldThrowExceptionWhenPlacementIdNotEqualToPlacementIdInDTOWhenUpdating() {
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createValidPlacementDTO();
    placementDTO.setPid(placementId + 1);
    assertThrows(
        GenevaValidationException.class,
        () ->
            placementDTOController.updatePlacementDTO(sellerId, siteId, placementId, placementDTO));
  }

  @Test
  void shouldUpdatePlacementDTOOnlyBiddersFilterWhitelistSet() {
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createValidPlacementDTO();
    placementDTO.setPid(placementId);
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(true);
    placementDTO.setDefaultRtbProfile(rtbProfileDTO);
    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    assertNotNull(
        placementDTOController
            .updatePlacementDTO(sellerId, siteId, placementId, placementDTO)
            .getBody());
    assertTrue(placementDTO.getDefaultRtbProfile().getBiddersFilterAllowlist());
    assertTrue(placementDTO.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void shouldUpdatePlacementDTOOnlyBiddersFilterAllowlistSet() {
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createValidPlacementDTO();
    placementDTO.setPid(placementId);
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterAllowlist(false);
    placementDTO.setDefaultRtbProfile(rtbProfileDTO);
    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    assertNotNull(
        placementDTOController
            .updatePlacementDTO(sellerId, siteId, placementId, placementDTO)
            .getBody());
    assertFalse(placementDTO.getDefaultRtbProfile().getBiddersFilterAllowlist());
    assertFalse(placementDTO.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void shouldUpdatePlacementDTOWhenBothBiddersFilterWhitelistBiddersFilterAllowlistSet() {
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createValidPlacementDTO();
    placementDTO.setPid(placementId);
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(true);
    rtbProfileDTO.setBiddersFilterAllowlist(false);
    placementDTO.setDefaultRtbProfile(rtbProfileDTO);
    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    assertNotNull(
        placementDTOController
            .updatePlacementDTO(sellerId, siteId, placementId, placementDTO)
            .getBody());
    // preference will be given to the inclusive term in the input
    assertFalse(placementDTO.getDefaultRtbProfile().getBiddersFilterAllowlist());
    assertFalse(placementDTO.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  private PlacementDTO createValidPlacementDTO() {
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setWidth(100);
    placementDTO.setHeight(100);
    placementDTO.setName("foo");
    placementDTO.setMemo("bar");
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setSite(siteDTO);
    return placementDTO;
  }

  @Test
  void shouldCreatePlacementDTOWithPlacementVideoDTO() {
    PlacementDTO placementDTO = createValidPlacementDTO();
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);

    PlacementDTO savedPlacementDTO =
        placementDTOController.createPlacementDTO(sellerId, siteId, placementDTO).getBody();
    assertNotNull(savedPlacementDTO);
    assertNotNull(savedPlacementDTO.getPlacementVideo());
  }

  @Test
  void shouldCreatePlacementDTOWithLongformPlacementVideoDTO() {
    PlacementDTO placementDTO = createValidPlacementDTO();
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    placementVideoDTO.setPlayerBrand("test_player");
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);

    PlacementDTO savedPlacementDTO =
        placementDTOController.createPlacementDTO(sellerId, siteId, placementDTO).getBody();
    assertNotNull(savedPlacementDTO);
    PlacementVideoDTO savedPlacementVideoDTO = savedPlacementDTO.getPlacementVideo();
    assertNotNull(savedPlacementVideoDTO);
    assertTrue(savedPlacementVideoDTO.isLongform());
    assertEquals(PlacementVideoStreamType.VOD, savedPlacementVideoDTO.getStreamType());
    assertEquals("test_player", savedPlacementVideoDTO.getPlayerBrand());
    assertEquals(PlacementVideoSsai.ALL_SERVER_SIDE, savedPlacementVideoDTO.getSsai());
  }

  @Test
  void shouldUpdatePlacementDTOWithPlacementVideoDTO() {
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createValidPlacementDTO();
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);
    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    PlacementDTO updatedPlacementDTO =
        placementDTOController
            .updatePlacementDTO(sellerId, siteId, placementId, placementDTO)
            .getBody();
    assertNotNull(updatedPlacementDTO);
    assertNotNull(updatedPlacementDTO.getPlacementVideo());
  }
}
