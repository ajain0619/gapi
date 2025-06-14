package com.nexage.app.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.nexage.admin.core.dto.SearchSummaryDTO;
import com.nexage.admin.core.dto.SiteSummaryDTO;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.SiteDealTermSummaryDTO;
import com.nexage.app.services.SearchService;
import com.nexage.app.services.SellerSiteService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SellerControllerIT {

  private MockMvc mockMvc;

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @Mock private SearchService<SearchSummaryDTO> searchService;

  @Mock private SellerSiteService sellerSiteService;

  @InjectMocks private SellerController sellerController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sellerController)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void getAllSiteSummaries() throws Exception {
    List<SiteSummaryDTO> siteSummaries = getSiteSummaries();
    SiteSummaryDTO first = siteSummaries.get(0);
    when(sellerSiteService.getAllSitesSummary()).thenReturn(siteSummaries);
    mockMvc
        .perform(get("/sellers/sitesummaries"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is("site1")))
        .andExpect(jsonPath("$[0].pid", is(1)))
        .andExpect(jsonPath("$[0].sellerPid", is(1)))
        .andExpect(jsonPath("$[0].sellerName", is("seller1")))
        .andExpect(jsonPath("$[0].name", is("site1")))
        .andExpect(jsonPath("$[0].globalAliasName", is(first.getGlobalAliasName())))
        .andExpect(jsonPath("$[0].type", is("MOBILE_WEB")))
        .andExpect(jsonPath("$[0].platform", is("OTHER")))
        .andExpect(jsonPath("$[0].status", is("ACTIVE")))
        .andExpect(jsonPath("$[0].live", is(true)))
        .andExpect(jsonPath("$[0].url", is("http://site1.com")))
        .andExpect(jsonPath("$[0].domain", is("site1.com")));
  }

  @Test
  void getAllSiteSummariesBySeller() throws Exception {
    when(sellerSiteService.getAllSitesSummaryByCompanyPid(1L)).thenReturn(getSiteSummaries());
    mockMvc
        .perform(get("/sellers/1/sitesummaries"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is("site1")))
        .andExpect(jsonPath("$[0].pid", is(1)))
        .andExpect(jsonPath("$[0].sellerPid", is(1)))
        .andExpect(jsonPath("$[0].sellerName", is("seller1")))
        .andExpect(jsonPath("$[0].name", is("site1")))
        .andExpect(jsonPath("$[0].type", is("MOBILE_WEB")))
        .andExpect(jsonPath("$[0].platform", is("OTHER")))
        .andExpect(jsonPath("$[0].status", is("ACTIVE")))
        .andExpect(jsonPath("$[0].live", is(true)))
        .andExpect(jsonPath("$[0].url", is("http://site1.com")))
        .andExpect(jsonPath("$[0].domain", is("site1.com")));
  }

  @Test
  void getAllowedSitesForUser() throws Exception {
    when(sellerSiteService.getAllowedSitesForUser(1L)).thenReturn(getSiteSummaries());
    mockMvc
        .perform(get("/sellers/sitesummaries?userPID=1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is("site1")))
        .andExpect(jsonPath("$[0].pid", is(1)))
        .andExpect(jsonPath("$[0].sellerPid", is(1)))
        .andExpect(jsonPath("$[0].sellerName", is("seller1")))
        .andExpect(jsonPath("$[0].name", is("site1")))
        .andExpect(jsonPath("$[0].type", is("MOBILE_WEB")))
        .andExpect(jsonPath("$[0].platform", is("OTHER")))
        .andExpect(jsonPath("$[0].status", is("ACTIVE")))
        .andExpect(jsonPath("$[0].live", is(true)))
        .andExpect(jsonPath("$[0].url", is("http://site1.com")))
        .andExpect(jsonPath("$[0].domain", is("site1.com")));
  }

  @Test
  void shouldUpdateSiteDealTermsToPubDefaultByYieldManager() throws Exception {
    // Given
    List<Long> list = new ArrayList<>();
    list.add(1L);
    list.add(2L);
    list.add(3L);

    String jsonRequest = mapper.writeValueAsString(list);

    List<SiteDealTermSummaryDTO> siteDealTermSummaryDTOList = new ArrayList<>();

    SiteDealTermSummaryDTO siteDealTermSummaryDTO =
        new SiteDealTermSummaryDTO.Builder()
            .setSiteName("SiteOne")
            .setSitePid(1L)
            .setRevShare(new BigDecimal("0.2"))
            .setRtbFee(new BigDecimal("0.01"))
            .build();

    siteDealTermSummaryDTOList.add(siteDealTermSummaryDTO);

    when(sellerSiteService.updateSiteDealTermsToPubDefaultByYieldManager(anyLong(), anyList()))
        .thenReturn(siteDealTermSummaryDTOList);

    // When
    mockMvc
        .perform(
            put("/sellers/sites/setdefaultdealterm?sellerPid=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        // Then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].siteName", is("SiteOne")))
        .andExpect(jsonPath("$[0].sitePid", is(1)))
        .andExpect(jsonPath("$[0].revShare", is(0.2)))
        .andExpect(jsonPath("$[0].rtbFee", is(0.01)));
  }

  @Test
  void shouldThrowGenevaValidationException() throws Exception {
    // Given
    List<Long> list = new ArrayList<>();
    list.add(1L);
    list.add(2L);
    list.add(3L);

    String jsonRequest = mapper.writeValueAsString(list);

    when(sellerSiteService.updateSiteDealTermsToPubDefaultByYieldManager(anyLong(), anyList()))
        .thenThrow(new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));

    // When
    mockMvc
        .perform(
            put("/sellers/sites/setdefaultdealterm?sellerPid=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        // Then
        .andExpect(status().isBadRequest());
  }

  @Test
  void getSiteForPid() throws Exception {
    when(sellerSiteService.getSite(1L)).thenReturn(getSite());
    mockMvc
        .perform(get("/sellers/sites/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is("test-id")))
        .andExpect(jsonPath("$.pid", is(1)))
        .andExpect(jsonPath("$.name", is("geneva-test")))
        .andExpect(jsonPath("$.impressionGroup", is(notNullValue())));
  }

  @Test
  void findSearchSummaryDtosContaining() throws Exception {
    when(searchService.findSearchSummaryDtosContaining("abc")).thenReturn(getSearchSummary());
    mockMvc
        .perform(get("/sellers?prefix=abc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  private List<SiteSummaryDTO> getSiteSummaries() {
    List<SiteSummaryDTO> summaries = new ArrayList<>();
    SiteSummaryDTO sum =
        new SiteSummaryDTO(
            "site1",
            1L,
            "http://site1.com",
            "site1",
            "Site global alias",
            Type.MOBILE_WEB,
            Platform.OTHER,
            Status.ACTIVE,
            true,
            1L,
            "seller1",
            "site1.com");
    summaries.add(sum);
    return summaries;
  }

  private Site getSite() {
    Site site = new Site();
    site.setId("test-id");
    site.setPid(1L);
    site.setName("geneva-test");
    site.setGroupsEnabled(true);
    return site;
  }

  private List<SearchSummaryDTO> getSearchSummary() {
    List<SearchSummaryDTO> summaries = new ArrayList<>();
    return summaries;
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(SellerControllerIT.class, name), Charset.forName("UTF-8"));
  }
}
