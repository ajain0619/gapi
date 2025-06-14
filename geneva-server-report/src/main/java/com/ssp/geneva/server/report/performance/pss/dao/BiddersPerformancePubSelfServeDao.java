package com.ssp.geneva.server.report.performance.pss.dao;

import com.ssp.geneva.server.report.performance.pss.model.BiddersPerformanceForPubSelfServe;
import java.util.List;
import java.util.Set;

public interface BiddersPerformancePubSelfServeDao {

  List<BiddersPerformanceForPubSelfServe> getBiddersPerformancePss(
      final String start, final String stop, final Set<Long> sitePids, final String loggedInUser);
}
