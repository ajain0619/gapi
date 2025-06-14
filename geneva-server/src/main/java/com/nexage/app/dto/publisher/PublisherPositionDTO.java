package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.seller.PlacementCommonDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.util.validator.DoohConstraint;
import com.nexage.app.util.validator.NonNullDoohConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOCategoryConstraint;
import com.nexage.app.util.validator.placement.PlacementDTODapConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOImpressionTypeHandlingConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOMRAIDSupportConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOScreenLocationConstraint;
import com.nexage.app.util.validator.placement.PlacementDTOVideoSupportConstraint;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.hibernate.validator.constraints.NotBlank;

@JsonInclude(Include.NON_NULL)
@Data
@DoohConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@NonNullDoohConstraint(groups = UpdateGroup.class)
@PlacementDTOScreenLocationConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOCategoryConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOVideoSupportConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTODapConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOMRAIDSupportConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@PlacementDTOImpressionTypeHandlingConstraint(groups = {CreateGroup.class, UpdateGroup.class})
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class PublisherPositionDTO implements Serializable, PlacementCommonDTO {
  private Long pid;
  private Integer version;
  private PublisherSiteDTO site;
  private String name;
  @NotBlank private String memo;
  private MRAIDSupport mraidSupport;
  private VideoSupport videoSupport;
  private ScreenLocation screenLocation;
  private Boolean interstitial;

  private VideoLinearity videoLinearity;
  private Integer height;
  private Integer width;
  private PlacementCategory placementCategory;

  @Builder.Default private TrafficType trafficType = TrafficType.MEDIATION;

  @Singular private Set<PublisherTagDTO> tags;
  @Singular private Set<PublisherTierDTO> tiers;
  private Status status;
  private PublisherTagDTO decisionMaker;
  private PublisherDefaultRTBProfileDTO defaultRtbProfile;

  private String positionAliasName;
  private Boolean mraidAdvancedTracking;

  private Set<HbPartnerAssignmentDTO> hbPartnerAttributes;
  private AdSizeType adSizeType;
  @Valid private PlacementDoohDTO dooh;

  private boolean longform = false;

  @Valid private PlacementVideoDTO placementVideo;

  private Float externalAdVerificationSamplingRate;

  @DecimalMin(value = "0.0", inclusive = false)
  @DecimalMax(value = "100.0")
  @Digits(integer = 3, fraction = 2)
  private BigDecimal creativeSuccessRateThreshold;

  @NotNull
  private ImpressionTypeHandling impressionTypeHandling =
      ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG;

  public void removeTags() {
    this.tags.clear();
  }

  public void removeTiers() {
    this.tiers.clear();
  }
}
