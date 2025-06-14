package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation;
import com.nexage.app.dto.seller.PlacementSummaryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.site.PlacementSummaryDTOMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class PlacementSummaryDTOServiceImplTest {

  private static final long SELLER_PID = 1L;
  private static final long ALTERNATE_SELLER_PID = 2L;
  private static final long SITE_PID = 1L;

  @Mock private SiteRepository siteRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private Pageable pageable;

  @InjectMocks private PlacementSummaryDTOServiceImpl placementsService;

  private final Page<PositionMetricsAggregation> returnedAggregatedPage =
      new PageImpl<>(TestObjectsFactory.createPositionMetricsAggregation(10));
  private final Page<PlacementSummaryDTO> expectedAggregatedPage =
      returnedAggregatedPage.map(PlacementSummaryDTOMapper.MAPPER::map);

  @Test
  void shouldGetPositionsWithMetrics() {
    // given
    Site site = new Site();
    site.setCompanyPid(SELLER_PID);
    given(siteRepository.findByPid(SELLER_PID)).willReturn(Optional.of(site));
    given(pageable.getPageNumber()).willReturn(1);
    given(pageable.getPageSize()).willReturn(10);
    given(pageable.getSort()).willReturn(Sort.unsorted());
    given(
            positionRepository.findPositionsWithMetrics(
                any(Date.class), any(Date.class), eq(SITE_PID), any(Pageable.class)))
        .willReturn(returnedAggregatedPage);
    given(
            positionRepository.findPositionsByPidsWithMetrics(
                any(Date.class), any(Date.class), anyList()))
        .willReturn(returnedAggregatedPage.getContent());

    // when
    Page<PlacementSummaryDTO> actualPage =
        placementsService.getPlacementsWithMetrics(
            new Date(),
            new Date(),
            SITE_PID,
            SELLER_PID,
            Optional.empty(),
            Optional.empty(),
            pageable);

    // then
    assertEquals(
        expectedAggregatedPage.getContent().get(0).getPid(),
        actualPage.getContent().get(0).getPid());
    assertEquals(0.0D, actualPage.getContent().get(0).getRevenueTrendPercent());
    verify(positionRepository, never())
        .findPositionsByNameWithMetrics(
            any(Date.class), any(Date.class), any(String.class), eq(SITE_PID), any(Pageable.class));
    verify(positionRepository, never())
        .findPositionsByPidWithMetrics(
            any(Date.class), any(Date.class), any(Long.class), eq(SITE_PID), any(Pageable.class));
  }

  @Test
  void shouldGetPositionsByNameWithMetrics() {
    // given
    Site site = new Site();
    site.setCompanyPid(SELLER_PID);
    given(siteRepository.findByPid(SELLER_PID)).willReturn(Optional.of(site));
    given(pageable.getPageNumber()).willReturn(1);
    given(pageable.getPageSize()).willReturn(10);
    given(pageable.getSort()).willReturn(Sort.unsorted());
    given(
            positionRepository.findPositionsByNameWithMetrics(
                any(Date.class), any(Date.class), eq("p1"), eq(SITE_PID), any(Pageable.class)))
        .willReturn(returnedAggregatedPage);
    given(
            positionRepository.findPositionsByPidsWithMetrics(
                any(Date.class), any(Date.class), anyList()))
        .willReturn(returnedAggregatedPage.getContent());

    // when
    Page<PlacementSummaryDTO> actualPage =
        placementsService.getPlacementsWithMetrics(
            new Date(),
            new Date(),
            SITE_PID,
            SELLER_PID,
            Optional.of("p1"),
            Optional.empty(),
            pageable);

    // then
    assertEquals(
        expectedAggregatedPage.getContent().get(0).getPid(),
        actualPage.getContent().get(0).getPid());
    verify(positionRepository, never())
        .findPositionsByPidWithMetrics(
            any(Date.class), any(Date.class), any(Long.class), eq(SITE_PID), any(Pageable.class));
    verify(positionRepository, never())
        .findPositionsWithMetrics(
            any(Date.class), any(Date.class), eq(SITE_PID), any(Pageable.class));
  }

  @Test
  void shouldGetPositionsByPidWithMetrics() {
    // given
    Site site = new Site();
    site.setCompanyPid(SELLER_PID);
    given(siteRepository.findByPid(SELLER_PID)).willReturn(Optional.of(site));
    given(pageable.getPageNumber()).willReturn(1);
    given(pageable.getPageSize()).willReturn(10);
    given(pageable.getSort()).willReturn(Sort.unsorted());
    given(
            positionRepository.findPositionsByPidWithMetrics(
                any(Date.class), any(Date.class), eq(1L), eq(SITE_PID), any(Pageable.class)))
        .willReturn(returnedAggregatedPage);
    given(
            positionRepository.findPositionsByPidsWithMetrics(
                any(Date.class), any(Date.class), anyList()))
        .willReturn(returnedAggregatedPage.getContent());

    // when
    Page<PlacementSummaryDTO> actualPage =
        placementsService.getPlacementsWithMetrics(
            new Date(),
            new Date(),
            SITE_PID,
            SELLER_PID,
            Optional.empty(),
            Optional.of(1L),
            pageable);

    // then
    assertEquals(
        expectedAggregatedPage.getContent().get(0).getPid(),
        actualPage.getContent().get(0).getPid());
    verify(positionRepository, never())
        .findPositionsByNameWithMetrics(
            any(Date.class), any(Date.class), any(String.class), eq(SITE_PID), any(Pageable.class));
    verify(positionRepository, never())
        .findPositionsWithMetrics(
            any(Date.class), any(Date.class), eq(SITE_PID), any(Pageable.class));
  }

  @Test
  void shouldGetPositionsWithSellerIdByName() {
    // given
    given(pageable.getPageNumber()).willReturn(1);
    given(pageable.getPageSize()).willReturn(10);
    given(pageable.getSort()).willReturn(Sort.unsorted());
    given(
            positionRepository.findPositionsByNameWithMetricsNoSitePid(
                any(Date.class),
                any(Date.class),
                eq(SELLER_PID),
                any(String.class),
                any(Pageable.class)))
        .willReturn(returnedAggregatedPage);
    given(
            positionRepository.findPositionsByPidsWithMetrics(
                any(Date.class), any(Date.class), anyList()))
        .willReturn(returnedAggregatedPage.getContent());

    // when
    Page<PlacementSummaryDTO> actualPage =
        placementsService.getPlacementsWithMetricsWithoutSitePid(
            new Date(), new Date(), SELLER_PID, "", pageable);

    // then
    assertEquals(
        expectedAggregatedPage.getContent().get(0).getPid(),
        actualPage.getContent().get(0).getPid());
    verify(positionRepository, never())
        .findPositionsByPidWithMetrics(
            any(Date.class), any(Date.class), any(Long.class), eq(SITE_PID), any(Pageable.class));
    verify(positionRepository, never())
        .findPositionsWithMetrics(
            any(Date.class), any(Date.class), eq(SITE_PID), any(Pageable.class));
  }

  @Test
  void shouldThrowExceptionOnSiteNotFound() {
    // given
    Date startDate = new Date();
    Date stopDate = new Date();
    Optional<String> name = Optional.empty();
    Optional<Long> pid = Optional.empty();
    given(siteRepository.findByPid(SELLER_PID)).willReturn(Optional.empty());

    // when
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                placementsService.getPlacementsWithMetrics(
                    startDate, stopDate, SITE_PID, ALTERNATE_SELLER_PID, name, pid, pageable));

    // then
    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnSiteSellerMismatch() {
    // given
    Date startDate = new Date();
    Date stopDate = new Date();
    Optional<String> name = Optional.empty();
    Optional<Long> pid = Optional.empty();
    Site site = new Site();
    site.setCompanyPid(SELLER_PID);
    given(siteRepository.findByPid(SELLER_PID)).willReturn(Optional.of(site));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                placementsService.getPlacementsWithMetrics(
                    startDate, stopDate, SITE_PID, ALTERNATE_SELLER_PID, name, pid, pageable));

    // then
    assertEquals(ServerErrorCodes.SERVER_SITE_SELLER_INVALID_COMBINATION, exception.getErrorCode());
  }
}
