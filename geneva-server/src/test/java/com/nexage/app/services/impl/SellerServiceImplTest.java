package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static com.nexage.app.web.support.TestObjectsFactory.gimme;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.dto.SiteSummaryDTO;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.SellerAdSourceRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.admin.core.sparta.jpa.model.SellerAdSource;
import com.nexage.app.dto.SiteDealTermSummaryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Log4j2
@ExtendWith(MockitoExtension.class)
class SellerServiceImplTest {

  private static final String PLACEHOLDER_VERSION = "@@VERSION@@";
  private static final String PLACEHOLDER_MACRO = "@@MACRO@@";
  private static final String REQUEST_VALID =
      "{\"native\": {"
          + PLACEHOLDER_VERSION
          + "\"layout\": 4, \"assets\": [{\"id\": 1, \"required\": 1, \"title\": {\"len\": 30, \"ext\": {\"test1\": \"@@MACRO@@\", \"test2\": \"@@MACRO@@\"}}}, {\"id\": 2, \"required\": 0, \"data\": {\"type\": 2, \"len\": 100}}]}}";

  @Mock private SiteRepository siteRepository;
  @Mock private SellerAttributesRepository sellerAttributesRepository;
  @Mock private UserContext userContext;
  @Mock private CompanyRepository companyRepository;
  @Mock private UserRepository userRepository;

  @Mock private SellerAdSourceRepository sellerAdSourceRepository;

  @InjectMocks private SellerServiceImpl sellerService;
  @InjectMocks private SellerSiteServiceImpl sellerSiteService;

  @Test
  void testProcessNativeRequestMacros() {
    String inputRequest = replacePlaceholders(REQUEST_VALID, null);
    String actualRequest = sellerService.processNativeRequestMacros(inputRequest);

    String expectedRequest = replacePlaceholdersNoMacros(REQUEST_VALID, null);

    assertEquals(expectedRequest, actualRequest);
  }

  @Test
  void getAllowedSitesForUser_userNotFound() {
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerSiteService.getAllowedSitesForUser(1L));

    assertEquals(CommonErrorCodes.COMMON_USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void getAllowedSitesForUser_userFound() {
    // given
    Long userId = 1L;
    Company c1 = createCompany(CompanyType.SELLER);
    Company c2 = createCompany(CompanyType.SELLER);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, c1, c2);
    user.setPid(userId);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    List<SiteSummaryDTO> expectedResult = singletonList(new SiteSummaryDTO());
    given(
            siteRepository.findSummaryDtosByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                userId, ImmutableSet.of(c1.getPid(), c2.getPid())))
        .willReturn(expectedResult);

    // when
    List<SiteSummaryDTO> result = sellerSiteService.getAllowedSitesForUser(1L);

    // then
    assertEquals(expectedResult, result);
  }

  @Test
  void updateSiteDealTermsNoCurrentDeal() {
    Company testCompany = TestObjectsFactory.createCompany(CompanyType.SELLER);
    SellerAttributes attribs = new SellerAttributes();
    attribs.setRevenueShare(new BigDecimal(0.2));
    testCompany.setSellerAttributes(attribs);
    given(companyRepository.findById(testCompany.getPid())).willReturn(Optional.of(testCompany));
    given(sellerAttributesRepository.existsBySellerPid(testCompany.getPid())).willReturn(true);

    Site mockSite = new Site();
    mockSite.setPid(10L);
    mockSite.setCurrentDealTerm(null);
    mockSite.setCompanyPid(testCompany.getPid());
    given(siteRepository.findByPid(mockSite.getPid())).willReturn(Optional.of(mockSite));
    given(userContext.canAccessSite(mockSite.getPid())).willReturn(true);

    List<SiteDealTermSummaryDTO> out =
        sellerSiteService.updateSiteDealTermsToPubDefault(
            testCompany.getPid(), Arrays.asList(mockSite.getPid()));
    assertEquals(1, out.size());
    assertEquals(testCompany.getSellerAttributes().getRevenueShare(), out.get(0).getRevShare());
  }

  @Test
  void shouldThrowWhenNotExistingSellerPidIsGiven_getAllSitesSummaryByCompanyPid() {
    final long companyPid = 20L;
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSiteService.getAllSitesSummaryByCompanyPid(companyPid));

    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldReturnSiteSummaryWhenValidSellerPidIsGiven_getAllSitesSummaryByCompanyPid() {
    final long companyPid = 20L;
    given(companyRepository.existsById(companyPid)).willReturn(true);
    List<SiteSummaryDTO> siteSummaries = gimme(2, SiteSummaryDTO.class);
    given(siteRepository.findSummaryDtosByCompanyPidWithStatusNotDeleted(companyPid))
        .willReturn(siteSummaries);

    // when
    List<SiteSummaryDTO> result = sellerSiteService.getAllSitesSummaryByCompanyPid(companyPid);

    // then
    assertEquals(siteSummaries, result);
  }

  @Test
  void shouldReturnSellerAdSourceList() {
    SellerAdSource sellerAdSource = new SellerAdSource();
    given(sellerAdSourceRepository.findAllBySellerPid(1L)).willReturn(List.of(sellerAdSource));
    assertEquals(List.of(sellerAdSource), sellerService.getAllAdsourceDefaults(1L));
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

  private static String replacePlaceholders(String nativeRequest, String nativeVersion) {
    String request = replaceVersionPlaceholder(nativeRequest, nativeVersion);

    request =
        request.replace(
            PLACEHOLDER_MACRO,
            "#if($natreq && $natreq != \"\" && $natreq && $natreq != \"\")nat=$natreq&natver=$natver#{end}");

    return request;
  }

  private static String replacePlaceholdersNoMacros(String nativeRequest, String nativeVersion) {
    String request = replaceVersionPlaceholder(nativeRequest, nativeVersion);

    request = request.replace(PLACEHOLDER_MACRO, "");

    return request;
  }

  private static String replaceVersionPlaceholder(String rawRequest, String nativeVersion) {
    String versionJson;
    if (StringUtils.isNotBlank(nativeVersion)) {
      versionJson = "\"ver\": \"" + nativeVersion + "\", ";
    } else {
      versionJson = "";
    }

    String request = rawRequest.replace(PLACEHOLDER_VERSION, versionJson);

    return request;
  }

  @Test
  void shouldCallRepositoryMethodWhenSellerAdSourceExistsCalled() {
    sellerService.existsSellerAdSource(1L, 3L);
    verify(sellerAdSourceRepository).existsBySellerPidAndAdSourcePid(1L, 3L);
  }

  @Test
  void shouldCallRepositoryMethodWhenSaveSellerAdSourceCalled() {
    SellerAdSource mock = mock(SellerAdSource.class);
    sellerService.saveSellerAdSource(mock);
    verify(sellerAdSourceRepository).save(mock);
  }

  @Test
  void shouldCallRepositoryMethodWhenGetSellerAdSourceBySellerPidAndAdSourcePidCalled() {
    sellerService.getSellerAdSourceBySellerPidAndAdSourcePid(1L, 3L);
    verify(sellerAdSourceRepository).findBySellerPidAndAdSourcePid(1L, 3L);
  }

  @Test
  void shouldCallRepositoryMethodWhenDeleteSellerAdSourceBySellerPidAndAdSourcePidCalled() {
    sellerService.deleteSellerAdSourceBySellerPidAndAdSourcePid(1L, 3L);
    verify(sellerAdSourceRepository).deleteBySellerPidAndAdSourcePid(1L, 3L);
  }
}
