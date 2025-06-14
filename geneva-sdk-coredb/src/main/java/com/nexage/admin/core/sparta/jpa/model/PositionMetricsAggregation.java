package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.model.Position;
import java.io.Serializable;
import lombok.Data;

/**
 * This class represents an aggregation of metrics.
 *
 * @see Position
 * @see com.nexage.admin.core.model.PositionMetrics
 */
@Data
public class PositionMetricsAggregation implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long pid;
  private String name;
  private String memo;
  private Status status = Status.ACTIVE;
  private PlacementCategory placementCategory;
  private Long sitePid;
  private TrafficType trafficType;
  private Long adRequested;
  private Long adDelivered;
  private Long adServed;
  private Double sellerRevenue;
  private Double revenueTrendPercent;
  private Double ecpm;
  private Double rpm;
  private Double ctr;
  private Double fillRate;

  public PositionMetricsAggregation(
      Long pid,
      String name,
      String memo,
      Status status,
      PlacementCategory placementCategory,
      Long sitePid,
      TrafficType trafficType,
      Long adRequested,
      Long adDelivered,
      Long adServed,
      Double sellerRevenue,
      Double ecpm,
      Double rpm,
      Double ctr,
      Double fillRate) {
    this.pid = pid;
    this.name = name;
    this.memo = memo;
    this.status = status;
    this.placementCategory = placementCategory;
    this.sitePid = sitePid;
    this.trafficType = trafficType;
    this.adRequested = adRequested;
    this.adDelivered = adDelivered;
    this.adServed = adServed;
    this.sellerRevenue = sellerRevenue;
    this.ecpm = ecpm;
    this.rpm = rpm;
    this.ctr = ctr;
    this.fillRate = fillRate;
  }
}
