package com.ssp.geneva.server.report.report.impl.finance.rtbbidderar;

import com.google.common.primitives.Longs;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.ExchangeConfig;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class RTBBidderARReport
    extends AbstractVelocityReport<RTBBidderARRequest, RTBBidderARResponse> {

  private final BidderConfigRepository bidderConfigRepository;

  private final RTBBidderARReportDelegate rtbBidderARReportDelegate;

  public RTBBidderARReport(
      BidderConfigRepository bidderConfigRepository,
      RTBBidderARReportDelegate rtbBidderARReportDelegate) {
    this.bidderConfigRepository = bidderConfigRepository;
    this.rtbBidderARReportDelegate = rtbBidderARReportDelegate;
  }

  public enum RTBBidderARReportProperty {
    AUCTION_BID_REQUEST_CPM_PROPERTY("auction.bid.request.cpm");

    private final String propertyName;

    RTBBidderARReportProperty(String propertyName) {
      this.propertyName = propertyName;
    }

    public String getPropertyName() {
      return propertyName;
    }
  }

  @Value("#{rtbBidderARQueries}")
  private Map<String, String> queries;

  public List<RTBBidderARResponse> postProcess(List<RTBBidderARResponse> results) {

    if (results == null || results.size() == 0) return results;

    Set<Long> ids =
        results.stream()
            .map(s -> Longs.tryParse(s.getBidderId().toString()))
            .collect(Collectors.toSet());
    Map<Long, BigDecimal> ecpmMap =
        bidderConfigRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(BidderConfig::getPid, BidderConfig::getBidRequestCpm));

    for (RTBBidderARResponse row : results) {
      BigDecimal minTrafficChargePerRequest = null;
      Long bidderId = Longs.tryParse(row.getBidderId().toString());
      if (ecpmMap.containsKey(bidderId)) {
        minTrafficChargePerRequest = ecpmMap.get(bidderId);
      } else {
        ExchangeConfig rxConfig = rtbBidderARReportDelegate.returnExchangeConfig();
        if (rxConfig != null) {
          minTrafficChargePerRequest = new BigDecimal(rxConfig.getValue());
        }
      }

      if (minTrafficChargePerRequest == null) {
        log.error(
            "Minumum traffic charge information is not available for bidderId["
                + row.getBidderId()
                + "]. "
                + "So accounts receivable report cannot be updated for balanceAfterMinTrafficCharge and minTrafficCharge");
      } else {
        // The minimum traffic charge for a bidder shall be a CPM fee for the bid request volume
        // sent to that bidder.
        row.setMinTrafficCharge(
            ((minTrafficChargePerRequest.multiply(new BigDecimal(row.getBidRequests().toString())))
                .divide(new BigDecimal("1000"), 8, RoundingMode.HALF_UP)));

        // total charges should be min of gross & net
        row.setTotalCharge(
            new BigDecimal(row.getNetAcquisitionCost().toString())
                .min(new BigDecimal(row.getGrossWins().toString())));
      }
    }

    return results;
  }

  @Override
  public List<RTBBidderARResponse> getReportData(RTBBidderARRequest request)
      throws ReportException, DataAccessException {

    return postProcess(
        getReportData(
            request,
            queries.get(ReportKeys.DEFAULT).trim(),
            new BeanPropertyRowMapper<>(RTBBidderARResponse.class)));
  }
}
