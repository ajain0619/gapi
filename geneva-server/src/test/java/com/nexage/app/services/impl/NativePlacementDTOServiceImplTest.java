package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.app.dto.NativePlacementRequestParamsDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.NativePlacementDTOMapper;
import com.nexage.app.queue.model.event.SyncEvent;
import com.nexage.app.queue.producer.PlacementSyncProducer;
import com.nexage.app.services.NativePlacementHbPartnerService;
import com.nexage.app.services.NativePlacementTierService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.validation.NativePlacementsParameterValidator;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.PositionValidator;
import com.nexage.app.util.ResourceLoader;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NativePlacementDTOServiceImplTest {

  private static final long SELLER_ID = 248L;
  private static final long SITE_ID = 456L;
  private static final long POSITION_ID = 100368L;
  private static final Long GEMINI_ID = 42927L;

  private static final String SITE_POJO = "/data/nativeplacement/create/site_database.json";
  private static final String REQ_NATIVE =
      "/data/nativeplacement/create/native_placement_created_dto.json";
  private static final String POSITION_POJO =
      "/data/nativeplacement/serialization/position_pojo.json";
  private static final String POSITION_POJO_NO_BUYER =
      "/data/nativeplacement/create/position_pojo_without_buyer.json";
  private static final String REQ_NATIVE_NO_BUYER =
      "/data/nativeplacement/create/native_placement_created_dto_without_buyer.json";

  private ObjectMapper objectMapper;

  @Mock private NativePlacementDTOMapper nativePlacementDTOMapper;
  @Mock private NativePlacementHbPartnerService nativePlacementHbPartnerService;
  @Mock private NativePlacementsParameterValidator nativePlacementsParameterValidator;
  @Mock private NativePlacementTierService nativePlacementTierService;
  @Mock private PlacementSyncProducer publisher;
  @Mock private PositionRepository positionRepository;
  @Mock private PositionValidator positionValidator;
  @Mock private SellerSiteService sellerSiteService;
  @InjectMocks private NativePlacementDTOServiceImpl nativePlacementDTOServiceImpl;

  @BeforeEach
  void setUp() {
    objectMapper = new CustomObjectMapper();
    ReflectionTestUtils.setField(nativePlacementDTOServiceImpl, "geminiCompanyId", GEMINI_ID);
  }

  @Test
  @SneakyThrows
  void createPlacement() {
    NativePlacementDTO nativePlacementDTO =
        objectMapper.readValue(
            ResourceLoader.getResourceAsStream(REQ_NATIVE), NativePlacementDTO.class);
    Position position =
        objectMapper.readValue(ResourceLoader.getResourceAsStream(POSITION_POJO), Position.class);
    NativePlacementDTO placementHelper = createPlacementHelper(nativePlacementDTO, position);
    assertEquals("123456789", placementHelper.getPlacementBuyer().getSectionId());
    assertEquals(GEMINI_ID, position.getPositionBuyer().getCompanyPid());
  }

  @Test
  @SneakyThrows
  void createPlacement_nameCanBeGenerated() {
    NativePlacementDTO nativePlacementDTO =
        objectMapper.readValue(
            ResourceLoader.getResourceAsStream(REQ_NATIVE_NO_BUYER), NativePlacementDTO.class);
    Position position =
        objectMapper.readValue(
            ResourceLoader.getResourceAsStream(POSITION_POJO_NO_BUYER), Position.class);
    position.setName(null);
    NativePlacementDTO placementHelper = createPlacementHelper(nativePlacementDTO, position);
    assertNotNull(placementHelper.getName());
  }

  @Test
  @SneakyThrows
  void createPlacement_withoutBuyer() {
    NativePlacementDTO nativePlacementDTO =
        objectMapper.readValue(
            ResourceLoader.getResourceAsStream(REQ_NATIVE_NO_BUYER), NativePlacementDTO.class);
    Position position =
        objectMapper.readValue(
            ResourceLoader.getResourceAsStream(POSITION_POJO_NO_BUYER), Position.class);
    NativePlacementDTO placementHelper = createPlacementHelper(nativePlacementDTO, position);
    assertNull(placementHelper.getPlacementBuyer());
  }

  @Test
  @SneakyThrows
  void createPlacement_siteNotFound() {
    NativePlacementDTO nativePlacementDTO =
        objectMapper.readValue(
            ResourceLoader.getResourceAsStream(REQ_NATIVE), NativePlacementDTO.class);
    Site sitePojo = getSite();
    Position position =
        objectMapper.readValue(ResourceLoader.getResourceAsStream(POSITION_POJO), Position.class);

    when(sellerSiteService.getSite(SITE_ID))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND));
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(SELLER_ID)
            .siteId(SITE_ID)
            .nativePlacement(nativePlacementDTO)
            .build();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> nativePlacementDTOServiceImpl.createPlacement(nativePlacementRequestParamsDTO));
    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @SneakyThrows
  void createPlacement_duplication() {
    NativePlacementDTO nativePlacementDTO =
        objectMapper.readValue(
            ResourceLoader.getResourceAsStream(REQ_NATIVE), NativePlacementDTO.class);

    when(sellerSiteService.getSite(SITE_ID)).thenThrow(new EntityNotFoundException(""));
    NativePlacementRequestParamsDTO nativePlacementRequestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(SELLER_ID)
            .siteId(SITE_ID)
            .nativePlacement(nativePlacementDTO)
            .build();
    assertThrows(
        EntityNotFoundException.class,
        () -> nativePlacementDTOServiceImpl.createPlacement(nativePlacementRequestParamsDTO));

    verifyNoInteractions(positionRepository);
  }

  @Test
  @SneakyThrows
  void updateNativePlacement() {
    int version = 11;
    Set<PublisherTierDTO> tiers = Set.of(new PublisherTierDTO());
    NativePlacementDTO inputDto = new NativePlacementDTO();
    inputDto.setTiers(tiers);
    inputDto.setVersion(version);
    Position positionToUpdate = new Position();
    positionToUpdate.setPid(POSITION_ID);
    Site site = getSite();
    site.setPositions(Sets.newHashSet(positionToUpdate));
    Position updatedPosition = new Position();
    NativePlacementDTO outputDto = new NativePlacementDTO();

    when(sellerSiteService.getSite(SITE_ID)).thenReturn(site);
    when(nativePlacementDTOMapper.map(
            inputDto, positionToUpdate, nativePlacementHbPartnerService, getSite()))
        .thenReturn(updatedPosition);
    when(positionRepository.saveAndFlush(updatedPosition)).thenReturn(updatedPosition);
    when(nativePlacementDTOMapper.map(updatedPosition, nativePlacementHbPartnerService))
        .thenReturn(outputDto);

    NativePlacementRequestParamsDTO requestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .siteId(SITE_ID)
            .placementId(POSITION_ID)
            .nativePlacement(inputDto)
            .build();
    NativePlacementDTO createdPlacement =
        nativePlacementDTOServiceImpl.updatePlacement(requestParamsDTO);

    assertSame(outputDto, createdPlacement);
    verify(positionValidator).validateVersion(positionToUpdate, version);
    verify(nativePlacementTierService).update(inputDto, site, positionToUpdate);
    verify(positionRepository).saveAndFlush(updatedPosition);
  }

  @Test
  @SneakyThrows
  void updateNativePlacementNotFound() {
    NativePlacementDTO inputPlacementDto = new NativePlacementDTO();
    Site site = getSite();
    Position otherPosition = new Position();
    otherPosition.setPid(POSITION_ID + 1);
    site.setPositions(Sets.newHashSet(otherPosition));
    when(sellerSiteService.getSite(SITE_ID)).thenReturn(site);

    NativePlacementRequestParamsDTO requestParamsDTO =
        NativePlacementRequestParamsDTO.builder()
            .siteId(SITE_ID)
            .placementId(POSITION_ID)
            .nativePlacement(inputPlacementDto)
            .build();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> nativePlacementDTOServiceImpl.updatePlacement(requestParamsDTO));

    assertEquals(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS, exception.getErrorCode());
  }

  @Test
  @SneakyThrows
  void getNativePlacementById() {
    NativePlacementDTO nativePlacementDTO =
        objectMapper.readValue(
            ResourceLoader.getResourceAsStream(REQ_NATIVE), NativePlacementDTO.class);
    Site sitePojo = getSite();
    Position position =
        sitePojo.getPositions().stream()
            .filter(p -> p.getPid().equals(POSITION_ID))
            .findFirst()
            .get();

    when(sellerSiteService.getSite(SITE_ID)).thenReturn(sitePojo);
    when(nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService))
        .thenReturn(nativePlacementDTO);

    NativePlacementDTO nativePlacementById =
        nativePlacementDTOServiceImpl.getNativePlacementById(SELLER_ID, SITE_ID, POSITION_ID);

    assertEquals(nativePlacementDTO, nativePlacementById);
  }

  @Test
  @SneakyThrows
  void getNativePlacementById_placementNotFound() {
    Site sitePojo = getSite();
    sitePojo.setPositions(new HashSet<>());

    when(sellerSiteService.getSite(SITE_ID)).thenReturn(sitePojo);
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                nativePlacementDTOServiceImpl.getNativePlacementById(
                    SELLER_ID, SITE_ID, POSITION_ID));
    assertEquals(ServerErrorCodes.SERVER_PLACEMENT_NOT_FOUND_IN_SITE, ex.getErrorCode());
  }

  @Test
  @SneakyThrows
  void getNativePlacementById_siteNotFound() {
    when(sellerSiteService.getSite(SITE_ID))
        .thenThrow(new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                nativePlacementDTOServiceImpl.getNativePlacementById(
                    SELLER_ID, SITE_ID, POSITION_ID));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @SneakyThrows
  private NativePlacementDTO createPlacementHelper(
      NativePlacementDTO nativePlacementDTO, Position position) {
    Site sitePojo = getSite();

    when(nativePlacementDTOMapper.map(
            nativePlacementDTO, nativePlacementHbPartnerService, sitePojo))
        .thenReturn(position);
    when(nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService))
        .thenReturn(nativePlacementDTO);
    when(sellerSiteService.getSite(SITE_ID)).thenReturn(sitePojo);
    when(positionRepository.saveAndFlush(position)).thenReturn(position);

    NativePlacementDTO createdPlacement =
        nativePlacementDTOServiceImpl.createPlacement(
            NativePlacementRequestParamsDTO.builder()
                .sellerId(SELLER_ID)
                .siteId(SITE_ID)
                .nativePlacement(nativePlacementDTO)
                .build());

    assertEquals(nativePlacementDTO, createdPlacement);
    verify(positionRepository, times(1)).saveAndFlush(position);

    ArgumentCaptor<SyncEvent> eventCaptor = ArgumentCaptor.forClass(SyncEvent.class);
    verify(publisher, times(1)).publishEvent(eventCaptor.capture());
    Position p = (Position) eventCaptor.getValue().getData();
    assertEquals(p, position);

    return createdPlacement;
  }

  private Site getSite() throws java.io.IOException {
    return objectMapper.readValue(ResourceLoader.getResourceAsStream(SITE_POJO), Site.class);
  }
}
