package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.seller.PlacementSummaryDTO;
import com.nexage.app.services.PlacementSummaryDTOService;
import com.nexage.app.web.placement.PlacementSummaryDTOController;
import com.nexage.app.web.support.TestObjectsFactory;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(MockitoExtension.class)
class PlacementSummaryDTOControllerTest {

  @Mock private PlacementSummaryDTOService placementsService;

  private MockMvc mockMvc;

  @InjectMocks private PlacementSummaryDTOController placementsSummaryController;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(placementsSummaryController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void getPlacementsSummaryBySellerWithName() throws Throwable {
    List<PlacementSummaryDTO> placementSummaryDTOList =
        TestObjectsFactory.gimme(10, PlacementSummaryDTO.class);
    Page<PlacementSummaryDTO> placementSummaryPage = new PageImpl(placementSummaryDTOList);
    PlacementSummaryDTO first = placementSummaryDTOList.get(0);
    when(placementsService.getPlacementsWithMetricsWithoutSitePid(
            any(Date.class),
            any(Date.class),
            any(Long.class),
            any(String.class),
            any(Pageable.class)))
        .thenReturn(placementSummaryPage);
    mockMvc
        .perform(
            get(
                "/v1/sellers/1/placements/summaries?page=0&size=10&startDate=2019-05-21&stopDate=2019-05-27&name=foot"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())));
  }

  @Test
  void getPlacementsSummaryBySellerWithNameWithParseException() throws Throwable {
    List<PlacementSummaryDTO> placementSummaryDTOList =
        TestObjectsFactory.gimme(10, PlacementSummaryDTO.class);
    Page<PlacementSummaryDTO> placementSummaryPage = new PageImpl(placementSummaryDTOList);
    PlacementSummaryDTO first = placementSummaryDTOList.get(0);
    given(
            placementsService.getPlacementsWithMetricsWithoutSitePid(
                any(Date.class),
                any(Date.class),
                any(Long.class),
                any(String.class),
                any(Pageable.class)))
        .willAnswer(
            invocation -> {
              throw new ParseException("parse", 0);
            });
    mockMvc
        .perform(
            get(
                "/v1/sellers/1/placements/summaries?page=0&size=10&startDate=2019-05-21&stopDate=2019-05-27&name=foot"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getPlacementsSummaryBySeller() throws Throwable {
    List<PlacementSummaryDTO> placementSummaryDTOList =
        TestObjectsFactory.gimme(10, PlacementSummaryDTO.class);
    Page<PlacementSummaryDTO> placementSummaryPage = new PageImpl(placementSummaryDTOList);
    PlacementSummaryDTO first = placementSummaryDTOList.get(0);
    when(placementsService.getPlacementsWithMetrics(
            any(Date.class),
            any(Date.class),
            any(Long.class),
            any(Long.class),
            any(Optional.class),
            any(Optional.class),
            any(Pageable.class)))
        .thenReturn(placementSummaryPage);
    mockMvc
        .perform(
            get(
                "/v1/sellers/1/sites/456/placements/summaries?page=0&size=10&startDate=2019-05-21&stopDate=2019-05-27&name=foot"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())));
  }

  @Test
  void getPlacementsSummaryBySellerWithParseException() throws Throwable {
    List<PlacementSummaryDTO> placementSummaryDTOList =
        TestObjectsFactory.gimme(10, PlacementSummaryDTO.class);
    Page<PlacementSummaryDTO> placementSummaryPage = new PageImpl(placementSummaryDTOList);
    PlacementSummaryDTO first = placementSummaryDTOList.get(0);
    given(
            placementsService.getPlacementsWithMetrics(
                any(Date.class),
                any(Date.class),
                any(Long.class),
                any(Long.class),
                any(Optional.class),
                any(Optional.class),
                any(Pageable.class)))
        .willAnswer(
            invocation -> {
              throw new ParseException("parse", 0);
            });
    mockMvc
        .perform(
            get(
                "/v1/sellers/1/sites/456/placements/summaries?page=0&size=10&startDate=2019-05-21&stopDate=2019-05-27&name=foot"))
        .andExpect(status().is4xxClientError());
  }
}
