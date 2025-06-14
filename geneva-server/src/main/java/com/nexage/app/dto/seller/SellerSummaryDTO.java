package com.nexage.app.dto.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SellerSummaryDTO implements Serializable {

  private String id;
  private Long pid;
  private String name;
  private Boolean defaultRtbProfilesEnabled;
  private Long adClicked;
  private Long adRequested;
  private Long adServed;
  private Long adDelivered;
  private Double fillRate;
  private Double ctr;
  private Double rpm;
  private Double ecpm;
  private Double totalEcpm;
  private Double totalRpm;
  private Double sellerRevenue;
  private Double totalRevenue;
  private Double verizonRevenue;
}
