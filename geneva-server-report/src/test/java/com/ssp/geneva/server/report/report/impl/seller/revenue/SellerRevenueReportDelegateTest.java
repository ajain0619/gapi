package com.ssp.geneva.server.report.report.impl.seller.revenue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.repository.CompanyRepository;
import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportMetadata;
import com.ssp.geneva.server.report.report.ReportUser;
import com.ssp.geneva.server.report.report.util.ReportUserFactory;
import com.ssp.geneva.server.report.report.util.RestrictedAccessUtil;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerRevenueReportDelegateTest {

  @Mock private RestrictedAccessUtil restrictedAccessUtil;
  @Mock private CompanyRepository companyRepository;

  @InjectMocks private SellerRevenueReportDelegate revenueReportDelegate;

  @Test
  void preProcess_reportIsForNexageUserDontModifyCompanies() {
    SellerRevenueRequest request =
        SellerRevenueRequestImpl.builder()
            .reportUser(ReportUserFactory.aNexageUser(ReportUserFactory.withCompanies(1L)))
            .build();

    given(
            restrictedAccessUtil.getSiteIdsRestrictionForCompanies(
                request.getCompanies(), request.getReportUser()))
        .willReturn(ImmutableSet.of(1L, 2L, 3L));

    revenueReportDelegate.preProcess(request);

    assertTrue(CollectionUtils.isEmpty(request.getCompanies()));
    assertEquals(ImmutableSet.of(1L, 2L, 3L), request.getSiteIds());
  }

  @Test
  void preProcess_reportIsForSellerUserLimitSiteIdsToUserCompanies() {
    SellerRevenueRequest request =
        SellerRevenueRequestImpl.builder()
            .reportUser(ReportUserFactory.aSeller(ReportUserFactory.withCompanies(4L, 5L, 6L)))
            .companies(ReportUserFactory.withCompanies(1L, 2L, 3L, 4L, 5L, 6L))
            .build();

    given(
            restrictedAccessUtil.getSiteIdsRestrictionForCompanies(
                ReportUserFactory.withCompanies(4L, 5L, 6L), request.getReportUser()))
        .willReturn(ImmutableSet.of(1L, 2L, 3L));

    revenueReportDelegate.preProcess(request);

    assertEquals(ImmutableSet.of(4L, 5L, 6L), request.getCompanies());
    assertEquals(ImmutableSet.of(1L, 2L, 3L), request.getSiteIds());
  }

  @Test
  void preProcess_reportIsForInternalUserScopeIdsWhenSellerSeatPidIsGiven() {
    Long sellerSeatPid = 3L;
    SellerRevenueRequest request =
        SellerRevenueRequestImpl.builder()
            .reportUser(ReportUserFactory.aNexageUser())
            .sellerSeat(sellerSeatPid)
            .build();

    given(companyRepository.findAllIdsBySellerSeatPid(sellerSeatPid))
        .willReturn(ReportUserFactory.withCompanies(4L, 5L, 6L));

    given(
            restrictedAccessUtil.getSiteIdsRestrictionForCompanies(
                ReportUserFactory.withCompanies(4L, 5L, 6L), request.getReportUser()))
        .willReturn(ImmutableSet.of(1L, 2L, 3L));

    revenueReportDelegate.preProcess(request);

    assertEquals(ImmutableSet.of(4L, 5L, 6L), request.getCompanies());
    assertEquals(ImmutableSet.of(1L, 2L, 3L), request.getSiteIds());
  }

  @Data
  @Builder
  private static class SellerRevenueRequestImpl implements SellerRevenueRequest {
    private ReportDimension dim;
    private Long seller, site, adsource, company, tag, adSourceTypeId, headerBidding, sellerSeat;
    private String country, start, stop, position;
    private Set<Long> siteIds, companies;
    private ReportUser reportUser;
    private ReportMetadata reportMetadata;
  }
}
