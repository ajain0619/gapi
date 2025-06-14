package com.nexage.app.services;

import com.nexage.app.dto.publisher.PublisherMetricsDTO;

public interface PubSelfServeMetricsService {

  PublisherMetricsDTO getMetrics(Long pubPid, String start, String stop, String interval);

  PublisherMetricsDTO getAdSourceMetrics(
      Long pubPid,
      Long adsourcePid,
      Long sitePid,
      String position,
      Long TagPid,
      String start,
      String stop,
      String interval);
}
