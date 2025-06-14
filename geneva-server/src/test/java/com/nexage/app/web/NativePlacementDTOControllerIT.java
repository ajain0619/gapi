package com.nexage.app.web;

import static com.nexage.app.util.PlacementAssociationTypeTestUtil.GOOGLE_EB;
import static com.nexage.app.web.placement.NativePlacementDTOController.NATIVE_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.validator.NativePlacementCreateGroup;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.NativePlacementRequestParamsDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeDataAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeTitleAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeVideoAssetDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.NativePlacementDTOService;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.ResourceLoader;
import com.nexage.app.web.placement.NativePlacementDTOController;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.validation.groups.Default;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
@Log4j2
class NativePlacementDTOControllerIT {

  private static final long SITE_ID = 220364L;
  private static final long SELLER_ID = 11L;
  private static final String REQUEST_DATA =
      "/data/nativeplacement/create/native_placement_create_dto.json";
  private static final String RESPONSE_DATA =
      "/data/nativeplacement/create/native_placement_created_dto.json";

  private static final String WEB_NATIVE_REQUEST_DATA =
      "/data/nativeplacement/create/web_native_placement_create_dto.json";
  private static final String WEB_NATIVE_RESPONSE_DATA =
      "/data/nativeplacement/create/web_native_placement_created_dto.json";

  @InjectMocks private NativePlacementDTOController placementsController;
  @Mock private NativePlacementDTOService nativePlacementDTOService;
  @Mock private BeanValidationService beanValidationService;

