package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance.RtbRevenuePerformanceDimension;
import com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance.RtbRevenuePerformanceRequest;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RtbRevenuePerformanceRequestImpl extends BaseReportRequest
    implements RtbRevenuePerformanceRequest {
  private RtbRevenuePerformanceDimension dim;
  private Long site;
  private Set<Long> siteIds;
  private String position;
  private Long tag;
  private String country;
  private Long bidder;
  private Long buyer;
  private String adomain;
  private String seat;
  private String deal;
  private Long company;
  private boolean dealAsNullParam;
  private Long s2sHbPartner;

  @Getter(AccessLevel.NONE)
  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "RTB_REVENUE_PERFORMANCE";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }
}
