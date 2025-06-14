package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
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
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.BuyerService;
import com.nexage.app.services.PubSelfServeSummaryService;
import com.nexage.app.services.PublisherSelfService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.SellerTagService;
import com.nexage.app.services.impl.support.PubSelfServeSummaryContext;
import com.nexage.app.web.publisher.PublisherSelfServeController;
import com.nexage.app.web.support.BaseControllerItTest;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

class AdnetLogoCreateIT extends BaseControllerItTest {

  private static final long BUYER_PID = 7071;
  private static final long PUBLISHER_PID = 10201;
  private static final long SITE_PID = 456;
  private static final long POSITION_PID = 10206;

  @InjectMocks private BuyerController buyerController;
  @Mock private BuyerService buyerService;

  @InjectMocks private PublisherSelfServeController publisherSelfServeController;
  @Mock private PublisherSelfService publisherSelfService;

  @Mock private SellerController sellerController;
  @Mock private SellerSiteService sellerSiteService;
  @Mock private SellerTagService sellerTagService;

  @InjectMocks private PubSelfServeSummaryController pubSelfServeSummaryController;
  @Mock private PubSelfServeSummaryService pubSelfServeSummaryService;

  @Mock private UserContext userContext;

  @Mock private SpringUserDetails springUserDetails;

  @Mock private BeanValidationService beanValidationService;

  @Autowired
  @Qualifier("jsonConverter")
  private MappingJackson2HttpMessageConverter converter;

  @Autowired
  @Qualifier("customViewLayerObjectMapper")
  private ObjectMapper mapper;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUpBuyerController() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(buyerController)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  @SneakyThrows
  void shouldCreateAndGetAdnetWithLogo() {
    String payload = getData(ResourcePath.CREATE_ADSOURCE_WITH_LOGO_PAYLOAD.getFilePath());

    AdSource expectedAdSource = mapper.readValue(payload, AdSource.class);
    AdSource adSourceCreatedResult = createAdSource(payload);
    assertEquals(expectedAdSource.toString(), adSourceCreatedResult.toString());

    AdSource adSourceGetResult = getAdSource(payload);
    assertEquals(expectedAdSource.getLogo(), adSourceGetResult.getLogo());
    assertEquals(expectedAdSource.getLogoUrl(), adSourceGetResult.getLogoUrl());

    AdSourceSummaryDTO dbAdSourceSummary = new AdSourceSummaryDTO();
    ImmutableList<AdSourceSummaryDTO> expectedAdSourceSummaries =
        ImmutableList.of(dbAdSourceSummary);
    List<AdSourceSummaryDTO> adSourceSummaryResult =
        getAdSourceSummaries(expectedAdSourceSummaries);
    assertEquals(
        expectedAdSourceSummaries.get(0).getLogoUrl(), adSourceSummaryResult.get(0).getLogoUrl());
    assertEquals(
        expectedAdSourceSummaries.get(0).getLogo(), adSourceSummaryResult.get(0).getLogo());

    setUpPublisherSelfServeController();
    PublisherBuyerDTO publisherBuyer = new PublisherBuyerDTO();
    publisherBuyer.setLogoUrl(expectedAdSource.getLogoUrl());
    publisherBuyer.setPid(PUBLISHER_PID);
    ImmutableList<PublisherBuyerDTO> expectedPublisherBuyerList = ImmutableList.of(publisherBuyer);
    List<PublisherBuyerDTO> getPublisherBuyersResult =
        getPublisherBuyers(expectedPublisherBuyerList);
    assertEquals(
        expectedPublisherBuyerList.get(0).getLogoUrl(),
        getPublisherBuyersResult.get(0).getLogoUrl());

    setUpSellerController();
    String createNonExchangeTagPayload =
        getData(ResourcePath.CREATE_NON_EXCHANGE_TAG_ALL_FIELDS_SMARTYIELD_PAYLOAD.getFilePath());
    createTag(createNonExchangeTagPayload);
    setUpPubSelfServeSummaryController();
    getTagSummary(payload);
  }

