package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
class SellerSeatSummaryServiceImplTest {

  private final Date startDate = new Date();
  private final Date stopDate = new Date();
  private Set<String> qf = Collections.emptySet();
  private static final Long SELLER_SEAT_PID = 66L;

  @Mock private CompanyRepository companyRepository;
  @Mock private UserContext userContext;
  @Mock private Pageable pageable;
  @InjectMocks private SellerSeatSummaryServiceImpl seatSummaryService;

  @BeforeEach
  void setUp() {
    lenient().when(pageable.getPageNumber()).thenReturn(0);
    lenient().when(pageable.getPageSize()).thenReturn(10);
    lenient().when(pageable.getSort()).thenReturn(Sort.unsorted());
  }

  @Test
  void shouldThrowExceptionOnFindSummaryInvalidQueryRequest() {
    // given
    Set<String> qf = Collections.singleton("whatever");
    // then
    assertThrows(
        GenevaValidationException.class,
        () -> seatSummaryService.findSummary(null, startDate, stopDate, qf, null, pageable));
  }

  @Test
  void shouldFindAllSellerSeatSummariesWithPaginatedResponseForNexageUser() {
    when(userContext.isNexageUser()).thenReturn(true);

    Page<CompanyMetricsAggregation> aggregations = getCompanyMetricsAggregations();
    List<SellerSummaryDTO> sellers =
        aggregations.map(SellerSummaryDTOMapper.MAPPER::map).getContent();

    when(companyRepository.aggregateMetricsBySellerSeatPid(
            eq(startDate), eq(stopDate), eq(SELLER_SEAT_PID), any(Pageable.class)))
        .thenReturn(aggregations);

    Page<SellerSummaryDTO> result =
        seatSummaryService.findSummary(SELLER_SEAT_PID, startDate, stopDate, qf, null, pageable);
    assertNotNull(result);
    assertEquals(result.getContent(), sellers);
  }

  @Test
  void shouldFindSellerSeatSummaryWithPaginatedResponseForSellerSeatUser() {
    Set<Long> companies = Sets.newHashSet(4L, 5L, 6L);
    when(userContext.getCompanyPids()).thenReturn(companies);

    Page<CompanyMetricsAggregation> aggregations = getCompanyMetricsAggregations();
    List<SellerSummaryDTO> sellers =
        aggregations.map(SellerSummaryDTOMapper.MAPPER::map).getContent();

    when(companyRepository.aggregateNonNexageMetricsByCompanies(
            eq(startDate), eq(stopDate), eq(companies), any(Pageable.class)))
        .thenReturn(aggregations);

    Page<SellerSummaryDTO> result =
        seatSummaryService.findSummary(SELLER_SEAT_PID, startDate, stopDate, qf, null, pageable);
    assertNotNull(result);
    assertEquals(result.getContent(), sellers);
  }

  @Test
  void shouldFindSellerSeatSummariesAndPageAndSortThem() {
    // given
    String property = "ecpm";
    PageRequest pageRequest = PageRequest.of(0, 10, Direction.ASC, property);
    PageRequest targetPage =
        PageRequest.of(0, 10, Sort.by(Direction.ASC, property).and(Sort.by(Company_.PID)));
    Page<CompanyMetricsAggregation> aggregations = getCompanyMetricsAggregations();

    when(userContext.isNexageUser()).thenReturn(true);
    when(companyRepository.aggregateMetricsBySellerSeatPid(
            startDate, stopDate, SELLER_SEAT_PID, targetPage))
        .thenReturn(aggregations);

    // when
    Page<SellerSummaryDTO> result =
        seatSummaryService.findSummary(SELLER_SEAT_PID, startDate, stopDate, qf, null, pageRequest);

    // then
    assertNotNull(result);
    verify(companyRepository)
        .aggregateMetricsBySellerSeatPid(startDate, stopDate, SELLER_SEAT_PID, targetPage);
  }

  @Test
  void shouldFindSellerSeatSummaryForCompaniesWithPaginatedResponseForExternalUser() {
    Set<Long> companies = Sets.newHashSet(4L, 5L, 6L);
    when(userContext.getCompanyPids()).thenReturn(companies);

    Page<CompanyMetricsAggregation> aggregations = getCompanyMetricsAggregations();
    List<SellerSummaryDTO> sellers =
        aggregations.map(SellerSummaryDTOMapper.MAPPER::map).getContent();

    String qt = "Seller1";
    when(companyRepository.aggregateNonNexageMetricsByNameAndCompanies(
            eq(startDate), eq(stopDate), eq(qt), eq(companies), any(Pageable.class)))
        .thenReturn(aggregations);

    qf = Collections.singleton("name");
    Page<SellerSummaryDTO> result =
        seatSummaryService.findSummary(SELLER_SEAT_PID, startDate, stopDate, qf, qt, pageable);
    assertNotNull(result);
    assertEquals(result.getContent(), sellers);
  }

  Page<CompanyMetricsAggregation> getCompanyMetricsAggregations() {
    String id = UUID.randomUUID().toString();
    Long pid = new Random().nextLong();
    CompanyMetricsAggregation metricsAggregation =
        CompanyMetricsAggregation.builder().id(id).pid(pid).build();
    return new PageImpl<>(Collections.singletonList(metricsAggregation));
  }
}
