package com.nexage.app.util.validator.placement;

import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.placement.DapVideoPlacementUtil;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import com.ssp.geneva.common.error.handler.MessageHandler;
import java.util.Objects;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlacementDTODapValidator
    extends BaseValidator<PlacementDTODapConstraint, PlacementDTO> {

  private final MessageHandler messageHandler;

  /**
   * Validates DAP fields in PlacementVideoDTO.
   *
   * @param placementDTO {@link PlacementDTO}
   * @param context {@link ConstraintValidatorContext}
   * @return true if valid
   */
  @Override
  public boolean isValid(PlacementDTO placementDTO, ConstraintValidatorContext context) {

    Type siteType = Optional.ofNullable(placementDTO.getSite()).map(SiteDTO::getType).orElse(null);
    PlacementCategory placementCategory = placementDTO.getPlacementCategory();
    VideoSupport videoSupport = placementDTO.getVideoSupport();
    PlacementVideoDTO placementVideoDTO = placementDTO.getPlacementVideo();

    if (Objects.isNull(placementVideoDTO)) {
      return true;
    }

    if (!ValidationUtils.validateAllObjectsNotNull(
        context,
        getAnnotation().field(),
        getAnnotation().emptyMessage(),
        placementCategory,
        videoSupport,
        siteType)) {
      return false;
    }

    return isPlacementDapCompatible(
        placementCategory, videoSupport, siteType, placementVideoDTO, context);
  }

  public boolean isPlacementDapCompatible(
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      Type siteType,
      PlacementVideoDTO placementVideoDTO,
      ConstraintValidatorContext context) {

    boolean hasDapPlayerParams =
        Objects.nonNull(placementVideoDTO.getDapPlayerType())
            || Objects.nonNull(placementVideoDTO.getPlayerId())
            || Objects.nonNull(placementVideoDTO.getPlayListId());

    if (Objects.isNull(placementVideoDTO.getVideoPlacementType()) && hasDapPlayerParams) {
      addConstraintMessage(
          context,
          "placementVideo.videoPlacementType",
          ServerErrorCodes.SERVER_VALIDATION_VIDEO_PLACEMENT_TYPE_CONSTRAINT_DAP);
      return false;
    }

    if (Objects.isNull(placementVideoDTO.getVideoPlacementType())) {
      return true;
    }

    PublisherSiteDTO.SiteType pubSiteType = PublisherSiteDTO.SiteType.valueOf(siteType.toString());
    boolean isDapCompatible =
        DapVideoPlacementUtil.isPlacementDapCompatible(
            placementCategory, videoSupport, pubSiteType);

    if ((Objects.nonNull(placementVideoDTO.getVideoPlacementType()) || hasDapPlayerParams)
        && !isDapCompatible) {
      addConstraintMessage(
          context,
          "placementCategory",
          ServerErrorCodes.SERVER_VALIDATION_PLACEMENT_DAP_CONSTRAINT_VIOLATION);
      return false;
    }

    DapPlayerType dapPlayerType = placementVideoDTO.getDapPlayerType();
    String playerId = placementVideoDTO.getPlayerId();
    String playlistId = placementVideoDTO.getPlayListId();
    Integer playerHeight = placementVideoDTO.getPlayerHeight();
    Integer playerWidth = placementVideoDTO.getPlayerWidth();
    boolean playerRequired = placementVideoDTO.isPlayerRequired();

    if (!playerRequired
        && (Objects.nonNull(dapPlayerType)
            || Objects.nonNull(playerId)
            || Objects.nonNull(playlistId))) {
      addConstraintMessage(
          context,
          "placementVideo.playerRequired",
          ServerErrorCodes.SERVER_VALIDATION_PLAYER_REQUIRED_DAP_CONSTRAINT);
      return false;
    }

    if (Boolean.FALSE.equals(validateDapPlayerParams(placementVideoDTO, context))) {
      return false;
    }

    if (Objects.nonNull(playerHeight) || Objects.nonNull(playerWidth)) {
      addConstraintMessage(
          context,
          "placementVideo.playerHeight",
          ServerErrorCodes.SERVER_VALIDATION_PLAYER_HEIGHT_WIDTH_CONSTRAINT_DAP);
      return false;
    }

    return true;
  }

  private boolean isPlayerIdValid(String playerId, DapPlayerType dapPlayerType) {
    return !(dapPlayerType != DapPlayerType.O2
        || playerId.length() > DapVideoPlacementUtil.MAX_PLAYER_ID_AND_PLAYLIST_ID_LENGTH
        || !playerId.matches(DapVideoPlacementUtil.PLAYER_ID_AND_PLAYLIST_ID_PATTERN));
  }

  private boolean isPlaylistIdValid(String playlistId) {
    return !(playlistId.length() > DapVideoPlacementUtil.MAX_PLAYER_ID_AND_PLAYLIST_ID_LENGTH
        || !playlistId.matches(DapVideoPlacementUtil.PLAYER_ID_AND_PLAYLIST_ID_PATTERN));
  }

  private boolean validateDapPlayerParams(
      PlacementVideoDTO placementVideoDTO, ConstraintValidatorContext context) {

    if (Boolean.FALSE.equals(placementVideoDTO.isPlayerRequired())) {
      return true;
    }

    DapPlayerType dapPlayerType = placementVideoDTO.getDapPlayerType();
    String playerId = placementVideoDTO.getPlayerId();
    String playlistId = placementVideoDTO.getPlayListId();

    if (Objects.isNull(dapPlayerType)) {
      addConstraintMessage(
          context,
          "placementVideo.dapPlayerType",
          ServerErrorCodes.SERVER_VALIDATION_DAP_PLAYER_TYPE_CONSTRAINT);
      return false;
    }

    if (Objects.nonNull(playerId) && !isPlayerIdValid(playerId, dapPlayerType)) {
      addConstraintMessage(
          context,
          "placementVideo.playerId",
          ServerErrorCodes.SERVER_VALIDATION_PLAYER_ID_CONSTRAINT);
      return false;
    }

    if (Objects.nonNull(playlistId) && !isPlaylistIdValid(playlistId)) {
      addConstraintMessage(
          context,
          "placementVideo.playListId",
          ServerErrorCodes.SERVER_VALIDATION_PLAYLIST_ID_CONSTRAINT);
      return false;
    }
    return true;
  }

  private void addConstraintMessage(
      ConstraintValidatorContext constraintValidatorContext,
      String field,
      ServerErrorCodes errorCode) {
    ValidationUtils.addConstraintMessage(
        constraintValidatorContext, field, messageHandler.getMessage(errorCode.toString()));
  }
}
