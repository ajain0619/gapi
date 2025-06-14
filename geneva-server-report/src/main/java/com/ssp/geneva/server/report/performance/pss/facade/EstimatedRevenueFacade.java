package com.ssp.geneva.server.report.performance.pss.facade;

import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdNetworksForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdvertiserForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueForPubSelfServe;
import java.time.LocalDate;

public interface EstimatedRevenueFacade {
  EstimatedRevenueForPubSelfServe getEstimatedRevenueForPubSelfServe(
      long publisher, LocalDate start, LocalDate stop, String loggedUser);

  EstimatedRevenueByAdNetworksForPubSelfServ getEstimatedRevenueByAdNetworksForPubSelfServ(
      long publisher, LocalDate start, LocalDate stop, String loggedUser);

  EstimatedRevenueByAdvertiserForPubSelfServ getEstimatedRevenueByAdvertiserForPubSelfServ(
      long publisher, LocalDate start, LocalDate stop, String loggedUser);
}
