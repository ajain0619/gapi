package com.ssp.geneva.server.report.report.impl.finance.rtbbidderar;

import com.nexage.admin.core.model.ExchangeConfig;
import com.nexage.admin.core.repository.ExchangeConfigRepository;
import org.springframework.stereotype.Component;

@Component
class RTBBidderARReportDelegate {
  private final ExchangeConfigRepository exchangeConfigRepository;

  public RTBBidderARReportDelegate(ExchangeConfigRepository exchangeConfigRepository) {
    this.exchangeConfigRepository = exchangeConfigRepository;
  }

  public ExchangeConfig returnExchangeConfig() {
    return exchangeConfigRepository.findByProperty(
        RTBBidderARReport.RTBBidderARReportProperty.AUCTION_BID_REQUEST_CPM_PROPERTY
            .getPropertyName());
  }
}
