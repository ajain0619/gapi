package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.SiteViewRepository;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.site.SiteDTOMapper;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.util.validator.site.SiteQueryParams;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class SiteDTOServiceImplTest {

  private static final Long SELLER_PID = 123L;
  private static final Long SELLER_SEAT_PID = 456L;

  @Mock private SiteRepository siteRepository;
  @Mock private Pageable pageable;
  @Mock private LoginUserContext userContext;
  @Mock private SpringUserDetails springUserDetails;
  @Mock private SiteViewRepository siteViewRepository;
  @InjectMocks private SiteServiceImpl siteService;
  private Page<Site> pagedEntity;
  private Page<SiteDTO> pagedSite;
  private Page<SiteView> pagedSiteView;

  @BeforeEach
  public void setUp() {
    pagedEntity = new PageImpl<>(TestObjectsFactory.gimme(10, Site.class));
    pagedSite = pagedEntity.map(SiteDTOMapper.MAPPER::map);
    pagedSiteView =
        new PageImpl<>(
            List.of(new SiteView(1L, "site name", Status.ACTIVE, "example.com", "Example")));
  }

  @Test
  void testAuthorizeSellerToReadSitesThrowsExceptionWhenSellerAttemptsToReadAnotherSellersSites() {
    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    Optional<String> qt, fetch;
    Optional<List<String>> siteTypesOpt, statusOpt;
    qt = fetch = Optional.empty();
    siteTypesOpt = statusOpt = Optional.empty();
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> siteService.getSites(SELLER_PID, pageable, qt, siteTypesOpt, statusOpt, fetch));

    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldGetSitesForSellerPid() {
    // given
    when(siteRepository.findAll(nullable(Specification.class), eq(pageable)))
        .thenReturn(pagedEntity);

    // when
    Page<SiteDTO> returnedPage =
        siteService.getSites(
            SELLER_PID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.of(List.of()),
            Optional.empty());

    // then
    assertEquals(pagedSite.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
  }

  @Test
  void shouldGetSitesForSellerSeatPid() {
    // given
    when(siteRepository.findAll(nullable(Specification.class), eq(pageable)))
        .thenReturn(pagedEntity);

    // when
    Page<SiteDTO> returnedPage =
        siteService.getSitesForSellerSeat(
            SELLER_SEAT_PID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.of(List.of()),
            Optional.empty());

    // then
    assertEquals(pagedSite.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
  }

  @Test
  void shouldGetLimitedSitesForSellerSeatPid() {
    // given
    when(siteRepository.findLimitedSiteBySellerSeatPid(SELLER_SEAT_PID, pageable))
        .thenReturn(pagedEntity);

    // when
    Page<SiteDTO> returnedPage =
        siteService.getSitesForSellerSeat(
            SELLER_SEAT_PID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.of(List.of()),
            Optional.of("limited"));

    // then
    assertEquals(pagedSite.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
  }

  @Test
  void shouldGetMinimalSitesForSellerSeat() {
    // given
    SiteView siteView = new SiteView(1L, "foo", Status.ACTIVE, "bar", "qux");
    Page<SiteView> pagedSites = new PageImpl<>(List.of(siteView));
    when(siteViewRepository.findAllSellerSitesForSellerSeat(SELLER_SEAT_PID, pageable))
        .thenReturn(pagedSites);

    // when
    Page<SiteDTO> result =
        siteService.getSiteMinimalDataForSellerSeat(SELLER_SEAT_PID, null, pageable);

    // then
    assertEquals(1, result.getContent().size());
    SiteDTO siteDto = result.getContent().get(0);
    assertEquals(siteView.getPid(), siteDto.getPid());
    assertEquals(siteView.getName(), siteDto.getName());
    assertEquals(siteView.getCompany().getName(), siteDto.getCompanyName());
    assertEquals(siteView.getUrl(), siteDto.getUrl());
  }

  @Test
  void shouldGetMinimalSitesForSellerSeatWithNameSearch() {
    // given
    SiteView siteView = new SiteView(1L, "foo", Status.ACTIVE, "bar", "qux");
    Page<SiteView> pagedSites = new PageImpl<>(List.of(siteView));
    when(siteViewRepository.searchSellerSitesByNameForSellerSeat(SELLER_SEAT_PID, "foo", pageable))
        .thenReturn(pagedSites);

    // when
    Page<SiteDTO> result =
        siteService.getSiteMinimalDataForSellerSeat(SELLER_SEAT_PID, "foo", pageable);

    // then
    assertEquals(1, result.getContent().size());
    SiteDTO siteDto = result.getContent().get(0);
    assertEquals(siteView.getPid(), siteDto.getPid());
    assertEquals(siteView.getName(), siteDto.getName());
    assertEquals(siteView.getCompany().getName(), siteDto.getCompanyName());
    assertEquals(siteView.getUrl(), siteDto.getUrl());
  }

  @Test
  void shouldThrowExceptionsWhenSitesIsNull() {
    when(siteRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(null);
    Optional<String> qt = Optional.empty();
    Optional<List<String>> siteTypesOpt = Optional.empty();
    Optional<List<String>> statusOpt =
        Optional.of(io.vavr.collection.List.<String>empty().toJavaList());
    Optional<String> fetch = Optional.empty();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> siteService.getSites(SELLER_PID, pageable, qt, siteTypesOpt, statusOpt, fetch));
    assertEquals(ServerErrorCodes.SERVER_ERROR_FETCHING_SITES, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionsWhenSitesIsNullOnSearch() {
    when(siteRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(null);
    Optional<String> qtOpt = Optional.empty();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> siteService.searchSitesAndPositionsForSeller(SELLER_PID, qtOpt, pageable));
    assertEquals(ServerErrorCodes.SERVER_ERROR_FETCHING_SITES, exception.getErrorCode());
  }

  @Test
  void testGetSitesWithFetchLimited() {
    when(siteRepository.findLimitedSiteByCompanyPid(123L, pageable)).thenReturn(pagedEntity);
    Page<SiteDTO> returnedPage =
        siteService.getSites(
            SELLER_PID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.of(io.vavr.collection.List.<String>empty().toJavaList()),
            Optional.of("limited"));
    assertEquals(pagedSite.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
  }

  @Test
  void testGetSitesWithWrongFetchFieldValue() {
    when(siteRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);
    Page<SiteDTO> returnedPage =
        siteService.getSites(
            SELLER_PID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.of(io.vavr.collection.List.<String>empty().toJavaList()),
            Optional.of("abc"));
    assertEquals(pagedSite.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
  }

  @Test
  void testGetSitesWithNullFetchFieldValue() {
    when(siteRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);
    Page<SiteDTO> returnedPage =
        siteService.getSites(
            SELLER_PID,
            pageable,
            Optional.empty(),
            Optional.empty(),
            Optional.of(io.vavr.collection.List.<String>empty().toJavaList()),
            Optional.ofNullable(null));
    assertEquals(pagedSite.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
  }

  @Test
  void testGetSitesForSellers() {
    when(siteRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);
    Page<SiteDTO> returnedPage = siteService.getSites("companyPid", Sets.newSet(1L, 2L), pageable);
    assertEquals(pagedSite.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
  }

  @Test
  void testGetSitesForSellersWithNullSellerIdSet() {
    assertThrows(GenevaValidationException.class, () -> siteService.getSites(null, null, pageable));
  }

  @Test
  void testGetSitesForSellersWithEmptySellerIdSet() {
    Set<Long> qt = Sets.newSet();
    assertThrows(
        GenevaValidationException.class, () -> siteService.getSites("companyPid", qt, pageable));
  }

  @Test
  void shouldReturnAllSitesByName() {
    // given
    when(siteRepository.findAll((Specification<Site>) any(), (Pageable) any()))
        .thenReturn(pagedEntity);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll("name", Arrays.asList("S1"));

    // when
    Page<SiteDTO> returnedPage =
        siteService.getSites(new SiteQueryParams(map, SearchQueryOperator.AND), pageable);

    // then
    assertEquals(pagedSite.getContent(), returnedPage.getContent());
  }

  @Test
  void shouldThrowsExceptionWhenPidIsNotNumeric() {
    // given
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll("pid", Arrays.asList("S1"));
    SiteQueryParams siteQueryParams = new SiteQueryParams(map, SearchQueryOperator.AND);

    // when & then
    assertThrows(
        NumberFormatException.class, () -> siteService.getSites(siteQueryParams, pageable));
  }

  @Test
  void shouldThrowsExceptionWhenCompanyPidIsNotNumeric() {
    // given
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll("companyPid", Arrays.asList("S1"));
    SiteQueryParams siteQueryParams = new SiteQueryParams(map, SearchQueryOperator.AND);

    // when & then
    assertThrows(
        NumberFormatException.class, () -> siteService.getSites(siteQueryParams, pageable));
  }

  @Test
  void shouldReturnAllSitesByCompanyName() {
    // given
    when(siteRepository.findAll((Specification<Site>) any(), (Pageable) any()))
        .thenReturn(pagedEntity);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll("companyName", Arrays.asList("companyName123"));

    // when
    Page<SiteDTO> returnedPage =
        siteService.getSites(new SiteQueryParams(map, SearchQueryOperator.AND), pageable);

    // then
    assertEquals(pagedSite.getContent(), returnedPage.getContent());
  }

  @Test
  void shouldReturnAllSitesByNameAndGlobalAliasNameAndPidAndCompanyPid() {
    // given
    when(siteRepository.findAll((Specification<Site>) any(), (Pageable) any()))
        .thenReturn(pagedEntity);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll("name", Arrays.asList("123"));
    map.addAll("pid", Arrays.asList("123"));
    map.addAll("companyPid", Arrays.asList("123"));
    map.addAll("globalAliasName", Arrays.asList("123"));
    map.addAll("companyName", Arrays.asList("123"));

    // when
    Page<SiteDTO> returnedPage =
        siteService.getSites(new SiteQueryParams(map, SearchQueryOperator.AND), pageable);

    // then
    assertEquals(pagedSite.getContent(), returnedPage.getContent());
  }

  @Test
  void shouldReturnAllSitesByCompanyPidForBuyerUser() {
    // given
    when(userContext.isOcUserBuyer()).thenReturn(true);
    when(userContext.getCompanyPids()).thenReturn(Sets.newSet(123L, 2L));
    when(siteRepository.findAll((Specification<Site>) any(), (Pageable) any()))
        .thenReturn(pagedEntity);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll("companyPid", Arrays.asList("123"));

    // when
    Page<SiteDTO> returnedPage =
        siteService.getSites(new SiteQueryParams(map, SearchQueryOperator.AND), pageable);

    // then
    assertEquals(pagedSite.getContent(), returnedPage.getContent());
  }

  @Test
  void shouldGetAllSellerSites() {
    when(siteViewRepository.findAllSellerSites(SELLER_PID, pageable)).thenReturn(pagedSiteView);

    var out = siteService.getSiteMinimalData(SELLER_PID, null, pageable);
    assertNotNull(out);
    assertEquals(1, out.getTotalElements());
    var siteData = out.iterator().next();
    assertEquals(1L, siteData.getPid());
    assertEquals("site name", siteData.getName());
  }

  @Test
  void shouldReturnSellerSitesMatchingName() {
    when(siteViewRepository.searchSellerSitesByName(SELLER_PID, "name", pageable))
        .thenReturn(pagedSiteView);
    var out = siteService.getSiteMinimalData(SELLER_PID, "name", pageable);

    assertNotNull(out);
    assertEquals(1, out.getTotalElements());
    var siteData = out.iterator().next();
    assertEquals(1L, siteData.getPid());
    assertEquals("site name", siteData.getName());
  }

  @Test
  void shouldReturnSitesAndPositionsForSeller() {
    // given
    when(siteRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);

    // when
    Page<SiteDTO> returnedPage =
        siteService.searchSitesAndPositionsForSeller(SELLER_PID, Optional.empty(), pageable);

    // then
    assertEquals(pagedSite.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
  }
}
