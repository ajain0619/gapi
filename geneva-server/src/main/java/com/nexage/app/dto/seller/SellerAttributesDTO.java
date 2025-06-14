package com.nexage.app.dto.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.transparency.TransparencyMgmtEnablement;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.util.validator.ValidationMessages;
import java.math.BigDecimal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellerAttributesDTO {

  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Long sellerPid;

  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Integer version;

  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Boolean humanOptOut = false;

  private Boolean smartQPSEnabled;

  private TransparencyMgmtEnablement defaultTransparencyMgmtEnablement;

  private TransparencyMode transparencyMode;

  private String sellerNameAlias;

  private Long sellerIdAlias;

  private BigDecimal revenueShare;

  private BigDecimal rtbFee;

  private boolean adStrictApproval;

  private Long revenueGroupPid;

  @Min(value = 0)
  @Max(value = 100)
  @Digits(integer = 3, fraction = 0)
  private Integer humanPrebidSampleRate;

  @Min(value = 0)
  @Max(value = 100)
  @Digits(integer = 3, fraction = 0)
  private Integer humanPostbidSampleRate;

  private boolean customDealFloorEnabled;
}
