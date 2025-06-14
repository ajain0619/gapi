package com.nexage.app.mapper.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.seller.RTBProfileDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementExtensionDTO;
import com.nexage.app.mapper.HbPartnerAssignmentDTOMapper;
import com.nexage.app.mapper.NativePlacementDTOMapper;
import com.nexage.app.mapper.NativePlacementDTOMapperImpl;
import com.nexage.app.services.NativePlacementHbPartnerService;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.ResourceLoader;
import com.nexage.app.web.support.TestObjectsFactory;
import java.io.IOException;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NativePlacementDTOMapperTest {
  private static final String LEGAL_POSITION_POJO =
      "/data/nativeplacement/serialization/position_pojo.json";
  private static final String NATIVE_POSITION_AS_STRING =
      "/data/nativeplacement/serialization/native_placement_dto.json";

  @InjectMocks
  private NativePlacementDTOMapper nativePlacementDTOMapper = new NativePlacementDTOMapperImpl();

  @Mock private NativePlacementExtensionDTOMapper nativePlacementExtensionDTOMapper;

  @Mock private NativePlacementHbPartnerService nativePlacementHbPartnerService;

  @Mock private HbPartnerAssignmentDTOMapper hbPartnerAssignmentDTOMapper;

  @Mock Site site;

  @Test
  @SneakyThrows
  void mapNativeToPosition() {
    final NativePlacementDTO nativePlacementDTO =
        new CustomObjectMapper()
            .readValue(
                ResourceLoader.getResourceAsStream(NATIVE_POSITION_AS_STRING),
                NativePlacementDTO.class);
    final Position position =
        nativePlacementDTOMapper.map(nativePlacementDTO, nativePlacementHbPartnerService, site);
    verifyNativePlacementToPositionEquality(nativePlacementDTO, position);
    assertPositionBuyerToPlacementBuyer(nativePlacementDTO, position);
    verify(nativePlacementHbPartnerService, times(1))
        .handleHbPartnersAssignmentMapping(nativePlacementDTO, position, site);
  }

  @Test
  @SneakyThrows
  void mapNativeWithoutBuyerToPosition() {
    final NativePlacementDTO nativePlacementDTO =
        new CustomObjectMapper()
            .readValue(
                ResourceLoader.getResourceAsStream(NATIVE_POSITION_AS_STRING),
                NativePlacementDTO.class);
    nativePlacementDTO.setPlacementBuyer(null);
    final Position position =
        nativePlacementDTOMapper.map(nativePlacementDTO, nativePlacementHbPartnerService, site);
    verifyNativePlacementToPositionEquality(nativePlacementDTO, position);
    assertNull(position.getPositionBuyer(), "position buyer should be null");
  }

  @Test
  @SneakyThrows
  void mapNativeWithoutBuyerToPositionNoId() {
    final NativePlacementDTO nativePlacementDTO =
        new CustomObjectMapper()
            .readValue(
                ResourceLoader.getResourceAsStream(NATIVE_POSITION_AS_STRING),
                NativePlacementDTO.class);
    nativePlacementDTO.setPlacementBuyer(null);
    nativePlacementDTO.setPid(null);
    final Position position =
        nativePlacementDTOMapper.map(nativePlacementDTO, nativePlacementHbPartnerService, site);
    verifyNativePlacementToPositionEquality(nativePlacementDTO, position);
    assertNull(position.getPositionBuyer(), "position buyer should be null");
  }

  @Test
  @SneakyThrows
  void updatePassesPositionToHbPartnerService() {
    Position position = new Position();
    NativePlacementDTO nativePlacementDTO = new NativePlacementDTO();
    nativePlacementDTOMapper.map(
        nativePlacementDTO, position, nativePlacementHbPartnerService, site);
    verify(nativePlacementHbPartnerService, only())
        .handleHbPartnersAssignmentMapping(nativePlacementDTO, position, site);
  }

  @Test
  void mapPositionToNative() throws IOException {
    final Position position =
        new CustomObjectMapper()
            .readValue(ResourceLoader.getResourceAsStream(LEGAL_POSITION_POJO), Position.class);
    NativePlacementDTOMapper spyMapper = spy(nativePlacementDTOMapper);
    final NativePlacementDTO nativePlacementDTO =
        spyMapper.map(position, nativePlacementHbPartnerService);
    verifyPlacementToDtoEquality(position, nativePlacementDTO);
    assertPlacementBuyerToPositionBuyer(position, nativePlacementDTO);
    verify(spyMapper, times(1))
        .mapHbPartnerPosition(nativePlacementDTO, position, nativePlacementHbPartnerService);
  }

  @Test
  void mapPositionToNativeWithoutBuyer() throws IOException {
    final Position position =
        new CustomObjectMapper()
            .readValue(ResourceLoader.getResourceAsStream(LEGAL_POSITION_POJO), Position.class);
    position.setPositionBuyer(null);
    final NativePlacementDTO nativePlacementDTO =
        nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService);
    verifyPlacementToDtoEquality(position, nativePlacementDTO);
    assertNull(position.getPositionBuyer(), "position buyer should be null");
  }

  @Test
  void testHandleHbPartnerMappingToPlacement() {

    NativePlacementDTO nativePlacementDTO = new NativePlacementDTO();
    Position position = new Position();
    Set<HbPartnerPosition> hbPartPos = Set.of(new HbPartnerPosition());
    position.setHbPartnerPosition(hbPartPos);
    nativePlacementDTOMapper.mapHbPartnerPosition(
        nativePlacementDTO, position, nativePlacementHbPartnerService);

    verify(nativePlacementHbPartnerService, times(1))
        .handleHbPartnerPositionMapping(nativePlacementDTO, position);
  }

  @Test
  void shouldMapWhenOnlyBiddersFilterWhitelistSet() {
    NativePlacementDTO nativePlacementDTO = new NativePlacementDTO();
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(true);
    nativePlacementDTO.setDefaultRtbProfile(rtbProfileDTO);
    Position position =
        nativePlacementDTOMapper.map(nativePlacementDTO, nativePlacementHbPartnerService, site);
    assertTrue(position.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void shouldMapWhenOnlyBiddersFilterAllowlistSet() {
    NativePlacementDTO nativePlacementDTO = new NativePlacementDTO();
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterAllowlist(false);
    nativePlacementDTO.setDefaultRtbProfile(rtbProfileDTO);
    Position position =
        nativePlacementDTOMapper.map(nativePlacementDTO, nativePlacementHbPartnerService, site);
    assertFalse(position.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void shouldMapWhenBothBiddersFilterWhitelistBiddersFilterAllowlistSet() {
    NativePlacementDTO nativePlacementDTO = new NativePlacementDTO();
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(false);
    rtbProfileDTO.setBiddersFilterAllowlist(true);
    nativePlacementDTO.setDefaultRtbProfile(rtbProfileDTO);
    Position position =
        nativePlacementDTOMapper.map(nativePlacementDTO, nativePlacementHbPartnerService, site);
    ;
    // preference will be given to the inclusive term
    assertTrue(position.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void shouldMapBiddersFilterWhiteAndAllowlistWhenMappingPosition() {
    Position position = TestObjectsFactory.createPositions(1).get(0);
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterAllowlist(true);
    position.setDefaultRtbProfile(rtbProfile);
    NativePlacementDTO nativePlacementDTO =
        nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService);
    assertTrue(nativePlacementDTO.getDefaultRtbProfile().getBiddersFilterAllowlist());
    assertTrue(nativePlacementDTO.getDefaultRtbProfile().getBiddersFilterWhitelist());
    rtbProfile.setBiddersFilterAllowlist(false);
    position.setDefaultRtbProfile(rtbProfile);
    nativePlacementDTO = nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService);
    assertFalse(nativePlacementDTO.getDefaultRtbProfile().getBiddersFilterAllowlist());
    assertFalse(nativePlacementDTO.getDefaultRtbProfile().getBiddersFilterWhitelist());
  }

  @Test
  void mapExternalAdVerificationSamplingRateWithNullValue() throws IOException {
    final Position position =
        new CustomObjectMapper()
            .readValue(ResourceLoader.getResourceAsStream(LEGAL_POSITION_POJO), Position.class);
    position.setExternalAdVerificationSamplingRate(null);
    final NativePlacementDTO nativePlacementDTO =
        nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService);
    verifyPlacementToDtoEquality(position, nativePlacementDTO);
    assertNull(
        nativePlacementDTO.getExternalAdVerificationSamplingRate(), "sampling rate should be null");
  }

  @Test
  void mapExternalAdVerificationSamplingRateWithSomeValue() throws IOException {
    final Position position =
        new CustomObjectMapper()
            .readValue(ResourceLoader.getResourceAsStream(LEGAL_POSITION_POJO), Position.class);
    final NativePlacementDTO nativePlacementDTO =
        nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService);
    verifyPlacementToDtoEquality(position, nativePlacementDTO);
    assertEquals(
        30.5f,
        nativePlacementDTO.getExternalAdVerificationSamplingRate(),
        "sampling rate should be have value 30.5f");
  }

  private void assertPlacementBuyerToPositionBuyer(
      Position position, NativePlacementDTO nativePlacementDTO) {
    assertEquals(
        position.getPositionBuyer().getVersion(),
        nativePlacementDTO.getPlacementBuyer().getVersion());
    assertEquals(
        position.getPositionBuyer().getPid(), nativePlacementDTO.getPlacementBuyer().getPid());
    assertEquals(
        position.getPositionBuyer().getBuyerPositionId(),
        nativePlacementDTO.getPlacementBuyer().getSectionId());
  }

  private void assertPositionBuyerToPlacementBuyer(NativePlacementDTO source, Position position) {
    assertEquals(source.getPlacementBuyer().getVersion(), position.getPositionBuyer().getVersion());
    assertEquals(
        source.getPlacementBuyer().getSectionId(),
        position.getPositionBuyer().getBuyerPositionId());
    assertEquals(source.getPlacementBuyer().getPid(), position.getPositionBuyer().getPid());
    assertEquals(source.getPid(), position.getPositionBuyer().getPositionPid());
  }

  void verifyNativePlacementToPositionEquality(
      NativePlacementDTO nativePlacementDTO, Position position) {
    assertEquals(nativePlacementDTO.getName(), position.getName());
    assertEquals(nativePlacementDTO.getMemo(), position.getMemo());
    assertEquals(nativePlacementDTO.getPid(), position.getPid());
    assertEquals(nativePlacementDTO.getVersion(), position.getVersion());
    assertEquals(nativePlacementDTO.getScreenLocation(), position.getScreenLocation());
    assertEquals(nativePlacementDTO.getVideoSupport(), position.getVideoSupport());
    assertEquals(nativePlacementDTO.getPlacementCategory(), position.getPlacementCategory());
    assertEquals(nativePlacementDTO.getMraidSupport(), position.getMraidSupport());
    assertEquals(nativePlacementDTO.getMraidAdvancedTracking(), position.isMraidAdvancedTracking());
    assertEquals(nativePlacementDTO.getPositionAliasName(), position.getPositionAliasName());

    verify(nativePlacementExtensionDTOMapper, times(1))
        .convertToPosition(any(NativePlacementExtensionDTO.class));

    verify(nativePlacementHbPartnerService, times(1))
        .handleHbPartnersAssignmentMapping(
            any(NativePlacementDTO.class), any(Position.class), eq(site));
  }

  void verifyPlacementToDtoEquality(Position position, NativePlacementDTO nativePlacementDTO) {
    assertEquals(position.getName(), nativePlacementDTO.getName());
    assertEquals(position.getMemo(), nativePlacementDTO.getMemo());
    assertEquals(position.getPid(), nativePlacementDTO.getPid());
    assertEquals(position.getVersion(), nativePlacementDTO.getVersion());
    assertEquals(position.getScreenLocation(), nativePlacementDTO.getScreenLocation());
    assertEquals(position.getVideoSupport(), nativePlacementDTO.getVideoSupport());
    assertEquals(position.getPlacementCategory(), nativePlacementDTO.getPlacementCategory());
    assertEquals(position.getMraidSupport(), nativePlacementDTO.getMraidSupport());
    assertEquals(position.isMraidAdvancedTracking(), nativePlacementDTO.getMraidAdvancedTracking());
    assertEquals(position.getPositionAliasName(), nativePlacementDTO.getPositionAliasName());
    assertNull(position.getSite());

    verify(nativePlacementExtensionDTOMapper, times(1))
        .convertToNativePlacementExtensionDto(any(String.class));
    assertEquals(
        position.getExternalAdVerificationSamplingRate(),
        nativePlacementDTO.getExternalAdVerificationSamplingRate());
  }
}
