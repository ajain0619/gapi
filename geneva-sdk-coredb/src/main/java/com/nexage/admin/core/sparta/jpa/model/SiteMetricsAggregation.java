package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Site;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an aggregation of metrics.
 *
 * @see Site
 * @see com.nexage.admin.core.model.SiteMetrics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteMetricsAggregation implements Serializable {
  private static final long serialVersionUID = 1L;
  private String dcn;
  private String id;
  private Long pid;
  private Type type;
  private Boolean live;
  private String name;
  private Platform platform;
  private Status status;
  private Double sellerRevenue;
  private Long adRequested;
  private Long adDelivered;
  private Long adServed;
  private Long adClicked;
  private Double fillRate;
  private Double ecpm;
  private Double ctr;
  private Double rpm;
  private Double revenueTrendPercent;

  public SiteMetricsAggregation(Long pid, Double sellerRevenue) {
    this.pid = pid;
    this.sellerRevenue = sellerRevenue;
  }

  public SiteMetricsAggregation(
      Double sellerRevenue,
      Long adRequested,
      Long adDelivered,
      Long adServed,
      Long adClicked,
      Double fillRate,
      Double ecpm,
      Double ctr,
      Double rpm) {
    this.sellerRevenue = sellerRevenue;
    this.adRequested = adRequested;
    this.adDelivered = adDelivered;
    this.adServed = adServed;
    this.adClicked = adClicked;
    this.fillRate = fillRate;
    this.ecpm = ecpm;
    this.ctr = ctr;
    this.rpm = rpm;
  }

  public SiteMetricsAggregation(
      String dcn,
      String id,
      Long pid,
      Type type,
      Boolean live,
      String name,
      Platform platform,
      Status status,
      Double sellerRevenue,
      Long adRequested,
      Long adDelivered,
      Long adServed,
      Long adClicked,
      Double fillRate,
      Double ecpm,
      Double ctr,
      Double rpm) {
    this.dcn = dcn;
    this.id = id;
    this.pid = pid;
    this.type = type;
    this.live = live;
    this.name = name;
    this.platform = platform;
    this.status = status;
    this.sellerRevenue = sellerRevenue;
    this.adRequested = adRequested;
    this.adDelivered = adDelivered;
    this.adServed = adServed;
    this.adClicked = adClicked;
    this.fillRate = fillRate;
    this.ecpm = ecpm;
    this.ctr = ctr;
    this.rpm = rpm;
  }
}
