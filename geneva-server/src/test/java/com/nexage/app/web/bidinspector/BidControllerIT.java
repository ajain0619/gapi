package com.nexage.app.web.bidinspector;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.util.PagedAdResult;
import com.nexage.app.web.ControllerExceptionHandler;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.search.MultiValueSearchParamsArgumentResolver;
import com.ssp.geneva.server.bidinspector.dto.AuctionDetailDTO;
import com.ssp.geneva.server.bidinspector.dto.BidDTO;
import com.ssp.geneva.server.bidinspector.services.BidService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class BidControllerIT extends SpringWebConstraintValidatorFactory {
  private static int bidderId = 100;
  private static int sellerId = 10;
  private static int placementId = 22;
  private static int siteId = 100;
  private static String dealId = "P123";
  private static String requestUrl =
      "https://reserved.v.ssp.yahoo.com/reserved?ext_id=SSPMigration";
  private static String bidderUrl =
      "https://reserved.bidder.ssp.yahoo.com/reserved?ext_id=SSPMigration";
  private static String requestPayload =
      "{\"bidderId\":13807,\"url\":\"https://yahoo.com\",\"auction\":\"e7d09415c18041b7b5d3ccc5cc9e9d6d\"}";
  private static String responsePayload =
      "{\"bidderId\":13807,\"url\":\"null\",\"auction\":\"e7d09415c18041b7b5d3ccc5cc9e9d6d\"}";
  private static final int TOTAL_PAGES = 1;
  private static final int TOTAL_ELEMENTS = 1;

  private MockMvc mockMvc;
  @Autowired ControllerExceptionHandler controllerExceptionHandler;

  @Mock private BidService bidService;
  @Mock private BeanValidationService beanValidationService;
  @InjectMocks private BidController bidController;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(bidController)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new MultiValueSearchParamsArgumentResolver())
            .alwaysDo(MockMvcResultHandlers.print())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldThrowExceptionWhenGetBidsInputParamHasInvalidKey() throws Exception {
    MultiValueMap<String, String> mVMap = new LinkedMultiValueMap<>();
    mVMap.add("invalidKey", "787");
    mVMap.add("bidderId", "22030");
    doThrow(EntityConstraintViolationException.class).when(beanValidationService).validate(any());
    when(bidService.getBidDetails(any(), eq(PageRequest.of(0, 50))))
        .thenReturn(getBids(bidBuilder));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/v1/bids?page=0&size=20&qf={mVMap}", mVMap))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        CommonErrorCodes.COMMON_BAD_REQUEST))));
  }

  @Test
  void shouldThrowExceptionWhenGetBidsInputParamHasInvalidValue() throws Exception {
    MultiValueMap<String, String> mVMap = new LinkedMultiValueMap<>();
    mVMap.add("sellerId", "abc787");
    mVMap.add("bidderId", "22030");
    doThrow(EntityConstraintViolationException.class).when(beanValidationService).validate(any());
    when(bidService.getBidDetails(any(), eq(PageRequest.of(0, 50))))
        .thenReturn(getBids(bidBuilder));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/v1/bids?page=0&size=20&qf={mVMap}", mVMap))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        CommonErrorCodes.COMMON_BAD_REQUEST))));
  }

  @Test
  void shouldReturnExpectedResultGetBidsWithNoFilterAndDefaultSort() throws Exception {
    when(bidService.getBidDetails(any(), eq(PageRequest.of(0, 50, Sort.Direction.DESC, "start"))))
        .thenReturn(getBids(bidBuilder));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/v1/bids?page=0&size=50"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.[0].sellerId", is(sellerId)))
        .andExpect(jsonPath("$.content.[0].siteId", is(siteId)))
        .andExpect(jsonPath("$.content.[0].placementId", is(placementId)))
        .andExpect(jsonPath("$.content.[0].dealId", is(dealId)))
        .andExpect(jsonPath("$.content.[0].requestUrl", is(requestUrl)))
        .andExpect(jsonPath("$.content.[0].requestPayload", is(requestPayload)))
        .andExpect(jsonPath("$.content.[0].responsePayload", is(responsePayload)))
        .andExpect(jsonPath("$.content.[0].bidderId", is(bidderId)))
        .andExpect(jsonPath("$.totalPages", is(TOTAL_PAGES)))
        .andExpect(jsonPath("$.totalElements", is(TOTAL_ELEMENTS)));
  }

  @Test
  void shouldReturnExpectedResultGetBidsWithFilterAndCustomSort() throws Exception {
    MultiValueMap<String, String> mVMap = new LinkedMultiValueMap<>();
    mVMap.add("sellerId", "10");
    mVMap.add("bidderId", "100");
    when(bidService.getBidDetails(any(), eq(PageRequest.of(0, 50, Sort.Direction.ASC, "sellerId"))))
        .thenReturn(getBids(bidBuilder));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                "/v1/bids?page=0&size=50&sort=sellerId,asc&qf={mVMap}", mVMap))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.[0].sellerId", is(sellerId)))
        .andExpect(jsonPath("$.content.[0].siteId", is(siteId)))
        .andExpect(jsonPath("$.content.[0].placementId", is(placementId)))
        .andExpect(jsonPath("$.content.[0].dealId", is(dealId)))
        .andExpect(jsonPath("$.content.[0].requestUrl", is(requestUrl)))
        .andExpect(jsonPath("$.content.[0].requestPayload", is(requestPayload)))
        .andExpect(jsonPath("$.content.[0].responsePayload", is(responsePayload)))
        .andExpect(jsonPath("$.content.[0].bidderId", is(bidderId)))
        .andExpect(jsonPath("$.totalPages", is(TOTAL_PAGES)))
        .andExpect(jsonPath("$.totalElements", is(TOTAL_ELEMENTS)));
  }

  @Test
  void shouldReturnExpectedResultGetAuctionDetailWithNoFilterAndDefaultSort() throws Exception {
    when(bidService.getAuctionDetails(
            any(), any(), eq(PageRequest.of(0, 50, Sort.Direction.DESC, "start"))))
        .thenReturn(getAuctionDetails(auctionDetailBuilder));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/v1/bids/45670/auction-details?page=0&size=50"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.[0].bidderId", is(bidderId)))
        .andExpect(jsonPath("$.content.[0].bidderUrl", is(bidderUrl)))
        .andExpect(jsonPath("$.content.[0].requestPayload", is(requestPayload)))
        .andExpect(jsonPath("$.content.[0].responsePayload", is(responsePayload)))
        .andExpect(jsonPath("$.content.[0].dealIds", is(List.of(dealId))))
        .andExpect(jsonPath("$.totalPages", is(TOTAL_PAGES)))
        .andExpect(jsonPath("$.totalElements", is(TOTAL_ELEMENTS)));
  }

  private Page<BidDTO> getBids(BidDTO bid) {
    List<BidDTO> bids = new ArrayList<>();
    bids.add(bid);
    return new PagedAdResult<>(bids, new HashSet<>(), PageRequest.of(0, 20), 1L);
  }

  private Page<AuctionDetailDTO> getAuctionDetails(AuctionDetailDTO auctionDetailDTO) {
    List<AuctionDetailDTO> auctionDetails = new ArrayList<>();
    auctionDetails.add(auctionDetailDTO);
    return new PagedAdResult<>(auctionDetails, new HashSet<>(), PageRequest.of(0, 20), 1L);
  }

  AuctionDetailDTO auctionDetailBuilder =
      AuctionDetailDTO.builder()
          .bidderUrl(bidderUrl)
          .requestPayload(requestPayload)
          .responsePayload(responsePayload)
          .bidderId(bidderId)
          .dealIds(List.of(dealId))
          .build();
  BidDTO bidBuilder =
      BidDTO.builder()
          .siteId((long) siteId)
          .sellerId((long) sellerId)
          .placementId(placementId)
          .dealId(dealId)
          .requestUrl(requestUrl)
          .requestPayload(requestPayload)
          .responsePayload(responsePayload)
          .bidderId(bidderId)
          .build();
}
