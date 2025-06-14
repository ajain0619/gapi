package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.ExchangeRate;
import com.nexage.admin.core.repository.ExchangeRateRepository;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/data/repository/exchange-rate-repository.sql")
class ExchangeRateSpecificationIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private ExchangeRateRepository exchangeRateRepository;
  private static final String VALID_QF = "currency";
  private static final String INVALID_QF = "forexId";
  private static final String EUR = "EUR";

  @Test
  void shouldFindAllWithLatestFilter() {
    // given
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(null, null, true)
            .orElse(null);

    // when
    assertNotNull(specification);
    List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll(specification);

    // then
    assertEquals(2, exchangeRates.size());
  }

  @Test
  void shouldFindAllWithNullSpec() {
    // given
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(null, null, false)
            .orElse(null);

    // when
    assertNull(specification);
    List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll(specification);

    // then
    assertEquals(4, exchangeRates.size());
  }

  @Test
  void shouldFindAllByCurrency() {
    // given
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(VALID_QF, EUR, false)
            .orElse(null);

    // when
    assertNotNull(specification);
    List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll(specification);

    // then
    assertEquals(2, exchangeRates.size());
  }

  @Test
  void shouldFindAllByCurrencyAndLatest() {
    // given
    Specification<ExchangeRate> specification =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(VALID_QF, EUR, true)
            .orElse(null);

    // when
    assertNotNull(specification);
    List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll(specification);

    // then
    assertEquals(1, exchangeRates.size());
  }

  @Test
  void shouldThrowExceptionWhenValidQfAndNullQt() {
    assertThrows(
        GenevaValidationException.class,
        () ->
            ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(VALID_QF, null, true));
  }

  @Test
  void shouldThrowExceptionWhenQfNotValid() {
    assertThrows(
        GenevaValidationException.class,
        () ->
            ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(INVALID_QF, EUR, true));
  }
}
