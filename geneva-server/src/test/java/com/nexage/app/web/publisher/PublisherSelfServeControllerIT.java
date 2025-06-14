package com.nexage.app.web.publisher;

import static com.nexage.app.util.PlacementAssociationTypeTestUtil.GOOGLE_EB;
import static java.lang.Boolean.FALSE;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.SiteUpdateInfoDTO;
import com.nexage.app.dto.Status;
import com.nexage.app.dto.publisher.PublisherAdSourceDefaultsDTO;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.PublisherSelfService;
import com.nexage.app.services.SellerLimitService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.util.ResourceLoader;
import com.nexage.app.web.ControllerExceptionHandler;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.groups.Default;
import lombok.SneakyThrows;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
public class PublisherSelfServeControllerIT {
  private static final String GET_POSITIONS_URL = "/pss/{publisher}/site/{site}/position";
  private static final String CREATE_POSITION_URL = GET_POSITIONS_URL;
  private static final String GET_ONE_POSITION_URL = GET_POSITIONS_URL + "/{position}";
  private static final String UPDATE_POSITION_URL = GET_ONE_POSITION_URL;

  private static final String DATA_DIRECTORY = "/data/controllers/publisher_self_serve";
  private static final String ADSOURCE_DEFAULTS_JSON_PATH =
      DATA_DIRECTORY + "/GetAdSourceDefaults_ER.json";
  private static final String GET_POSITIONS_ER =
      DATA_DIRECTORY + "/positions/expected_results/GetAllPlacements_ER.json";
  private static final String GET_ONE_POSITION_ER =
      DATA_DIRECTORY + "/positions/expected_results/GetSelectedPlacement_ER.json";
  private static final String CREATE_POSITION_PAYLOAD =
      DATA_DIRECTORY + "/positions/create/CreateBannerPlacement_payload.json";
  private static final String CREATE_POSITION_ER =
      DATA_DIRECTORY + "/positions/expected_results/CreateBannerPlacement_ER.json";
  private static final String UPDATE_POSITION_PAYLOAD =
      DATA_DIRECTORY + "/positions/update/UpdateBannerPlacement_payload.json";
  private static final String UPDATE_POSITION_ER =
      DATA_DIRECTORY + "/positions/expected_results/UpdateBannerPlacement_ER.json";

  private MockMvc mockMvc;
  private ObjectWriter ow;

  @Autowired
  @Qualifier("jsonConverter")
  private MappingJackson2HttpMessageConverter converter;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @Mock private PublisherSelfService publisherSelfService;
  @Mock private UserContext userContext;
  @Mock private SpringUserDetails springUserDetails;
  @Mock private SellerLimitService sellerLimitService;
  @Mock private BeanValidationService beanValidationService;

  @InjectMocks private PublisherSelfServeController publisherSelfServeController;

  private final CustomViewLayerObjectMapper mapper = new CustomViewLayerObjectMapper();

  @BeforeEach
  void setUp() {
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(publisherSelfServeController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setMessageConverters(converter)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
    ow = mapper.writer().withDefaultPrettyPrinter();
  }

  @Test
  @SneakyThrows
  void shouldCreatePosition() {
    String payload = getData(CREATE_POSITION_PAYLOAD);
    String expected = getData(CREATE_POSITION_ER);

    PublisherPositionDTO position = mapper.readValue(payload, PublisherPositionDTO.class);
    PublisherPositionDTO expectedPosition = mapper.readValue(expected, PublisherPositionDTO.class);

    when(publisherSelfService.createPosition(
            anyLong(), any(PublisherPositionDTO.class), anyBoolean()))
        .thenReturn(position);
    when(userContext.isNexageUser()).thenReturn(true);

    MvcResult result =
        mockMvc
            .perform(
                post(CREATE_POSITION_URL, 100, 100)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

    assertEquals(
        result.getResponse().getContentAsString(), mapper.writeValueAsString(expectedPosition));
  }

  @Test
  @SneakyThrows
  void shouldGetSites() {
    Long publisher = RandomUtils.nextLong();
    List<PublisherSiteDTO> sites =
        Lists.newArrayList(createPublisherSite(publisher), createPublisherSite(publisher));
    PublisherSiteDTO first = sites.get(0);
    PublisherSiteDTO second = sites.get(1);

    when(publisherSelfService.getSites(anyLong(), eq(false))).thenReturn(sites);
    mockMvc
        .perform(get("/pss/{publisher}/site", publisher))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(sites.size())))
        .andExpect(jsonPath("$[0].pid", is(first.getPid())))
        .andExpect(jsonPath("$[0].publisher.pid", is(publisher)))
        .andExpect(jsonPath("$[1].pid", is(second.getPid())))
        .andExpect(jsonPath("$[1].publisher.pid", is(publisher)));
  }

