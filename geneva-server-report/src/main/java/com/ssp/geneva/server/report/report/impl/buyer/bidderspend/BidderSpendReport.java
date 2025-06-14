package com.ssp.geneva.server.report.report.impl.buyer.bidderspend;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.DateUtil;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BidderSpendReport
    extends AbstractVelocityReport<BidderSpendRequest, BidderSpendResponse> {

  @Value("#{bidderSpendQueries}")
  private Map<String, String> queries;

  public List<BidderSpendResponse> postProcess(
      List<BidderSpendResponse> results, BidderSpendRequest request) {
    clearResultIfContainsNullAggregation(results);
    Map<String, String> dateMap = DateUtil.getZeroDatesMap(request, request.getDim());

    for (BidderSpendResponse bidderSpendResponse : results) {
      if (bidderSpendResponse.getSeat() != null
          && ReportKeys.UNKNOWN_VALUE.equals(bidderSpendResponse.getSeat())) {
        bidderSpendResponse.setSeat(ReportKeys.UNKNOWN_NAME);
      }
      if (request.getDim() == BidderSpendReportDimension.deal
          && bidderSpendResponse.getDealID() == null) {
        bidderSpendResponse.setDealID(ReportKeys.UNKNOWN_DEAL_VALUE);
      }
      if (request.getDim() == BidderSpendReportDimension.site) {
        if (request.getReportUser().isNexageUser()) {
          if (!StringUtils.isEmpty(bidderSpendResponse.getSite())
              && !bidderSpendResponse
                  .getSite()
                  .equalsIgnoreCase(bidderSpendResponse.getSiteInternalName())) {
            bidderSpendResponse.setSite(
                bidderSpendResponse.getSiteInternalName()
                    + " ("
                    + bidderSpendResponse.getSite()
                    + ")");
          } else {
            bidderSpendResponse.setSite(bidderSpendResponse.getSiteInternalName());
          }
        }
        if (bidderSpendResponse.getSite() == null) {
          bidderSpendResponse.setSite("");
        }
      }
      bidderSpendResponse.postInitialize();
      if (bidderSpendResponse.getInterval() != null) {
        if (request.getDim() != BidderSpendReportDimension.hour
            && bidderSpendResponse.getInterval().compareTo(request.getStart()) < 0) {
          bidderSpendResponse.setInterval(request.getStart().split(ReportKeys.DATE_T_SEPARATOR)[0]);
        }
        String removedDate = dateMap.remove(bidderSpendResponse.getInterval().split(" ")[0]);
        if (removedDate != null) {
          bidderSpendResponse.setInterval(removedDate);
        }
      }
    }
    results.addAll(
        dateMap.values().stream().map(BidderSpendResponse::new).collect(Collectors.toList()));
    return results;
  }

  private void clearResultIfContainsNullAggregation(List<BidderSpendResponse> results) {
    if (results.size() == 1 && results.get(0).getAdDelivered() == null) {
      results.remove(0);
    }
  }

  @Override
  public List<BidderSpendResponse> getReportData(BidderSpendRequest request)
      throws ReportException, DataAccessException {
    String sql = queries.get(getReportKeyByDimension(request.getDim()));
    if (sql == null) {
      throw new ReportException("unable to find report");
    }
    requestPreProcessing(request);
    return postProcess(
        getReportData(request, sql, new BeanPropertyRowMapper<>(BidderSpendResponse.class)),
        request);
  }

  private void requestPreProcessing(BidderSpendRequest request) {
    if (ReportKeys.UNKNOWN_DEAL_VALUE.equalsIgnoreCase(request.getDeal())) {
      request.setDeal(null);
      request.setDealAsNullParam(true);
    }
  }
}
