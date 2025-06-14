package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.services.SiteService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
class SellerSeatSiteControllerIT {

  private static final String URL = "/v1/seller-seats/{sellerSeatPid}/sites";
  private static final long SELLER_SEAT_PID = 123L;

  private MockMvc mockMvc;

  @Mock private SiteService siteService;

  @InjectMocks private SellerSeatSiteController sellerSeatSiteController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerSeatSiteController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void shouldReturnPagedFullSitesForSellerSeat() throws Exception {
    // given
    List<SiteDTO> sites = TestObjectsFactory.createSite(3);

    given(
            siteService.getSitesForSellerSeat(
                eq(SELLER_SEAT_PID),
                any(Pageable.class),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.empty())))
        .willReturn(new PageImpl<>(sites));
    mockMvc
        .perform(get(URL, SELLER_SEAT_PID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(sites.get(0).getPid())));
  }

  @Test
  void shouldReturnPagedMinimalSitesForSellerSeat() throws Exception {
    // given
    List<SiteDTO> sites = TestObjectsFactory.createSite(3);

    given(
            siteService.getSiteMinimalDataForSellerSeat(
                eq(SELLER_SEAT_PID), eq(""), any(Pageable.class)))
        .willReturn(new PageImpl<>(sites));

    mockMvc
        .perform(get(URL + "?minimal=true", SELLER_SEAT_PID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(sites.get(0).getPid())));
  }
}