  private ObjectMapper objectMapper;
  private MockMvc mockMvc;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @BeforeEach
  public void setUp() {
    objectMapper = new CustomObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    mockMvc =
        MockMvcBuilders.standaloneSetup(placementsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  @SneakyThrows
  void getNativePlacementById() {
    NativePlacementDTO res;
    Long pid;
    try (InputStream resJsonInputStream = ResourceLoader.getResourceAsStream(RESPONSE_DATA)) {
      res = objectMapper.readValue(resJsonInputStream, NativePlacementDTO.class);
      pid = res.getPid();
      when(nativePlacementDTOService.getNativePlacementById(SELLER_ID, SITE_ID, pid))
          .thenReturn(res);
    }
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    "/v1/sellers/" + SELLER_ID + "/sites/" + SITE_ID + "/placements/" + pid)
                .accept(NATIVE_HEADER))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(res)));
  }

  @Test
  @SneakyThrows
  void updateNativePlacement() {
    NativePlacementDTO res, req;
    Long pid;
    try (InputStream resInputStream = ResourceLoader.getResourceAsStream(RESPONSE_DATA);
        InputStream reqJsonInputStream = ResourceLoader.getResourceAsStream(REQUEST_DATA)) {
      req = objectMapper.readValue(reqJsonInputStream, NativePlacementDTO.class);
      res = objectMapper.readValue(resInputStream, NativePlacementDTO.class);
      pid = res.getPid();
      when(nativePlacementDTOService.updatePlacement(
              NativePlacementRequestParamsDTO.builder()
                  .sellerId(SELLER_ID)
                  .siteId(SITE_ID)
                  .placementId(pid)
                  .nativePlacement(req)
                  .build()))
          .thenReturn(res);
    }
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(
                    "/v1/sellers/" + SELLER_ID + "/sites/" + SITE_ID + "/placements/" + pid)
                .contentType(NATIVE_HEADER)
                .content(objectMapper.writeValueAsString(req))
                .accept(NATIVE_HEADER))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(res)));
  }

  @Test
  @SneakyThrows
  void createNativePlacement() {
    NativePlacementDTO req, res;
    try (InputStream reqJsonInputStream = ResourceLoader.getResourceAsStream(REQUEST_DATA);
        InputStream resJsonInputStream = this.getClass().getResourceAsStream(RESPONSE_DATA)) {

      req = objectMapper.readValue(reqJsonInputStream, NativePlacementDTO.class);
      res = objectMapper.readValue(resJsonInputStream, NativePlacementDTO.class);

      when(nativePlacementDTOService.createPlacement(
              NativePlacementRequestParamsDTO.builder()
                  .sellerId(SELLER_ID)
                  .siteId(SITE_ID)
                  .nativePlacement(req)
                  .build()))
          .thenReturn(res);
    }

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                    "/v1/sellers/" + SELLER_ID + "/sites/" + SITE_ID + "/placements/")
                .content(objectMapper.writeValueAsString(req))
                .contentType(NATIVE_HEADER)
                .accept(NATIVE_HEADER))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(res)));
  }

  @Test
  @SneakyThrows
  void createWebNativePlacement() {
    NativePlacementDTO req, res;
    try (InputStream reqJsonInputStream =
            ResourceLoader.getResourceAsStream(WEB_NATIVE_REQUEST_DATA);
        InputStream resJsonInputStream =
            this.getClass().getResourceAsStream(WEB_NATIVE_RESPONSE_DATA)) {

      req = objectMapper.readValue(reqJsonInputStream, NativePlacementDTO.class);
      res = objectMapper.readValue(resJsonInputStream, NativePlacementDTO.class);

      when(nativePlacementDTOService.createPlacement(
              NativePlacementRequestParamsDTO.builder()
                  .sellerId(SELLER_ID)
                  .siteId(SITE_ID)
                  .nativePlacement(req)
                  .build()))
          .thenReturn(res);
    }

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                    "/v1/sellers/" + SELLER_ID + "/sites/" + SITE_ID + "/placements/")
                .content(objectMapper.writeValueAsString(req))
                .contentType(NATIVE_HEADER)
                .accept(NATIVE_HEADER))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(res)));
  }

  @Test
  @SneakyThrows
  void createNativePlacement_assetSetIsEmpty() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension().setAssetSets(new HashSet<>());
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_assetTemplateEmpty() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension().setAssetTemplate(null);
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_ruleEmpty() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension().getAssetSets().forEach(x -> x.setRule(null));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_invalidMemo() {
    // given
    NativePlacementDTO req = getLegalNativePlacementReqDTO();

    // when
    req.setMemo("<div>test</div>");

    // then
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_invalidName() {
    // given
    NativePlacementDTO req = getLegalNativePlacementReqDTO();

    // when
    req.setName("<div>Test_$</div>");

    // them
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_NoAssetInAssetSet() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension().getAssetSets().forEach(x -> x.setAssets(null));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_assetKeyIsNull() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension()
        .getAssetSets()
        .forEach(x -> x.getAssets().forEach(y -> y.setKey(null)));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_data_MaxLengthIsMinimal() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension()
        .getAssetSets()
        .forEach(
            x ->
                x.getAssets()
                    .forEach(
                        item -> {
                          if ((item instanceof NativeDataAssetDTO)) {
                            NativeDataAssetDTO d = (NativeDataAssetDTO) item;
                            d.getData().setMaxLength(-1);
                          }
                        }));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_title_MaxLengthIsNull() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension()
        .getAssetSets()
        .forEach(
            x ->
                x.getAssets()
                    .forEach(
                        item -> {
                          if ((item instanceof NativeTitleAssetDTO)) {
                            NativeTitleAssetDTO d = (NativeTitleAssetDTO) item;
                            d.getTitle().setMaxLength(null);
                          }
                        }));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_title_MaxLengthIsMinimal() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension()
        .getAssetSets()
        .forEach(
            x ->
                x.getAssets()
                    .forEach(
                        item -> {
                          if ((item instanceof NativeTitleAssetDTO)) {
                            NativeTitleAssetDTO d = (NativeTitleAssetDTO) item;
                            d.getTitle().setMaxLength(-1);
                          }
                        }));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_video_MaxDurationIsMinimal() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension()
        .getAssetSets()
        .forEach(
            x ->
                x.getAssets()
                    .forEach(
                        item -> {
                          if ((item instanceof NativeVideoAssetDTO)) {
                            NativeVideoAssetDTO d = (NativeVideoAssetDTO) item;
                            d.getVideo().setMaxDuration(-1);
                          }
                        }));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_video_MaxDurationIsNull() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension()
        .getAssetSets()
        .forEach(
            x ->
                x.getAssets()
                    .forEach(
                        item -> {
                          if ((item instanceof NativeVideoAssetDTO)) {
                            NativeVideoAssetDTO d = (NativeVideoAssetDTO) item;
                            d.getVideo().setMaxDuration(null);
                          }
                        }));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_video_MinDurationIsMinimal() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension()
        .getAssetSets()
        .forEach(
            x ->
                x.getAssets()
                    .forEach(
                        item -> {
                          if ((item instanceof NativeVideoAssetDTO)) {
                            NativeVideoAssetDTO d = (NativeVideoAssetDTO) item;
                            d.getVideo().setMinDuration(-1);
                          }
                        }));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_video_MinDurationIsNull() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension()
        .getAssetSets()
        .forEach(
            x ->
                x.getAssets()
                    .forEach(
                        item -> {
                          if ((item instanceof NativeVideoAssetDTO)) {
                            NativeVideoAssetDTO d = (NativeVideoAssetDTO) item;
                            d.getVideo().setMinDuration(null);
                          }
                        }));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void createNativePlacement_video_protocolsIsEmpty() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    req.getNativePlacementExtension()
        .getAssetSets()
        .forEach(
            x ->
                x.getAssets()
                    .forEach(
                        item -> {
                          if ((item instanceof NativeVideoAssetDTO)) {
                            NativeVideoAssetDTO d = (NativeVideoAssetDTO) item;
                            d.getVideo().setProtocols(null);
                          }
                        }));
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void updateNativePlacement_emptyAssetSet() {
    NativePlacementDTO req;
    Long pid;
    try (InputStream reqInputStream = ResourceLoader.getResourceAsStream(REQUEST_DATA)) {
      req = objectMapper.readValue(reqInputStream, NativePlacementDTO.class);

      pid = req.getPid();
      req.getNativePlacementExtension().setAssetSets(new HashSet<>());
    }
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(
                    "/v1/sellers/" + SELLER_ID + "/sites/" + SITE_ID + "/placements/" + pid)
                .contentType(NATIVE_HEADER)
                .content(objectMapper.writeValueAsString(req))
                .accept(NATIVE_HEADER))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldThrowErrorWhenWeCreateNativePlacementWithAssociationTypeInvalid() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    setInvalidHbAttributes(req);
    verifyError(req);
  }

  @Test
  @SneakyThrows
  void shouldThrowErrorWhenWeUpdateNativePlacementWithAssociationTypeInvalid() {
    NativePlacementDTO req = getLegalNativePlacementReqDTO();
    setInvalidHbAttributes(req);
    Long pid = req.getPid();
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(
                    "/v1/sellers/" + SELLER_ID + "/sites/" + SITE_ID + "/placements/" + pid)
                .contentType(NATIVE_HEADER)
                .content(objectMapper.writeValueAsString(req))
                .accept(NATIVE_HEADER))
        .andExpect(status().isBadRequest());
  }

  void setInvalidHbAttributes(NativePlacementDTO position) {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(GOOGLE_EB);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT_BANNER);
    HbPartnerAssignmentDTO invalidHbPartnerAttribute = new HbPartnerAssignmentDTO();
    invalidHbPartnerAttribute.setHbPartnerPid(88L);
    invalidHbPartnerAttribute.setType(AssociationType.DEFAULT_VIDEO);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(validHbPartnerAttribute, invalidHbPartnerAttribute);
    position.setHbPartnerAttributes(hbPartnerAttributes);
  }

  @SneakyThrows
  private NativePlacementDTO getLegalNativePlacementReqDTO() {
    NativePlacementDTO req;
    try (InputStream reqInputStream = ResourceLoader.getResourceAsStream(REQUEST_DATA)) {
      req = objectMapper.readValue(reqInputStream, NativePlacementDTO.class);
    }
    return req;
  }

  @SneakyThrows
  private void verifyError(NativePlacementDTO req) {
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), eq(Default.class), eq(NativePlacementCreateGroup.class));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                    "/v1/sellers/" + SELLER_ID + "/sites/" + SITE_ID + "/placements")
                .content(objectMapper.writeValueAsString(req))
                .contentType(NATIVE_HEADER)
                .accept(NATIVE_HEADER))
        .andExpect(status().isBadRequest());
  }
}
