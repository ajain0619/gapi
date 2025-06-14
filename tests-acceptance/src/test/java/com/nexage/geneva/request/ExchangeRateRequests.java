package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateRequests {

  @Autowired private Request request;

  public Request getExchangeRatesWithQfAndQtAndLatest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/exchange-rates?qf=%s&qt=%s&latest=%s",
                RequestParams.QF, RequestParams.QT, RequestParams.LATEST));
  }
}
