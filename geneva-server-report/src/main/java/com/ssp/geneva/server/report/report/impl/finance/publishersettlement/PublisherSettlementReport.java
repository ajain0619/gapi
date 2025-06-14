package com.ssp.geneva.server.report.report.impl.finance.publishersettlement;

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
public class PublisherSettlementReport
    extends AbstractVelocityReport<PublisherSettlementRequest, PublisherSettlementResponse> {

  @Value("#{publisherSettlementQueries}")
  Map<String, String> queries;

  @Override
  public List<PublisherSettlementResponse> getReportData(PublisherSettlementRequest request)
      throws ReportException, DataAccessException {
    return getReportData(
        request,
        queries.get(ReportKeys.DEFAULT).trim(),
        new BeanPropertyRowMapper<>(PublisherSettlementResponse.class));
  }
}
