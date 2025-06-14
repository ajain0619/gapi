package com.nexage.app.services;

import com.nexage.app.dto.buyer.BuyerTrafficConfigDTO;
import java.util.Map;

public interface BuyerAssistantService {

  enum MetricInterval {
    today,
    yesterday,
    last7days,
    month,
    lastmonth
  }

  Map<String, Object> getBusinessMetrics(Long companyPid, MetricInterval interval);

  BuyerTrafficConfigDTO getBuyerTrafficConfig(long companyPid);

  BuyerTrafficConfigDTO updateBuyerTrafficConfig(
      long companyPid, BuyerTrafficConfigDTO buyerTrafficConfig);
}
