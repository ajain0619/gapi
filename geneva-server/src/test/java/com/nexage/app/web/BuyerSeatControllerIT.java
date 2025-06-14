package com.nexage.app.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.BuyerSeat;
import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.buyer.BuyerSeatDTO;
import com.nexage.app.services.BuyerService;
import com.nexage.app.web.buyer.BuyerSeatController;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BuyerSeatControllerIT extends BaseControllerItTest {

  @Mock private BuyerService buyerService;
  @InjectMocks private BuyerSeatController buyerSeatController;

  @BeforeEach
  public void setUp() throws Exception {
    this.mockMvc = MockMvcBuilders.standaloneSetup(buyerSeatController).build();
  }

  @Test
  void shouldReturnAllBuyerSeatsByCompanyPid() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");

    List<BuyerSeatDTO> buyerSeatDtos = new ArrayList<>();

    BuyerSeat seat1 =
        TestObjectsFactory.createBuyerSeat("seat-1", "name-1", true, buyerGroup, company, true, 1L);
    buyerSeatDtos.add(new BuyerSeatDTO(seat1));

    BuyerSeat seat2 =
        TestObjectsFactory.createBuyerSeat(
            "seat-2", "name-2", true, buyerGroup, company, false, 2L);
    buyerSeatDtos.add(new BuyerSeatDTO(seat2));

    when(buyerService.getAllBuyerSeatsForCompanyAndName(company.getPid(), null, null, null))
        .thenReturn(buyerSeatDtos);

    mockMvc
        .perform(get("/buyers/{companyPid}/buyerseats", company.getPid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name", is(seat1.getName())))
        .andExpect(jsonPath("$[1].name", is(seat2.getName())))
        .andExpect(jsonPath("$[0].seat", is(seat1.getSeat())))
        .andExpect(jsonPath("$[1].seat", is(seat2.getSeat())))
        .andExpect(
            jsonPath(
                "$[0].buyerTransparencyFeedEnabled", is(seat1.getBuyerTransparencyFeedEnabled())))
        .andExpect(
            jsonPath(
                "$[1].buyerTransparencyFeedEnabled", is(seat2.getBuyerTransparencyFeedEnabled())))
        .andExpect(jsonPath("$[0].buyerTransparencyDataFeedPid", is(1)))
        .andExpect(jsonPath("$[1].buyerTransparencyDataFeedPid", is(2)))
        .andReturn();
  }

  @Test
  void shouldReturnAllBuyerSeatsByNameParamAndCompanyPid() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");

    List<BuyerSeatDTO> buyerSeatDtos = new ArrayList<>();

    BuyerSeat seat1 =
        TestObjectsFactory.createBuyerSeat("seat-1", "name-1", true, buyerGroup, company, true, 1L);
    buyerSeatDtos.add(new BuyerSeatDTO(seat1));

    when(buyerService.getAllBuyerSeatsForCompanyAndName(
            company.getPid(), null, Set.of("name"), "name"))
        .thenReturn(buyerSeatDtos);

    mockMvc
        .perform(get("/buyers/{companyPid}/buyerseats?qf=name&qt=name", company.getPid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is(seat1.getName())))
        .andExpect(jsonPath("$[0].seat", is(seat1.getSeat())))
        .andExpect(
            jsonPath(
                "$[0].buyerTransparencyFeedEnabled", is(seat1.getBuyerTransparencyFeedEnabled())))
        .andExpect(jsonPath("$[0].buyerTransparencyDataFeedPid", is(1)))
        .andReturn();
  }

  @Test
  void whenDtoValid_thenUpdateBuyerSeatSucceeds() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");

    BuyerSeatDTO dto =
        new BuyerSeatDTO(
            1L, "name-1", "seat-1", true, buyerGroup.getPid(), 0, company.getPid(), true, 1L);

    String requestJson = new ObjectMapper().writeValueAsString(dto);

    when(buyerService.updateBuyerSeat(
            eq(company.getPid()), eq(dto.getPid()), any(BuyerSeatDTO.class)))
        .thenReturn(dto);

    mockMvc
        .perform(
            put("/buyers/{companyPid}/buyerseats/{seatPid}", company.getPid(), dto.getPid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("name-1")))
        .andExpect(jsonPath("$.seat", is("seat-1")))
        .andExpect(jsonPath("$.enabled", is(true)))
        .andExpect(jsonPath("$.buyerGroupPid", is(buyerGroup.getPid())))
        .andExpect(jsonPath("$.version", is(0)))
        .andExpect(jsonPath("$.version", is(0)))
        .andExpect(jsonPath("$.buyerTransparencyFeedEnabled", is(true)))
        .andExpect(jsonPath("$.buyerTransparencyDataFeedPid", is(1)));
  }

  @Test
  void whenDtoInvalid_thenUpdateBuyerSeatFails() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");

    BuyerSeatDTO[] invalids = {
      new BuyerSeatDTO(
          1L, null, "seat-1", true, buyerGroup.getPid(), 0, company.getPid(), false, 1L),
      new BuyerSeatDTO(
          1L, "name-1", null, true, buyerGroup.getPid(), 0, company.getPid(), false, 1L),
      new BuyerSeatDTO(1L, "name-1", "seat-1", true, null, 0, company.getPid(), false, 1L)
    };

    for (BuyerSeatDTO dto : invalids) {
      assertFail(dto, company);
    }
  }

  private void assertFail(BuyerSeatDTO dto, Company company) throws Exception {
    String requestJson = new ObjectMapper().writeValueAsString(dto);

    when(buyerService.updateBuyerSeat(
            eq(company.getPid()), eq(dto.getPid()), any(BuyerSeatDTO.class)))
        .thenReturn(dto);

    mockMvc
        .perform(
            put("/buyers/{companyPid}/buyerseats/{seatPid}", company.getPid(), dto.getPid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCreateBuyerSeat() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "EUR", "IRL");

    BuyerSeat buyerSeat =
        TestObjectsFactory.createBuyerSeat(
            "seatXYZ", "seatNameControllerTest", true, buyerGroup, company, true, 1L);

    BuyerSeatDTO dto = new BuyerSeatDTO(buyerSeat);

    String requestJson = new ObjectMapper().writeValueAsString(dto);

    when(buyerService.createBuyerSeat(eq(company.getPid()), any(BuyerSeatDTO.class)))
        .thenReturn(dto);

    mockMvc
        .perform(
            post("/buyers/{companyPid}/buyerseats", company.getPid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.seat", is(buyerSeat.getSeat())))
        .andExpect(jsonPath("$.name", is(buyerSeat.getName())))
        .andExpect(jsonPath("$.name", is(buyerSeat.getName())))
        .andExpect(jsonPath("$.buyerTransparencyFeedEnabled", is(true)))
        .andExpect(jsonPath("$.buyerTransparencyDataFeedPid", is(1)));
  }
}