  @Test
  @SneakyThrows
  void shouldCreateAdnetWithNullLogoAndUrl() {
    String payload =
        getData(
            AdnetLogoUpdateIT.ResourcePath.CREATE_ADSOURCE_NULL_LOGO_AND_URL_PAYLOAD.getFilePath());

    when(buyerService.createAdSource(anyLong(), any(AdSource.class)))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_ADSOURCE_LOGO));
    mockMvc
        .perform(
            put(AdnetLogoUpdateIT.UrlPath.CREATE_ADSOURCE_URL.getUrlPath(), BUYER_PID)
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @SneakyThrows
  private AdSource createAdSource(String payload) {
    AdSource adSource = mapper.readValue(payload, AdSource.class);

    when(buyerService.createAdSource(anyLong(), any(AdSource.class))).thenReturn(adSource);

    MvcResult result =
        mockMvc
            .perform(
                put(UrlPath.CREATE_ADSOURCE_URL.getUrlPath(), BUYER_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    return mapper.readValue(result.getResponse().getContentAsString(), AdSource.class);
  }

  @SneakyThrows
  private AdSource getAdSource(String payload) {
    AdSource adSource = mapper.readValue(payload, AdSource.class);
    when(buyerService.getAdSource(anyLong())).thenReturn(adSource);
    MvcResult result =
        mockMvc
            .perform(
                get(UrlPath.GET_ADSOURCE_URL.getUrlPath(), adSource.getPid())
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    return mapper.readValue(result.getResponse().getContentAsString(), AdSource.class);
  }

  @SneakyThrows
  private List<AdSourceSummaryDTO> getAdSourceSummaries(
      List<AdSourceSummaryDTO> adSourceSummaryResult) {
    when(buyerService.getAllAdSourceSummaries()).thenReturn(adSourceSummaryResult);

    MvcResult getAllAdSourceSummariesResults =
        mockMvc
            .perform(
                get(UrlPath.GET_ALL_ADSOURCE_SUMMARIES_URL.getUrlPath())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    return mapper.readValue(
        getAllAdSourceSummariesResults.getResponse().getContentAsString(),
        new TypeReference<>() {});
  }

  @SneakyThrows
  private List<PublisherBuyerDTO> getPublisherBuyers(List<PublisherBuyerDTO> publisherBuyerList) {

    when(publisherSelfService.getBuyers(anyLong(), nullable(String.class)))
        .thenReturn(publisherBuyerList);

    MvcResult getBuyersResult =
        mockMvc
            .perform(
                get(UrlPath.GET_BUYERS_URL.getUrlPath(), PUBLISHER_PID)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    return mapper.readValue(
        getBuyersResult.getResponse().getContentAsString(), new TypeReference<>() {});
  }

  @SneakyThrows
  private void createTag(String payload) {
    Tag newTagNonExchange = mapper.readValue(payload, Tag.class);
    Set<Tag> tags = new HashSet<>();
    tags.add(newTagNonExchange);
    Site site = new Site();
    site.setTags(tags);
    when(sellerTagService.createTag(anyLong(), any(Tag.class), anyBoolean())).thenReturn(site);
    mockMvc
        .perform(
            put(
                    UrlPath.CREATE_SELLER_NONEXCHANGE_TAG_URL.getUrlPath(),
                    PUBLISHER_PID,
                    SITE_PID,
                    POSITION_PID)
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  private void getTagSummary(String payload) {
    AdSource adSource = mapper.readValue(payload, AdSource.class);
    List<SitePubSelfServeView> sitePubSelfServeViews = setupSitePubSelfServeViewList();

    List<Long> nexageRTBList = new ArrayList<>();

    String logoBaseUrl = "http://www.oath.com/images/";

    PubSelfServeSummaryContext pubContext =
        new PubSelfServeSummaryContext(
            sellerSiteService.getAllSitesByCompanyPid(10102L), logoBaseUrl);

    PubSelfServeMediationRuleMetricsDTO pubSelfServeMediationRuleMetrics =
        new PubSelfServeMediationRuleMetricsDTO(
            10201, sitePubSelfServeViews, pubContext, nexageRTBList);

    performTestCreateAdSourceLogoUrl(logoBaseUrl, adSource.getLogo());

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

  private void performTestCreateAdSourceLogoUrl(String baseLogoUrl, String logo) {
    TagPubSelfServeView pubTag = mock(TagPubSelfServeView.class);
    when(pubTag.getAdsource()).thenReturn(mock(AdsourcePubSelfServeView.class));
    when(pubTag.getEcpmProvision()).thenReturn(TagPubSelfServeView.EcpmProvision.Auto.name());
    Site siteDTO = mock(Site.class);
    Tag tag = mock(Tag.class);
    when(tag.getBuyerLogo()).thenReturn(logo);
    PubSelfServeTagMetricsDTO pubSelfServeTagMetrics =
        new PubSelfServeTagMetricsDTO(pubTag, siteDTO, tag, baseLogoUrl);

    assertEquals(
        "http://www.oath.com/images/logos/7076-1582656356971.jpg",
        pubSelfServeTagMetrics.getAdSourceLogoUrl());
  }

  private List<SitePubSelfServeView> setupSitePubSelfServeViewList() {
    List<SitePubSelfServeView> sitePubSelfServeViews = new ArrayList<>();
    SitePubSelfServeView sitePubSelfServeView = new SitePubSelfServeView();
    sitePubSelfServeView.setName("Test");
    sitePubSelfServeView.setPid(1234L);
    sitePubSelfServeViews.add(sitePubSelfServeView);
    return sitePubSelfServeViews;
  }

  private void setUpPublisherSelfServeController() {
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(publisherSelfServeController)
            .setMessageConverters(converter)
            .build();
  }

  private void setUpSellerController() {
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(sellerController).setMessageConverters(converter).build();
  }

  private void setUpPubSelfServeSummaryController() {
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(pubSelfServeSummaryController)
            .setMessageConverters(converter)
            .build();
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(com.nexage.app.web.AdnetLogoCreateIT.class, name),
        StandardCharsets.UTF_8);
  }

  @Getter
  enum UrlPath {
    CREATE_ADSOURCE_URL("/buyers/{buyerPID}/adsources/"),
    CREATE_SELLER_NONEXCHANGE_TAG_URL("/sellers/sites/{sitePID}/tags"),
    GET_ADSOURCE_URL("/buyers/adsources/{adsourcePID}"),
    GET_ALL_ADSOURCE_SUMMARIES_URL("/buyers/adsourcesummaries"),
    GET_BUYERS_URL("/pss/{publisher}/buyer"),
    GET_TAG_SUMMARIES_URL("/publisher/{pub}/tagsummary?start={start}&stop={stop}");

    private final String UrlPath;

    UrlPath(String UrlPath) {
      this.UrlPath = UrlPath;
    }
  }

  @Getter
  enum ResourcePath {
    CREATE_ADSOURCE_WITH_LOGO_PAYLOAD(
        "/data/buyer_controller/create/createAdsourceWithLogo_payload.json"),
    CREATE_NON_EXCHANGE_TAG_ALL_FIELDS_SMARTYIELD_PAYLOAD(
        "/data/buyer_controller/create/newNonExchangeTagAllFields_SmartYield_payload.json");

    private final String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
