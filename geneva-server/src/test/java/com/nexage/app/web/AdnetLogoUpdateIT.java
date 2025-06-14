package com.nexage.app.web;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.pubselfserve.AdsourcePubSelfServeView;
import com.nexage.admin.core.pubselfserve.SitePubSelfServeView;
import com.nexage.admin.core.pubselfserve.TagPubSelfServeView;
import com.nexage.app.dto.pub.self.serve.PubSelfServeMediationRuleMetricsDTO;
import com.nexage.app.dto.pub.self.serve.PubSelfServeTagMetricsDTO;
import com.nexage.app.dto.publisher.PublisherBuyerDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.BuyerService;
import com.nexage.app.services.PubSelfServeSummaryService;
import com.nexage.app.services.PublisherSelfService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.impl.support.PubSelfServeSummaryContext;
import com.nexage.app.web.support.BaseControllerItTest;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdnetLogoUpdateIT extends BaseControllerItTest {

  private static final long BUYER_PID = 10024;
  private static final long ADSOURCE_PID = 7071;
  private static final long PUBLISHER_PID = 10201;

  @InjectMocks private BuyerController buyerController;
  @Mock private BuyerService buyerService;

  @Mock private SellerSiteService sellerSiteService;

  @Mock private PublisherSelfService publisherSelfService;

  @InjectMocks private PubSelfServeSummaryController pubSelfServeSummaryController;
  @Mock private PubSelfServeSummaryService pubSelfServeSummaryService;

  @Mock private UserContext userContext;

  @Mock private SpringUserDetails springUserDetails;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @Autowired
  @Qualifier("customViewLayerObjectMapper")
  private ObjectMapper mapper;

  private MockMvc mockMvc;

  @Autowired
  @Qualifier("jsonConverter")
  private MappingJackson2HttpMessageConverter converter;

  @BeforeEach
  public void setUpBuyerController() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(buyerController)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  @SneakyThrows
  void updateAdnetLogo() {
    String payload = getData(ResourcePath.UPDATE_ADSOURCE_WITH_LOGO.getFilePath());
    AdSource expectedAdSource = mapper.readValue(payload, AdSource.class);

    AdSource updateAdnetLogoResult = updateAdSource(payload);
    assertEquals(expectedAdSource.getLogo(), updateAdnetLogoResult.getLogo());
    assertEquals(expectedAdSource.getLogoUrl(), updateAdnetLogoResult.getLogoUrl());

    AdSource getAdSourceResult = getAdSource(expectedAdSource);
    assertEquals(expectedAdSource.getLogo(), getAdSourceResult.getLogo());
    assertEquals(expectedAdSource.getLogoUrl(), getAdSourceResult.getLogoUrl());

    AdSource getBuyersResult = getBuyers(expectedAdSource);
    assertEquals(expectedAdSource.getLogoUrl(), getBuyersResult.getLogoUrl());

    AdSourceSummaryDTO adSourceSummary = new AdSourceSummaryDTO();
    adSourceSummary.setLogoUrl(expectedAdSource.getLogoUrl());
    adSourceSummary.setLogo(expectedAdSource.getLogo());
    List<AdSourceSummaryDTO> adSourceSummaries = singletonList(adSourceSummary);
    List<AdSourceSummaryDTO> getAllAdSourceSummariesResult =
        getAllAdSourceSummaries(adSourceSummaries);
    assertEquals(expectedAdSource.getLogoUrl(), getAllAdSourceSummariesResult.get(0).getLogoUrl());

    setUpPubSelfServeSummaryController();

    List<SitePubSelfServeView> sitePubSelfServeViews = setupSitePubSelfServeViewList();
    List<Long> nexageRTBList = new ArrayList<>();
    String logoBaseUrl = "http://www.oath.com/images/";
    PubSelfServeSummaryContext pubContext =
        new PubSelfServeSummaryContext(
            sellerSiteService.getAllSitesByCompanyPid(10102L), logoBaseUrl);
    PubSelfServeMediationRuleMetricsDTO pubSelfServeMediationRuleMetrics =
        new PubSelfServeMediationRuleMetricsDTO(
            10201, sitePubSelfServeViews, pubContext, nexageRTBList);
    getTagSummary(pubSelfServeMediationRuleMetrics);
    performTestGetAdSourceLogoUrl(logoBaseUrl, expectedAdSource.getLogo());
  }

  @Test
  @SneakyThrows
  void updateExistingAdnetWithLogo() {
    String payload = getData(ResourcePath.UPDATE_EXISTING_ADSOURCE_WITH_LOGO.getFilePath());
    AdSource expectedAdSource = mapper.readValue(payload, AdSource.class);

    AdSource updateAdnetLogoResult = updateAdSource(payload);
    assertEquals(expectedAdSource.getLogo(), updateAdnetLogoResult.getLogo());
    assertEquals(expectedAdSource.getLogoUrl(), updateAdnetLogoResult.getLogoUrl());

    AdSource getAdSourceResult = getAdSource(expectedAdSource);
    assertEquals(expectedAdSource.getLogo(), getAdSourceResult.getLogo());
    assertEquals(expectedAdSource.getLogoUrl(), getAdSourceResult.getLogoUrl());

    AdSource getBuyersResult = getBuyers(expectedAdSource);
    assertEquals(expectedAdSource.getLogoUrl(), getBuyersResult.getLogoUrl());

    AdSourceSummaryDTO adSourceSummary = new AdSourceSummaryDTO();
    adSourceSummary.setLogoUrl(expectedAdSource.getLogoUrl());
    adSourceSummary.setLogo(expectedAdSource.getLogo());
    List<AdSourceSummaryDTO> adSourceSummaries = singletonList(adSourceSummary);
    List<AdSourceSummaryDTO> getAllAdSourceSummariesResult =
        getAllAdSourceSummaries(adSourceSummaries);
    assertEquals(expectedAdSource.getLogoUrl(), getAllAdSourceSummariesResult.get(0).getLogoUrl());

    setUpPubSelfServeSummaryController();

    List<SitePubSelfServeView> sitePubSelfServeViews = setupSitePubSelfServeViewList();
    List<Long> nexageRTBList = new ArrayList<>();
    String logoBaseUrl = "http://www.oath.com/images/";
    PubSelfServeSummaryContext pubContext =
        new PubSelfServeSummaryContext(
            sellerSiteService.getAllSitesByCompanyPid(10102L), logoBaseUrl);
    PubSelfServeMediationRuleMetricsDTO pubSelfServeMediationRuleMetrics =
        new PubSelfServeMediationRuleMetricsDTO(
            10201, sitePubSelfServeViews, pubContext, nexageRTBList);
    getTagSummary(pubSelfServeMediationRuleMetrics);
    performTestGetAdSourceLogoUrl(logoBaseUrl, expectedAdSource.getLogo());
  }

  @Test
  @SneakyThrows
  void updateAdnetWithoutLogo() {
    String payload = getData(ResourcePath.UPDATE_ADSOURCE_WITHOUT_LOGO.getFilePath());
    AdSource expectedAdSource = mapper.readValue(payload, AdSource.class);
    AdSource updateAdnetLogoResult = updateAdSource(payload);
    assertEquals(expectedAdSource.getLogo(), updateAdnetLogoResult.getLogo());
    assertEquals(expectedAdSource.getLogoUrl(), updateAdnetLogoResult.getLogoUrl());
  }

  @SneakyThrows
  private AdSource updateAdSource(String payload) {
    AdSource adSource = mapper.readValue(payload, AdSource.class);

    when(buyerService.updateAdSource(anyLong(), any(AdSource.class), anyLong()))
        .thenReturn(adSource);

    MvcResult updateAdnetLogoresult =
        mockMvc
            .perform(
                put(UrlPath.UPDATE_ADSOURCE_URL.getUrlPath(), BUYER_PID, ADSOURCE_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    return mapper.readValue(
        updateAdnetLogoresult.getResponse().getContentAsString(), AdSource.class);
  }

  @SneakyThrows
  private AdSource getAdSource(AdSource adSource) {
    when(buyerService.getAdSource(anyLong())).thenReturn(adSource);

    MvcResult getAdnetLogoresult =
        mockMvc
            .perform(
                get(UrlPath.GET_ADSOURCE_URL.getUrlPath(), adSource.getPid())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    return mapper.readValue(getAdnetLogoresult.getResponse().getContentAsString(), AdSource.class);
  }

  @SneakyThrows
  private List<AdSourceSummaryDTO> getAllAdSourceSummaries(
      List<AdSourceSummaryDTO> adSourceSummaries) {

    when(buyerService.getAllAdSourceSummaries()).thenReturn(adSourceSummaries);
    MvcResult getAllAdSourceSummariesResult =
        mockMvc
            .perform(
                get(UrlPath.GET_ALL_ADSOURCE_SUMMARIES_URL.getUrlPath())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    return mapper.readValue(
        getAllAdSourceSummariesResult.getResponse().getContentAsString(), new TypeReference<>() {});
  }

  @SneakyThrows
  private AdSource getBuyers(AdSource adSource) {

    PublisherBuyerDTO publisherBuyer = new PublisherBuyerDTO();
    publisherBuyer.setPid(adSource.getPid());
    publisherBuyer.setLogoUrl(adSource.getLogoUrl());

    List<PublisherBuyerDTO> publisherBuyerList = singletonList(publisherBuyer);

    when(publisherSelfService.getBuyers(anyLong(), eq("foo"))).thenReturn(publisherBuyerList);

    MvcResult getBuyer =
        mockMvc
            .perform(
                get(UrlPath.GET_ADSOURCE_URL.getUrlPath(), publisherBuyerList.get(0).getPid())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    return mapper.readValue(getBuyer.getResponse().getContentAsString(), AdSource.class);
  }

  @SneakyThrows
  private void getTagSummary(PubSelfServeMediationRuleMetricsDTO pubSelfServeMediationRuleMetrics) {

    when(pubSelfServeSummaryService.getTagSummary(anyLong(), any(Date.class), any(Date.class)))
        .thenReturn(pubSelfServeMediationRuleMetrics);

    mockMvc
        .perform(
            get(
                    UrlPath.GET_TAG_SUMMARIES_URL.getUrlPath(),
                    PUBLISHER_PID,
                    "2017-10-06T00:00:00-04:00",
                    "2019-10-06T23:59:59-04:00")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  private void performTestGetAdSourceLogoUrl(String baseLogoUrl, String logo) {
    TagPubSelfServeView pubTag = mock(TagPubSelfServeView.class);
    when(pubTag.getAdsource()).thenReturn(mock(AdsourcePubSelfServeView.class));
    when(pubTag.getEcpmProvision()).thenReturn(TagPubSelfServeView.EcpmProvision.Auto.name());
    Site siteDTO = mock(Site.class);
    Tag tag = mock(Tag.class);
    when(tag.getBuyerLogo()).thenReturn(logo);
    PubSelfServeTagMetricsDTO pubSelfServeTagMetrics =
        new PubSelfServeTagMetricsDTO(pubTag, siteDTO, tag, baseLogoUrl);
    if (pubSelfServeTagMetrics.getAdSourceLogoUrl() != null) {
      assertEquals(
          "http://www.oath.com/images/7053-1507659339451.jpg",
          pubSelfServeTagMetrics.getAdSourceLogoUrl());
    }
  }

  private List<SitePubSelfServeView> setupSitePubSelfServeViewList() {
    List<SitePubSelfServeView> sitePubSelfServeViews = new ArrayList<>();
    SitePubSelfServeView sitePubSelfServeView = new SitePubSelfServeView();
    sitePubSelfServeView.setName("Test");
    sitePubSelfServeView.setPid(1234L);
    sitePubSelfServeViews.add(sitePubSelfServeView);
    return sitePubSelfServeViews;
  }

  private void setUpPubSelfServeSummaryController() {
    openMocks(this);
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(pubSelfServeSummaryController)
            .setMessageConverters(converter)
            .build();
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(AdnetLogoUpdateIT.class, name), Charset.forName("UTF-8"));
  }

  @Getter
  enum UrlPath {
    UPDATE_ADSOURCE_URL("/buyers/{buyerPID}/adsources/{adsourcePID}"),
    GET_ADSOURCE_URL("/buyers/adsources/{adsourcePID}"),
    CREATE_ADSOURCE_URL("/buyers/{buyerPID}/adsources/"),
    GET_ALL_ADSOURCE_SUMMARIES_URL("/buyers/adsourcesummaries"),
    GET_BUYERS_URL("/pss/{publisher}/buyer"),
    CREATE_SELLER_NON_EXCHANGETAGS_URL("/sellers/sites/{sitePID}/tags"),
    GET_TAG_SUMMARIES_URL("/publisher/{pub}/tagsummary?start={start}&stop={stop}");

    private String UrlPath;

    UrlPath(String UrlPath) {
      this.UrlPath = UrlPath;
    }
  }

  @Getter
  enum ResourcePath {
    UPDATE_ADSOURCE_WITH_LOGO("/data/buyer_controller/update/updateAdsourceWithLogo_payload.json"),
    UPDATE_EXISTING_ADSOURCE_WITH_LOGO(
        "/data/buyer_controller/update/updateExistingAdsourceWithLogo_payload.json"),
    UPDATE_ADSOURCE_WITHOUT_LOGO(
        "/data/buyer_controller/update/updateAdsourceWithoutLogo_payload.json"),
    CREATE_NON_EXCHANGE_TAG_ALL_FIELDS_SMARTYIELD_PAYLOAD(
        "/data/buyer_controller/create/newNonExchangeTagAllFields_SmartYield_payload.json"),
    CREATE_ADSOURCE_NULL_LOGO_AND_URL_PAYLOAD(
        "/data/buyer_controller/create/createAdsourceNullLogoAndUrl_payload.json");

    private String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
