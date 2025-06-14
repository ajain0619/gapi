package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.services.SellerDealService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SellerDealControllerIT {

  @Mock private SellerDealService sellerDealService;
  @InjectMocks SellerDealController sellerDealController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerDealController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void getPagesDealsAssociatedWithSeller_returnsAllDealsAssociatedWithSeller() throws Exception {
    List<DirectDeal> dealList = TestObjectsFactory.gimme(10, DirectDeal.class);
    doReturn(new PageImpl(dealList))
        .when(sellerDealService)
        .getPagedDealsAssociatedWithSeller(any(), any(), any(), any());

    mockMvc
        .perform(get("/v1/sellers/1/deals"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("content.[0].pid", is(dealList.get(0).getPid())))
        .andExpect(jsonPath("content.[0].description", is(dealList.get(0).getDescription())))
        .andExpect(jsonPath("content.[0].dealId", is(dealList.get(0).getDealId())));
  }

  @Test
  void getDealAssociatedWithSeller_returnsDealAssociatedWithSeller() throws Exception {
    DirectDealDTO directDealDTO = TestObjectsFactory.createBasicDirectDealDTO(1L);
    doReturn(directDealDTO).when(sellerDealService).getDealAssociatedWithSeller(any(), any());

    mockMvc
        .perform(get("/v1/sellers/1/deals/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("pid", is(directDealDTO.getPid().intValue())))
        .andExpect(jsonPath("description", is(directDealDTO.getDescription())))
        .andExpect(jsonPath("dealId", is(directDealDTO.getDealId())));
  }

  @Test
  void shouldCreateDealAssociatedWithSeller() throws Exception {
    DirectDealDTO directDealDTO = TestObjectsFactory.createBasicDirectDealDTO(1L);
    doReturn(directDealDTO)
        .when(sellerDealService)
        .createDealAssociatedWithSeller(anyLong(), any());

    mockMvc
        .perform(post("/v1/sellers/1/deals").content("{}").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("pid", is(directDealDTO.getPid().intValue())))
        .andExpect(jsonPath("description", is(directDealDTO.getDescription())))
        .andExpect(jsonPath("dealId", is(directDealDTO.getDealId())));
  }

  @Test
  void shouldUpdateDealAssociatedWithSeller() throws Exception {
    DirectDealDTO directDealDTO = TestObjectsFactory.createBasicDirectDealDTO(1L);
    doReturn(directDealDTO)
        .when(sellerDealService)
        .updateDealAssociatedWithSeller(anyLong(), anyLong(), any());

    mockMvc
        .perform(put("/v1/sellers/1/deals/1").content("{}").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("pid", is(directDealDTO.getPid().intValue())))
        .andExpect(jsonPath("description", is(directDealDTO.getDescription())))
        .andExpect(jsonPath("dealId", is(directDealDTO.getDealId())));
  }
}
