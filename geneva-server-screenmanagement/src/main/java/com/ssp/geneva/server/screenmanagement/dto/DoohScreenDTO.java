package com.ssp.geneva.server.screenmanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.enums.AdType;
import com.nexage.admin.core.enums.Bearing;
import com.nexage.admin.core.enums.LocationType;
import com.nexage.admin.core.validator.CreateGroup;
import com.ssp.geneva.server.screenmanagement.validator.DmaConstraint;
import com.ssp.geneva.server.screenmanagement.validator.DoohScreenCountryStateConstraint;
import com.ssp.geneva.server.screenmanagement.validator.Iso2CountryCodeConstraint;
import com.ssp.geneva.server.screenmanagement.validator.Iso2StateCodeConstraint;
import com.ssp.geneva.server.screenmanagement.validator.RestrictionsConstraint;
import com.ssp.geneva.server.screenmanagement.validator.ZipCodeConstraint;
import java.math.BigDecimal;
import java.util.Set;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DoohScreenCountryStateConstraint
public class DoohScreenDTO {

  private static final long serialVersionUID = 1L;

  @JsonIgnore
  @Null(groups = CreateGroup.class)
  private Long pid;

  @Size(min = 1, max = 80)
  @NotNull
  @JsonIgnore
  private String sspScreenId;

  @JsonIgnore @NotNull private Long sellerPid;

  @NotNull
  @Size(min = 1, max = 64)
  private String sellerScreenId;

  @Size(min = 1, max = 128)
  private String sellerScreenName;

  @Size(min = 1, max = 64)
  private String network;

  @NotNull @PositiveOrZero private Integer venueTypeId;

  private LocationType locationType;

  @DecimalMin("-90")
  @DecimalMax("90")
  @NotNull
  private Double latitude;

  @DecimalMin("-180")
  @DecimalMax("180")
  @NotNull
  private Double longitude;

  @NotNull
  @Size(min = 2, max = 2)
  @Iso2CountryCodeConstraint
  private String country;

  @Size(min = 2, max = 2)
  @Iso2StateCodeConstraint
  private String state;

  @Size(min = 1, max = 64)
  @DmaConstraint
  private String dma;

  @NotNull
  @Size(min = 1, max = 64)
  private String city;

  @Size(min = 5, max = 10)
  @ZipCodeConstraint
  private String zip;

  @NotNull
  @Size(min = 1, max = 128)
  private String address;

  private Bearing bearing;

  private String link;

  @NotNull
  @Size(min = 1, max = 3)
  private Set<AdType> adTypes;

  @NotNull @PositiveOrZero private Integer minAdDuration;

  @NotNull @PositiveOrZero private Integer maxAdDuration;

  @NotNull
  @Size(min = 1, max = 12)
  @Pattern(regexp = "[1-9][0-9]*x[1-9][0-9]*", message = "Must be in form of [width]x[height]")
  private String resolution;

  private Set<
          @Pattern(
              regexp = "[1-9][0-9]*x[1-9][0-9]*",
              message = "Must be in form of [width]x[height]")
          String>
      acceptedAdSizes;

  @Size(max = 8)
  @Pattern(regexp = "[1-9][0-9]*:[1-9][0-9]*", message = "Must be in form of width:height")
  private String aspectRatio;

  @PositiveOrZero private Double avgDwellTime;
  @NotNull @PositiveOrZero private Double avgImpressionMultiplier;
  @NotNull @PositiveOrZero private Double avgWeeklyImpressions;
  @PositiveOrZero private Double avgDailyImpressions;
  @PositiveOrZero private Double avgMonthlyImpressions;
  @PositiveOrZero private Double avgCpm;

  @Size(min = 1, max = 444)
  @RestrictionsConstraint
  private Set<String> restrictions;

  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 7, fraction = 8)
  private BigDecimal floorPrice;
}
