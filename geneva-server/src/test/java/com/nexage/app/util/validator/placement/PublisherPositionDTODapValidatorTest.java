package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.BANNER;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoPlacementType;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.handler.MessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

class PublisherPositionDTODapValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTODapConstraint placementDTODapConstraint;
  @Mock private MessageHandler messageHandler;
  @InjectMocks private PublisherPositionDTODapValidator validator;
  @InjectMocks private PlacementDTODapValidator placementDTODapValidator;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(validator, "placementDTODapValidator", placementDTODapValidator);
  }

  private static final String VALID_PLAYER_ID_PLAYLIST_ID = "test-valid-playerid-playlistid";
  private static final String INVALID_PLAYER_ID_PLAYLIST_ID = "Invalid#@PlayerID!PlayListID";
  private static final String MAX_LENGTH_PLAYER_ID_PLAYLIST_ID =
      "abcdefghijklmnopqrstuvwxyz-1234567890-abcd-123-123-123-asdfasdf-asdf";
  private static final String VIDEO_PLACEMENT_TYPE_CONSTRAINT_DAP =
      "Video placement type is mandatory for DAP placement";
  private static final String PLACEMENT_DAP_CONSTRAINT_VIOLATION =
      "Placement Category must be Banner or Medium Rectangle or Interstitial and site type should be Desktop or Mobile Web and Placement should support both Video and Banner for enabling  DAP settings";
  private static final String PLAYER_REQUIRED_DAP_CONSTRAINT =
      "If player required is false for DAP then it should not have player type or player id or playlist id ";
  private static final String PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP =
      "Player height and width should be null for DAP Placement ";
  private static final String DAP_PLAYER_TYPE_CONSTRAINT =
      "DAP Player Type can not be null if player required is true";
  private static final String PLAYER_ID_CONSTRAINT =
      "Invalid player Id or Player ID is not supported for Yahoo player type DAP";
  private static final String PLAYLIST_ID_CONSTRAINT = "Invalid playlist Id";

  @Test
  void falseWhenSiteTypeNull() {
    assertFalse(
        validator.isValid(
            createDapPublisherPositionDTO(null, INSTREAM_VIDEO, VIDEO_AND_BANNER), ctx));
    verifyValidationMessage(placementDTODapConstraint.emptyMessage());
  }

  @Test
  void falseWhenPlacementCategoryNull() {
    assertFalse(
        validator.isValid(
            createDapPublisherPositionDTO(SiteType.MOBILE_WEB, null, VIDEO_AND_BANNER), ctx));
    verifyValidationMessage(placementDTODapConstraint.emptyMessage());
  }

  @Test
  void trueWhenNonVideoPlacement() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, BANNER);
    publisherPositionDTO.setPlacementVideo(null);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
  }

  @Test
  void trueDapWhenVideoSupportIsNotVideoAndBanner() {
    assertTrue(
        validator.isValid(
            createDapPublisherPositionDTO(
                SiteType.MOBILE_WEB, PlacementCategory.BANNER, VideoSupport.BANNER),
            ctx));
  }

  @Test
  void validateNonDapVideoPlacementWithDapVideoPlacementType() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(
            SiteType.DESKTOP, PlacementCategory.INSTREAM_VIDEO, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setVideoPlacementType(VideoPlacementType.INTERSTITIAL);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateNonDapVideoPlacementWithDapPlayerType() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(
            SiteType.DESKTOP, PlacementCategory.INSTREAM_VIDEO, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateNonDapVideoPlacementWithDapPlayerId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(
            SiteType.DESKTOP, PlacementCategory.INSTREAM_VIDEO, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateNonDapVideoPlacementWithDapPlayListId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(
            SiteType.DESKTOP, PlacementCategory.INSTREAM_VIDEO, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateDapPlacementWithPlayerRequired() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_DAP_PLAYER_TYPE_CONSTRAINT.toString()))
        .thenReturn(DAP_PLAYER_TYPE_CONSTRAINT);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(DAP_PLAYER_TYPE_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithDapPlayerTypeAndInvalidPlayerId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    publisherPositionDTO.getPlacementVideo().setPlayerId(INVALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYER_ID_CONSTRAINT);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLAYER_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithDapPlayerTypeAndMaxLenPlayerId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    publisherPositionDTO.getPlacementVideo().setPlayerId(MAX_LENGTH_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYER_ID_CONSTRAINT);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLAYER_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithDapPlayerTypeAndInvalidPlayListId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    publisherPositionDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    publisherPositionDTO.getPlacementVideo().setPlayListId(INVALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYLIST_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYLIST_ID_CONSTRAINT);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLAYLIST_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithDapPlayerTypeAndMaxLenPlayListId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    publisherPositionDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    publisherPositionDTO.getPlacementVideo().setPlayListId(MAX_LENGTH_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYLIST_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYLIST_ID_CONSTRAINT);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLAYLIST_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithYahooPlayerTypeAndPlayerId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.YAHOO);
    publisherPositionDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYER_ID_CONSTRAINT);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLAYER_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithPlayerHight() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    publisherPositionDTO.getPlacementVideo().setPlayerHeight(350);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP.toString()))
        .thenReturn(PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP);
  }

  @Test
  void validateDapPlacementWithPlayerWidth() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    publisherPositionDTO.getPlacementVideo().setPlayerWidth(400);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP.toString()))
        .thenReturn(PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP);
  }

  @Test
  void validateDapPlacementWithoutPlayerRequired() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(false);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_REQUIRED_DAP_CONSTRAINT.toString()))
        .thenReturn(PLAYER_REQUIRED_DAP_CONSTRAINT);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(PLAYER_REQUIRED_DAP_CONSTRAINT);
  }

  @Test
  void validateDapPlacemenWithoutVideoPlacementType() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(
            SiteType.DESKTOP, PlacementCategory.INTERSTITIAL, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.YAHOO);
    publisherPositionDTO.getPlacementVideo().setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);
    publisherPositionDTO.getPlacementVideo().setVideoPlacementType(null);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_VIDEO_PLACEMENT_TYPE_CONSTRAINT_DAP.toString()))
        .thenReturn(VIDEO_PLACEMENT_TYPE_CONSTRAINT_DAP);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(VIDEO_PLACEMENT_TYPE_CONSTRAINT_DAP);
  }

  @Test
  void validateDapPlacementWithDapDefaultParams() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(false);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE,
        publisherPositionDTO.getPlacementVideo().getVideoPlacementType());
    assertFalse(publisherPositionDTO.getPlacementVideo().isPlayerRequired());
    assertNull(publisherPositionDTO.getPlacementVideo().getDapPlayerType());
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayerId());
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validateDapInterstitialPlacementWithDapDefaultParams() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(
            SiteType.DESKTOP, PlacementCategory.INTERSTITIAL, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(false);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE,
        publisherPositionDTO.getPlacementVideo().getVideoPlacementType());
    assertFalse(publisherPositionDTO.getPlacementVideo().isPlayerRequired());
    assertNull(publisherPositionDTO.getPlacementVideo().getDapPlayerType());
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayerId());
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validO2DapPlacementWithoutPlayerId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayerId());
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validO2DapPlacementWithoutPlayListId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    publisherPositionDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
    assertEquals(
        VALID_PLAYER_ID_PLAYLIST_ID, publisherPositionDTO.getPlacementVideo().getPlayerId());
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validO2DapPlacementWithInputValues() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);

    VideoPlacementType videoPlacementType = VideoPlacementType.IN_ARTICLE;
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    publisherPositionDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    publisherPositionDTO.getPlacementVideo().setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);
    publisherPositionDTO.getPlacementVideo().setVideoPlacementType(videoPlacementType);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
    assertEquals(
        videoPlacementType, publisherPositionDTO.getPlacementVideo().getVideoPlacementType());
    assertEquals(DapPlayerType.O2, publisherPositionDTO.getPlacementVideo().getDapPlayerType());
    assertEquals(
        VALID_PLAYER_ID_PLAYLIST_ID, publisherPositionDTO.getPlacementVideo().getPlayerId());
    assertEquals(
        VALID_PLAYER_ID_PLAYLIST_ID, publisherPositionDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validYahooDapPlacementWithoutPlaylistId() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(SiteType.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.YAHOO);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE,
        publisherPositionDTO.getPlacementVideo().getVideoPlacementType());
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayListId());
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayerId());
  }

  @Test
  void validYahooDapPlacementWithInputValues() {
    PublisherPositionDTO publisherPositionDTO =
        createDapPublisherPositionDTO(
            SiteType.DESKTOP, PlacementCategory.INTERSTITIAL, VIDEO_AND_BANNER);
    publisherPositionDTO.getPlacementVideo().setPlayerRequired(true);
    publisherPositionDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.YAHOO);
    publisherPositionDTO.getPlacementVideo().setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE,
        publisherPositionDTO.getPlacementVideo().getVideoPlacementType());
    assertEquals(
        VALID_PLAYER_ID_PLAYLIST_ID, publisherPositionDTO.getPlacementVideo().getPlayListId());
    assertNull(publisherPositionDTO.getPlacementVideo().getPlayerId());
  }

  private PublisherPositionDTO createMinimalPublisherPositionDTO(
      PublisherSiteDTO.SiteType siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      PublisherSiteDTO.Platform platformType) {
    PublisherSiteDTO site = new PublisherSiteDTO();
    site.setType(siteType);
    site.setPlatform(platformType);

    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setSite(site);
    publisherPositionDTO.setPlacementCategory(placementCategory);
    publisherPositionDTO.setVideoSupport(videoSupport);
    return publisherPositionDTO;
  }

  private PublisherPositionDTO createDapPublisherPositionDTO(
      PublisherSiteDTO.SiteType siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport) {

    PublisherSiteDTO site = new PublisherSiteDTO();
    site.setType(siteType);

    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setSite(site);
    publisherPositionDTO.setPlacementCategory(placementCategory);
    publisherPositionDTO.setVideoSupport(videoSupport);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPlayerHeight(null);
    placementVideoDTO.setPlayerWidth(null);
    placementVideoDTO.setVideoPlacementType(VideoPlacementType.IN_ARTICLE);
    publisherPositionDTO.setPlacementVideo(placementVideoDTO);
    return publisherPositionDTO;
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(ctx).buildConstraintViolationWithTemplate(expectedMessage);
  }

  @Override
  public void initializeConstraint() {
    lenient().when(placementDTODapConstraint.message()).thenReturn(ValidationMessages.WRONG_VALUE);
    lenient()
        .when(placementDTODapConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
    lenient().when(placementDTODapConstraint.field()).thenReturn("placementCategory");
  }
}
