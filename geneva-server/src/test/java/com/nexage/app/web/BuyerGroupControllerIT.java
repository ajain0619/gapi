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
import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.mapper.BuyerGroupDTOMapper;
import com.nexage.app.services.BuyerService;
import com.nexage.app.web.buyer.BuyerGroupController;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BuyerGroupControllerIT extends BaseControllerItTest {

  @Mock private BuyerService buyerService;
  @InjectMocks private BuyerGroupController buyerGroupController;

  @BeforeEach
  public void setUp() throws Exception {
    this.mockMvc = MockMvcBuilders.standaloneSetup(buyerGroupController).build();
  }

  @Test
  void testGetAllBuyerGroups() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);

    List<BuyerGroupDTO> buyerGroupDtos = new ArrayList<>();

    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    buyerGroupDtos.add(BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup));

    BuyerGroup buyerGroup2 = TestObjectsFactory.createBuyerGroup(company, "EUR", "IRL");
    buyerGroupDtos.add(BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup2));

    when(buyerService.getAllBuyerGroupsForCompany(company.getPid())).thenReturn(buyerGroupDtos);

    mockMvc
        .perform(get("/buyers/{companyPid}/buyergroups", company.getPid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].currency", is(buyerGroup.getCurrency())))
        .andExpect(jsonPath("$[1].currency", is(buyerGroup2.getCurrency())))
        .andExpect(jsonPath("$[0].billingCountry", is(buyerGroup.getBillingCountry())))
        .andExpect(jsonPath("$[1].billingCountry", is(buyerGroup2.getBillingCountry())))
        .andReturn();
  }

  @Test
  void testCreateBuyerGroup() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO dto = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);

    String requestJson = new ObjectMapper().writeValueAsString(dto);

    when(buyerService.createBuyerGroup(eq(company.getPid()), any(BuyerGroupDTO.class)))
        .thenReturn(dto);

    mockMvc
        .perform(
            post("/buyers/{companyPid}/buyergroups", company.getPid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currency", is(buyerGroup.getCurrency())))
        .andExpect(jsonPath("$.billingCountry", is(buyerGroup.getBillingCountry())));
  }

  @Test
  void whenDtoValid_thenUpdateBuyerGroupSucceeds() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");

    BuyerGroupDTO dto =
        new BuyerGroupDTO(
            1L, "name", "sfdcLineId", "sfdcIoId", "USD", "USA", true, 0, company.getPid());

    String requestJson = new ObjectMapper().writeValueAsString(dto);

    when(buyerService.updateBuyerGroup(
            eq(company.getPid()), eq(buyerGroup.getPid()), any(BuyerGroupDTO.class)))
        .thenReturn(dto);

    mockMvc
        .perform(
            put(
                    "/buyers/{companyPid}/buyergroups/{buyerGroupPid}",
                    company.getPid(),
                    buyerGroup.getPid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currency", is("USD")))
        .andExpect(jsonPath("$.billingCountry", is("USA")));
  }

  @Test
  void whenDtoInvalid_thenUpdateBuyerGroupFails() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");

    BuyerGroupDTO[] invalids = {
      new BuyerGroupDTO(
          1L, null, "sfdcLineId", "sfdcIoId", "USD", "USA", true, 0, company.getPid()),
      new BuyerGroupDTO(1L, "name", null, "sfdcIoId", "USD", "USA", true, 0, company.getPid()),
      new BuyerGroupDTO(1L, "name", "sfdcLineId", null, "USD", "USA", true, 0, company.getPid()),
      new BuyerGroupDTO(
          1L, "name", "sfdcLineId", "sfdcIoId", null, "USA", true, 0, company.getPid()),
      new BuyerGroupDTO(
          1L, "name", "sfdcLineId", "sfdcIoId", "USD", null, true, 0, company.getPid())
    };

    for (BuyerGroupDTO dto : invalids) {
      assertFail(dto, company, buyerGroup);
    }
  }

  private void assertFail(BuyerGroupDTO dto, Company company, BuyerGroup buyerGroup)
      throws Exception {
    String requestJson = new ObjectMapper().writeValueAsString(dto);

    when(buyerService.updateBuyerGroup(
            eq(company.getPid()), eq(buyerGroup.getPid()), any(BuyerGroupDTO.class)))
        .thenReturn(dto);

    mockMvc
        .perform(
            put(
                    "/buyers/{companyPid}/buyergroups/{buyerGroupPid}",
                    company.getPid(),
                    buyerGroup.getPid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isBadRequest());
  }
}
