package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createDefaultPlacementVideoDTO;
import static com.nexage.app.web.support.TestObjectsFactory.createPlacements;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoPlacementType;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.PlacementDooh;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.dto.seller.PlacementSummaryDTO;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.site.PlacementDTOMapper;
import com.nexage.app.mapper.site.PlacementSummaryDTOMapper;
import com.nexage.app.mapper.site.SiteDTOMapper;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.groups.Default;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class PlacementsServiceImplTest {

  @Mock private PositionRepository positionRepository;
  @Mock private PositionViewRepository positionViewRepository;
  @Mock private Pageable pageable;
  @Mock private Specification<Position> fakeSpec;
  @Mock private SiteRepository siteRepository;
  @Mock private PlacementVideoServiceImpl placementVideoService;
  @Mock private BeanValidationService beanValidationService;

  @InjectMocks private PlacementsServiceImpl placementsService;

  Page<Position> returnedPage;
  Page<PositionView> returnedPageView;
  Page<PositionMetricsAggregation> returnedAggregatedPage;
  Page<PlacementDTO> expectedPageView;
  Page<PlacementDTO> expectedPage;
  Page<PlacementSummaryDTO> expectedAggregatedPage;
  Page<Position> returnedPageWithoutVideoPlacements;
  Page<Position> returnedPageWithVideoPlacements;
  Page<Position> returnedPageWithVideoPlacementsAndCompanion;
  Page<Position> returnedPageWithVideoPlacementsAndMultipleCompanion;
  Page<PlacementDTO> expectedPageWithoutVideoPlacements;
  Page<PlacementDTO> expectedPageWithVideoPlacements;
  Page<PlacementDTO> expectedPageWithVideoPlacementsAndCompanion;
  Page<PlacementDTO> expectedPageWithVideoPlacementsAndMultipleCompanion;

  private static final VideoPlacementType IN_ARTICLE_VIDEO_PLACEMENT =
      VideoPlacementType.IN_ARTICLE;
  private static final String VALID_PLAYER_ID = "Valid-Player-Id";
  private static final String VALID_PLAYLIST_ID = "Valid-Playlist-Id";

  @BeforeEach
  void setUp() {
    returnedPage = new PageImpl<>(TestObjectsFactory.createPositions(10));
    returnedPageView = new PageImpl<>(List.of(TestObjectsFactory.createPositionView()));
    expectedPageView = returnedPageView.map(PlacementDTOMapper.MAPPER::map);
    returnedAggregatedPage =
        new PageImpl<>(TestObjectsFactory.createPositionMetricsAggregation(10));
    expectedPage = returnedPage.map(PlacementDTOMapper.MAPPER::map);
    expectedPageWithoutVideoPlacements =
        new PageImpl<>(createPlacementDTOSWithoutVideoPlacements());
    returnedPageWithoutVideoPlacements =
        expectedPageWithoutVideoPlacements.map(PlacementDTOMapper.MAPPER::map);
    expectedPageWithVideoPlacements = new PageImpl<>(createPlacementDTOSWithVideoPlacements());
    returnedPageWithVideoPlacements =
        expectedPageWithVideoPlacements.map(PlacementDTOMapper.MAPPER::map);
    expectedPageWithVideoPlacementsAndCompanion =
        new PageImpl<>(createPlacementDTOSWithVideoPlacementsAndCompanion());
    returnedPageWithVideoPlacementsAndCompanion =
        expectedPageWithVideoPlacementsAndCompanion.map(PlacementDTOMapper.MAPPER::map);
    expectedPageWithVideoPlacementsAndMultipleCompanion =
        new PageImpl<>(createPlacementDTOSWithVideoPlacementsAndMultipleCompanion());
    returnedPageWithVideoPlacementsAndMultipleCompanion =
        expectedPageWithVideoPlacementsAndMultipleCompanion.map(PlacementDTOMapper.MAPPER::map);
    expectedAggregatedPage = returnedAggregatedPage.map(PlacementSummaryDTOMapper.MAPPER::map);
  }

  @Test
  void shouldCreatePlacementWithoutName() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setName(null);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));

    PlacementDTO savedPlacement = placementsService.save(123L, placementDTO);
    assertNotNull(savedPlacement.getName());
    assertEquals(32, savedPlacement.getName().length());
  }

  @Test
  void shouldGetPlacementsQuery() {
    when(positionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(returnedPage);
    Page<PlacementDTO> actualPage =
        placementsService.getPlacements(
            pageable,
            Optional.of(123L),
            123L,
            Optional.of("{action=search,name=banner,memo=banner}"),
            Optional.empty(),
            Optional.empty());
    assertEquals(
        expectedPage.getContent().get(0).getPid(), actualPage.getContent().get(0).getPid());
  }

  @Test
  void shouldThrowExceptionWhenPositionsCouldNotBeFetched() {
    when(positionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(null);
    Optional<Long> siteIdOpt = Optional.of(123L);
    Optional<String> qt = Optional.of("{action=search,name=banner,memo=banner}");
    Optional<List<String>> placementTypes = Optional.empty();
    Optional<List<String>> statusOpt = Optional.empty();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                placementsService.getPlacements(
                    pageable, siteIdOpt, 123L, qt, placementTypes, statusOpt));

    assertEquals(ServerErrorCodes.SERVER_ERROR_FETCHING_POSITIONS, exception.getErrorCode());
  }

  @Test
  void shouldGetPlacementsMinimalData() {
    when(positionViewRepository.findAllPlacements(any(Long.class), any(Pageable.class)))
        .thenReturn(returnedPageView);
    Page<PlacementDTO> actualPage =
        placementsService.getPlacementsMinimalData(pageable, 123L, 123L, null);
    assertEquals(
        expectedPageView.getContent().get(0).getPid(), actualPage.getContent().get(0).getPid());
  }

  @Test
  void shouldGetPlacementsMinimalDataWithQT() {
    when(positionViewRepository.searchPlacementsByName(
            any(Long.class), any(String.class), any(Pageable.class)))
        .thenReturn(returnedPageView);
    Page<PlacementDTO> actualPage =
        placementsService.getPlacementsMinimalData(pageable, 123L, 123L, "name");
    assertEquals(
        expectedPageView.getContent().get(0).getPid(), actualPage.getContent().get(0).getPid());
  }

  @Test
  void shouldGetPlacementsInvalidQueryAction() {
    Optional<Long> siteIdOpt = Optional.of(123L);
    Optional<String> qt = Optional.of("{action=find,name=banner}");
    Optional<List<String>> placementTypes = Optional.empty();
    Optional<List<String>> statusOpt = Optional.empty();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                placementsService.getPlacements(
                    pageable, siteIdOpt, 123L, qt, placementTypes, statusOpt));

    assertEquals(
        ServerErrorCodes.SERVER_PLACEMENT_SEARCH_ERROR_WITH_QUERY_TERM_FORMAT,
        exception.getErrorCode());
  }

  @Test
  void shouldGetPlacementsInvalidQuery() {
    Optional<Long> siteIdOpt = Optional.of(123L);
    Optional<String> qt = Optional.of("{action=search,id=banner}");
    Optional<List<String>> placementTypes = Optional.empty();
    Optional<List<String>> statusOpt = Optional.empty();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                placementsService.getPlacements(
                    pageable, siteIdOpt, 123L, qt, placementTypes, statusOpt));

    assertEquals(
        ServerErrorCodes.SERVER_PLACEMENT_SEARCH_ERROR_WITH_QUERY_TERM_FORMAT,
        exception.getErrorCode());
  }

  @Test
  void shouldGetPlacements() {
    when(positionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(returnedPageWithoutVideoPlacements);

    Page<PlacementDTO> actualPage =
        placementsService.getPlacements(
            pageable,
            Optional.of(123L),
            123L,
            Optional.of("{action=search,name=banner,memo=banner}"),
            Optional.empty(),
            Optional.empty());
    assertAll(
        "getPlacementsTest",
        () ->
            assertEquals(
                expectedPageWithoutVideoPlacements.getContent().get(0),
                actualPage.getContent().get(0)),
        () ->
            assertEquals(
                expectedPageWithoutVideoPlacements.getContent().get(0).getPid(),
                actualPage.getContent().get(0).getPid()),
        () ->
            assertEquals(
                expectedPageWithoutVideoPlacements.getContent().get(0).getPlacementVideo(),
                actualPage.getContent().get(0).getPlacementVideo()));
  }

  @Test
  void shouldGetPlacementsWithVideoDTO() {
    when(positionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(returnedPageWithVideoPlacements);

    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    Page<PlacementDTO> actualPage =
        placementsService.getPlacements(
            pageable,
            Optional.of(123L),
            123L,
            Optional.of("{action=search,name=banner,memo=banner}"),
            Optional.empty(),
            Optional.empty());
    assertAll(
        "getPlacementsWithVideoDTOTest",
        () ->
            assertEquals(
                expectedPageWithVideoPlacements.getContent().get(0).getPid(),
                actualPage.getContent().get(0).getPid()),
        () ->
            assertEquals(
                expectedPageWithVideoPlacements.getContent().get(0).getPlacementVideo(),
                actualPage.getContent().get(0).getPlacementVideo()));
  }

  @Test
  void shouldGetPlacementsWithVideoDTOAndCompanion() {
    when(positionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(returnedPageWithVideoPlacementsAndCompanion);

    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    Page<PlacementDTO> actualPage =
        placementsService.getPlacements(
            pageable,
            Optional.of(123L),
            123L,
            Optional.of("{action=search,name=banner,memo=banner}"),
            Optional.empty(),
            Optional.empty());
    assertAll(
        "getPlacementsWithVideoDTOAndCompanionTest",
        () ->
            assertEquals(
                expectedPageWithVideoPlacementsAndCompanion.getContent().get(0).getPid(),
                actualPage.getContent().get(0).getPid()),
        () ->
            assertEquals(
                expectedPageWithVideoPlacementsAndCompanion.getContent().get(0).getPlacementVideo(),
                actualPage.getContent().get(0).getPlacementVideo()),
        () ->
            assertEquals(
                expectedPageWithVideoPlacementsAndCompanion
                    .getContent()
                    .get(0)
                    .getPlacementVideo()
                    .getCompanions(),
                actualPage.getContent().get(0).getPlacementVideo().getCompanions()));
  }

  @Test
  void shouldGetPlacementsWithVideoDTOAndMultipleCompanion() {
    when(positionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(returnedPageWithVideoPlacementsAndMultipleCompanion);

    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO2 =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTO2.setWidth(350);
    placementVideoCompanionDTO2.setHeight(250);
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    placementVideoDTO.addCompanion(placementVideoCompanionDTO2);
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    Page<PlacementDTO> actualPage =
        placementsService.getPlacements(
            pageable,
            Optional.of(123L),
            123L,
            Optional.of("{action=search,name=banner,memo=banner}"),
            Optional.empty(),
            Optional.empty());
    assertAll(
        "getPlacementsWithVideoDTOAndMultipleCompanionTest",
        () ->
            assertEquals(
                expectedPageWithVideoPlacementsAndMultipleCompanion.getContent().get(0).getPid(),
                actualPage.getContent().get(0).getPid()),
        () ->
            assertEquals(
                expectedPageWithVideoPlacementsAndMultipleCompanion
                    .getContent()
                    .get(0)
                    .getPlacementVideo(),
                actualPage.getContent().get(0).getPlacementVideo()),
        () ->
            assertEquals(
                expectedPageWithVideoPlacementsAndMultipleCompanion
                    .getContent()
                    .get(0)
                    .getPlacementVideo()
                    .getCompanions()
                    .size(),
                actualPage.getContent().get(0).getPlacementVideo().getCompanions().size()),
        () ->
            assertEquals(
                expectedPageWithVideoPlacementsAndMultipleCompanion
                    .getContent()
                    .get(0)
                    .getPlacementVideo()
                    .getCompanions(),
                actualPage.getContent().get(0).getPlacementVideo().getCompanions()));
  }

  @Test
  void shouldSavePlacementDTO() {
    Long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(RandomUtils.nextLong());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(
        createSite(sellerId, placementDTO.getSite().getPid(), placementDTO.getSite().getType()));

    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));

    assertNotNull(placementsService.save(sellerId, placementDTO));
    assertNull(placementDTO.getDooh());
  }

  @Test
  void shouldSaveDoohPlacementDTOWithPlacementDooh() {
    Long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createDoohPlacementDTO(RandomUtils.nextLong());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(
        createSite(sellerId, placementDTO.getSite().getPid(), placementDTO.getSite().getType()));

    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    PlacementDTO savedPlacementDTO = placementsService.save(sellerId, placementDTO);
    assertNotNull(savedPlacementDTO);
    assertNotNull(savedPlacementDTO.getDooh().getVersion());
    assertNotNull(savedPlacementDTO.getDooh().getDefaultImpressionMultiplier());
    assertEquals(savedPlacementDTO.getPid(), position.getPlacementDooh().getPid());
  }

  @Test
  void shouldSaveDoohPlacementDTOWithoutPlacementDooh() {
    Long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createDoohPlacementDTO(RandomUtils.nextLong());
    placementDTO.setDooh(null);
    PlacementDooh placementDooh = new PlacementDooh();
    placementDooh.setVersion(0);
    placementDooh.setDefaultImpressionMultiplier(BigDecimal.ONE);
    placementDooh.setPid(placementDTO.getPid());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(
        createSite(sellerId, placementDTO.getSite().getPid(), placementDTO.getSite().getType()));
    position.setPlacementDooh(placementDooh);

    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    PlacementDTO savedPlacementDTO = placementsService.save(sellerId, placementDTO);
    assertNotNull(savedPlacementDTO);
    assertNotNull(savedPlacementDTO.getDooh().getVersion());
    assertEquals(BigDecimal.ONE, savedPlacementDTO.getDooh().getDefaultImpressionMultiplier());
    assertEquals(savedPlacementDTO.getPid(), position.getPlacementDooh().getPid());
  }

  @Test
  void shouldSaveDoohPlacementDTOWithoutDefaultImpressionMultiplier() {
    Long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createDoohPlacementDTO(RandomUtils.nextLong());
    placementDTO.getDooh().setDefaultImpressionMultiplier(null);
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(
        createSite(sellerId, placementDTO.getSite().getPid(), placementDTO.getSite().getType()));

    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    PlacementDTO savedPlacementDTO = placementsService.save(sellerId, placementDTO);
    assertNotNull(savedPlacementDTO);
    assertNotNull(savedPlacementDTO.getDooh().getVersion());
    assertEquals(BigDecimal.ONE, savedPlacementDTO.getDooh().getDefaultImpressionMultiplier());
    assertEquals(savedPlacementDTO.getPid(), position.getPlacementDooh().getPid());
  }

  @Test
  void shouldThrowExceptionWhenSiteReturnedDoesNotHaveCorrectSellerIdWhenCreate() {
    long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(RandomUtils.nextLong());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.getSite().setCompanyPid(sellerId + 1);
    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> placementsService.save(sellerId, placementDTO));

    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldUpdatePlacementDTO() {
    Long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(RandomUtils.nextLong());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(
        createSite(sellerId, placementDTO.getSite().getPid(), placementDTO.getSite().getType()));

    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.BANNER);
    when(positionRepository.saveAndFlush(position)).thenReturn(position);
    assertNotNull(placementsService.update(sellerId, placementDTO));
    assertNull(placementDTO.getDooh());
  }

  @Test
  void shouldUpdatePlacementDTOWithPlacementDooh() {
    Long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createDoohPlacementDTO(RandomUtils.nextLong());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(
        createSite(sellerId, placementDTO.getSite().getPid(), placementDTO.getSite().getType()));

    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.BANNER);
    when(positionRepository.saveAndFlush(position)).thenReturn(position);

    assertNotNull(placementsService.update(sellerId, placementDTO));
    assertEquals(placementDTO.getPid(), position.getPlacementDooh().getPid());
  }

  @Test
  void shouldUpdatePlacementDTOWithoutPlacementDooh() {
    Long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createDoohPlacementDTO(RandomUtils.nextLong());
    placementDTO.setDooh(null);
    PlacementDooh placementDooh = new PlacementDooh();
    placementDooh.setVersion(0);
    placementDooh.setDefaultImpressionMultiplier(BigDecimal.ONE);
    placementDooh.setPid(placementDTO.getPid());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(
        createSite(sellerId, placementDTO.getSite().getPid(), placementDTO.getSite().getType()));
    position.setPlacementDooh(placementDooh);

    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.BANNER);
    when(positionRepository.saveAndFlush(position)).thenReturn(position);

    assertNotNull(placementsService.update(sellerId, placementDTO));
    assertEquals(placementDTO.getPid(), position.getPlacementDooh().getPid());
  }

  @Test
  void shouldThrowExceptionWhenSiteReturnedDoesntHaveCorrectSellerIdWhenUpdate() {
    long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(RandomUtils.nextLong());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(
        createSite(
            sellerId + 1, placementDTO.getSite().getPid(), placementDTO.getSite().getType()));

    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> placementsService.update(sellerId, placementDTO));

    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingPlacementDTOThatDoesntExist() {
    Long sellerId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(RandomUtils.nextLong());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(
        createSite(sellerId, placementDTO.getSite().getPid(), placementDTO.getSite().getType()));
    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(false);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> placementsService.update(sellerId, placementDTO));

    assertEquals(ServerErrorCodes.SERVER_POSITION_NOT_FOUND_IN_SITE, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenCreatingPlacementAndSiteTypeNotExpectedValue() {
    Long sellerPid = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(RandomUtils.nextLong());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(createSite(sellerPid, placementDTO.getSite().getPid(), Type.MOBILE_WEB));
    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> placementsService.save(sellerPid, placementDTO));

    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingPlacementAndSiteTypeNotExpectedValue() {
    Long sellerPid = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(RandomUtils.nextLong());
    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(createSite(sellerPid, placementDTO.getSite().getPid(), Type.MOBILE_WEB));
    when(siteRepository.findById(position.getSite().getPid()))
        .thenReturn(Optional.of(position.getSite()));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> placementsService.save(sellerPid, placementDTO));

    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowValidationExceptionWhenUpdateInvalidPlacementDTO() {
    PlacementDTO placementDTO = new PlacementDTO();
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(placementDTO, Default.class, UpdateGroup.class);
    assertThrows(
        EntityConstraintViolationException.class,
        () -> placementsService.update(123L, placementDTO));
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveInvalidPlacementDTO() {
    PlacementDTO placementDTO = new PlacementDTO();
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(placementDTO, Default.class, CreateGroup.class);
    assertThrows(
        EntityConstraintViolationException.class, () -> placementsService.save(123L, placementDTO));
  }

  private PlacementDTO createPlacementDTO(Long sitePid) {
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(sitePid);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setWidth(100);
    placementDTO.setHeight(100);
    placementDTO.setName("foo");
    placementDTO.setMemo("foo");
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setSite(siteDTO);
    return placementDTO;
  }

  private PlacementDTO createDoohPlacementDTO(Long sitePid) {
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(sitePid);
    siteDTO.setType(Type.DOOH);
    PlacementDoohDTO doohDTO = new PlacementDoohDTO();
    doohDTO.setDefaultImpressionMultiplier(BigDecimal.valueOf(2L));
    doohDTO.setVersion(0);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setWidth(100);
    placementDTO.setHeight(100);
    placementDTO.setName("foo");
    placementDTO.setMemo("foo");
    placementDTO.setDooh(doohDTO);
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setSite(siteDTO);
    return placementDTO;
  }

  private Site createSite(Long companyPid, Long sitePid, Type siteType) {
    Site site = new Site();
    site.setPid(sitePid);
    site.setType(siteType);
    site.setStatus(Status.ACTIVE);
    site.setCompanyPid(companyPid);
    return site;
  }

  @Test
  void shouldCreatePlacementWithPlacementVideo() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setName(null);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));
    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);

    PlacementDTO savedPlacement = placementsService.save(123L, placementDTO);
    assertNotNull(savedPlacement.getName());
    assertEquals(32, savedPlacement.getName().length());
    assertNotNull(savedPlacement.getPlacementVideo());
    // default longform false
    assertFalse(savedPlacement.getPlacementVideo().isLongform());
    assertNull(savedPlacement.getPlacementVideo().getStreamType());
    assertNull(savedPlacement.getPlacementVideo().getPlayerBrand());
    assertNull(savedPlacement.getPlacementVideo().getSsai());
  }

  @Test
  void shouldCreatePlacementWithPlacementVideoWithCompanion() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setName(null);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));
    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    List<PlacementVideoCompanionDTO> placementVideoCompanionDTOList = new ArrayList<>();
    placementVideoCompanionDTOList.add(placementVideoCompanionDTO);
    placementVideoDTO.setCompanions(placementVideoCompanionDTOList);
    placementDTO.setPlacementVideo(placementVideoDTO);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);

    PlacementDTO savedPlacement = placementsService.save(123L, placementDTO);
    assertNotNull(savedPlacement.getName());
    assertEquals(32, savedPlacement.getName().length());
    assertNotNull(savedPlacement.getPlacementVideo());
    assertEquals(1, savedPlacement.getPlacementVideo().getCompanions().size());
  }

  // No Video Placement DTO -> old DTO
  @Test
  void shouldCreatePlacementVideoDTO() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // With old DTO
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);

    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);

    PlacementDTO savedPlacementDTO = placementsService.save(123L, placementDTO);
    assertNotNull(savedPlacementDTO);
    PlacementVideoDTO savedPlacementVideoDTO = savedPlacementDTO.getPlacementVideo();
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(
        savedPlacementDTO.getVideoLinearity().toString(),
        savedPlacementVideoDTO.getLinearity().toString());
    // default longform false
    assertFalse(savedPlacementVideoDTO.isLongform());
    assertNull(savedPlacementVideoDTO.getStreamType());
    assertNull(savedPlacementVideoDTO.getPlayerBrand());
    assertNull(savedPlacementVideoDTO.getSsai());
  }

  @Test
  void shouldCreatePlacementDTOWithoutPlacementVideoDTO() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // With old DTO
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);

    PlacementDTO savedPlacementDTO = placementsService.save(123L, placementDTO);
    assertNotNull(savedPlacementDTO);
    assertNull(savedPlacementDTO.getPlacementVideo());
  }

  // With PlacementVideoDTO -> override position values
  @Test
  void shouldCreatePlacementDTOWithPlacementVideoDTO() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // With new DTO
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);
    placementDTO.setVideoSupport(VideoSupport.VIDEO);
    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    placementVideoDTO.setSsai(PlacementVideoSsai.UNKNOWN);
    placementVideoDTO.setPlayerBrand("test_player");
    placementDTO.setPlacementVideo(placementVideoDTO);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);

    PlacementDTO savedPlacementDTO = placementsService.save(123L, placementDTO);
    assertNotNull(savedPlacementDTO);
    PlacementVideoDTO savedPlacementVideoDTO = savedPlacementDTO.getPlacementVideo();
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(
        savedPlacementDTO.getVideoLinearity().toString(),
        savedPlacementVideoDTO.getLinearity().toString());
    assertTrue(savedPlacementVideoDTO.isLongform());
    assertEquals(PlacementVideoStreamType.VOD, savedPlacementVideoDTO.getStreamType());
    assertEquals("test_player", savedPlacementVideoDTO.getPlayerBrand());
    assertEquals(PlacementVideoSsai.UNKNOWN, savedPlacementVideoDTO.getSsai());
    assertNull(savedPlacementVideoDTO.getVideoPlacementType());
    assertNull(savedPlacementVideoDTO.getDapPlayerType());
    assertNull(savedPlacementVideoDTO.getPlayerId());
    assertNull(savedPlacementVideoDTO.getPlayListId());
  }

  @Test
  void shouldUpdatePlacementWithPlacementVideo() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setPid(placementId);
    placementDTO.setVersion(1);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(placementId);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.VIDEO_AND_BANNER);
    when(positionRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);
    PlacementDTO updatedPlacement = placementsService.update(123L, placementDTO);
    assertNotNull(updatedPlacement.getPlacementVideo());
    assertNull(updatedPlacement.getPlacementVideo().getVideoPlacementType());
    assertNull(updatedPlacement.getPlacementVideo().getDapPlayerType());
    assertNull(updatedPlacement.getPlacementVideo().getPlayerId());
    assertNull(updatedPlacement.getPlacementVideo().getPlayListId());
  }

  @Test
  void shouldUpdatePlacementWithoutPlacementVideo() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setPid(placementId);
    placementDTO.setVersion(1);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));

    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.VIDEO_AND_BANNER);
    when(positionRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PlacementDTO updatedPlacement = placementsService.update(123L, placementDTO);
    assertNull(updatedPlacement.getPlacementVideo());
  }

  @Test
  void shouldUpdatePlacementFromBannerToVideo() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setPid(placementId);
    placementDTO.setVersion(1);
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementVideoDTO.setVersion(1);

    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.BANNER);
    when(positionRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.populateVideoData(any(), any())).thenCallRealMethod();
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);
    PlacementDTO updatedPlacement = placementsService.update(123L, placementDTO);
    assertNull(updatedPlacement.getPlacementVideo());

    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);
    PlacementDTO updatedToPlacementVideo = placementsService.update(123L, placementDTO);
    assertNotNull(updatedToPlacementVideo.getPlacementVideo());
  }

  @Test
  void shouldUpdatePlacementFromVideoToBanner() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setPid(placementId);
    placementDTO.setVersion(1);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementVideoDTO.setVersion(1);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.VIDEO_AND_BANNER);
    when(positionRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.populateVideoData(any(), any())).thenCallRealMethod();
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);
    PlacementDTO updatedPlacement = placementsService.update(123L, placementDTO);
    assertNotNull(updatedPlacement.getPlacementVideo());

    placementDTO.setVideoSupport(VideoSupport.BANNER);
    PlacementDTO updatedToBannerPlacement = placementsService.update(123L, placementDTO);
    assertNull(updatedToBannerPlacement.getPlacementVideo());
  }

  @Test
  void shouldUpdatePlacementFromNativeToVideo() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setPid(placementId);
    placementDTO.setVersion(1);
    placementDTO.setVideoSupport(VideoSupport.NATIVE);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementVideoDTO.setVersion(1);

    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.NATIVE);
    when(positionRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);
    PlacementDTO updatedPlacement = placementsService.update(123L, placementDTO);
    assertNull(updatedPlacement.getPlacementVideo());
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);
    when(placementVideoService.populateVideoData(any(), any())).thenCallRealMethod();
    PlacementDTO updatedToPlacementVideo = placementsService.update(123L, placementDTO);
    assertNotNull(updatedToPlacementVideo.getPlacementVideo());
  }

  @Test
  void shouldUpdatePlacementFromVideoToNative() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setPid(placementId);
    placementDTO.setVersion(1);
    placementDTO.setVideoSupport(VideoSupport.VIDEO);
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementVideoDTO.setVersion(1);
    placementDTO.setPlacementVideo(placementVideoDTO);

    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.VIDEO);
    when(positionRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.populateVideoData(any(), any())).thenCallRealMethod();
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);
    PlacementDTO updatedPlacement = placementsService.update(123L, placementDTO);
    assertNotNull(updatedPlacement.getPlacementVideo());

    placementDTO.setVideoSupport(VideoSupport.NATIVE);
    PlacementDTO updatedToBannerPlacement = placementsService.update(123L, placementDTO);
    assertNull(updatedToBannerPlacement.getPlacementVideo());

    placementDTO.setVideoSupport(null);
    PlacementDTO updatePlacement = placementsService.update(123L, placementDTO);
    assertNull(updatePlacement.getPlacementVideo());
  }

  @Test
  void shouldUpdatePlacementWithLongformPlacementVideo() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setPid(placementId);
    placementDTO.setVersion(1);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.VIDEO_AND_BANNER);
    when(positionRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);
    PlacementDTO updatedPlacement = placementsService.update(123L, placementDTO);
    PlacementVideoDTO updatedPlacementVideoDTO = updatedPlacement.getPlacementVideo();
    assertNotNull(updatedPlacementVideoDTO);
    assertTrue(updatedPlacementVideoDTO.isLongform());
    assertEquals(PlacementVideoStreamType.VOD, updatedPlacementVideoDTO.getStreamType());
    assertEquals("test_player", updatedPlacementVideoDTO.getPlayerBrand());
    assertEquals(PlacementVideoSsai.ALL_CLIENT_SIDE, updatedPlacementVideoDTO.getSsai());
  }

  @Test
  void shouldCreateDapPlacementWithDapInputParams() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // With new DTO
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);
    PlacementVideoDTO placementVideoDTO = createDapPlacementVideoDTO();
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);
    placementVideoDTO.setLongform(false);
    placementDTO.setPlacementVideo(placementVideoDTO);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);

    PlacementDTO savedPlacementDTO = placementsService.save(123L, placementDTO);
    assertNotNull(savedPlacementDTO);
    PlacementVideoDTO savedPlacementVideoDTO = savedPlacementDTO.getPlacementVideo();
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(
        savedPlacementDTO.getVideoLinearity().toString(),
        savedPlacementVideoDTO.getLinearity().toString());
    assertFalse(savedPlacementVideoDTO.isLongform());
    assertEquals(VideoPlacementType.IN_ARTICLE, savedPlacementVideoDTO.getVideoPlacementType());
    assertEquals(DapPlayerType.O2, savedPlacementVideoDTO.getDapPlayerType());
    assertEquals(VALID_PLAYER_ID, savedPlacementVideoDTO.getPlayerId());
    assertEquals(VALID_PLAYLIST_ID, savedPlacementVideoDTO.getPlayListId());
  }

  @Test
  void shouldUpdatePlacementWithDapSettings() {
    Site site = createSite(123L, 123L, Type.DESKTOP);
    Long placementId = RandomUtils.nextLong();
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setPid(placementId);
    placementDTO.setVersion(1);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));
    PlacementVideoDTO placementVideoDTO = createDapPlacementVideoDTO();
    placementVideoDTO.setPid(placementId);
    placementDTO.setPlacementVideo(placementVideoDTO);
    when(siteRepository.findById(123L)).thenReturn(Optional.of(site));
    when(positionRepository.existsByPid(placementDTO.getPid())).thenReturn(true);
    when(positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid()))
        .thenReturn(VideoSupport.VIDEO_AND_BANNER);
    when(positionRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);
    PlacementDTO updatedPlacement = placementsService.update(123L, placementDTO);
    PlacementVideoDTO updatedPlacementVideoDTO = updatedPlacement.getPlacementVideo();
    assertNotNull(updatedPlacementVideoDTO);
    assertTrue(updatedPlacementVideoDTO.isPlayerRequired());
    assertEquals(IN_ARTICLE_VIDEO_PLACEMENT, updatedPlacementVideoDTO.getVideoPlacementType());
    assertEquals(DapPlayerType.O2, updatedPlacementVideoDTO.getDapPlayerType());
    assertEquals(VALID_PLAYER_ID, updatedPlacementVideoDTO.getPlayerId());
    assertEquals(VALID_PLAYLIST_ID, updatedPlacementVideoDTO.getPlayListId());
  }

  private PlacementVideoDTO createDapPlacementVideoDTO() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPlayerHeight(null);
    placementVideoDTO.setPlayerWidth(null);
    placementVideoDTO.setVideoPlacementType(IN_ARTICLE_VIDEO_PLACEMENT);
    placementVideoDTO.setPlayerRequired(true);
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);
    placementVideoDTO.setPlayerId(VALID_PLAYER_ID);
    placementVideoDTO.setPlayListId(VALID_PLAYLIST_ID);
    return placementVideoDTO;
  }

  private List<PlacementDTO> createPlacementDTOSWithoutVideoPlacements() {
    List<PlacementDTO> placementDTOS = createPlacements(1);
    placementDTOS.get(0).setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTOS.get(0).setVideoLinearity(VideoLinearity.LINEAR);

    return placementDTOS;
  }

  private List<PlacementDTO> createPlacementDTOSWithVideoPlacements() {
    List<PlacementDTO> placementDTOS = createPlacementDTOSWithoutVideoPlacements();
    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    placementDTOS.get(0).setPlacementVideo(placementVideoDTO);
    return placementDTOS;
  }

  private List<PlacementDTO> createPlacementDTOSWithVideoPlacementsAndCompanion() {
    List<PlacementDTO> placementDTOS = createPlacementDTOSWithoutVideoPlacements();
    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    placementDTOS.get(0).setPlacementVideo(placementVideoDTO);
    return placementDTOS;
  }

  private List<PlacementDTO> createPlacementDTOSWithVideoPlacementsAndMultipleCompanion() {
    List<PlacementDTO> placementDTOS = createPlacementDTOSWithoutVideoPlacements();
    PlacementVideoDTO placementVideoDTO = createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO2 =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTO2.setHeight(250);
    placementVideoCompanionDTO2.setWidth(350);
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    placementVideoDTO.addCompanion(placementVideoCompanionDTO2);
    placementDTOS.get(0).setPlacementVideo(placementVideoDTO);
    return placementDTOS;
  }
}
