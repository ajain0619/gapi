package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.BANNER;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO;
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
import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.handler.MessageHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTODapValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTODapConstraint placementDTODapConstraint;
  @Mock private MessageHandler messageHandler;
  @InjectMocks private PlacementDTODapValidator validator;

  private static final String VALID_PLAYER_ID_PLAYLIST_ID = "test-valid-playerid-playlistid";
  private static final String INVALID_PLAYER_ID_PLAYLIST_ID = "Invalid#@PlayerID!PlayListID";
  private static final String MAX_LENGTH_PLAYER_ID_PLAYLIST_ID =
      "abcdefghijklmnopqrstuvwxyz-1234567890-abcd-123-123-123-asdfasdf-asdf";
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
        validator.isValid(createDapPlacementDTO(null, INSTREAM_VIDEO, VIDEO_AND_BANNER), ctx));
    verifyValidationMessage(placementDTODapConstraint.emptyMessage());
  }

  @Test
  void falseWhenPlacementCategoryNull() {
    assertFalse(
        validator.isValid(createDapPlacementDTO(Type.MOBILE_WEB, null, VIDEO_AND_BANNER), ctx));
    verifyValidationMessage(placementDTODapConstraint.emptyMessage());
  }

  @Test
  void falseWhenVideoSupportNull() {
    assertFalse(
        validator.isValid(
            createDapPlacementDTO(Type.MOBILE_WEB, PlacementCategory.BANNER, null), ctx));
    verifyValidationMessage(placementDTODapConstraint.emptyMessage());
  }

  @Test
  void trueWhenVideoPlacementTypeIsNull() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.setPlacementVideo(null);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void trueWhenVideoSupportIsNotVideoAndBanner() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, BANNER);
    placementDTO.setPlacementVideo(null);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void falseDapWhenVideoSupportIsNotVideoAndBannerAndHasDapParams() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.MOBILE_WEB, PlacementCategory.BANNER, BANNER);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void falseWhenNonSupportSiteTypeForDapPlacement() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(
            Type.APPLICATION, PlacementCategory.MEDIUM_RECTANGLE, VIDEO_AND_BANNER);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateVideoTypeForDapPlacement() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.MEDIUM_RECTANGLE, VIDEO);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateNonDapVideoPlacementWithDapVideoPlacementType() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.INSTREAM_VIDEO, VIDEO_AND_BANNER);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateNonDapVideoPlacementWithDapPlayerType() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.INSTREAM_VIDEO, VIDEO_AND_BANNER);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateNonDapVideoPlacementWithDapPlayerId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.INSTREAM_VIDEO, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateNonDapVideoPlacementWithDapPlayListId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.INSTREAM_VIDEO, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION.toString()))
        .thenReturn(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLACEMENT_DAP_CONSTRAINT_VIOLATION);
  }

  @Test
  void validateDapPlacementWithPlayerRequired() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_DAP_PLAYER_TYPE_CONSTRAINT.toString()))
        .thenReturn(DAP_PLAYER_TYPE_CONSTRAINT);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(DAP_PLAYER_TYPE_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithDapPlayerTypeAndInvalidPlayerId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setPlayerId(INVALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYER_ID_CONSTRAINT);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYER_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithDapPlayerTypeAndMaxLenPlayerId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setPlayerId(MAX_LENGTH_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYER_ID_CONSTRAINT);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYER_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithDapPlayerTypeAndInvalidPlayListId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    placementDTO.getPlacementVideo().setPlayListId(INVALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYLIST_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYLIST_ID_CONSTRAINT);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYLIST_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithDapPlayerTypeAndMaxLenPlayListId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    placementDTO.getPlacementVideo().setPlayListId(MAX_LENGTH_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYLIST_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYLIST_ID_CONSTRAINT);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYLIST_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithYahooPlayerTypeAndPlayerId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.YAHOO);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_ID_CONSTRAINT.toString()))
        .thenReturn(PLAYER_ID_CONSTRAINT);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYER_ID_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithPlayerHight() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setPlayerHeight(350);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP.toString()))
        .thenReturn(PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP);
  }

  @Test
  void validateDapPlacementWithPlayerWidth() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setPlayerWidth(400);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP.toString()))
        .thenReturn(PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP);
  }

  @Test
  void validateDapPlacementWithoutPlayerRequiredWithDapPlayerParam() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(false);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_REQUIRED_DAP_CONSTRAINT.toString()))
        .thenReturn(PLAYER_REQUIRED_DAP_CONSTRAINT);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYER_REQUIRED_DAP_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithoutPlayerRequiredWithPlayerIdParam() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(false);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_REQUIRED_DAP_CONSTRAINT.toString()))
        .thenReturn(PLAYER_REQUIRED_DAP_CONSTRAINT);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYER_REQUIRED_DAP_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithoutPlayerRequiredWithPlaylistIdParam() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(false);
    placementDTO.getPlacementVideo().setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);
    when(messageHandler.getMessage(
            ServerErrorCodes.SERVER_VALIDATION_PLAYER_REQUIRED_DAP_CONSTRAINT.toString()))
        .thenReturn(PLAYER_REQUIRED_DAP_CONSTRAINT);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(PLAYER_REQUIRED_DAP_CONSTRAINT);
  }

  @Test
  void validateDapPlacementWithDapDefaultParams() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(false);
    assertTrue(validator.isValid(placementDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE, placementDTO.getPlacementVideo().getVideoPlacementType());
    assertFalse(placementDTO.getPlacementVideo().isPlayerRequired());
    assertNull(placementDTO.getPlacementVideo().getDapPlayerType());
    assertNull(placementDTO.getPlacementVideo().getPlayerId());
    assertNull(placementDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validateDapInterstitialPlacementWithDapDefaultParams() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.INTERSTITIAL, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(false);
    assertTrue(validator.isValid(placementDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE, placementDTO.getPlacementVideo().getVideoPlacementType());
    assertFalse(placementDTO.getPlacementVideo().isPlayerRequired());
    assertNull(placementDTO.getPlacementVideo().getDapPlayerType());
    assertNull(placementDTO.getPlacementVideo().getPlayerId());
    assertNull(placementDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validO2DapPlacementWithoutPlayerId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    assertTrue(validator.isValid(placementDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE, placementDTO.getPlacementVideo().getVideoPlacementType());
    assertNull(placementDTO.getPlacementVideo().getPlayerId());
    assertNull(placementDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validO2DapPlacementWithoutPlayListId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    assertTrue(validator.isValid(placementDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE, placementDTO.getPlacementVideo().getVideoPlacementType());
    assertEquals(VALID_PLAYER_ID_PLAYLIST_ID, placementDTO.getPlacementVideo().getPlayerId());
    assertNull(placementDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validO2DapPlacementWithInputValues() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);

    VideoPlacementType videoPlacementType = VideoPlacementType.INTERSTITIAL;
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setVideoPlacementType(videoPlacementType);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    placementDTO.getPlacementVideo().setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);
    assertTrue(validator.isValid(placementDTO, ctx));
    assertEquals(videoPlacementType, placementDTO.getPlacementVideo().getVideoPlacementType());
    assertEquals(DapPlayerType.O2, placementDTO.getPlacementVideo().getDapPlayerType());
    assertEquals(VALID_PLAYER_ID_PLAYLIST_ID, placementDTO.getPlacementVideo().getPlayerId());
    assertEquals(VALID_PLAYER_ID_PLAYLIST_ID, placementDTO.getPlacementVideo().getPlayListId());
  }

  @Test
  void validYahooDapPlacementWithoutPlaylistId() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.YAHOO);
    assertTrue(validator.isValid(placementDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE, placementDTO.getPlacementVideo().getVideoPlacementType());
    assertNull(placementDTO.getPlacementVideo().getPlayListId());
    assertNull(placementDTO.getPlacementVideo().getPlayerId());
  }

  @Test
  void validYahooDapPlacementWithInputValues() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.INTERSTITIAL, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.YAHOO);
    placementDTO.getPlacementVideo().setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);
    assertTrue(validator.isValid(placementDTO, ctx));
    assertEquals(
        VideoPlacementType.IN_ARTICLE, placementDTO.getPlacementVideo().getVideoPlacementType());
    assertEquals(VALID_PLAYER_ID_PLAYLIST_ID, placementDTO.getPlacementVideo().getPlayListId());
    assertNull(placementDTO.getPlacementVideo().getPlayerId());
  }

  @Test
  void validateNonDapPlacementShouldReturnTrue() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.INSTREAM_VIDEO, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setVideoPlacementType(null);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void validatePlacementWithDapCompatableAndWithoutVideoPlacementType() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(Type.DESKTOP, PlacementCategory.BANNER, VIDEO_AND_BANNER);
    placementDTO.getPlacementVideo().setVideoPlacementType(null);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  private PlacementDTO createDapPlacementDTO(
      Type siteType, PlacementCategory placementCategory, VideoSupport videoSupport) {
    SiteDTO site = new SiteDTO();
    site.setType(siteType);

    PlacementDTO position = new PlacementDTO();
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPlayerHeight(null);
    placementVideoDTO.setPlayerWidth(null);
    placementVideoDTO.setVideoPlacementType(VideoPlacementType.IN_ARTICLE);
    position.setPlacementVideo(placementVideoDTO);
    position.setSite(site);
    position.setPlacementCategory(placementCategory);
    position.setVideoSupport(videoSupport);
    return position;
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
