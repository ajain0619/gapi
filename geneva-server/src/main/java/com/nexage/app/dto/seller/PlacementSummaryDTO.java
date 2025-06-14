package com.nexage.app.dto.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TrafficType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PlacementSummaryDTO {
  private Long pid;
  private String name;
  private String memo;
  private Status status = Status.ACTIVE;
  private TrafficType trafficType;
  private PlacementCategory placementCategory;
  private Long adRequested;
  private Long adDelivered;
  private Long adServed;
  private Double sellerRevenue;
  private Double ecpm;
  private Double rpm;
  private Double ctr;
  private Double fillRate;
  private Double revenueTrendPercent;
  private Long sitePid;
}
