package com.nexage.app.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.ExchangeRateDTO;
import com.nexage.app.services.ExchangeRateService;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ExchangeRateControllerIT extends BaseControllerItTest {

  private static final String BASE_URL = "/v1/exchange-rates";

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @InjectMocks private ExchangeRateController exchangeRateController;

  @Mock private ExchangeRateService exchangeRateService;

  private static final String EUR = "EUR";
  private static final BigDecimal RATE = new BigDecimal("0.984433");
  private static final String VALID_QF = "currency";

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(exchangeRateController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldGetLatestExchangeRateByCurrency() throws Exception {
    // given
    List<ExchangeRateDTO> exchangeRatesList =
        Lists.newArrayList(new ExchangeRateDTO(EUR, new Date(), RATE, 1L));
    Page<ExchangeRateDTO> exchangeRates = new PageImpl(exchangeRatesList);
    when(exchangeRateService.getAllExchangeRates(eq(VALID_QF), eq(EUR), any(), eq(true)))
        .thenReturn(exchangeRates);

    // when
    mockMvc
        .perform(get(BASE_URL.concat("?qf=currency&qt=EUR")))
        // then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("totalElements").value(exchangeRatesList.size()))
        .andExpect(
            jsonPath("content.[0].currency").value(exchangeRates.getContent().get(0).getCurrency()))
        .andExpect(jsonPath("content.[0].rate").value(exchangeRates.getContent().get(0).getRate()))
        .andExpect(
            jsonPath("content.[0].checkDate")
                .value(exchangeRates.getContent().get(0).getCheckDate()))
        .andExpect(
            jsonPath("content.[0].forexId").value(exchangeRates.getContent().get(0).getForexId()));
  }

  @Test
  void shouldGetAllExchangeRatesByCurrency() throws Exception {
    // given
    List<ExchangeRateDTO> exchangeRatesList = TestObjectsFactory.gimme(10, ExchangeRateDTO.class);
    Page<ExchangeRateDTO> exchangeRates = new PageImpl(exchangeRatesList);
    when(exchangeRateService.getAllExchangeRates(eq(VALID_QF), eq(EUR), any(), eq(false)))
        .thenReturn(exchangeRates);

    // when
    mockMvc
        .perform(
            get(BASE_URL.concat("?qf=" + VALID_QF).concat("&qt=" + EUR).concat("&latest=false")))
        // then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("totalElements").value(exchangeRatesList.size()))
        .andExpect(
            jsonPath("content.[0].currency").value(exchangeRates.getContent().get(0).getCurrency()))
        .andExpect(jsonPath("content.[0].rate").value(exchangeRates.getContent().get(0).getRate()))
        .andExpect(
            jsonPath("content.[0].checkDate")
                .value(exchangeRates.getContent().get(0).getCheckDate()))
        .andExpect(
            jsonPath("content.[0].forexId").value(exchangeRates.getContent().get(0).getForexId()));
  }

  @Test
  void shouldGetAllLatestExchangeRates() throws Exception {
    // given
    List<ExchangeRateDTO> exchangeRatesList = TestObjectsFactory.gimme(20, ExchangeRateDTO.class);
    Page<ExchangeRateDTO> exchangeRates = new PageImpl(exchangeRatesList);
    when(exchangeRateService.getAllExchangeRates(isNull(), isNull(), any(), eq(true)))
        .thenReturn(exchangeRates);

    // when
    mockMvc
        .perform(get(BASE_URL))
        // then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("totalElements").value(exchangeRatesList.size()))
        .andExpect(
            jsonPath("content.[0].currency").value(exchangeRates.getContent().get(0).getCurrency()))
        .andExpect(jsonPath("content.[0].rate").value(exchangeRates.getContent().get(0).getRate()))
        .andExpect(
            jsonPath("content.[0].checkDate")
                .value(exchangeRates.getContent().get(0).getCheckDate()))
        .andExpect(
            jsonPath("content.[0].forexId").value(exchangeRates.getContent().get(0).getForexId()));
  }

  @Test
  void shouldGetAllExchangeRates() throws Exception {
    // given
    List<ExchangeRateDTO> exchangeRatesList = TestObjectsFactory.gimme(10, ExchangeRateDTO.class);
    Page<ExchangeRateDTO> exchangeRates = new PageImpl(exchangeRatesList);
    when(exchangeRateService.getAllExchangeRates(isNull(), isNull(), any(), eq(false)))
        .thenReturn(exchangeRates);

    // when
    mockMvc
        .perform(get(BASE_URL.concat("?latest=false")))
        // then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("totalElements").value(exchangeRatesList.size()))
        .andExpect(
            jsonPath("content.[0].currency").value(exchangeRates.getContent().get(0).getCurrency()))
        .andExpect(jsonPath("content.[0].rate").value(exchangeRates.getContent().get(0).getRate()))
        .andExpect(
            jsonPath("content.[0].checkDate")
                .value(exchangeRates.getContent().get(0).getCheckDate()))
        .andExpect(
            jsonPath("content.[0].forexId").value(exchangeRates.getContent().get(0).getForexId()));
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenQfIsNotCurrency() throws Exception {
    // given
    String qf = "forexId";
    String qt = "1";
    when(exchangeRateService.getAllExchangeRates(eq(qf), eq(qt), any(), eq(true)))
        .thenThrow(new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));

    // when
    mockMvc
        .perform(get(BASE_URL.concat("?qf=" + qf).concat("&qt=" + qt)))
        // then
        .andExpect(status().isBadRequest());
  }
}
