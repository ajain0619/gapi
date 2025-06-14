package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.Company_;
import com.nexage.admin.core.model.aggregation.CompanyMetricsAggregation;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.seller.SellerSummaryDTO;
import com.nexage.app.mapper.SellerSummaryDTOMapper;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class SellerSummaryDTOServiceImplTest {

  private final Date startDate = new Date();
  private final Date stopDate = new Date();
  private final Date fixedStopDate = fixStopDate(new Date());
  private Set<String> qf = Collections.emptySet();

  @Mock private CompanyRepository companyRepository;
  @Mock private UserContext userContext;
  @Mock private Pageable pageable;
  @InjectMocks private SellerSummaryDTOServiceImpl sellerSummaryDTOService;

  @Test
  void shouldThrowExceptionOnFindSummaryInvalidQueryRequest() {
    // given
    Set<String> qf = Collections.singleton("whatever");
    // then
    assertThrows(
        GenevaValidationException.class,
        () -> sellerSummaryDTOService.findSummary(startDate, stopDate, qf, null, pageable));
  }

  @Test
  void shouldFindAllSellersSummariesWithPaginatedResponseForNexageUser() {
    when(userContext.isNexageUser()).thenReturn(true);
    when(pageable.getPageNumber()).thenReturn(0);
    when(pageable.getPageSize()).thenReturn(10);
    when(pageable.getSort()).thenReturn(Sort.unsorted());
    Page<CompanyMetricsAggregation> aggregations = getCompanyMetricsAggregations();
    List<SellerSummaryDTO> sellers =
        aggregations.map(SellerSummaryDTOMapper.MAPPER::map).getContent();

    when(companyRepository.aggregateMetrics(eq(startDate), eq(fixedStopDate), any(Pageable.class)))
        .thenReturn(aggregations);

    Page<SellerSummaryDTO> result =
        sellerSummaryDTOService.findSummary(startDate, stopDate, qf, null, pageable);
    assertNotNull(result);
    assertEquals(result.getContent(), sellers);
    verify(companyRepository)
        .aggregateMetrics(eq(startDate), eq(fixedStopDate), any(Pageable.class));
  }

  @Test
  void shouldFindSellerSummaryWithPaginatedResponseForExternalUser() {
    Set<Long> companies = Sets.newHashSet(4L, 5L, 6L);
    when(userContext.getCompanyPids()).thenReturn(companies);
    when(pageable.getPageNumber()).thenReturn(0);
    when(pageable.getPageSize()).thenReturn(10);
    when(pageable.getSort()).thenReturn(Sort.unsorted());
    Page<CompanyMetricsAggregation> aggregations = getCompanyMetricsAggregations();
    List<SellerSummaryDTO> sellers =
        aggregations.map(SellerSummaryDTOMapper.MAPPER::map).getContent();

    when(companyRepository.aggregateNonNexageMetricsByCompanies(
            eq(startDate), eq(fixedStopDate), eq(companies), any(Pageable.class)))
        .thenReturn(aggregations);

    Page<SellerSummaryDTO> result =
        sellerSummaryDTOService.findSummary(startDate, stopDate, qf, null, pageable);
    assertNotNull(result);
    assertEquals(result.getContent(), sellers);
    verify(companyRepository)
        .aggregateNonNexageMetricsByCompanies(
            eq(startDate), eq(fixedStopDate), eq(companies), any(Pageable.class));
  }

  @Test
  void shouldFindSellerSummariesAndPageAndSortThem() {
    // given
    String property = "ecpm";
    PageRequest pageRequest = PageRequest.of(0, 10, Direction.ASC, property);
    PageRequest targetPage =
        PageRequest.of(0, 10, Sort.by(Direction.ASC, property).and(Sort.by(Company_.PID)));
    Page<CompanyMetricsAggregation> aggregations = getCompanyMetricsAggregations();

    when(userContext.isNexageUser()).thenReturn(true);
    when(companyRepository.aggregateMetrics(startDate, fixedStopDate, targetPage))
        .thenReturn(aggregations);

    // when
    Page<SellerSummaryDTO> result =
        sellerSummaryDTOService.findSummary(startDate, stopDate, qf, null, pageRequest);

    // then
    assertNotNull(result);
    verify(companyRepository).aggregateMetrics(startDate, fixedStopDate, targetPage);
  }

  @Test
  void shouldFindSellerSummaryForCompaniesWithPaginatedResponseForExternalUser() {
    // given
    Set<Long> companies = Sets.newHashSet(4L, 5L, 6L);
    Page<CompanyMetricsAggregation> aggregations = getCompanyMetricsAggregations();

    when(userContext.getCompanyPids()).thenReturn(companies);
    when(pageable.getPageNumber()).thenReturn(0);
    when(pageable.getPageSize()).thenReturn(10);
    when(pageable.getSort()).thenReturn(Sort.unsorted());

    List<SellerSummaryDTO> sellers =
        aggregations.map(SellerSummaryDTOMapper.MAPPER::map).getContent();

    String qt = "Seller1";
    when(companyRepository.aggregateNonNexageMetricsByNameAndCompanies(
            eq(startDate), eq(fixedStopDate), eq(qt), eq(companies), any(Pageable.class)))
        .thenReturn(aggregations);

    // when
    qf = Collections.singleton("name");
    Page<SellerSummaryDTO> result =
        sellerSummaryDTOService.findSummary(startDate, stopDate, qf, qt, pageable);

    // then
    assertNotNull(result);
    assertEquals(result.getContent(), sellers);
    verify(companyRepository)
        .aggregateNonNexageMetricsByNameAndCompanies(
            eq(startDate), eq(fixedStopDate), eq(qt), eq(companies), any(Pageable.class));
  }

  private Page<CompanyMetricsAggregation> getCompanyMetricsAggregations() {
    String id = UUID.randomUUID().toString();
    Long pid = new Random().nextLong();
    CompanyMetricsAggregation metricsAggregation =
        CompanyMetricsAggregation.builder().id(id).pid(pid).build();
    return new PageImpl<>(Collections.singletonList(metricsAggregation));
  }

  // Need to make sure the time on the stop date includes the whole day
  private Date fixStopDate(Date stopDate) {
    if (stopDate == null) {
      return null;
    }
    Calendar cal = DateUtils.toCalendar(stopDate);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    return cal.getTime();
  }
}
