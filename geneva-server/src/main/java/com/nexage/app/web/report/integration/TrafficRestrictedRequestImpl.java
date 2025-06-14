package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficRequest;
import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficRestrictedReportDimension;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** POJO for Traffic Restricted report request. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficRestrictedRequestImpl extends BaseReportRequest implements TrafficRequest {

  private Long company;
  private Integer site;
  private String position;
  private Set<Long> siteIds;
  private Long headerBidding;
  private Long s2sHbPartner;

  @Getter(AccessLevel.NONE)
  private TrafficRestrictedReportDimension dim;

  @Getter(AccessLevel.NONE)
  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "TRAFFIC_BY_SITE_RESTRICTED_DD";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }

  @Override
  public ReportDimension getDim() {
    return dim;
  }

  @Override
  public Long getAdsource() {
    return null;
  }

  @Override
  public Long getTag() {
    return null;
  }
}
