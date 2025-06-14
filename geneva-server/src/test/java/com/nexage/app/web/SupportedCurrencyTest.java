package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.SupportedCurrencyDTO;
import com.nexage.app.services.impl.support.SupportedCurrencyProvider;
import com.nexage.app.util.assemblers.provisionable.ProvisionableUtils;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * This is a class for testing data returned by static endpoint, that is
 * http://localhost:8080/geneva/static/supported_currencies.json
 */
class SupportedCurrencyTest {

  private static final String CURRENCY_FILE_PATH =
      ProvisionableUtils.getStaticJsonFolder()
          .concat(SupportedCurrencyProvider.SUPPORTED_CUR_FILE_NAME);

  private static final Set<String> CURRENCY_CODES =
      Set.of(
          "USD", "CAD", "EUR", "BRL", "GBP", "JPY", "AUD", "KRW", "CHF", "DKK", "NOK", "SEK", "HKD",
          "TWD", "SGD", "INR", "NZD", "IDR", "MYR", "PHP", "THB", "VND", "MXN", "CLP", "PEN", "COP",
          "ARS", "ZAR", "AED", "TRY");

  @Test
  void shouldRetrieveCompleteListOfSupportedCurrencies() {
    // when
    var supportedCurrencies = fetchSupportedCurrencies();

    // then
    assertEquals(
        CURRENCY_CODES,
        supportedCurrencies.stream().map(SupportedCurrencyDTO::getCode).collect(Collectors.toSet()),
        "Currency set does not match requirements.");
  }

  private Set<SupportedCurrencyDTO> fetchSupportedCurrencies() {
    try (var in = getClass().getResourceAsStream(SupportedCurrencyTest.CURRENCY_FILE_PATH)) {
      return new ObjectMapper().readValue(in, new TypeReference<>() {});
    } catch (IOException e) {
      return Set.of();
    }
  }
}
