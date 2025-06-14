package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation;
import com.nexage.app.dto.pub.self.serve.PubSelfServeSummaryMetrics;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.dto.seller.SiteSummaryDTO;
import com.nexage.app.dto.seller.SitesSummaryDTO;
import com.nexage.app.mapper.site.SiteDTOMapper;
import com.nexage.app.mapper.site.SiteSummaryDTOMapper;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
class SitesSummaryDTOServiceImplTest {
  @Mock SiteRepository siteRepository;
  @Mock Pageable pageable;
  @Mock LoginUserContext userContext;
  @Mock SpringUserDetails springUserDetails;
  @InjectMocks private SitesSummaryDTOServiceImpl siteSummaryService;
  Page<Site> pagedEntity;
  Page<SiteMetricsAggregation> summaryPagedEntity;
  Page<SiteDTO> pagedSite;
  Page<SiteSummaryDTO> summaryPagedSite;
  SiteMetricsAggregation summary;
  Optional<List<Long>> sitePids = Optional.of(new ArrayList<>());

  @BeforeEach
  public void setUp() {
    pagedEntity = new PageImpl(TestObjectsFactory.gimme(10, Site.class));
    summaryPagedEntity = new PageImpl(TestObjectsFactory.gimme(10, SiteMetricsAggregation.class));
    pagedSite = pagedEntity.map(SiteDTOMapper.MAPPER::map);
    summaryPagedSite = summaryPagedEntity.map(SiteSummaryDTOMapper.MAPPER::map);
    summary = TestObjectsFactory.gimme(10, SiteMetricsAggregation.class).get(0);
    sitePids.get().add(123L);
    when(pageable.getPageNumber()).thenReturn(1);
    when(pageable.getPageSize()).thenReturn(10);
    when(pageable.getSort()).thenReturn(Sort.unsorted());
  }

  @Test
  void shouldGetSitesSummary() throws Exception {
    when(siteRepository.aggregateMetrics(
            any(Date.class), any(Date.class), eq(123L), any(Pageable.class)))
        .thenReturn(summaryPagedEntity);
    when(siteRepository.findSellerRevenue(any(Date.class), any(Date.class), any(List.class)))
        .thenReturn(summaryPagedEntity.getContent());
    when(siteRepository.findSummaryByCompanyPid(any(Date.class), any(Date.class), eq(123L)))
        .thenReturn(summary);
    SitesSummaryDTO sitesSummaryDTO =
        siteSummaryService.getSitesSummaryDTO(
            123L, new Date(), new Date(), Optional.empty(), Optional.empty(), pageable);
    PubSelfServeSummaryMetrics expectedTotal = buildExpectedSummaryMetrics(summary);
    assertMetrics(expectedTotal, sitesSummaryDTO);
    verify(siteRepository, never())
        .aggregateMetricsWithName(
            any(Date.class), any(Date.class), any(String.class), eq(123L), eq(pageable));
    verify(siteRepository, never())
        .aggregateMetricsWithPid(
            any(Date.class), any(Date.class), any(Long.class), eq(123L), eq(pageable));
  }

  @Test
  void shouldGetSitesSummaryWhenSellerHasNotSites() throws Exception {
    when(siteRepository.aggregateMetrics(
            any(Date.class), any(Date.class), eq(123L), any(Pageable.class)))
        .thenReturn(summaryPagedEntity);
    when(siteRepository.findSellerRevenue(any(Date.class), any(Date.class), any(List.class)))
        .thenReturn(summaryPagedEntity.getContent());
    when(siteRepository.findSummaryByCompanyPid(any(Date.class), any(Date.class), eq(123L)))
        .thenReturn(null);
    SitesSummaryDTO sitesSummaryDTO =
        siteSummaryService.getSitesSummaryDTO(
            123L, new Date(), new Date(), Optional.empty(), Optional.empty(), pageable);
    PubSelfServeSummaryMetrics expectedTotal = new PubSelfServeSummaryMetrics();
    assertMetrics(expectedTotal, sitesSummaryDTO);
    verify(siteRepository, never())
        .aggregateMetricsWithName(
            any(Date.class), any(Date.class), any(String.class), eq(123L), eq(pageable));
    verify(siteRepository, never())
        .aggregateMetricsWithPid(
            any(Date.class), any(Date.class), any(Long.class), eq(123L), eq(pageable));
  }

