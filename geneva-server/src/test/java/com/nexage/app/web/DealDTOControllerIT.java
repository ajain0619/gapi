package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deals.DealDTO;
import com.nexage.app.services.DealDTOService;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import java.util.List;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class DealDTOControllerIT {

  private static final Long DEAL_PID = 1L;

  private MockMvc mockMvc;

  @InjectMocks private DealDTOController dealController;

  @Mock private DealDTOService dealService;
  @Mock private Validator mockValidator;

  private final ObjectMapper objectMapper = new CustomObjectMapper();

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(dealController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void getPagedDeals() throws Throwable {
    List<DirectDealDTO> dealList = TestObjectsFactory.gimme(10, DirectDealDTO.class);
    Page dealPage = new PageImpl(dealList);
    DirectDealDTO deal = dealList.get(0);

    when(dealService.findAll(nullable(String.class), nullable(Set.class), nullable(Pageable.class)))
        .thenReturn(dealPage);

    mockMvc
        .perform(get("/v1/deals"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(deal.getPid())))
        .andExpect(jsonPath("content.[0].description", is(deal.getDescription())))
        .andDo(print());
  }

  @Test
  void getDealTest() throws Exception {
    var dealDTO =
        DealDTO.builder()
            .pid(DEAL_PID)
            .dealId("test")
            .description("test")
            .placementFormulaStatus(PlacementFormulaStatus.DONE)
            .build();
    when(dealService.findOne(DEAL_PID)).thenReturn(dealDTO);

    mockMvc
        .perform(get("/v1/deals/{dealPid}", DEAL_PID))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.pid").value(DEAL_PID))
        .andExpect(jsonPath("$.dealId").value("test"))
        .andExpect(jsonPath("$.description").value("test"))
        .andExpect(jsonPath("$.placementFormulaStatus").value("DONE"));
  }

  @Test
  void shouldGetAllValidDealsSearchParams() throws Exception {
    Page<DealDTO> returnPage =
        new PageImpl<>(
            List.of(
                DealDTO.builder().pid(1L).description("Test Deal 1").build(),
                DealDTO.builder().pid(2L).description("Test Deal 2").build()));

    String expectedOutboundJson = objectMapper.writeValueAsString(returnPage);
    when(dealService.getDeals(any(MultiValueQueryParams.class), any(Pageable.class)))
        .thenReturn(returnPage);

    mockMvc
        .perform(get("/v1/deals?multiSearch&qf=sellers=1,dspBuyerSeats=2_%3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].description", Matchers.is("Test Deal 1")));
  }
}
