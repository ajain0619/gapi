package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.seller.SellerSummaryDTO;
import com.nexage.app.services.SellerSeatSummaryService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SellerSeatSummaryControllerIT {

  private MockMvc mockMvc;

  @Autowired private SellerSeatSummaryService sellerSeatSummaryService;

  @Autowired private SellerSeatSummaryController sellerSeatSummaryController;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerSeatSummaryController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void findSummary() throws Throwable {
    List<SellerSummaryDTO> sellerSummaryDTOList = TestObjectsFactory.createSellerSummaries(10);
    Page<SellerSummaryDTO> sellerSummaryDTOPage = new PageImpl<>(sellerSummaryDTOList);
    SellerSummaryDTO first = sellerSummaryDTOList.get(0);
    when(sellerSeatSummaryService.findSummary(anyLong(), any(), any(), any(), any(), any()))
        .thenReturn(sellerSummaryDTOPage);
    mockMvc
        .perform(
            get(
                "/v1/seller-seats/{sellerSeatPid}/summaries?page=0&size=10&startDate=2019-05-21&stopDate=2019-05-27",
                1L))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].adRequested", is(first.getAdRequested())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())));
  }
}
