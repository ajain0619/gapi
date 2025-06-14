package com.nexage.app.dto.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.VideoPlacementType;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.util.validator.placement.PlacementVideoLongformConstraint;
import com.nexage.app.util.validator.placement.PlacementVideoMultiBiddingConstraint;
import com.nexage.app.util.validator.placement.PlacementVideoPlayerRequiredConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@PlacementVideoLongformConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementVideoPlayerRequiredConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementVideoMultiBiddingConstraint(groups = {CreateGroup.class, UpdateGroup.class})
public class PlacementVideoDTO {
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Long pid;

  @Min(groups = UpdateGroup.class, value = 0, message = ValidationMessages.WRONG_NUMBER_MIN)
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Integer version;

  private PlacementVideoLinearity linearity;

  @Valid private List<PlacementVideoCompanionDTO> companions;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private boolean playerRequired;

  @Min(
      value = 1,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MIN)
  @Max(
      value = 9999,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MAX)
  private Integer playerHeight;

  @Min(
      value = 1,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MIN)
  @Max(
      value = 9999,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MAX)
  private Integer playerWidth;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private boolean longform;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private boolean multiImpressionBid;

  @NotNull(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_EMPTY)
  private boolean competitiveSeparation;

  private PlacementVideoStreamType streamType;

  private String playerBrand;

  private PlacementVideoSsai ssai;

  private VideoPlacementType videoPlacementType;

  private DapPlayerType dapPlayerType;

  private String playerId;

  private String playListId;

  private List<PlacementVideoPlaylistDTO> playlistInfo;

  /**
   * adds companion to {@link PlacementVideoDTO}
   *
   * @param companion {@link PlacementVideoCompanionDTO}
   * @return {@link null}
   */
  public void addCompanion(PlacementVideoCompanionDTO companion) {
    if (Objects.isNull(companions)) companions = new ArrayList<>();

    companions.add(companion);
  }

  /**
   * deletes companion from {@link PlacementVideoDTO}
   *
   * @param companion {@link PlacementVideoCompanionDTO}
   * @return {@link null}
   */
  public void removeCompanion(PlacementVideoCompanionDTO companion) {
    companions.remove(companion);
  }
}
