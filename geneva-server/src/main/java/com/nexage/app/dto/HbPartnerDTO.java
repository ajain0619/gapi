package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.FeeType;
import com.nexage.admin.core.enums.Status;
import com.nexage.app.util.validator.HBPartnerResponseConfigConstraint;
import com.nexage.app.util.validator.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HbPartnerDTO {

  @Schema(title = "Primary key for the table")
  private Long pid;

  private Integer version;

  @Schema(title = "Unique id string of Hb partner")
  @NotNull
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String id;

  @Schema(title = "HbPartner name")
  @NotNull
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String name;

  @Schema(title = "Partner specific inventory handling")
  @NotNull
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String partnerHandler;

  @NotNull private Status status;

  @NotNull
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String description;

  /** represents placement set as default for generic default hb integration * */
  private Long defaultPlacement;
  /** represents placement set as default for BANNER default hb integration * */
  private Long bannerDefaultPlacement;
  /** represents placement set as default for VIDEO default hb integration * */
  private Long videoDefaultPlacement;

  private Long defaultSite;

  @NotNull private FeeType feeType;
  @NotNull private BigDecimal fee;

  @Valid @HBPartnerResponseConfigConstraint private String responseConfig;
  @NotNull private boolean formattedDefaultTypeEnabled;
  @NotNull private boolean multiImpressionBid;
  @NotNull private boolean fillMaxDuration;

  @Min(value = 1, message = ValidationMessages.WRONG_NUMBER_MIN)
  @Max(value = 10, message = ValidationMessages.WRONG_NUMBER_MAX)
  private Integer maxAdsPerPod;
}
