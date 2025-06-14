package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.ExchangeRate;
import com.nexage.admin.core.model.ExchangeRatePrimaryKey;
import com.nexage.app.dto.ExchangeRateDTO;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;

class ExchangeRateDTOMapperTest {

  @Test
  void shouldMap() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey("EUR", new Date());
    ExchangeRate exchangeRate =
        new ExchangeRate(exchangeRatePrimaryKey, new BigDecimal("0.954488"), 1L);

    // when
    ExchangeRateDTO exchangeRateDTO = ExchangeRateDTOMapper.MAPPER.map(exchangeRate);

    // then
    assertNotNull(exchangeRateDTO);
    assertEquals(exchangeRate.getId().getCurrency(), exchangeRateDTO.getCurrency());
    assertEquals(exchangeRate.getId().getCheckDate(), exchangeRateDTO.getCheckDate());
    assertEquals(exchangeRate.getRate(), exchangeRateDTO.getRate());
    assertEquals(exchangeRate.getForexId(), exchangeRateDTO.getForexId());
  }
}
