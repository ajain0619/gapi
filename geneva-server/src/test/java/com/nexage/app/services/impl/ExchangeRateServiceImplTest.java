package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.ExchangeRate;
import com.nexage.admin.core.repository.ExchangeRateRepository;
import com.nexage.app.dto.ExchangeRateDTO;
import com.nexage.app.mapper.ExchangeRateDTOMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {

  @InjectMocks private ExchangeRateServiceImpl exchangeRateService;

  @Mock private ExchangeRateRepository exchangeRateRepository;
  @Mock private Pageable pageable;

  private static final String EUR = "EUR";
  private static final String VALID_QF = "currency";
  private static final String CURRENCY_FOR_WHICH_EXCHANGE_RATE_NOT_FOUND = "test";
  private static final BigDecimal RATE = new BigDecimal("0.98");

  private Page<ExchangeRate> pagedEntity;
  private Page<ExchangeRateDTO> pagedEntityDTO;

  public void setUpEntities(Integer numberOfObjects) {
    pagedEntity = new PageImpl<>(TestObjectsFactory.gimme(numberOfObjects, ExchangeRate.class));
    pagedEntityDTO = pagedEntity.map(ExchangeRateDTOMapper.MAPPER::map);
  }

  @Test
  void shouldReturnAllExchangeRates() {
    // given
    setUpEntities(10);
    when(exchangeRateRepository.findAll(nullable(Specification.class), eq(pageable)))
        .thenReturn(pagedEntity);

    // when
    Page<ExchangeRateDTO> pagedExchangeRateDTO =
        exchangeRateService.getAllExchangeRates(null, null, pageable, false);

    // then
    assertEquals(pagedEntityDTO.getContent().size(), pagedExchangeRateDTO.getTotalElements());
    assertEquals(
        pagedEntityDTO.getContent().get(0).getCurrency(),
        pagedExchangeRateDTO.getContent().get(0).getCurrency());
    assertEquals(
        pagedEntityDTO.getContent().get(0).getCheckDate(),
        pagedExchangeRateDTO.getContent().get(0).getCheckDate());
  }

  @Test
  void shouldReturnAllLatestExchangeRates() {
    // given
    setUpEntities(10);
    when(exchangeRateRepository.findAll(nullable(Specification.class), eq(pageable)))
        .thenReturn(pagedEntity);

    // when
    Page<ExchangeRateDTO> pagedExchangeRateDTO =
        exchangeRateService.getAllExchangeRates(null, null, pageable, true);

    // then
    assertEquals(pagedEntityDTO.getContent().size(), pagedExchangeRateDTO.getTotalElements());
    assertEquals(
        pagedEntityDTO.getContent().get(0).getCurrency(),
        pagedExchangeRateDTO.getContent().get(0).getCurrency());
    assertEquals(
        pagedEntityDTO.getContent().get(0).getCheckDate(),
        pagedExchangeRateDTO.getContent().get(0).getCheckDate());
  }

  @Test
  void shouldReturnAllExchangeRatesByCurrency() {
    // given
    setUpEntities(10);
    when(exchangeRateRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(pagedEntity);

    // when
    Page<ExchangeRateDTO> pagedExchangeRateDTO =
        exchangeRateService.getAllExchangeRates(VALID_QF, EUR, pageable, false);

    // then
    assertEquals(pagedEntityDTO.getContent().size(), pagedExchangeRateDTO.getTotalElements());
    assertEquals(
        pagedEntityDTO.getContent().get(0).getCurrency(),
        pagedExchangeRateDTO.getContent().get(0).getCurrency());
    assertEquals(
        pagedEntityDTO.getContent().get(0).getCheckDate(),
        pagedExchangeRateDTO.getContent().get(0).getCheckDate());
  }

  @Test
  void shouldReturnLatestExchangeRateByCurrency() {
    // given
    setUpEntities(1);
    when(exchangeRateRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(pagedEntity);

    // when
    Page<ExchangeRateDTO> pagedExchangeRateDTO =
        exchangeRateService.getAllExchangeRates(VALID_QF, EUR, pageable, true);

    // then
    assertEquals(pagedEntityDTO.getContent().size(), pagedExchangeRateDTO.getTotalElements());
    assertEquals(
        pagedEntityDTO.getContent().get(0).getCurrency(),
        pagedExchangeRateDTO.getContent().get(0).getCurrency());
    assertEquals(
        pagedEntityDTO.getContent().get(0).getCheckDate(),
        pagedExchangeRateDTO.getContent().get(0).getCheckDate());
  }

  @Test
  void shouldThrowExceptionWhenQfIsNotCurrency() {
    // when
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> exchangeRateService.getAllExchangeRates("forexId", "123", pageable, false));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenQfIsCurrencyAndQtIsNull() {
    // when
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> exchangeRateService.getAllExchangeRates("currency", null, pageable, false));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }
}