  @Test
  @SneakyThrows
  void shouldGetSite() {
    Long publisher = RandomUtils.nextLong();
    PublisherSiteDTO first = createPublisherSite(publisher);

    when(publisherSelfService.getSite(anyLong(), eq(false))).thenReturn(first);
    mockMvc
        .perform(get("/pss/{publisher}/site/{site}", publisher, first.getPid()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.pid", is(first.getPid())))
        .andExpect(jsonPath("$.publisher.pid", is(publisher)));

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
  }

  @Test
  @SneakyThrows
  void shouldGetSiteWithDrpWhenDetailParamIsTrue() {
    Long publisher = RandomUtils.nextLong();
    PublisherSiteDTO first = createPublisherSiteWithDRP(publisher);
    first.getDefaultRtbProfile().setAlterReserve(AlterReserve.ONLY_IF_HIGHER);

    when(publisherSelfService.getSite(anyLong(), eq(true))).thenReturn(first);
    mockMvc
        .perform(get("/pss/{publisher}/site/{site}?detail=true", publisher, first.getPid()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.defaultRtbProfile.pid", is(8888)))
        .andExpect(jsonPath("$.defaultRtbProfile.id", is("id8888")))
        .andExpect(jsonPath("$.defaultRtbProfile.description", is("desc8888")))
        .andExpect(jsonPath("$.defaultRtbProfile.tag").doesNotExist())
        .andExpect(jsonPath("$.defaultRtbProfile.defaultRtbProfileOwnerCompanyPid", is(publisher)))
        .andExpect(
            jsonPath(
                "$.defaultRtbProfile.alterReserve", is(AlterReserve.ONLY_IF_HIGHER.toString())));
  }

  @Test
  @SneakyThrows
  void shouldGetSiteWithDrpWhenDetailParamIsFalse() {
    Long publisher = RandomUtils.nextLong();
    PublisherSiteDTO first = createPublisherSiteWithDRP(publisher);

    when(publisherSelfService.getSite(anyLong(), eq(false))).thenReturn(first);
    mockMvc
        .perform(get("/pss/{publisher}/site/{site}?detail=false", publisher, first.getPid()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        // pick arbitrary fields just to prove getSite(long, false) was called
        .andExpect(jsonPath("$.defaultRtbProfile.pid", is(8888)))
        .andExpect(jsonPath("$.defaultRtbProfile.defaultRtbProfileOwnerCompanyPid", is(publisher)));
  }

  @Test
  @SneakyThrows
  void shouldGetSiteWithDrpWhenDetailParamIsAbsent() {
    Long publisher = RandomUtils.nextLong();
    PublisherSiteDTO first = createPublisherSiteWithDRP(publisher);

    when(publisherSelfService.getSite(anyLong(), eq(false))).thenReturn(first);
    mockMvc
        .perform(get("/pss/{publisher}/site/{site}", publisher, first.getPid()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        // pick arbitrary fields just to prove getSite(long, false) was called
        .andExpect(jsonPath("$.defaultRtbProfile.pid", is(8888)))
        .andExpect(jsonPath("$.defaultRtbProfile.defaultRtbProfileOwnerCompanyPid", is(publisher)));
  }

  @Test
  @SneakyThrows
  void shouldGetTier() {
    Long publisher = RandomUtils.nextLong();
    Long site = RandomUtils.nextLong();
    Long pid = RandomUtils.nextLong();
    Long position = RandomUtils.nextLong();
    Long tagPid = RandomUtils.nextLong();
    PublisherTierDTO tier =
        createPublisherTier(pid, position, TierType.WATERFALL, Collections.singletonList(tagPid));

    when(publisherSelfService.getTier(anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(tier);
    mockMvc
        .perform(
            get(
                "/pss/{publisher}/site/{site}/position/{position}/tier/{tier}",
                publisher,
                site,
                position,
                pid))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.pid", is(tier.getPid())))
        .andExpect(jsonPath("$.version", is(0)))
        .andExpect(jsonPath("$.level", is(0)))
        .andExpect(jsonPath("$.tierType", is(TierType.WATERFALL.toString())))
        .andExpect(jsonPath("$.tags", hasSize(tier.getTags().size())))
        .andExpect(jsonPath("$.tags[0].pid", is(tagPid)));
  }

  @Test
  @SneakyThrows
  void shouldCreateTier() {
    Long publisher = RandomUtils.nextLong();
    Long site = RandomUtils.nextLong();
    Long pid = RandomUtils.nextLong();
    Long position = RandomUtils.nextLong();
    Long tagPid = RandomUtils.nextLong();

    for (TierType type : TierType.values()) {
      PublisherTierDTO respTier =
          createPublisherTier(pid, position, type, Collections.singletonList(tagPid));

      String requestJson =
          "{\"level\":0,\"orderStrategy\":\"Dynamic\",\"pid\":null,\"tags\":[{\"pid\":5566584}],\"tierType\":\""
              + type.name()
              + "\",\"version\":null}";
      when(publisherSelfService.createTier(
              anyLong(), anyLong(), anyLong(), any(PublisherTierDTO.class)))
          .thenReturn(respTier);
      if (type == TierType.SY_DECISION_MAKER) {
        mockMvc
            .perform(
                post(
                        "/pss/{publisher}/site/{site}/position/{position}/tier",
                        publisher,
                        site,
                        position)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().isBadRequest());
      } else {
        mockMvc
            .perform(
                post(
                        "/pss/{publisher}/site/{site}/position/{position}/tier",
                        publisher,
                        site,
                        position)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.pid", is(respTier.getPid())))
            .andExpect(jsonPath("$.version", is(0)))
            .andExpect(jsonPath("$.level", is(0)))
            .andExpect(jsonPath("$.tierType", is(type.toString())))
            .andExpect(jsonPath("$.tags", hasSize(respTier.getTags().size())))
            .andExpect(jsonPath("$.tags[0].pid", is(tagPid)));
      }
    }
  }

  @Test
  @SneakyThrows
  void shouldUpdateTier() {
    Long publisher = RandomUtils.nextLong();
    Long site = RandomUtils.nextLong();
    Long pid = RandomUtils.nextLong();
    Long position = RandomUtils.nextLong();
    Long tagPid = RandomUtils.nextLong();

    for (TierType type : TierType.values()) {
      PublisherTierDTO respTier =
          createPublisherTier(pid, position, type, Collections.singletonList(tagPid));

      String requestJson =
          "{\"level\":0,\"orderStrategy\":\"Dynamic\",\"pid\":"
              + pid
              + ",\"tags\":[{\"pid\":5566584}],\"tierType\":\""
              + type.name()
              + "\",\"version\":1}";
      when(publisherSelfService.updateTier(
              anyLong(), anyLong(), anyLong(), any(PublisherTierDTO.class)))
          .thenReturn(respTier);
      if (type == TierType.SY_DECISION_MAKER) {
        mockMvc
            .perform(
                put(
                        "/pss/{publisher}/site/{site}/position/{position}/tier/{tier}",
                        publisher,
                        site,
                        position,
                        pid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().isBadRequest());
      } else {
        mockMvc
            .perform(
                put(
                        "/pss/{publisher}/site/{site}/position/{position}/tier/{tier}",
                        publisher,
                        site,
                        position,
                        pid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.pid", is(respTier.getPid())))
            .andExpect(jsonPath("$.version", is(0)))
            .andExpect(jsonPath("$.level", is(0)))
            .andExpect(jsonPath("$.tierType", is(type.toString())))
            .andExpect(jsonPath("$.tags", hasSize(respTier.getTags().size())))
            .andExpect(jsonPath("$.tags[0].pid", is(tagPid)));
      }
    }
  }

  @Test
  @SneakyThrows
  void shouldThrowWhenCreatingSiteWithInvalidInput() {
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    Long publisher = RandomUtils.nextLong();
    String requestJson = "{}";
    doThrow(EntityConstraintViolationException.class).when(beanValidationService).validate(any());

    mockMvc
        .perform(
            post("/pss/{publisher}/site", publisher)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldThrowWhenUpdatingSiteWithInvalidInput() {
    when(userContext.isApiUser()).thenReturn(true);
    when(userContext.canAccessPublisher(1L)).thenReturn(true);
    doThrow(EntityConstraintViolationException.class).when(beanValidationService).validate(any());

    Long publisher = 1L;
    String requestJson = "{}";

    mockMvc
        .perform(
            put("/pss/{publisher}/site/950", publisher)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnForbiddenStatusWhenGettingAnotherPublishersSites() {
    when(userContext.isApiUser()).thenReturn(true);
    when(userContext.getCompanyPids()).thenReturn(Collections.singleton(1L));

    Long publisher = 2L;

    mockMvc.perform(get("/pss/{publisher}/site", publisher)).andExpect(status().isForbidden());
  }

  @Test
  @SneakyThrows
  void shouldReturnForbiddenStatusWhenGettingAnotherPublishersSite() {
    when(userContext.isApiUser()).thenReturn(true);
    when(userContext.getCompanyPids()).thenReturn(Collections.singleton(1L));

    Long publisher = 2L;

    mockMvc.perform(get("/pss/{publisher}/site/950", publisher)).andExpect(status().isForbidden());
  }

  @Test
  @SneakyThrows
  void shouldReturnForbiddenStatusWhenCreatingAnotherPublishersSite() {
    when(userContext.isApiUser()).thenReturn(true);
    when(userContext.getCompanyPids()).thenReturn(Collections.singleton(1L));

    Long publisher = 2L;
    String requestJson = "{}";

    mockMvc
        .perform(
            post("/pss/{publisher}/site", publisher)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isForbidden());
  }

  @Test
  @SneakyThrows
  void shouldReturnForbiddenStatusWhenUpdatingAnotherPublishersSite() {
    when(userContext.isApiUser()).thenReturn(true);
    when(userContext.getCompanyPids()).thenReturn(Collections.singleton(1L));

    Long publisher = 2L;
    String requestJson = "{}";

    mockMvc
        .perform(
            put("/pss/{publisher}/site/950", publisher)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isForbidden());
  }

  @Test
  @SneakyThrows
  void shouldReturnForbiddenStatusWhenDeletingAnotherPublishersSite() {
    when(userContext.isApiUser()).thenReturn(true);
    when(userContext.getCompanyPids()).thenReturn(Collections.singleton(1L));

    Long publisher = 2L;

    mockMvc
        .perform(delete("/pss/{publisher}/site/950", publisher))
        .andExpect(status().isForbidden());
  }

  @Test
  @SneakyThrows
  void shouldUpdatePosition() {
    String payload = getData(UPDATE_POSITION_PAYLOAD);
    String expected = getData(UPDATE_POSITION_ER);

    PublisherPositionDTO position = mapper.readValue(payload, PublisherPositionDTO.class);
    PublisherPositionDTO expectedPosition = mapper.readValue(expected, PublisherPositionDTO.class);

    when(publisherSelfService.updatePosition(
            anyLong(), anyLong(), any(PublisherPositionDTO.class), anyBoolean()))
        .thenReturn(position);

    MvcResult result =
        mockMvc
            .perform(
                put(UPDATE_POSITION_URL, 100, 100, 100247)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    assertEquals(
        result.getResponse().getContentAsString(), mapper.writeValueAsString(expectedPosition));
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestStatusWhenUpdatingPositionWithInvalidInput() {
    long positionPid = 100;
    long sitePid = 10000202;
    long publisherPid = 10020;
    String payload = "{}";

    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), any(), any());

    mockMvc
        .perform(
            put(UPDATE_POSITION_URL, publisherPid, sitePid, positionPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldGetAdsourceDefaults() {
    String expected = getData(ADSOURCE_DEFAULTS_JSON_PATH);

    PublisherAdSourceDefaultsDTO adDefaults =
        mapper.readValue(expected, PublisherAdSourceDefaultsDTO.class);

    long publisherID = 3;
    long adsourceID = 7008;

    when(publisherSelfService.getAdsourceDefaultsForSeller(anyLong(), anyLong()))
        .thenReturn(adDefaults);

    MvcResult result =
        mockMvc
            .perform(
                get(
                        "/pss/{publisherID}/adsourcedefaults/adsource/{adsourceId}",
                        publisherID,
                        adsourceID)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String content = result.getResponse().getContentAsString();

    expected = expected.replaceAll("[\\t\\n\\s]+", "");

    assertEquals(expected, content);
  }

  @Test
  @SneakyThrows
  void shouldGetPositions() {
    String expected = getData(GET_POSITIONS_ER);

    List<PublisherPositionDTO> positions = mapper.readValue(expected, List.class);

    when(publisherSelfService.getPositions(anyLong(), anyLong(), anyBoolean()))
        .thenReturn(positions);

    MvcResult result =
        mockMvc.perform(get(GET_POSITIONS_URL, 100, 100)).andExpect(status().isOk()).andReturn();

    String resultStr = result.getResponse().getContentAsString();

    expected = expected.replaceAll("[\\t\\n\\s]+", "");

    resultStr = resultStr.replaceAll("[\\s]+", "");

    assertEquals(expected, resultStr);
  }

  @Test
  @SneakyThrows
  void shouldGetOnePosition() {
    String expected = getData(GET_ONE_POSITION_ER);

    PublisherPositionDTO position = mapper.readValue(expected, PublisherPositionDTO.class);

    when(publisherSelfService.getPosition(anyLong(), anyLong(), anyLong(), anyBoolean()))
        .thenReturn(position);

    MvcResult result =
        mockMvc
            .perform(get(GET_ONE_POSITION_URL, 100, 100, 100246))
            .andExpect(status().isOk())
            .andReturn();

    String resultStr = result.getResponse().getContentAsString();

    expected = expected.replaceAll("[\\t\\n\\s]+", "");

    resultStr = resultStr.replaceAll("[\\s]+", "");

    assertEquals(expected, resultStr);
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenCreatingPositionWithInvalidInput() {
    long publisher = RandomUtils.nextLong();
    PublisherSiteDTO first = createPublisherSiteWithDRP(publisher);

    PublisherPositionDTO publisherPositionDTO = CreatePublisherPositionDTO();
    when(publisherSelfService.createPosition(anyLong(), any(), eq(false)))
        .thenReturn(publisherPositionDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), eq(CreateGroup.class), eq(Default.class));
    String payload = "{}";

    mockMvc
        .perform(
            post("/pss/{publisher}/site/{site}/position", publisher, first.getPid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldUpdatePublisherPosition() {
    long positionPid = 100251;
    long sitePid = 10000174;
    long publisherPid = 10201;
    String payload = String.format("{\"pid\":%d}", positionPid);

    mockMvc
        .perform(
            put(
                    "/pss/{publiherId}/site/{siteId}/position/{positionId}",
                    publisherPid,
                    sitePid,
                    positionPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenUpdatingPublisherPositionWithInvalidInput() {
    long positionPid = 100251;
    long sitePid = 10000174;
    long publisherPid = 10201;
    String payload = "{}";
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), any(), any());

    mockMvc
        .perform(
            put(
                    "/pss/{publiherId}/site/{siteId}/position/{positionId}",
                    publisherPid,
                    sitePid,
                    positionPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenCreatingSiteWithInvalidInput() {
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    Long publisher = RandomUtils.nextLong();
    String requestJson = "{}";
    doThrow(EntityConstraintViolationException.class).when(beanValidationService).validate(any());

    mockMvc
        .perform(
            post("/pss/{publisher}/site", publisher)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCreatePublisherSite() throws Exception {
    // given
    long publisherPid = 123L;
    PublisherSiteDTO site = createPublisherSite(publisherPid);
    String expectedSiteJson = mapper.writeValueAsString(site);

    when(sellerLimitService.canCreateSites(publisherPid)).thenReturn(true);
    given(publisherSelfService.createSite(anyLong(), any(), anyBoolean())).willReturn(site);

    // when
    var result =
        mockMvc
            .perform(
                post("/pss/{publisher}/site", publisherPid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
            .andExpect(status().isCreated())
            .andReturn();

    // then
    assertEquals(expectedSiteJson, result.getResponse().getContentAsString());
  }

  @Test
  void shouldUpdatePublisherSite() throws Exception {
    // given
    long publisherPid = 123L;
    PublisherSiteDTO site = createPublisherSite(publisherPid);
    long sitePid = site.getPid();
    String expectedSiteJson = mapper.writeValueAsString(site);
    String txId = "abc";
    SiteUpdateInfoDTO updateInfo = new SiteUpdateInfoDTO.Builder().setTxId(txId).build();

    given(publisherSelfService.updateSite(anyLong(), any(), anyBoolean())).willReturn(site);
    given(publisherSelfService.siteUpdateInfo(anyLong(), any(), anyBoolean()))
        .willReturn(updateInfo);

    // when
    var result =
        mockMvc
            .perform(
                put("/pss/{publisher}/site/{site}?txIdSiteUpdate={tx}", publisherPid, sitePid, txId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(expectedSiteJson))
            .andExpect(status().isOk())
            .andReturn();

    // then
    assertEquals(expectedSiteJson, result.getResponse().getContentAsString());
  }

  @Test
  void shouldReturnOkStatusWhenValidatingValidTag() throws Exception {
    // given
    long publisherPid = 123L;
    long adnetPid = 234L;
    String primaryId = "abc";
    String primaryName = "def";
    String secondaryId = "ghi";
    String secondaryName = "jkl";

    // when/then
    mockMvc
        .perform(
            get(
                "/pss/{publisher}/tag?adnet={adnet}&pid={pid}&pname={pname}&sid={sid}&sname={sname}",
                publisherPid,
                adnetPid,
                primaryId,
                primaryName,
                secondaryId,
                secondaryName))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturnBadRequestStatusWhenValidatingInvalidTag() throws Exception {
    // given
    long publisherPid = 123L;
    long adnetPid = 234L;
    String primaryId = "abc";
    String primaryName = "def";
    String secondaryId = "ghi";
    String secondaryName = "jkl";

    doThrow(GenevaValidationException.class)
        .when(publisherSelfService)
        .validateTag(publisherPid, adnetPid, primaryId, primaryName, secondaryId, secondaryName);

    // when/then
    mockMvc
        .perform(
            get(
                "/pss/{publisher}/tag?adnet={adnet}&pid={pid}&pname={pname}&sid={sid}&sname={sname}",
                publisherPid,
                adnetPid,
                primaryId,
                primaryName,
                secondaryId,
                secondaryName))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldDeleteAdsourceDefaultsForAdSource() throws Exception {
    // given
    long publisherPid = 123L;
    long adSourcePid = 456L;

    // when/then
    mockMvc
        .perform(
            delete(
                "/pss/{publisher}/adsourcedefaults/adsource/{adsourceId}",
                publisherPid,
                adSourcePid))
        .andExpect(status().isNoContent());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenCreatingPositionWithInvalidAssociationType() throws Exception {
    String payload = getData(CREATE_POSITION_PAYLOAD);

    PublisherPositionDTO position = mapper.readValue(payload, PublisherPositionDTO.class);

    setInvalidHbAttributes(position);
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), eq(CreateGroup.class), eq(Default.class));

    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            post(CREATE_POSITION_URL, 100, 100)
                .content(ow.writeValueAsString(position))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenUpdatingPositionWithInvalidAssociationType() throws Exception {
    String payload = getData(UPDATE_POSITION_PAYLOAD);

    PublisherPositionDTO position = mapper.readValue(payload, PublisherPositionDTO.class);

    setInvalidHbAttributes(position);
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), eq(UpdateGroup.class), eq(Default.class));

    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            put(UPDATE_POSITION_URL, 100, 100, position.getPid())
                .content(ow.writeValueAsString(position))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenCreatingPositionWithInvalidMultiBiddingFields() throws Exception {
    String payload = getData(CREATE_POSITION_PAYLOAD);

    PublisherPositionDTO position = mapper.readValue(payload, PublisherPositionDTO.class);

    setInvalidMultiBiddingFields(position);
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), eq(CreateGroup.class), eq(Default.class));

    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            post(CREATE_POSITION_URL, 100, 100)
                .content(ow.writeValueAsString(position))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenUpdatingPositionWithInvalidMultiBiddingFields() throws Exception {
    String payload = getData(UPDATE_POSITION_PAYLOAD);

    PublisherPositionDTO position = mapper.readValue(payload, PublisherPositionDTO.class);

    setInvalidMultiBiddingFields(position);
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), eq(UpdateGroup.class), eq(Default.class));

    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            put(UPDATE_POSITION_URL, 100, 100, position.getPid())
                .content(ow.writeValueAsString(position))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  void setInvalidHbAttributes(PublisherPositionDTO position) {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(GOOGLE_EB);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT_BANNER);
    HbPartnerAssignmentDTO invalidHbPartnerAttribute = new HbPartnerAssignmentDTO();
    invalidHbPartnerAttribute.setHbPartnerPid(GOOGLE_EB);
    invalidHbPartnerAttribute.setType(AssociationType.DEFAULT_VIDEO);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(validHbPartnerAttribute, invalidHbPartnerAttribute);
    position.setHbPartnerAttributes(hbPartnerAttributes);
  }

  void setInvalidMultiBiddingFields(PublisherPositionDTO position) {
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(false);
    placementVideoDTO.setCompetitiveSeparation(true);
    position.setPlacementVideo(placementVideoDTO);
  }

  private PublisherSiteDTO createPublisherSite(long publisher) {
    PublisherSiteDTO.Builder builder =
        PublisherSiteDTO.newBuilder()
            .withPid(RandomUtils.nextLong())
            .withVersion(RandomUtils.nextInt())
            .withDescription(RandomStringUtils.randomAlphanumeric(44))
            .withDomain(RandomStringUtils.randomAlphanumeric(12))
            .withPlatform(
                PublisherSiteDTO.Platform.values()[
                    RandomUtils.nextInt(PublisherSiteDTO.Platform.values().length)])
            .withStatus(Status.ACTIVE)
            .withType(
                PublisherSiteDTO.SiteType.values()[
                    RandomUtils.nextInt(PublisherSiteDTO.SiteType.values().length)])
            .withUrl(RandomStringUtils.randomAlphanumeric(255))
            .withAppBundle(RandomStringUtils.randomAlphanumeric(16))
            .withCoppaRestricted(RandomUtils.nextBoolean())
            .withName(RandomStringUtils.randomAlphanumeric(20))
            .withDcn(RandomStringUtils.randomAlphanumeric(32))
            .withIabCategories(
                Sets.newHashSet(
                    RandomStringUtils.randomAlphanumeric(4),
                    RandomStringUtils.randomAlphanumeric(4)))
            .withRtb1CategoryRollup(RandomStringUtils.randomAlphanumeric(4))
            .withPublisher(PublisherDTO.newBuilder().withPid(publisher).build());
    return builder.build();
  }

  private PublisherSiteDTO createPublisherSiteWithDRP(long publisher) {
    PublisherSiteDTO.Builder builder =
        PublisherSiteDTO.newBuilder()
            .withPid(RandomUtils.nextLong())
            .withVersion(RandomUtils.nextInt())
            .withDescription(RandomStringUtils.randomAlphanumeric(44))
            .withDomain(RandomStringUtils.randomAlphanumeric(12))
            .withPlatform(
                PublisherSiteDTO.Platform.values()[
                    RandomUtils.nextInt(PublisherSiteDTO.Platform.values().length)])
            .withStatus(Status.ACTIVE)
            .withType(
                PublisherSiteDTO.SiteType.values()[
                    RandomUtils.nextInt(PublisherSiteDTO.SiteType.values().length)])
            .withUrl(RandomStringUtils.randomAlphanumeric(255))
            .withAppBundle(RandomStringUtils.randomAlphanumeric(16))
            .withCoppaRestricted(RandomUtils.nextBoolean())
            .withName(RandomStringUtils.randomAlphanumeric(20))
            .withDcn(RandomStringUtils.randomAlphanumeric(32))
            .withIabCategories(
                Sets.newHashSet(
                    RandomStringUtils.randomAlphanumeric(4),
                    RandomStringUtils.randomAlphanumeric(4)))
            .withRtb1CategoryRollup(RandomStringUtils.randomAlphanumeric(4))
            .withPublisher(PublisherDTO.newBuilder().withPid(publisher).build())
            .withDefaultRtbProfile(
                PublisherDefaultRTBProfileDTO.newBuilder()
                    .withPid(8888L)
                    .withId("id8888")
                    .withDescription("desc8888")
                    .withTag(null)
                    .withDefaultRtbProfileOwnerCompanyPid(publisher)
                    .build());
    return builder.build();
  }

  private PublisherTierDTO createPublisherTier(
      Long pid, Long position, TierType tierType, List<Long> tags) {
    PublisherTierDTO.Builder builder =
        PublisherTierDTO.newBuilder()
            .withIsAutogenerated(Boolean.FALSE)
            .withLevel(0)
            .withName(RandomStringUtils.randomAlphanumeric(12))
            .withOrderStrategy(PublisherTierDTO.OrderStrategy.Dynamic)
            .withPid(pid)
            .withPosition(PublisherPositionDTO.builder().withPid(position).build())
            .withTierType(tierType)
            .withVersion(0);
    tags.forEach(
        t ->
            builder.withTag(
                PublisherTagDTO.newBuilder().withPid(t).withStatus(Status.ACTIVE).build()));
    return builder.build();
  }

  private String getData(String name) throws IOException {
    return ResourceLoader.getResource(PublisherSelfServeControllerIT.class, name);
  }

  private PublisherPositionDTO CreatePublisherPositionDTO() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setWidth(300);
    publisherPositionDTO.setHeight(50);
    publisherPositionDTO.setName("foo");
    publisherPositionDTO.setMemo("foo");
    publisherPositionDTO.setVideoSupport(VideoSupport.BANNER);
    publisherPositionDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    publisherPositionDTO.setPlacementCategory(PlacementCategory.BANNER);
    publisherPositionDTO.setInterstitial(false);
    publisherPositionDTO.setMraidAdvancedTracking(FALSE);
    publisherPositionDTO.setMraidSupport(MRAIDSupport.YES);
    publisherPositionDTO.setAdSizeType(AdSizeType.STANDARD);

    return publisherPositionDTO;
  }
}
