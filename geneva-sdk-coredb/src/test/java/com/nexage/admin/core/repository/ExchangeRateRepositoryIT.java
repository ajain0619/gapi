package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.ExchangeRate;
import com.nexage.admin.core.specification.ExchangeRateSpecification;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/data/repository/exchange-rate-repository.sql")
class ExchangeRateRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private ExchangeRateRepository exchangeRateRepository;
  private static final String VALID_QF = "currency";

  @Test
  void shouldReturnLatestExchangeRateByCurrencyAndRoundUp() {
    // given
    String currency = "EUR";
    BigDecimal latestExchangeRate = new BigDecimal("0.980403");
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(VALID_QF, currency, true)
            .orElse(null);

    // when
    assertNotNull(specification);
    Page<ExchangeRate> out = exchangeRateRepository.findAll(specification, PageRequest.of(0, 10));

    // then
    assertEquals(1L, out.getTotalElements());
    assertEquals(latestExchangeRate, out.getContent().get(0).getRate());
  }

  @Test
  void shouldReturnLatestExchangeRateByCurrencyAndRoundDown() {
    // given
    String currency = "GBP";
    BigDecimal latestExchangeRate = new BigDecimal("1.108299");
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(VALID_QF, currency, true)
            .orElse(null);

    // when
    assertNotNull(specification);
    Page<ExchangeRate> out = exchangeRateRepository.findAll(specification, PageRequest.of(0, 10));

    // then
    assertEquals(1L, out.getTotalElements());
    assertEquals(latestExchangeRate, out.getContent().get(0).getRate());
  }

  @Test
  void shouldReturnAllLatestExchangeRates() {
    // given
    Set<BigDecimal> exchangeRates = Set.of(new BigDecimal("0.980403"), new BigDecimal("1.108299"));
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(null, null, true)
            .orElse(null);

    // when
    assertNotNull(specification);
    Page<ExchangeRate> out = exchangeRateRepository.findAll(specification, PageRequest.of(0, 10));

    // then
    assertEquals(
        exchangeRates,
        out.getContent().stream().map(ExchangeRate::getRate).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnAllExchangeRatesByCurrency() {
    // given
    Set<BigDecimal> exchangeRates = Set.of(new BigDecimal("0.970403"), new BigDecimal("0.980403"));
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(VALID_QF, "EUR", false)
            .orElse(null);

    // when
    assertNotNull(specification);
    Page<ExchangeRate> out = exchangeRateRepository.findAll(specification, PageRequest.of(0, 10));

    // then
    assertEquals(
        exchangeRates,
        out.getContent().stream().map(ExchangeRate::getRate).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnAllExchangeRatesOrderedByForexIdDescAndCurrencyAsc() {
    // given
    List<BigDecimal> rates =
        List.of(
            new BigDecimal("0.980403"),
            new BigDecimal("1.108299"),
            new BigDecimal("0.970403"),
            new BigDecimal("1.109299"));
    List<Long> forexIds = List.of(2L, 2L, 1L, 1L);
    List<String> currencies = List.of("EUR", "GBP", "EUR", "GBP");
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(null, null, false)
            .orElse(null);

    // when
    assertNull(specification);
    Page<ExchangeRate> out =
        exchangeRateRepository.findAll(
            specification,
            PageRequest.of(
                0,
                10,
                Sort.by(Arrays.asList(Sort.Order.desc("forexId"), Sort.Order.asc("id.currency")))));

    // then
    assertEquals(
        rates, out.getContent().stream().map(ExchangeRate::getRate).collect(Collectors.toList()));
    assertEquals(
        forexIds,
        out.getContent().stream().map(ExchangeRate::getForexId).collect(Collectors.toList()));
    assertEquals(
        currencies,
        out.getContent().stream().map(er -> er.getId().getCurrency()).collect(Collectors.toList()));
  }

  @Test
  void shouldReturnNoExchangeRatesWhenCurrencyDoesNotMatch() {
    // given
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(VALID_QF, "CHF", false)
            .orElse(null);

    // when
    assertNotNull(specification);
    Page<ExchangeRate> out = exchangeRateRepository.findAll(specification, PageRequest.of(0, 10));

    // then
    assertEquals(0L, out.getTotalElements());
  }
}
