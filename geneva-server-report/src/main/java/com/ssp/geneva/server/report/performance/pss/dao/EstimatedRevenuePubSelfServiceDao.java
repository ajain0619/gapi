package com.ssp.geneva.server.report.performance.pss.dao;

import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdNetworksForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdvertiserForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueForPubSelfServe;
import java.time.LocalDate;

public interface EstimatedRevenuePubSelfServiceDao {
  EstimatedRevenueForPubSelfServe getEstimatedRevenueForPss(
      long publisher, final LocalDate start, final LocalDate stop, final String loggedInUser);

  EstimatedRevenueByAdNetworksForPubSelfServ getEstimatedRevenueByAdNetworksForPss(
      long publisher, final LocalDate start, final LocalDate stop, final String loggedInUser);

  EstimatedRevenueByAdvertiserForPubSelfServ getEstimatedRevenueByAdvertiserForPss(
      long publisher, final LocalDate start, final LocalDate stop, final String loggedInUser);
}
