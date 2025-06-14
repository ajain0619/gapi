package com.nexage.app.services.impl;

import static org.mockito.Mockito.when;

import com.nexage.app.dto.SupportedCurrencyDTO;
import com.nexage.app.services.impl.support.SupportedCurrencyProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

  @Mock private SupportedCurrencyProvider currencyProvider;

  @InjectMocks private CurrencyServiceImpl currencyService;

  @Test
  void currenciesShouldBeLoadedOnlyOnceRegardlessAmountOfCurrenciesToBeChecked() {
    when(currencyProvider.loadCurrencies()).thenReturn(new SupportedCurrencyDTO[0]);

    currencyService.isCurrencySupported(
        "USD"); // "USD" is just an example, actual value here does not matter

    Mockito.verify(currencyProvider, Mockito.atMost(1)).loadCurrencies();
  }
}
