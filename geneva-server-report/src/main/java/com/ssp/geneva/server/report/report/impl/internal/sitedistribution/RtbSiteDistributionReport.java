package com.ssp.geneva.server.report.report.impl.internal.sitedistribution;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RtbSiteDistributionReport
    extends AbstractVelocityReport<RtbSiteDistributionRequest, RtbSiteDistributionResponse> {

  @Value("#{rtbSiteDistributionQueries}")
  private Map<String, String> queries;

  public List<RtbSiteDistributionResponse> postProcess(List<RtbSiteDistributionResponse> results) {
    return results;
  }

  @Override
  public List<RtbSiteDistributionResponse> getReportData(RtbSiteDistributionRequest request)
      throws ReportException, DataAccessException {
    return postProcess(
        getReportData(
            request,
            queries.get(ReportKeys.DEFAULT).trim(),
            new BeanPropertyRowMapper<>(RtbSiteDistributionResponse.class)));
  }
}
