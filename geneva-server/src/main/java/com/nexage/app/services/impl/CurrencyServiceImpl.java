package com.nexage.app.services.impl;

import com.nexage.app.dto.SupportedCurrencyDTO;
import com.nexage.app.services.CurrencyService;
import com.nexage.app.services.impl.support.SupportedCurrencyProvider;
import java.util.Arrays;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceImpl implements CurrencyService {

  private final SupportedCurrencyProvider currencyProvider;

  public CurrencyServiceImpl(SupportedCurrencyProvider currencyProvider) {
    this.currencyProvider = currencyProvider;
  }

  public boolean isCurrencySupported(String currencyCode) {
    SupportedCurrencyDTO[] supportedCurrencies = currencyProvider.loadCurrencies();
    return Arrays.stream(supportedCurrencies)
        .anyMatch(sc -> sc.getCode().equalsIgnoreCase(currencyCode));
  }
}
