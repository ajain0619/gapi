package com.nexage.app.web;

import static java.lang.Boolean.FALSE;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.jayway.jsonpath.JsonPath;
import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.VideoPlacementType;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.PlacementsService;
import com.nexage.app.services.SellerLimitService;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.util.validator.CheckPermissionValidator;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.util.validator.placement.PlacementDTOVideoSettingsConstraint;
import com.nexage.app.web.placement.PlacementDTOController;
import com.nexage.app.web.support.TestObjectsFactory;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintValidator;
import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class PlacementDTOControllerIT extends SpringWebConstraintValidatorFactory {

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  private MockMvc mockMvc;
  @Autowired private MockServletContext servletContext;
  @Autowired private CustomViewLayerObjectMapper mapper;
  @Mock private CheckPermissionValidator checkPermissionValidator;
  @Mock private PlacementsService placementsService;
  @Mock private UserContext userContext;
  @Mock private SellerLimitService sellerLimitService;
  @Mock private PlacementDTOVideoSettingsConstraint placementDTOVideoSettingsConstraint;
  @InjectMocks private PlacementDTOController placementsController;
  private ObjectMapper objectMapper;

  private static final String VALID_PLAYER_ID_PLAYLIST_ID = "test-valid-playerid-playlistid";
  private static final String INVALID_PLAYER_ID_PLAYLIST_ID = "Invalid#@PlayerID!PlayListID";
  private static final String MAX_LENGTH_PLAYER_ID_PLAYLIST_ID =
      "abcdefghijklmnopqrstuvwxyz-1234567890-abcd-123-123-123-asdfasdf-asdf";

  @BeforeEach
  void setUp() {
    objectMapper = new CustomObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    mockMvc =
        MockMvcBuilders.standaloneSetup(placementsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setValidator(
                createLocalValidatorFactoryBean(new GenericWebApplicationContext(servletContext)))
            .setControllerAdvice(controllerExceptionHandler)
            .build();
    when(checkPermissionValidator.isValid(any(PlacementDTO.class), any())).thenReturn(true);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v1/sellers/1/sites/1/placements",
        "/v1/sellers/1/sites/1/placements?qt=search,abc",
        "/v1/sellers/1/sites/1/placements?qt=duplicate,name,abc,memo,bcd,alias,cde"
      })
  void shouldGetPlacements(String api) throws Throwable {
    List<PlacementDTO> placementsList = TestObjectsFactory.createPlacements(10);
    Page placementPage = new PageImpl(placementsList);
    PlacementDTO first = placementsList.get(0);
    when(placementsService.getPlacements(
            nullable(Pageable.class),
            nullable(Optional.class),
            nullable(Long.class),
            nullable(Optional.class),
            nullable(Optional.class),
            nullable(Optional.class)))
        .thenReturn(placementPage);
    mockMvc
        .perform(get(api))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())));
  }

  @Test
  void getPlacementsWithVideo() throws Exception {
    String expectedResponse = getData(ResourcePath.GET_PLACEMENT_JSON.filePath);
    PlacementDTO placementDTO = mapper.readValue(expectedResponse, PlacementDTO.class);
    List<PlacementDTO> placementsList = new ArrayList<>();
    placementsList.add(placementDTO);
    Page<PlacementDTO> output = new PageImpl<>(placementsList);
    PlacementDTO first = placementsList.get(0);
    when(placementsService.getPlacements(
            nullable(Pageable.class),
            nullable(Optional.class),
            nullable(Long.class),
            nullable(Optional.class),
            nullable(Optional.class),
            nullable(Optional.class)))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(get("/v1/sellers/1/sites/1/placements").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
            .andExpect(jsonPath("content.[0].name", is(first.getName())))
            .andReturn();

    String actual =
        JsonPath.parse(mvcRsult.getResponse().getContentAsString())
            .read("content[0].placementVideo")
            .toString();
    assertEquals(JsonPath.parse(expectedResponse).read("placementVideo").toString(), actual);
  }

  @Test
  void getPlacementsWithVideoAndCompanion() throws Exception {
    String expectedResponse = getData(ResourcePath.GET_PLACEMENT_WITH_COMPANION_JSON.filePath);
    PlacementDTO placementDTO = mapper.readValue(expectedResponse, PlacementDTO.class);
    List<PlacementDTO> placementsList = new ArrayList<>();
    placementsList.add(placementDTO);
    Page<PlacementDTO> output = new PageImpl<>(placementsList);
    PlacementDTO first = placementsList.get(0);
    when(placementsService.getPlacements(
            nullable(Pageable.class),
            nullable(Optional.class),
            nullable(Long.class),
            nullable(Optional.class),
            nullable(Optional.class),
            nullable(Optional.class)))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(get("/v1/sellers/1/sites/1/placements").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
            .andExpect(jsonPath("content.[0].name", is(first.getName())))
            .andReturn();

    String actual =
        JsonPath.parse(mvcRsult.getResponse().getContentAsString())
            .read("content[0].placementVideo")
            .toString();
    assertEquals(JsonPath.parse(expectedResponse).read("placementVideo").toString(), actual);
  }

  @Test
  void getPlacementsWithLongformVideo() throws Exception {
    String expectedResponse = getData(ResourcePath.GET_LONGFORM_PLACEMENT_JSON.filePath);
    PlacementDTO placementDTO = mapper.readValue(expectedResponse, PlacementDTO.class);
    List<PlacementDTO> placementsList = new ArrayList<>();
    placementsList.add(placementDTO);
    Page<PlacementDTO> output = new PageImpl<>(placementsList);
    PlacementDTO first = placementsList.get(0);
    when(placementsService.getPlacements(
            nullable(Pageable.class),
            nullable(Optional.class),
            nullable(Long.class),
            nullable(Optional.class),
            nullable(Optional.class),
            nullable(Optional.class)))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(get("/v1/sellers/1/sites/1/placements").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
            .andExpect(jsonPath("content.[0].name", is(first.getName())))
            .andReturn();

    String actual =
        JsonPath.parse(mvcRsult.getResponse().getContentAsString())
            .read("content[0].placementVideo")
            .toString();
    assertEquals(JsonPath.parse(expectedResponse).read("placementVideo").toString(), actual);
  }

  @Test
  void createPlacementDTOStatusBadRequest() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setWidth(300);
    placementDTO.setHeight(50);
    placementDTO.setName("foo");
    placementDTO.setMemo("foo");
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setInterstitial(false);
    placementDTO.setMraidAdvancedTracking(FALSE);
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    placementDTO.setAdSizeType(AdSizeType.STANDARD);
    placementDTO.setSite(siteDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void createPlacementDTOStatusBadWhenInvalidPlacementDTO() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    PlacementDTO placementDTO = new PlacementDTO();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    placementDTO.setSite(siteDTO);

    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updatePlacementDTOStatusBadClientRequest() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPid(placementId);
    placementDTO.setWidth(300);
    placementDTO.setHeight(50);
    placementDTO.setName("foo");
    placementDTO.setMemo("foo");
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setInterstitial(false);
    placementDTO.setMraidAdvancedTracking(FALSE);
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    placementDTO.setAdSizeType(AdSizeType.STANDARD);
    placementDTO.setSite(siteDTO);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void updatePlacementDTOStatusBadWhenInvalidPlacementDTO() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = new PlacementDTO();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    placementDTO.setSite(siteDTO);

    when(userContext.isNexageUser()).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createPlacementDTOStatus200Ok() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setSite(siteDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void createPlacementDTOWithVideoStatus200Ok() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setSite(siteDTO);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void createPlacementDTOWithVideoStatusBadRequest() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setSite(siteDTO);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void createPlacementDTOWithVideoAndCompanionStatus200Ok() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setSite(siteDTO);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void createPlacementDTOWithVideoAndCompanionStatusBadRequest() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setSite(siteDTO);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTO.setHeight(null);
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void createPlacementDTOWithLongform200Ok() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setSite(siteDTO);

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void createPlacementDTOWithLongformInvalidFieldsBadRequest() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setSite(siteDTO);

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();

    placementVideoDTO.setCompanions(
        List.of(TestObjectsFactory.createDefaultPlacementVideoCompanionDTO()));
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.placementVideo",
                is(ValidationMessages.PLACEMENT_VIDEO_LONGFORM_CONSTRAINT_VIOLATION)));
  }

  @Test
  void createPlacementDTOWithLongformNonInstreamBadRequest() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = createInstreamPlacementDTO();

    placementDTO.setPlacementCategory(PlacementCategory.IN_FEED);
    placementDTO.setSite(siteDTO);

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);
    lenient()
        .when(placementDTOVideoSettingsConstraint.invalidPlacementCategoryForLongform())
        .thenReturn("Placement category must be INSTREAM_VIDEO for enabling longform");

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.placementCategory",
                is(placementDTOVideoSettingsConstraint.invalidPlacementCategoryForLongform())));
  }

  @Test
  void updatePlacementDTOWithVideoStatus200OK() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void updatePlacementDTOWithVideoWithCompanionWithStatus200OK() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void updatePlacementDTOWithVideoWithCompanionWithStatusBadClientRequest() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTO.setHeight(null);
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.['placementVideo.companions[0].height']",
                is(ValidationMessages.WRONG_IS_EMPTY)));
  }

  @Test
  void updatePlacementDTOWithLongformVideoStatus200OK() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void updatePlacementDTOWithLongformVideoStatusBadClientRequest() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementVideoDTO.setCompanions(
        List.of(TestObjectsFactory.createDefaultPlacementVideoCompanionDTO()));
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.['placementVideo']",
                is(ValidationMessages.PLACEMENT_VIDEO_LONGFORM_CONSTRAINT_VIOLATION)));
  }

  @Test
  void shouldCreatePlacementDTOInArticleWithStatus200Ok() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setSite(siteDTO);
    placementDTO.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    placementDTO.setScreenLocation(ScreenLocation.UNKNOWN);
    placementDTO.setAdSizeType(null);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPlayerRequired(true);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void shouldCreatePlacementDTOInFeedWithStatus200Ok() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setSite(siteDTO);
    placementDTO.setPlacementCategory(PlacementCategory.IN_FEED);
    placementDTO.setScreenLocation(ScreenLocation.UNKNOWN);
    placementDTO.setAdSizeType(null);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPlayerRequired(false);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void shouldFailCreatePlacementDTOInArticleWithScreenLocationAsFullscreenWithStatusAsBadRequest()
      throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setSite(siteDTO);
    placementDTO.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    placementDTO.setScreenLocation(ScreenLocation.FULLSCREEN_VISIBLE);
    placementDTO.setAdSizeType(null);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.screenLocation",
                is(ValidationMessages.PLACEMENT_SCREEN_LOCATION_CONSTRAINT)));
  }

  @Test
  void shouldFailCreatePlacementDTOInFeedWithScreenLocationAsFullscreenWithStatusAsBadRequest()
      throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setSite(siteDTO);
    placementDTO.setPlacementCategory(PlacementCategory.IN_FEED);
    placementDTO.setScreenLocation(ScreenLocation.FULLSCREEN_VISIBLE);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.screenLocation",
                is(ValidationMessages.PLACEMENT_SCREEN_LOCATION_CONSTRAINT)));
  }

  @Test
  void
      shouldFailCreatePlacementDTOWithImpressionTypeHandlingAsBasedOnInboundRequestWithStatusAsBadRequest()
          throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();

    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setSite(siteDTO);
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.UNKNOWN);
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    placementDTO.setImpressionTypeHandling(ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);
    placementDTO.setWidth(300);
    placementDTO.setHeight(50);
    placementDTO.setAdSizeType(AdSizeType.STANDARD);

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.impressionTypeHandling",
                is("Placement Impression Type Handling is invalid")));
  }

  @Test
  void shouldUpdatePlacementDTOWithInArticleStatus200OK() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    placementDTO.setScreenLocation(ScreenLocation.UNKNOWN);
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void shouldNotUpdatePlacementDTOWithInArticleWithScreenLocationAsFullScreenStatusAsBadRequest()
      throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    placementDTO.setScreenLocation(ScreenLocation.FULLSCREEN_VISIBLE);
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.screenLocation",
                is(ValidationMessages.PLACEMENT_SCREEN_LOCATION_CONSTRAINT)));
  }

  @Test
  void
      shouldNotUpdatePlacementDTOWithImpressionTypeHandlingAsBasedOnInboundRequestWithStatusAsBadRequest()
          throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.FULLSCREEN_VISIBLE);
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    placementDTO.setImpressionTypeHandling(ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);
    placementDTO.setWidth(300);
    placementDTO.setHeight(50);
    placementDTO.setAdSizeType(AdSizeType.STANDARD);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.impressionTypeHandling",
                is("Placement Impression Type Handling is invalid")));
  }

  @Test
  void shouldUpdatePlacementDTOWithInFeedStatus200OK() throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setPlacementCategory(PlacementCategory.IN_FEED);
    placementDTO.setScreenLocation(ScreenLocation.UNKNOWN);
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void shouldNotUpdatePlacementDTOWithInFeedWithScreenLocationAsFullScreenStatusAsBadRequest()
      throws Exception {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    long placementId = RandomUtils.nextLong();
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = createInstreamPlacementDTO();
    placementDTO.setPlacementCategory(PlacementCategory.IN_FEED);
    placementDTO.setScreenLocation(ScreenLocation.FULLSCREEN_VISIBLE);
    placementDTO.setPid(placementId);
    placementDTO.setSite(siteDTO);
    placementDTO.setVersion(1);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.screenLocation",
                is(ValidationMessages.PLACEMENT_SCREEN_LOCATION_CONSTRAINT)));
  }

  @Test
  void validatePlacementWithDapInputParamAndInvalidPlacementCategory() throws Exception {
    // VideoSupport.VIDEO_AND_BANNER, PlacementCategory.INSTREAM_VIDEO and Site Type.DESKTOP
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    placementDTO.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();
    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.placementCategory",
                is(ValidationMessages.PLACEMENT_DAP_CONSTRAINT_VIOLATION)));
  }

  @Test
  void validateDapPlacementWithoutPlayerRequiredWithDapPlayerType() throws Exception {
    // VideoSupport.VIDEO_AND_BANNER, PlacementCategory.BANNER and Site Type.DESKTOP
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    placementDTO.getPlacementVideo().setPlayerRequired(false);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();
    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.['placementVideo.playerRequired']",
                is(ValidationMessages.PLAYER_REQUIRED_DAP_CONSTRAINT)));
  }

  @Test
  void validateDapPlacementWithPlayerRequiredWithoutDapPlayerType() throws Exception {
    // VideoSupport.VIDEO_AND_BANNER, PlacementCategory.BANNER and Site Type.DESKTOP
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();
    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.fieldErrors.['placementVideo.dapPlayerType']",
                is(ValidationMessages.DAP_PLAYER_TYPE_CONSTRAINT)));
  }

  @Test
  void validateDapPlacementWithoutVideoPlacementType() throws Exception {
    // VideoSupport.VIDEO_AND_BANNER, PlacementCategory.BANNER and Site Type.DESKTOP
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setVideoPlacementType(null);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();
    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.['placementVideo.videoPlacementType']",
                is(ValidationMessages.VIDEO_PLACEMENT_TYPE_CONSTRAINT_DAP)));
  }

  @Test
  void validateDapPlacementForYahooPlayerWithPlayerID() throws Exception {
    // VideoSupport.VIDEO_AND_BANNER, PlacementCategory.BANNER and Site Type.DESKTOP
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.YAHOO);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();
    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.['placementVideo.playerId']",
                is(ValidationMessages.PLAYER_ID_CONSTRAINT)));
  }

  @Test
  void validateDapPlacementForO2PlayerWithInvalidPlayListID() throws Exception {
    // VideoSupport.VIDEO_AND_BANNER, PlacementCategory.BANNER and Site Type.DESKTOP
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    placementDTO.getPlacementVideo().setPlayListId(INVALID_PLAYER_ID_PLAYLIST_ID);
    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();
    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.['placementVideo.playListId']",
                is(ValidationMessages.PLAYLIST_ID_CONSTRAINT)));
  }

  @Test
  void validateDapPlacementForYahooPlayerWithInvalidPlayListID() throws Exception {
    // VideoSupport.VIDEO_AND_BANNER, PlacementCategory.BANNER and Site Type.DESKTOP
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    placementDTO.getPlacementVideo().setPlayerRequired(true);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.O2);
    placementDTO.getPlacementVideo().setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    placementDTO.getPlacementVideo().setPlayListId(MAX_LENGTH_PLAYER_ID_PLAYLIST_ID);
    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();
    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.['placementVideo.playListId']",
                is(ValidationMessages.PLAYLIST_ID_CONSTRAINT)));
  }

  @Test
  void createDapPlacementDTOWithDefaultValues201OK() throws Exception {
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    // Set VideoSupport.VIDEO_AND_BANNER, PlacementCategory.MEDIUM_RECTANGLE and Site Type.DESKTOP
    placementDTO.setPlacementCategory(PlacementCategory.MEDIUM_RECTANGLE);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setAdSizeType(AdSizeType.CUSTOM);
    placementDTO.setAdSize(null);
    placementDTO.getPlacementVideo().setDapPlayerType(DapPlayerType.YAHOO);
    placementDTO.getPlacementVideo().setPlayerHeight(null);
    placementDTO.getPlacementVideo().setPlayerWidth(null);
    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();

    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void createDapPlacementDTOWithInputValues201OK() throws Exception {
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    // Set VideoSupport.VIDEO_AND_BANNER, PlacementCategory.MEDIUM_RECTANGLE and Site Type.DESKTOP
    placementDTO.setPlacementCategory(PlacementCategory.MEDIUM_RECTANGLE);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setAdSizeType(AdSizeType.CUSTOM);
    placementDTO.setAdSize(null);

    PlacementVideoDTO placementVideoDTO = placementDTO.getPlacementVideo();
    placementVideoDTO.setPlayerHeight(null);
    placementVideoDTO.setPlayerWidth(null);
    placementVideoDTO.setVideoPlacementType(VideoPlacementType.INTERSTITIAL);
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);
    placementVideoDTO.setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    placementVideoDTO.setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);

    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();
    when(placementsService.save(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/v1/sellers/{sellerId}/sites/{siteId}/placements", sellerId, siteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void updateDapPlacementDTOWithInvalidSiteType() throws Exception {
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    // Set VideoSupport.VIDEO_AND_BANNER, PlacementCategory.MEDIUM_RECTANGLE and Site Type.DESKTOP
    placementDTO.setPlacementCategory(PlacementCategory.MEDIUM_RECTANGLE);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setAdSizeType(AdSizeType.CUSTOM);
    placementDTO.setAdSize(null);

    placementDTO.getSite().setType(Type.APPLICATION);

    PlacementVideoDTO placementVideoDTO = placementDTO.getPlacementVideo();
    placementVideoDTO.setPlayerHeight(null);
    placementVideoDTO.setPlayerWidth(null);
    placementVideoDTO.setVideoPlacementType(VideoPlacementType.INTERSTITIAL);

    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();

    // Set placement pid & version
    long placementPid = RandomUtils.nextLong();
    placementDTO.setPid(placementPid);
    placementDTO.setVersion(1);
    placementVideoDTO.setPid(placementPid);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is4xxClientError())
        .andExpect(
            jsonPath(
                "$.fieldErrors.placementCategory",
                is(ValidationMessages.PLACEMENT_DAP_CONSTRAINT_VIOLATION)));
  }

  @Test
  void updateDapPlacementDTOWithInputValues201OK() throws Exception {
    PlacementDTO placementDTO = CreateDapPlacementDTO();
    // Set VideoSupport.VIDEO_AND_BANNER, PlacementCategory.MEDIUM_RECTANGLE and Site Type.DESKTOP
    placementDTO.setPlacementCategory(PlacementCategory.MEDIUM_RECTANGLE);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setAdSizeType(AdSizeType.CUSTOM);
    placementDTO.setAdSize(null);

    PlacementVideoDTO placementVideoDTO = placementDTO.getPlacementVideo();
    placementVideoDTO.setPlayerHeight(null);
    placementVideoDTO.setPlayerWidth(null);
    placementVideoDTO.setVideoPlacementType(VideoPlacementType.IN_ARTICLE);
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);

    long sellerId = placementDTO.getSite().getCompanyPid();
    long siteId = placementDTO.getSite().getPid();

    // Set placement pid & version
    long placementPid = RandomUtils.nextLong();
    placementDTO.setPid(placementPid);
    placementDTO.setVersion(1);
    placementVideoDTO.setPid(placementPid);
    placementVideoDTO.setPlayerId(VALID_PLAYER_ID_PLAYLIST_ID);
    placementVideoDTO.setPlayListId(VALID_PLAYER_ID_PLAYLIST_ID);

    when(placementsService.update(sellerId, placementDTO)).thenReturn(placementDTO);
    when(userContext.isNexageUser()).thenReturn(true);
    when(checkPermissionValidator.isValid(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(
                    "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}",
                    sellerId,
                    siteId,
                    placementPid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placementDTO)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void getPlacementWithDapParams() throws Exception {
    String expectedResponse = getData(ResourcePath.GET_DAP_PLACEMENT_JSON.filePath);
    PlacementDTO placementDTO = mapper.readValue(expectedResponse, PlacementDTO.class);
    List<PlacementDTO> placementsList = new ArrayList<>();
    placementsList.add(placementDTO);
    Page<PlacementDTO> output = new PageImpl<>(placementsList);
    PlacementDTO first = placementsList.get(0);
    when(placementsService.getPlacements(
            nullable(Pageable.class),
            nullable(Optional.class),
            nullable(Long.class),
            nullable(Optional.class),
            nullable(Optional.class),
            nullable(Optional.class)))
        .thenReturn(output);

    MvcResult mvcRsult =
        mockMvc
            .perform(get("/v1/sellers/1/sites/1/placements").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
            .andExpect(jsonPath("content.[0].name", is(first.getName())))
            .andReturn();

    String actual =
        JsonPath.parse(mvcRsult.getResponse().getContentAsString())
            .read("content[0].placementVideo")
            .toString();
    assertEquals(JsonPath.parse(expectedResponse).read("placementVideo").toString(), actual);
  }

  @Override
  public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
    ConstraintValidator instance = super.getInstance(key);
    if (instance instanceof CheckPermissionValidator) {
      instance = checkPermissionValidator;
    }
    return (T) instance;
  }

  @Override
  protected WebApplicationContext getWebApplicationContext() {
    return webApplicationContext;
  }

  private LocalValidatorFactoryBean createLocalValidatorFactoryBean(
      GenericWebApplicationContext context) {
    LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
    validatorFactoryBean.setApplicationContext(context);
    validatorFactoryBean.setConstraintValidatorFactory(this);
    validatorFactoryBean.setProviderClass(HibernateValidator.class);
    validatorFactoryBean.afterPropertiesSet();
    return validatorFactoryBean;
  }

  private PlacementDTO CreatePlacementDTO() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setWidth(300);
    placementDTO.setHeight(50);
    placementDTO.setName("foo");
    placementDTO.setMemo("foo");
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setInterstitial(false);
    placementDTO.setMraidAdvancedTracking(FALSE);
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    placementDTO.setAdSizeType(AdSizeType.STANDARD);

    return placementDTO;
  }

  private PlacementDTO createInstreamPlacementDTO() {
    PlacementDTO placementDTO = CreatePlacementDTO();
    placementDTO.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    placementDTO.setScreenLocation(ScreenLocation.FULLSCREEN_VISIBLE);
    placementDTO.setVideoSupport(VideoSupport.VIDEO);
    placementDTO.setWidth(1920);
    placementDTO.setHeight(1080);
    placementDTO.setMraidSupport(MRAIDSupport.NO);

    return placementDTO;
  }

  private PlacementDTO CreateDapPlacementDTO() {
    long sellerId = RandomUtils.nextLong();
    long siteId = RandomUtils.nextLong();
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setName("foo");
    placementDTO.setMemo("foo");
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setInterstitial(false);
    placementDTO.setMraidAdvancedTracking(FALSE);
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    placementDTO.setAdSizeType(AdSizeType.STANDARD);
    placementDTO.setAdSize("300x200");

    // site config
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(siteId);
    siteDTO.setType(Type.DESKTOP);
    siteDTO.setPlatform(Platform.OTHER);

    // company config
    siteDTO.setCompanyPid(sellerId);

    placementDTO.setSite(siteDTO);

    // placementvideo config
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setVideoPlacementType(VideoPlacementType.IN_ARTICLE);
    placementDTO.setPlacementVideo(placementVideoDTO);

    placementDTO.getPlacementVideo().setPlayerWidth(null);
    placementDTO.getPlacementVideo().setPlayerHeight(null);
    return placementDTO;
  }

  @Test
  void getPlacementsMinimal() throws Throwable {
    List<PlacementDTO> placementsList = TestObjectsFactory.createPlacements(10);
    Page placementPage = new PageImpl(placementsList);
    PlacementDTO first = placementsList.get(0);
    when(placementsService.getPlacementsMinimalData(
            nullable(Pageable.class),
            nullable(Long.class),
            nullable(Long.class),
            nullable(String.class)))
        .thenReturn(placementPage);
    mockMvc
        .perform(get("/v1/sellers/1/sites/1/placements?minimal=true"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(first.getPid())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())));
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(PlacementDTOControllerIT.class, name), Charset.forName("UTF-8"));
  }

  private enum ResourcePath {
    GET_PLACEMENT_JSON("/data/placement_dto_Controller_it/GetPlacement_ER.json"),

    GET_PLACEMENT_WITH_COMPANION_JSON(
        "/data/placement_dto_Controller_it/GetplacementWithCompanion_ER.json"),

    GET_LONGFORM_PLACEMENT_JSON("/data/placement_dto_Controller_it/GetLongformPlacement_ER.json"),

    GET_DAP_PLACEMENT_JSON("/data/placement_dto_Controller_it/GetDapPlacement_ER.json");

    // src/test/resources/data/placement_dto_Controller_it
    private String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