  @Test
  void shouldGetSitesSummaryByName() throws Exception {
    when(siteRepository.aggregateMetricsWithName(
            any(Date.class), any(Date.class), eq("SiteA"), eq(123L), any(Pageable.class)))
        .thenReturn(summaryPagedEntity);
    when(siteRepository.findSellerRevenue(any(Date.class), any(Date.class), any(List.class)))
        .thenReturn(summaryPagedEntity.getContent());
    when(siteRepository.findSummaryByCompanyPid(any(Date.class), any(Date.class), eq(123L)))
        .thenReturn(summary);
    PubSelfServeSummaryMetrics expectedTotal = buildExpectedSummaryMetrics(summary);
    SitesSummaryDTO sitesSummaryDTO =
        siteSummaryService.getSitesSummaryDTO(
            123L, new Date(), new Date(), Optional.of("SiteA"), Optional.empty(), pageable);
    assertMetrics(expectedTotal, sitesSummaryDTO);
    verify(siteRepository, never())
        .aggregateMetricsWithPid(
            any(Date.class), any(Date.class), any(Long.class), eq(123L), eq(pageable));
    verify(siteRepository, never())
        .aggregateMetricsWithPids(
            any(Date.class), any(Date.class), any(Long.class), any(), eq(pageable));
    verify(siteRepository, never())
        .aggregateMetrics(any(Date.class), any(Date.class), eq(123L), eq(pageable));
  }

  @Test
  void shouldGetSitesSummarieswithMultiplePids() throws Exception {
    when(siteRepository.aggregateMetricsWithPids(
            any(Date.class), any(Date.class), eq(123L), eq(sitePids), any(Pageable.class)))
        .thenReturn(summaryPagedEntity);
    when(siteRepository.findSellerRevenue(any(Date.class), any(Date.class), any(List.class)))
        .thenReturn(summaryPagedEntity.getContent());
    when(siteRepository.findSummaryByCompanyPid(any(Date.class), any(Date.class), eq(123L)))
        .thenReturn(summary);
    SitesSummaryDTO sitesSummaryDTO =
        siteSummaryService.getSitesSummaryDTO(
            123L, new Date(), new Date(), Optional.empty(), sitePids, pageable);
    PubSelfServeSummaryMetrics expectedTotal = buildExpectedSummaryMetrics(summary);
    assertMetrics(expectedTotal, sitesSummaryDTO);
    verify(siteRepository, never())
        .aggregateMetricsWithName(
            any(Date.class), any(Date.class), any(String.class), eq(123L), eq(pageable));
    verify(siteRepository, never())
        .aggregateMetricsWithPid(
            any(Date.class), any(Date.class), any(Long.class), eq(123L), eq(pageable));
    verify(siteRepository, never())
        .aggregateMetrics(any(Date.class), any(Date.class), eq(123L), eq(pageable));
  }

  private void assertMetrics(
      PubSelfServeSummaryMetrics expectedTotal, SitesSummaryDTO sitesSummaryDTO) {
    assertEquals(
        summaryPagedSite.getContent().get(0).getPId(),
        sitesSummaryDTO.getSites().getContent().get(0).getPId());
    assertEquals(
        expectedTotal.getClicks().getCurrent(),
        sitesSummaryDTO.getSummary().getClicks().getCurrent(),
        0);
    assertEquals(
        expectedTotal.getServed().getCurrent(),
        sitesSummaryDTO.getSummary().getServed().getCurrent(),
        0);
    assertEquals(
        expectedTotal.getRequests().getCurrent(),
        sitesSummaryDTO.getSummary().getRequests().getCurrent(),
        0);
    assertEquals(
        expectedTotal.getDelivered().getCurrent(),
        sitesSummaryDTO.getSummary().getDelivered().getCurrent(),
        0);
    assertEquals(
        expectedTotal.getCtr().getCurrent(), sitesSummaryDTO.getSummary().getCtr().getCurrent(), 0);
    assertEquals(
        expectedTotal.getRpm().getCurrent(), sitesSummaryDTO.getSummary().getRpm().getCurrent(), 0);
    assertEquals(
        expectedTotal.getEcpm().getCurrent(),
        sitesSummaryDTO.getSummary().getEcpm().getCurrent(),
        0);
    assertEquals(
        expectedTotal.getFillRate().getCurrent(),
        sitesSummaryDTO.getSummary().getFillRate().getCurrent(),
        0);
    assertEquals(
        expectedTotal.getRevenue().getCurrent(),
        sitesSummaryDTO.getSummary().getRevenue().getCurrent(),
        0);
  }

  private PubSelfServeSummaryMetrics buildExpectedSummaryMetrics(SiteMetricsAggregation summary) {
    PubSelfServeSummaryMetrics expectedTotal = new PubSelfServeSummaryMetrics();
    expectedTotal.getClicks().aggregate(summary.getAdClicked(), 0);
    expectedTotal.getServed().aggregate(summary.getAdServed(), 0);
    expectedTotal.getRequests().aggregate(summary.getAdRequested(), 0);
    expectedTotal.getDelivered().aggregate(summary.getAdDelivered(), 0);
    expectedTotal.getRevenue().aggregate(summary.getSellerRevenue(), 0);
    return expectedTotal;
  }
}
