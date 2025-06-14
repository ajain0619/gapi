package com.nexage.geneva.model.crud;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SellerAttributes {
  private Long sellerPid;
  private Integer version;
  private Boolean humanOptOut = false;
  private Boolean smartQPSEnabled = false;
  private String defaultTransparencyMgmtEnablement;
  private String transparencyMode;
  private String sellerNameAlias;
  private Long sellerIdAlias;
  private BigDecimal revenueShare;
  private boolean adStrictApproval;
  private Long revenueGroupPid;
  private boolean customDealFloorEnabled;

  @JsonInclude(Include.NON_NULL)
  private Integer humanPrebidSampleRate;

  @JsonInclude(Include.NON_NULL)
  private Integer humanPostbidSampleRate;
}
