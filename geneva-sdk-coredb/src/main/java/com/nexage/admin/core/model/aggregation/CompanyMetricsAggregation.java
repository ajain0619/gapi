package com.nexage.admin.core.model.aggregation;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an aggregation of metrics.
 *
 * @see com.nexage.admin.core.model.Company
 * @see com.nexage.admin.core.model.SellerMetrics
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyMetricsAggregation implements Serializable {

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

  public CompanyMetricsAggregation(
      String id,
      Long pid,
      String name,
      Boolean defaultRtbProfilesEnabled,
      Long adClicked,
      Long adRequested,
      Long adServed,
      Long adDelivered,
      Double fillRate,
      Double ctr,
      Double rpm,
      Double ecpm,
      Double sellerRevenue) {
    this.id = id;
    this.pid = pid;
    this.name = name;
    this.defaultRtbProfilesEnabled = defaultRtbProfilesEnabled;
    this.adClicked = adClicked;
    this.adRequested = adRequested;
    this.adServed = adServed;
    this.adDelivered = adDelivered;
    this.fillRate = fillRate;
    this.ctr = ctr;
    this.rpm = rpm;
    this.ecpm = ecpm;
    this.sellerRevenue = sellerRevenue;
  }
}
