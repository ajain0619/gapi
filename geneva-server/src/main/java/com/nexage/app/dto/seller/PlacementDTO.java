package com.nexage.app.dto.seller;

import static com.nexage.admin.core.model.User.Role.ROLE_API;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.FullScreenTiming;
import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.NativePlacementCreateGroup;
import com.nexage.admin.core.validator.NativePlacementUpdateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.CheckPermissionConstraint;
import com.nexage.app.util.validator.DoohConstraint;
import com.nexage.app.util.validator.NonNullDoohConstraint;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.util.validator.placement.PlacementDTOAdSizeConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOAdSizeTypeConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOCategoryConstraint;
import com.nexage.app.util.validator.placement.PlacementDTODapConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOImpressionTypeHandlingConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOInterstitialBooleanConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOMRAIDAdvancedTrackingConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOMRAIDSupportConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOScreenLocationConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOVideoSettingsConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOVideoSupportConstraint;
import com.nexage.app.util.validator.placement.PlacementNameConstraint;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@PlacementDTOVideoSupportConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTODapConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOScreenLocationConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOCategoryConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOMRAIDSupportConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOMRAIDAdvancedTrackingConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOAdSizeConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOAdSizeTypeConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOVideoSettingsConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOInterstitialBooleanConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOImpressionTypeHandlingConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@DoohConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@NonNullDoohConstraint(groups = UpdateGroup.class)
public class PlacementDTO implements PlacementCommonDTO {
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Long pid;

  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  @Min(groups = UpdateGroup.class, value = 0, message = ValidationMessages.WRONG_NUMBER_MIN)
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Integer version;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Long sitePid;

  private SiteDTO site;

  @Valid
  @NotNull(groups = {UpdateGroup.class, NativePlacementUpdateGroup.class})
  @Size.List({
    @Size(
        min = 0,
        max = 45,
        groups = {CreateGroup.class, NativePlacementCreateGroup.class}),
    @Size(
        min = 1,
        max = 45,
        groups = {UpdateGroup.class, NativePlacementUpdateGroup.class})
  })
  @PlacementNameConstraint
  private String name;

  @NotNull
  @Size(min = 1, max = 200)
  @Pattern(regexp = "^[\\w\\s\\[\\]\\-&,(./)]+$")
  private String memo;

  private MRAIDSupport mraidSupport;
  private VideoSupport videoSupport;
  private ScreenLocation screenLocation;
  private Boolean mraidAdvancedTracking = true;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private String adSize;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Boolean staticAdUnit;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Boolean richMediaAdUnit;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Boolean richMediaMRAIDVersion;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Boolean videoMRAID2;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Boolean videoProprietary;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private VideoLinearity videoLinearity;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private FullScreenTiming fullScreenTiming;

  @CheckPermissionConstraint(
      checkIfNexageUser = true,
      roles = ROLE_API,
      groups = {CreateGroup.class, UpdateGroup.class})
  private String positionAliasName;

  private AdSizeType adSizeType;

  @Min(
      value = 1,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MIN)
  @Max(
      value = 9999,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MAX)
  private Integer height;

  @Min(
      value = 1,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MIN)
  @Max(
      value = 9999,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MAX)
  private Integer width;

  private Status status = Status.ACTIVE;
  private Boolean interstitial;

  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  private PlacementCategory placementCategory;

  private TrafficType trafficType = TrafficType.MEDIATION;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private RTBProfileDTO defaultRtbProfile;

  private String defaultPositionNames;

  @Null(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Date updatedOn;

  private Long rtbProfilePid;

  @Valid private PlacementVideoDTO placementVideo;

  @Valid private PlacementDoohDTO dooh;

  @Min(
      value = 0,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MIN)
  @Max(
      value = 100,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MAX)
  private Float externalAdVerificationSamplingRate;

  @DecimalMin(value = "0.0", inclusive = false)
  @DecimalMax(value = "100.0")
  @Digits(integer = 3, fraction = 2)
  private BigDecimal creativeSuccessRateThreshold;

  @NotNull
  private ImpressionTypeHandling impressionTypeHandling =
      ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG;
}
