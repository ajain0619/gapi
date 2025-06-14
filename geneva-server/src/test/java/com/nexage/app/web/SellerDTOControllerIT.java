package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.seller.SellerDTO;
import com.nexage.app.services.SellerDTOService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
class SellerDTOControllerIT {

  private MockMvc mockMvc;

  @InjectMocks private SellerDTOController sellerDTOController;

  @Mock private SellerDTOService sellerDTOService;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerDTOController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void shouldFindAllSellersAsPagedResult() throws Throwable {
    List<SellerDTO> sellersList = TestObjectsFactory.createSellers(10);
    Page<SellerDTO> sellerPage = new PageImpl<>(sellersList);
    SellerDTO first = sellersList.get(0);
    when(sellerDTOService.findAll(any(), any(), eq(false), any())).thenReturn(sellerPage);
    mockMvc
        .perform(get("/v1/sellers/"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())));
  }

  @Test
  void shouldReturnSingleSeller() throws Throwable {
    List<SellerDTO> sellersList = TestObjectsFactory.createSellers(10);
    SellerDTO first = sellersList.get(0);
    when(sellerDTOService.findOne(first.getPid())).thenReturn(first);
    mockMvc
        .perform(get("/v1/sellers/" + first.getPid()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("pid", is(first.getPid())))
        .andExpect(jsonPath("name", is(first.getName())));
  }
}
