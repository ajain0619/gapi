package com.nexage.app.web.buyer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.mapper.BuyerGroupDTOMapper;
import com.nexage.app.services.BuyerGroupDTOService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

class BuyerGroupDTOControllerIT extends BaseControllerItTest {

  @Autowired private PageableHandlerMethodArgumentResolver pageableResolver;

  @Mock private BuyerGroupDTOService buyerGroupDTOService;
  @InjectMocks private BuyerGroupDTOController buyerGroupDTOController;

  private final CustomViewLayerObjectMapper mapper = new CustomViewLayerObjectMapper();

  @BeforeEach
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(buyerGroupDTOController)
            .setCustomArgumentResolvers(pageableResolver)
            .build();
  }

  @Test
  void shouldGetAllBuyerGroups() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);

    List<BuyerGroupDTO> buyerGroupDTOs = new ArrayList<>();

    BuyerGroup buyerGroup1 = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    buyerGroupDTOs.add(BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup1));
    BuyerGroup buyerGroup2 = TestObjectsFactory.createBuyerGroup(company, "EUR", "IRL");
    buyerGroupDTOs.add(BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup2));

    Page<BuyerGroupDTO> buyerGroups = new PageImpl<>(Collections.unmodifiableList(buyerGroupDTOs));

    when(buyerGroupDTOService.findAll(eq(company.getPid()), any(), any(), any()))
        .thenReturn(buyerGroups);

    mockMvc
        .perform(get("/v1/dsps/{companyPid}/buyerGroups", company.getPid()).param("qf", "name"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.size").exists())
        .andExpect(jsonPath("$.content").exists())
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalElements", is(2)))
        .andExpect(jsonPath("$.content[0].pid", is(buyerGroup1.getPid())))
        .andExpect(jsonPath("$.content[1].pid", is(buyerGroup2.getPid())))
        .andExpect(jsonPath("$.content[0].name", is(buyerGroup1.getName())))
        .andExpect(jsonPath("$.content[1].name", is(buyerGroup2.getName())))
        .andExpect(jsonPath("$.content[0].companyPid", is(buyerGroup2.getCompany().getPid())))
        .andExpect(jsonPath("$.content[1].companyPid", is(buyerGroup2.getCompany().getPid())))
        .andReturn();
  }

  @Test
  void shouldCreateBuyerGroup() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO buyerGroupDTO = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);
    when(buyerGroupDTOService.create(eq(company.getPid()), any(BuyerGroupDTO.class)))
        .thenReturn(buyerGroupDTO);

    mockMvc
        .perform(
            post("/v1/dsps/{dspPid}/buyer-groups", company.getPid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buyerGroupDTO)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.currency", equalTo("VND")))
        .andExpect(jsonPath("$.billingCountry", equalTo("ARG")));
  }

  @Test
  void shouldUpdateBuyerGroup() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO buyerGroupDTO = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);
    buyerGroupDTO.setCurrency("USD");
    buyerGroupDTO.setBillingCountry("USA");
    when(buyerGroupDTOService.update(
            eq(company.getPid()), eq(buyerGroupDTO.getPid()), any(BuyerGroupDTO.class)))
        .thenReturn(buyerGroupDTO);

    mockMvc
        .perform(
            put(
                    "/v1/dsps/{dspPid}/buyer-groups/{buyerGroupPid}",
                    company.getPid(),
                    buyerGroupDTO.getPid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buyerGroupDTO)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.currency", equalTo("USD")))
        .andExpect(jsonPath("$.billingCountry", equalTo("USA")));
  }

  @Test
  void shouldGetOneBuyerGroup() throws Exception {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);

    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO buyerGroupDTO = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);

    when(buyerGroupDTOService.findOne(buyerGroup.getPid())).thenReturn(buyerGroupDTO);

    mockMvc
        .perform(get("/v1/dsps/buyer-groups/{buyerGroupPid}", buyerGroup.getPid()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.currency", equalTo("VND")))
        .andExpect(jsonPath("$.billingCountry", equalTo("ARG")))
        .andExpect(jsonPath("$.name", equalTo(buyerGroup.getName())))
        .andExpect(jsonPath("$.sfdcLineId", equalTo(buyerGroup.getSfdcLineId())))
        .andExpect(jsonPath("$.sfdcIoId", equalTo(buyerGroup.getSfdcIoId())));
  }
}
