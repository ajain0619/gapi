package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.BidderFormat;
import com.nexage.admin.core.enums.BuyerDomainVerificationAuthLevel;
import com.nexage.admin.core.enums.VerificationType;
import com.nexage.app.dto.BidderConfigDTO;
import com.nexage.app.dto.BidderConfigDTOView;
import com.nexage.app.services.BidderConfigDTOService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class BidderConfigDTOControllerIT {

  @Mock private BidderConfigDTOService bidderConfigDTOService;
  @InjectMocks private BidderConfigDTOController bidderConfigDTOController;
  private ObjectMapper objectMapper;
  @Autowired private PageableHandlerMethodArgumentResolver pageableResolver;
  @Autowired ControllerExceptionHandler controllerExceptionHandler;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(bidderConfigDTOController)
            .setControllerAdvice(controllerExceptionHandler)
            .setCustomArgumentResolvers(pageableResolver)
            .build();
    objectMapper = new CustomViewLayerObjectMapper();
  }

  static BidderConfigDTO validBidderConfigDTO() {
    BidderConfigDTO bidderConfigDTO = new BidderConfigDTO();
    bidderConfigDTO.setBidRequestCpm(new BigDecimal("1.5"));
    bidderConfigDTO.setCountryFilter("USA");
    bidderConfigDTO.setRequestRateFilter(5);
    bidderConfigDTO.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfigDTO.setSubscriptions(Set.of());
    bidderConfigDTO.setExchangeRegionals(Set.of());
    bidderConfigDTO.setAllowedDeviceTypes(Set.of());
    bidderConfigDTO.setVerificationType(VerificationType.STANDARD);
    bidderConfigDTO.setRegionLimits(Set.of());
    bidderConfigDTO.setDomainFilterAllowUnknownUrls(true);
    bidderConfigDTO.setAllowBridgeIdMatch(true);
    bidderConfigDTO.setAllowConnectId(true);
    bidderConfigDTO.setAllowIdGraphMatch(true);
    bidderConfigDTO.setAllowLiveramp(true);
    bidderConfigDTO.setDomainVerificationAuthLevel(BuyerDomainVerificationAuthLevel.ALLOW_ALL);
    bidderConfigDTO.setBidderConfigDenyAllowFilterLists(Set.of());
    bidderConfigDTO.setSendDealSizes(true);
    return bidderConfigDTO;
  }

  MockHttpServletRequestBuilder request(HttpMethod method, String uri, Object payload)
      throws JsonProcessingException {
    return MockMvcRequestBuilders.request(method, uri)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload));
  }

  @Test
  void createDelegatesToService() throws Exception {
    BidderConfigDTO input = validBidderConfigDTO();
    input.setId("input_id_value");
    BidderConfigDTO output = new BidderConfigDTO();
    output.setPid(5L);
    when(bidderConfigDTOService.create(eq(8L), argThat(bc -> bc.getId().equals("input_id_value"))))
        .thenReturn(output);

    mockMvc
        .perform(request(HttpMethod.POST, "/v1/dsps/8/bidder-configs", input))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.pid", is(5)));
  }

  @Test
  void createValidatesRequestBody() throws Exception {
    BidderConfigDTO input = validBidderConfigDTO();
    input.setBidRequestCpm(null);

    mockMvc
        .perform(request(HttpMethod.POST, "/v1/dsps/4/bidder-configs", input))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.fieldErrors.bidRequestCpm", is("must not be null")));
  }

  @Test
  void createNullRequestBodyIsValid() throws Exception {
    BidderConfigDTO output = new BidderConfigDTO();
    output.setPid(8L);
    when(bidderConfigDTOService.create(14L, null)).thenReturn(output);

    mockMvc
        .perform(request(HttpMethod.POST, "/v1/dsps/14/bidder-configs", null))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.pid", is(8)));
  }

  @Test
  void getDelegatesToService() throws Exception {
    BidderConfigDTO bidderConfigDTO = new BidderConfigDTO();
    bidderConfigDTO.setId("id_value");
    when(bidderConfigDTOService.get(17L, 12L)).thenReturn(bidderConfigDTO);

    mockMvc
        .perform(get("/v1/dsps/17/bidder-configs/12"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is("id_value")));
  }

  @Test
  void updateDelegatesToService() throws Exception {
    BidderConfigDTO input = validBidderConfigDTO();
    input.setId("input_id_value");
    BidderConfigDTO output = new BidderConfigDTO();
    output.setPid(11L);
    when(bidderConfigDTOService.update(
            eq(15L), eq(19L), argThat(bc -> bc.getId().equals("input_id_value"))))
        .thenReturn(output);

    mockMvc
        .perform(request(HttpMethod.PUT, "/v1/dsps/15/bidder-configs/19", input))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.pid", is(11)));
  }

  @Test
  void updateValidatesRequestBody() throws Exception {
    BidderConfigDTO input = validBidderConfigDTO();
    input.setBidRequestCpm(null);

    mockMvc
        .perform(request(HttpMethod.PUT, "/v1/dsps/5/bidder-configs/6", input))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.fieldErrors.bidRequestCpm", is("must not be null")));
  }

  @Test
  void shouldFindAllSummariesDelegatesToService() throws Exception {
    Long dspPid = TestObjectsFactory.randomLong();
    BidderConfigDTOView dto1 = TestObjectsFactory.createBidderConfigDTOView(dspPid, "dto1");
    BidderConfigDTOView dto2 = TestObjectsFactory.createBidderConfigDTOView(dspPid, "dto2");
    Page<BidderConfigDTOView> bidderConfigSummaryDTOS = new PageImpl<>(List.of(dto1, dto2));
    when(bidderConfigDTOService.findAllBidderConfigs(any(), any(), any(), any()))
        .thenReturn(bidderConfigSummaryDTOS);

    mockMvc
        .perform(
            get("/v1/dsps/{dspPid}/bidder-configs/?", dspPid)
                .param("qf", "name")
                .param("qt", "dto"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.size").exists())
        .andExpect(jsonPath("$.content").exists())
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalElements", is(2)))
        .andExpect(jsonPath("$.content[0].pid", is((dto1.getPid()))))
        .andExpect(jsonPath("$.content[1].pid", is(dto2.getPid())))
        .andExpect(jsonPath("$.content[0].name", is(dto1.getName())))
        .andExpect(jsonPath("$.content[1].name", is(dto2.getName())))
        .andExpect(jsonPath("$.content[0].companyPid", is(dto1.getCompanyPid())))
        .andExpect(jsonPath("$.content[1].companyPid", is(dto2.getCompanyPid())));
  }
}
