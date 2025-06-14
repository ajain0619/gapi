package com.nexage.app.dto.seller;

import com.nexage.app.dto.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteSummaryDTO {
  @Schema(title = "DCN value also known an alternate Site ID")
  private String dcn;

  @Schema(title = "Site Id")
  private String id;

  @Schema(title = "Primary key for the table")
  private Long pId;

  @Schema(title = "Status of Site")
  private Status status;

  @Schema(title = "Site Name")
  private String name;

  private String type;
  private String integration;
  private Boolean live;
  private String platform;
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
}
