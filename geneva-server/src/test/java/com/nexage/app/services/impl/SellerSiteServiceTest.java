package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.dto.SiteSummaryDTO;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.app.dto.SiteDealTermSummaryDTO;
import com.nexage.app.dto.SiteUpdateInfoDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.PositionValidator;
import com.nexage.app.util.RTBProfileUtil;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class SellerSiteServiceTest {

  @Mock private UserContext userContext;
  @Mock private CompanyRepository companyRepository;
  @Mock private SiteRepository siteRepository;
  @Mock private UserRepository userRepository;
  @Mock private RTBProfileRepository rtbProfileRepository;
  @Mock private SellerAttributesRepository sellerAttributesRepository;
  @Mock private PositionValidator positionValidator;
  @Mock private RTBProfileUtil rtbProfileUtil;
  @Mock private EntityManager entityManager;

  @InjectMocks private SellerSiteServiceImpl sellerSiteService;

  @Test
  void shouldGetSite() {
    // given
    Site site = getSite();
    given(siteRepository.findByPid(1L)).willReturn(Optional.of(site));

    // when
    Site returnedSite = sellerSiteService.getSite(1L);

    // then
    assertEquals(site, returnedSite);
  }

  @Test
  void shouldThrowResourceNotFoundExceptionWhenGetNotExistingSite() {
    // given
    given(siteRepository.findByPid(10L)).willReturn(Optional.empty());

    // when
    var exception =
        assertThrows(GenevaValidationException.class, () -> sellerSiteService.getSite(10L));

    // then
    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldGetAllSitesSummary() {
    // given
    SiteSummaryDTO siteSummaryDTO =
        SiteSummaryDTO.builder()
            .id("testId")
            .name("Test Site Summary")
            .status(Status.ACTIVE)
            .build();
    List<SiteSummaryDTO> siteSummaryDTOList = new ArrayList<>();
    siteSummaryDTOList.add(siteSummaryDTO);
    given(siteRepository.findSummaryDtosWithStatusNotDeleted()).willReturn(siteSummaryDTOList);

    // when
    List<SiteSummaryDTO> returnedList = sellerSiteService.getAllSitesSummary();

    // then
    assertEquals(siteSummaryDTOList, returnedList);
  }

  @Test
  void shouldGetAllSitesSummaryByCompanyPid() {
    // given
    SiteSummaryDTO siteSummaryDTO =
        SiteSummaryDTO.builder()
            .id("testId")
            .name("Test Site Summary")
            .status(Status.ACTIVE)
            .build();
    List<SiteSummaryDTO> siteSummaryDTOList = new ArrayList<>();
    siteSummaryDTOList.add(siteSummaryDTO);
    given(companyRepository.existsById(anyLong())).willReturn(true);
    given(siteRepository.findSummaryDtosByCompanyPidWithStatusNotDeleted(anyLong()))
        .willReturn(siteSummaryDTOList);

    // when
    List<SiteSummaryDTO> returnedList = sellerSiteService.getAllSitesSummaryByCompanyPid(10L);

    // then
    assertEquals(siteSummaryDTOList, returnedList);
  }

  @Test
  void testGetAllSitesSummaryByCompanyPidCompanyNotFound() {
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSiteService.getAllSitesSummaryByCompanyPid(10L));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void testCreateSite() {
    Site site = getSite();

    SiteDealTerm siteDealTerm = new SiteDealTerm();
    siteDealTerm.setSite(site);
    siteDealTerm.setPid(123L);

    site.setCurrentDealTerm(siteDealTerm);

    Company company = new Company();
    company.setName("test company");

    Site expectedSite = getSite();
    expectedSite.setCurrentDealTerm(siteDealTerm);
    expectedSite.setCompany(company);

    given(companyRepository.findById(anyLong())).willReturn(Optional.of(company));
    given(siteRepository.save(any(Site.class))).willReturn(expectedSite);

    Site returnedSite = sellerSiteService.createSite(10L, site);

    assertEquals(expectedSite, returnedSite);
  }

  @Test
  void testCreateSiteCompanyPidDoesNotMatch() {
    Site site = getSite();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerSiteService.createSite(100L, site));
    assertEquals(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH, exception.getErrorCode());
  }

  @Test
  void testCreateSiteCompanyNotFound() {
    Site site = getSite();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerSiteService.createSite(10L, site));
    assertEquals(ServerErrorCodes.SERVER_CREATE_SITE_COMPANY_MISSING, exception.getErrorCode());
  }

  @Test
  void testCreateSiteCurrentDealTermNulled() {
    Site site = getSite();
    Company company = new Company();
    given(companyRepository.findById(anyLong())).willReturn(Optional.of(company));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerSiteService.createSite(10L, site));
    assertEquals(ServerErrorCodes.SERVER_DEAL_TERM_REQUIRED_ON_CREATE, exception.getErrorCode());
  }

  @Test
  void testGetAllSiteDealTerms() {
    Site site = getSite();

    SiteDealTerm siteDealTerm = new SiteDealTerm();
    siteDealTerm.setPid(1L);
    siteDealTerm.setSite(site);
    siteDealTerm.setRtbFee(BigDecimal.ONE);
    siteDealTerm.setNexageRevenueShare(BigDecimal.ZERO);

    site.setCurrentDealTerm(siteDealTerm);

    List<Site> sites = new ArrayList<>();
    sites.add(site);

    List<SiteDealTermSummaryDTO> siteDealTermSummaryDTOS = new ArrayList<>();

    SiteDealTermSummaryDTO siteDealTermSummaryDTO =
        new SiteDealTermSummaryDTO.Builder()
            .setSiteName("geneva-test")
            .setRtbFee(BigDecimal.ONE)
            .setRevShare(BigDecimal.ZERO)
            .build();

    siteDealTermSummaryDTOS.add(siteDealTermSummaryDTO);

    given(siteRepository.findAll(any(Specification.class))).willReturn(sites);
    given(userContext.doSameOrNexageAffiliation(anyLong())).willReturn(true);

    List<SiteDealTermSummaryDTO> returnedList = sellerSiteService.getAllSiteDealTerms(10L);

    SiteDealTermSummaryDTO returnedDTO = returnedList.iterator().next();
    SiteDealTermSummaryDTO expectedDTO = siteDealTermSummaryDTOS.iterator().next();

    assertEquals(expectedDTO.getRevShare(), returnedDTO.getRevShare());
    assertEquals(expectedDTO.getRtbFee(), returnedDTO.getRtbFee());
    assertEquals(expectedDTO.getSiteName(), returnedDTO.getSiteName());
  }

  @Test
  void testGetAllSiteDealTermsNullSellerPid() {

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerSiteService.getAllSiteDealTerms(null));
    assertEquals(ServerErrorCodes.SERVER_EMPTY_SELLER_PID, exception.getErrorCode());
  }

  @Test
  void testGetAllSoteDealTermsUserNotAuthorized() {

    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> sellerSiteService.getAllSiteDealTerms(1L));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void testGetAllSitesByCompanyPid() {
    List<Site> sites = new ArrayList<>();

    Site site1 = getSite();

    Site site2 = getSite();
    site2.setPid(2L);

    sites.add(site1);
    sites.add(site2);

    given(siteRepository.findAll(any(Specification.class))).willReturn(sites);

    List<Site> returnedSites = sellerSiteService.getAllSitesByCompanyPid(10L);

    assertEquals(sites, returnedSites);
  }

  @Test
  void shouldProcessUpdateSiteRequest() {
    // given
    Site site = getSite();
    given(siteRepository.findByPid(any())).willReturn(Optional.of(site));

    // when
    SiteUpdateInfoDTO returnedSiteUpdateInfoDTO = sellerSiteService.processUpdateSiteRequest(site);

    // then
    assertEquals("cfcd208495d565ef66e7dff9f98764da", returnedSiteUpdateInfoDTO.getTxId());
  }

  @Test
  void shouldUpdateSite() {
    // given
    Site site = getSite();
    Site expectedSite = getSite();
    expectedSite.setName("update site");
    given(siteRepository.findByPid(any())).willReturn(Optional.of(site));
    given(siteRepository.saveAndFlush(any(Site.class))).willReturn(expectedSite);

    // when
    Site returnedSite = sellerSiteService.updateSite(site);

    // then
    assertEquals(expectedSite, returnedSite);
  }

  @Test
  void shouldThrowResourceNotFoundExceptionWhenDeleteNonExistingSite() {
    // given
    given(siteRepository.findByPid(any())).willReturn(Optional.empty());

    // when
    var exception =
        assertThrows(GenevaValidationException.class, () -> sellerSiteService.deleteSite(1L));

    // then
    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldDeleteSite() {
    // given
    Site site = getSite();
    given(siteRepository.findByPid(any())).willReturn(Optional.of(site));

    // when
    sellerSiteService.deleteSite(site.getPid());

    // then
    verify(siteRepository).delete(site);
  }

  @Test
  void shouldReturnSiteDealTermSummaryDTOListWhenRoleIsNotYieldManager() {
    // Given
    Company company = new Company();
    company.setSellerAttributes(new SellerAttributes());

    Site siteOne = createSite(1L, null, 1L, null);
    Site siteTwo = createSite(1L, null, 2L, null);
    Site siteThree = createSite(1L, null, 3L, null);

    List<Long> sitePids = new ArrayList<>();
    sitePids.add(siteOne.getPid());
    sitePids.add(siteTwo.getPid());
    sitePids.add(siteThree.getPid());

    given(sellerAttributesRepository.existsBySellerPid(1L)).willReturn(true);
    given(companyRepository.findById(1L)).willReturn(Optional.of(company));
    given(userContext.canAccessSite(anyLong())).willReturn(true);
    given(siteRepository.findByPid(1L)).willReturn(Optional.of(siteOne));
    given(siteRepository.findByPid(2L)).willReturn(Optional.of(siteTwo));
    given(siteRepository.findByPid(3L)).willReturn(Optional.of(siteThree));

    // When
    List<SiteDealTermSummaryDTO> siteDealTermSummaryDTOList =
        sellerSiteService.updateSiteDealTermsToPubDefault(1L, sitePids);

    // Then
    assertEquals(3, siteDealTermSummaryDTOList.size());
    assertEquals(1L, siteDealTermSummaryDTOList.get(0).getSitePid());
    assertEquals(2L, siteDealTermSummaryDTOList.get(1).getSitePid());
    assertEquals(3L, siteDealTermSummaryDTOList.get(2).getSitePid());
  }

  @Test
  void shouldThrowNotFoundWhenSellerPidDoesNotExist() {
    when(sellerAttributesRepository.existsBySellerPid(anyLong())).thenReturn(false);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSiteService.updateSiteDealTermsToPubDefault(1L, null));
    assertEquals(ServerErrorCodes.SERVER_SELLER_ATTRIBUTES_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenSellerPidIsNull() {
    List<Long> sitePids = List.of(1L, 2L);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSiteService.updateSiteDealTermsToPubDefault(null, sitePids));
    assertEquals(ServerErrorCodes.SERVER_EMPTY_SELLER_PID, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenSitePidIsNull() {
    when(sellerAttributesRepository.existsBySellerPid(anyLong())).thenReturn(true);
    List<Long> sitePids = new ArrayList<>();
    sitePids.add(null);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSiteService.updateSiteDealTermsToPubDefault(1L, sitePids));
    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowUnauthorizedWhenSiteAccessIsNotAvailable() {
    // given
    Site siteOne = createSite(1L, null, 1L, null);
    List<Long> sitePids = new ArrayList<>();
    sitePids.add(siteOne.getPid());
    given(sellerAttributesRepository.existsBySellerPid(1L)).willReturn(true);

    // then
    var ex =
        assertThrows(
            GenevaSecurityException.class,
            () -> sellerSiteService.updateSiteDealTermsToPubDefault(1L, sitePids));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, ex.getErrorCode());
  }

  @Test
  void shouldReturnSiteDealTermSummaryDTOListWhenRoleIsYieldManager() {
    // Given
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setRevenueShare(new BigDecimal(0.2));
    sellerAttributes.setRtbFee(new BigDecimal(0.01));

    Company company = new Company();
    company.setSellerAttributes(sellerAttributes);

    Site siteOne =
        createSite(
            1L, "SiteOne", 1L, createSiteDealTerm(new BigDecimal(0.8), new BigDecimal(0.04)));

    Site siteTwo =
        createSite(
            1L, "SiteTwo", 2L, createSiteDealTerm(new BigDecimal(0.8), new BigDecimal(0.04)));

    Site siteThree =
        createSite(
            1L, "SiteThree", 3L, createSiteDealTerm(new BigDecimal(0.2), new BigDecimal(0.01)));

    List<Long> sitePids = new ArrayList<>();
    sitePids.add(siteOne.getPid());
    sitePids.add(siteTwo.getPid());
    sitePids.add(siteThree.getPid());

    given(sellerAttributesRepository.existsBySellerPid(1L)).willReturn(true);
    given(companyRepository.findById(1L)).willReturn(Optional.of(company));
    given(userContext.canAccessSite(anyLong())).willReturn(true);
    given(siteRepository.findByPid(1L)).willReturn(Optional.of(siteOne));
    given(siteRepository.findByPid(2L)).willReturn(Optional.of(siteTwo));
    given(siteRepository.findByPid(3L)).willReturn(Optional.of(siteThree));

    // When
    List<SiteDealTermSummaryDTO> siteDealTermSummaryDTOList =
        sellerSiteService.updateSiteDealTermsToPubDefaultByYieldManager(1L, sitePids);

    // Then
    assertEquals(2, siteDealTermSummaryDTOList.size());

    assertEquals("SiteOne", siteDealTermSummaryDTOList.get(0).getSiteName());
    assertEquals(1L, siteDealTermSummaryDTOList.get(0).getSitePid());
    assertEquals(
        company.getSellerAttributes().getRevenueShare(),
        siteDealTermSummaryDTOList.get(0).getRevShare());
    assertEquals(
        company.getSellerAttributes().getRtbFee(), siteDealTermSummaryDTOList.get(0).getRtbFee());

    assertEquals("SiteTwo", siteDealTermSummaryDTOList.get(1).getSiteName());
    assertEquals(2L, siteDealTermSummaryDTOList.get(1).getSitePid());
    assertEquals(
        company.getSellerAttributes().getRevenueShare(),
        siteDealTermSummaryDTOList.get(1).getRevShare());
    assertEquals(
        company.getSellerAttributes().getRtbFee(), siteDealTermSummaryDTOList.get(1).getRtbFee());
  }

  @Test
  void shouldReturnEmptySiteDealTermSummaryDTOList() {
    // Given
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setRevenueShare(new BigDecimal(0.2));
    sellerAttributes.setRtbFee(new BigDecimal(0.01));

    Company company = createCompany(CompanyType.SELLER);
    company.setSellerAttributes(sellerAttributes);

    Site site =
        createSite(
            company.getPid(),
            null,
            1L,
            createSiteDealTerm(new BigDecimal(0.2), new BigDecimal(0.01)));

    given(sellerAttributesRepository.existsBySellerPid(company.getPid())).willReturn(true);
    given(companyRepository.findById(company.getPid())).willReturn(Optional.of(company));
    given(userContext.canAccessSite(1L)).willReturn(true);
    given(siteRepository.findByPid(1L)).willReturn(Optional.of(site));

    // When
    List<SiteDealTermSummaryDTO> siteDealTermSummaryDTOList =
        sellerSiteService.updateSiteDealTermsToPubDefaultByYieldManager(
            company.getPid(), Arrays.asList(1L));

    // Then
    assertEquals(0, siteDealTermSummaryDTOList.size());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenSiteNameIsNotUnique() {
    // given
    long sitePid = 1L;
    long companyPid = 2L;
    String name = "notUniqueName";

    given(siteRepository.existsByPidNotAndCompanyPidAndName(sitePid, companyPid, name))
        .willReturn(true);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSiteService.validateSiteNameUniqueness(sitePid, companyPid, name));

    // then
    assertEquals(ServerErrorCodes.SERVER_DUPLICATE_SITE_NAME, exception.getErrorCode());
  }

  @Test
  void testAssignRTBProfileToSite() {
    Site site = getSite();

    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setSitePid(site.getPid());
    rtbProfile.setSite(site);
    rtbProfile.setExchangeSiteTagId("test-rtb-profile");
    rtbProfile.setPid(1L);

    Set<RTBProfile> rtbProfiles = new HashSet<>();
    rtbProfiles.add(rtbProfile);

    Site expectedSite = getSite();
    expectedSite.setRtbProfiles(rtbProfiles);

    given(siteRepository.findByPid(anyLong())).willReturn(Optional.of(site));
    given(rtbProfileRepository.findByDefaultRtbProfileOwnerCompanyPidAndPid(anyLong(), anyLong()))
        .willReturn(Optional.of(rtbProfile));

    sellerSiteService.assignRTBProfileToSite(site.getPid(), 1L, 10L);

    given(siteRepository.findByPid(anyLong())).willReturn(Optional.of(expectedSite));

    Site returnedSite = sellerSiteService.getSite(expectedSite.getPid());

    assertEquals(
        expectedSite.getRtbProfiles().iterator().next(),
        returnedSite.getRtbProfiles().iterator().next());
  }

  @Test
  void shouldRetrieveSiteWhenPublisherPidIsValidated() {
    // given
    long sitePid = 1L;
    long publisherPid = 1L;
    Site site = new Site();
    site.setCompanyPid(publisherPid);
    given(siteRepository.findByPid(anyLong())).willReturn(Optional.of(site));

    // when
    Site result = sellerSiteService.getValidatedSiteForPublisher(sitePid, publisherPid);

    // then
    assertEquals(site, result);
  }

  @Test
  void shouldThrowGenevaValidationExceptionOnPublisherPidMismatch() {
    // given
    long sitePid = 1L;
    long publisherPid = 1L;
    Site site = new Site();
    site.setCompanyPid(publisherPid);
    given(siteRepository.findByPid(anyLong())).willReturn(Optional.of(site));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSiteService.getValidatedSiteForPublisher(sitePid, publisherPid + 1));

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_PUBLISHER_ID, exception.getErrorCode());
  }

  private Site createSite(
      Long companyPid, String siteName, Long sitePid, SiteDealTerm siteDealTerm) {
    Site site = new Site();
    site.setCompanyPid(companyPid);
    site.setName(siteName);
    site.setPid(sitePid);
    site.setCurrentDealTerm(siteDealTerm);
    return site;
  }

  private SiteDealTerm createSiteDealTerm(BigDecimal revenueShare, BigDecimal rtbFee) {
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    siteDealTerm.setNexageRevenueShare(revenueShare);
    siteDealTerm.setRtbFee(rtbFee);
    return siteDealTerm;
  }

  private Site getSite() {
    return getSite(1L);
  }

  private Site getSite(long pid) {
    Site site = new Site();
    site.setId("test-id");
    site.setPid(pid);
    site.setName("geneva-test");
    site.setGroupsEnabled(true);
    site.setCompanyPid(10l);
    return site;
  }
}
