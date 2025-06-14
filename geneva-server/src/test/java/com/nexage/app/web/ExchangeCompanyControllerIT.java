package com.nexage.app.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.bidder.model.BDRExchange;
import com.nexage.admin.core.bidder.model.BdrExchangeCompany;
import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.bdr.BdrExchangeCompanyDTO;
import com.nexage.app.mapper.BdrExchangeCompanyDTOMapper;
import com.nexage.app.services.ExchangeCompanyService;
import com.nexage.app.web.support.BaseControllerItTest;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ExchangeCompanyControllerIT extends BaseControllerItTest {

  @Mock private ExchangeCompanyService exchangeCompanyService;
  @InjectMocks private ExchangeCompanyController exchangeCompanyController;
  private static final long COMPANY_PID = 1L;
  private static final long EXCHANGE_PID = 123L;
  private static Company company;
  private static BdrExchangeCompany bdrExchangeCompany;
  private static BdrExchangeCompanyDTO bdrExchangeCompanyDto;

  @BeforeAll
  static void init() {
    company = new Company();
    company.setPid(COMPANY_PID);

    BDRExchange exchange = new BDRExchange();
    exchange.setPid(EXCHANGE_PID);

    bdrExchangeCompany = new BdrExchangeCompany(exchange, company);
    bdrExchangeCompanyDto = BdrExchangeCompanyDTOMapper.MAPPER.map(bdrExchangeCompany);
  }

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(exchangeCompanyController).build();
  }

  @Test
  void shouldGetExchangesForCompany() throws Exception {
    // given
    long exchangePid2 = 234L;
    BDRExchange exchange2 = new BDRExchange();
    ReflectionTestUtils.setField(exchange2, "pid", exchangePid2);
    BdrExchangeCompany bdrExchangeCompany2 = new BdrExchangeCompany(exchange2, company);

    List<BdrExchangeCompany> exchanges = List.of(bdrExchangeCompany, bdrExchangeCompany2);
    List<BdrExchangeCompanyDTO> exchangeCompanyDtos =
        exchanges.stream()
            .map(BdrExchangeCompanyDTOMapper.MAPPER::map)
            .collect(Collectors.toList());
    when(exchangeCompanyService.getAllForSeatholder(COMPANY_PID)).thenReturn(exchangeCompanyDtos);

    // when
    mockMvc
        .perform(get("/companies/{seatholderPID}/exchanges", COMPANY_PID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].exchangeCompanyPk.bidderExchange.pid").value(EXCHANGE_PID))
        .andExpect(jsonPath("$[0].exchangeCompanyPk.company.pid").value(COMPANY_PID))
        .andExpect(jsonPath("$[1].exchangeCompanyPk.bidderExchange.pid").value(exchangePid2))
        .andExpect(jsonPath("$[1].exchangeCompanyPk.company.pid").value(COMPANY_PID));

    // then
    verify(exchangeCompanyService).getAllForSeatholder(COMPANY_PID);
    verifyNoMoreInteractions(exchangeCompanyService);
  }

  @Test
  void shouldCreateExchangeCompany() throws Exception {
    // given
    String jsonContent = getJsonContent();
    when(exchangeCompanyService.create(any(BdrExchangeCompanyDTO.class)))
        .thenReturn(bdrExchangeCompanyDto);

    // when
    mockMvc
        .perform(
            put("/companies/{seatholderPID}/exchanges", COMPANY_PID)
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.exchangeCompanyPk.bidderExchange.pid").value(EXCHANGE_PID))
        .andExpect(jsonPath("$.exchangeCompanyPk.company.pid").value(COMPANY_PID));

    // then
    verify(exchangeCompanyService).create(any(BdrExchangeCompanyDTO.class));
    verifyNoMoreInteractions(exchangeCompanyService);
  }

  @Test
  void shouldUpdateExchangeCompany() throws Exception {
    // given
    String jsonContent = getJsonContent();

    when(exchangeCompanyService.update(any(BdrExchangeCompanyDTO.class)))
        .thenReturn(bdrExchangeCompanyDto);

    // when
    mockMvc
        .perform(
            put("/companies/{seatholderPID}/exchanges/{exchangePID}", COMPANY_PID, EXCHANGE_PID)
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.exchangeCompanyPk.bidderExchange.pid").value(EXCHANGE_PID))
        .andExpect(jsonPath("$.exchangeCompanyPk.company.pid").value(COMPANY_PID));

    // then
    verify(exchangeCompanyService).update(any(BdrExchangeCompanyDTO.class));
    verifyNoMoreInteractions(exchangeCompanyService);
  }

  @Test
  void shouldDeleteBdrExchangeCompany() throws Exception {
    // given
    doNothing().when(exchangeCompanyService).delete(COMPANY_PID, EXCHANGE_PID);

    // when
    mockMvc
        .perform(
            delete("/companies/{seatholderPID}/exchanges/{exchangePID}", COMPANY_PID, EXCHANGE_PID))
        .andExpect(status().isOk());

    // then
    verify(exchangeCompanyService).delete(COMPANY_PID, EXCHANGE_PID);
    verifyNoMoreInteractions(exchangeCompanyService);
  }

  private String getJsonContent() {
    return "{\n"
        + "  \"biddingFee\": 0.1,\n"
        + "  \"exchangeCompanyPk\": {\n"
        + "    \"bidderExchange\": {      \n"
        + "      \"pid\": 123,\n"
        + "      \"version\": 0\n"
        + "    },\n"
        + "    \"company\": {\n"
        + "      \"pid\": 1,\n"
        + "      \"version\":0\n"
        + "    }\n"
        + "  }\n"
        + "}";
  }
}
