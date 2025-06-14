package com.nexage.app.security;

import static com.nexage.app.web.support.EntitlementsTestUtil.buildOneCentralEntitlements;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.CompanyMdmId;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRestrictedSiteRepository;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.common.security.service.UserDetailsServiceImpl;
import com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class LoginUserContextTest {

  private Company microsoft;
  private Company msnUS;
  private Company nonSellerSeatCompany;
  private User msnUser;
  private User microsoftUser;
  private User nexageUser;
  private User sellerUser;
  @Mock private PositionRepository positionRepository;
  @Mock private SiteRepository siteRepository;
  @Mock private CompanyRuleRepository companyRuleRepository;
  @Mock private UserRestrictedSiteRepository userRestrictedSiteRepository;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;
  @Mock private SpringUserDetails springUserDetails;
  @InjectMocks private LoginUserContext loginUserContext;

  @BeforeEach
  void setUp() {
    microsoft = createCompany(1L);
    msnUS = createCompany(2L);
    nonSellerSeatCompany = createCompany(3L);
    msnUser = createUser(msnUS);
    microsoftUser = createUser(microsoft, msnUS);
    sellerUser = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    nexageUser = createUser(Role.ROLE_USER, CompanyType.NEXAGE);
  }

  @Test
  void shouldDoSameOrNexageAffiliationNexageUser() {
    // given
    loggedUserIs(new UserAuth(nexageUser, List.of()));

    assertTrue(
        loginUserContext.doSameOrNexageAffiliation(microsoftUser),
        "nexage user can access any user");
    Site site = new Site();
    site.setCompany(msnUS);

    // when & then
    assertTrue(loginUserContext.doSameOrNexageAffiliation(site), "nexage user can access any site");
    assertTrue(
        loginUserContext.doSameOrNexageAffiliation(msnUS.getPid()),
        "nexage user can access any company");
  }

  @Test
  void shouldReturnTrueWhenUserHasAccessToSellerSeatAndHasSellerCompanyType() {
    // given
    SellerSeat sellerSeat = TestObjectsFactory.createSellerSeat();
    msnUser.setSellerSeat(sellerSeat);

    loggedUserIs(new UserAuth(msnUser, null));

    // when
    boolean result =
        loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(sellerSeat.getPid());

    // then
    assertTrue(result, "User with the same sellerSeatPid has access");
  }

  @Test
  void shouldReturnFalseWhenUserDoesNotHaveAccessToSellerSeatAndHasSellerCompanyType() {
    // given
    SellerSeat sellerSeat = TestObjectsFactory.createSellerSeat();
    sellerSeat.setPid(123L);
    msnUser.setSellerSeat(sellerSeat);

    Long sellerSeatPid = 456L;

    loggedUserIs(new UserAuth(msnUser, null));

    // when
    boolean result = loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(sellerSeatPid);

    // then
    assertFalse(result, "User with the different sellerSeatPid does not have access");
  }

  @Test
  void shouldReturnTrueWhenUserDoesNotHaveAccessToSellerSeatAndHasNexageCompanyType() {
    // given
    Long sellerSeatPid = 456L;

    loggedUserIs(new UserAuth(nexageUser, null));

    // when
    boolean result = loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(sellerSeatPid);

    // then
    assertTrue(
        result, "User with the different sellerSeatPid and nexage company type does have access");
  }

  @Test
  void doSameOrNexageAffiliation() {
    // given
    loggedUserIs(new UserAuth(microsoftUser, null));

    // when & then
    assertTrue(
        loginUserContext.doSameOrNexageAffiliation(msnUser),
        "seller seat user can operate on dependent seller company");
  }

  @Test
  void doSameOrNexageAffiliation1() {
    // given
    loggedUserIs(new UserAuth(msnUser, null));

    // when & then
    assertFalse(
        loginUserContext.doSameOrNexageAffiliation(microsoftUser),
        "seller company user can operate on parent seller seat company");
  }

  @Test
  void doSameOrNexageAffiliation2() {
    // given
    Site site = new Site();
    site.setCompany(msnUS);

    loggedUserIs(new UserAuth(microsoftUser, null));

    // when & then
    assertTrue(
        loginUserContext.doSameOrNexageAffiliation(site),
        "seller seat company user can operate on dependent site");
  }

  @Test
  void doSameOrNexageAffiliation3() {
    // given
    Site site = new Site();
    site.setCompany(microsoft);

    loggedUserIs(new UserAuth(msnUser, null));

    // when & then
    assertFalse(
        loginUserContext.doSameOrNexageAffiliation(site),
        "seller company user cannot operate on parent seller seat site");
  }

  @Test
  void isPublisherSelfServeEnabled_sellerSeatUserCanAccessSelfServeIfDependentSellerCan() {
    // given
    enableSelfServe(msnUS);
    enableSelfServe(nonSellerSeatCompany);
    loggedUserIs(new UserAuth(microsoftUser, null));

    // when & then
    assertTrue(
        loginUserContext.isPublisherSelfServeEnabled(msnUS.getPid()),
        "seller user is allowed to access self serve if it's enabled for a company");
    assertFalse(
        loginUserContext.isPublisherSelfServeEnabled(microsoft.getPid()),
        "seller user is not allowed to access self serve if it's disabled for a company");
    assertFalse(
        loginUserContext.isPublisherSelfServeEnabled(microsoft.getPid()),
        "seller user is not allowed to access self serve if querying for company that has not been assigned");
  }

  @Test
  void
      isPublisherSelfServeEnabledNexageUser_sellerSeatUserCanAccessSelfServeIfDependentSellerCan() {
    // given
    enableSelfServe(msnUS);
    enableSelfServe(nonSellerSeatCompany);
    loggedUserIs(new UserAuth(nexageUser, buildOneCentralEntitlements(nexageUser)));

    // when & then
    assertTrue(
        loginUserContext.isPublisherSelfServeEnabled(msnUS.getPid()),
        "seller user is allowed to access self serve if it's enabled for a company");
    assertTrue(
        loginUserContext.isPublisherSelfServeEnabled(microsoft.getPid()),
        "nexage user is allowed to access self serve if it's disabled for a company");
    assertTrue(
        loginUserContext.isPublisherSelfServeEnabled(microsoft.getPid()),
        "nexage user is allowed to access self serve if querying for company that has not been assigned");
  }

  @Test
  void isPublisherSelfServeEnabled_nexageUserCanAccessWhatever() {
    // given
    enableSelfServe(msnUS);
    loggedUserIs(new UserAuth(nexageUser, buildOneCentralEntitlements(nexageUser)));

    // when & then
    assertTrue(
        loginUserContext.isPublisherSelfServeEnabled(msnUS.getPid()),
        "nexage user is allowed to access self serve if it's enabled for a company");
    assertTrue(
        loginUserContext.isPublisherSelfServeEnabled(microsoft.getPid()),
        "nexage user is allowed to access self serve if it's disabled for a company");
  }

  @Test
  void shouldAccessSiteWithNexageUer() {
    // given
    loggedUserIs(new UserAuth(nexageUser, null));

    long anySitePid = 1L;

    // when & then
    assertTrue(loginUserContext.canAccessSite(anySitePid), "nexage user can access any site");
  }

  @Test
  void testCanAccessSite_nonNexageUserHasAccessTo() {
    // given
    loggedUserIs(new UserAuth(microsoftUser, null));

    Site msnUsSite = TestObjectsFactory.createSiteDTO(1).iterator().next();
    msnUsSite.setCompanyPid(msnUS.getPid());
    msnUsSite.setCompany(msnUS);
    Site nonMicrosoftSite = TestObjectsFactory.createSiteDTO(1).iterator().next();

    when(siteRepository.findCompanyPidByPidWithStatusNotDeleted(msnUsSite.getPid()))
        .thenReturn(msnUS.getPid());
    when(siteRepository.findCompanyPidByPidWithStatusNotDeleted(nonMicrosoftSite.getPid()))
        .thenReturn(null);
    when(userRestrictedSiteRepository.findPidByUserIdAndSiteId(
            eq(microsoftUser.getPid()), anyLong()))
        .thenReturn(Optional.empty());

    loginUserContext.siteRepository = siteRepository;
    loginUserContext.userRestrictedSiteRepository = userRestrictedSiteRepository;

    // when & then
    assertTrue(
        loginUserContext.canAccessSite(msnUsSite.getPid()),
        "user can access site assigned to company that user is assigned to");
    assertFalse(
        loginUserContext.canAccessSite(nonMicrosoftSite.getPid()),
        "user cannot access site of other companies");
  }

  @Test
  void testCanNotAccessSite_nonNexageUserRestrictedTo() {
    // given
    loggedUserIs(new UserAuth(microsoftUser, null));

    Site msnUsSite = TestObjectsFactory.createSiteDTO(1).iterator().next();
    msnUsSite.setCompanyPid(msnUS.getPid());
    msnUsSite.setCompany(msnUS);

    when(siteRepository.findCompanyPidByPidWithStatusNotDeleted(msnUsSite.getPid()))
        .thenReturn(msnUS.getPid());
    when(userRestrictedSiteRepository.findPidByUserIdAndSiteId(
            microsoftUser.getPid(), msnUsSite.getPid()))
        .thenReturn(Optional.of(msnUsSite.getPid()));

    loginUserContext.siteRepository = siteRepository;
    loginUserContext.userRestrictedSiteRepository = userRestrictedSiteRepository;

    // when & then
    assertFalse(
        loginUserContext.canAccessSite(msnUsSite.getPid()), "user cannot access restricted site");
  }

  @Test
  void testCanNotAccessSite_SiteCompanyNotFound() {
    // given
    loggedUserIs(new UserAuth(microsoftUser, null));

    Site msnUsSite = TestObjectsFactory.createSiteDTO(1).iterator().next();
    msnUsSite.setCompanyPid(msnUS.getPid());
    msnUsSite.setCompany(msnUS);

    when(siteRepository.findCompanyPidByPidWithStatusNotDeleted(msnUsSite.getPid()))
        .thenReturn(null);

    loginUserContext.siteRepository = siteRepository;
    loginUserContext.userRestrictedSiteRepository = userRestrictedSiteRepository;
    // when & then
    assertFalse(loginUserContext.canAccessSite(msnUsSite.getPid()), "user company not found");
  }

  @Test
  void shouldBeApiUserWhenUserRoleIsApiRole() {
    // given
    User user = createUser(Role.ROLE_API, CompanyType.SELLER);
    loggedUserIs(new UserAuth(user, buildOneCentralEntitlements(user)));
    // when & then
    assertTrue(loginUserContext.isApiUser());
  }

  @Test
  void shouldBeApiUserWhenUserRoleIsNotApiRole() {
    // given
    User apiUser = createUser(Role.ROLE_USER, CompanyType.SELLER);
    loggedUserIs(new UserAuth(apiUser, null));
    // when & then
    assertFalse(loginUserContext.isApiUser());
  }

  @Test
  void testIsApiUserWhenCurrentUserIsNull() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(new Object());
    // when & then
    assertFalse(loginUserContext.isApiUser());
  }

  @Test
  void testIsApiUserWhenCurrentUserRoleIsNull() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    // when & then
    assertFalse(loginUserContext.isApiUser());
  }

  @Test
  void testCanAccessPublisher() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(Collections.singleton(1L));
    // when & then
    assertTrue(loginUserContext.canAccessPublisher(1L));
  }

  @Test
  void testCanAccessPublisherWhenCompanyPidsIsNull() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(null);
    // when & then
    assertFalse(loginUserContext.canAccessPublisher(1L));
  }

  @Test
  void testCanAccessPublisherWhenCompanyPidsMatchPid() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(Collections.singleton(2L));

    // when & then
    assertFalse(loginUserContext.canAccessPublisher(1L));
  }

  @Test
  void testCanAccessSellerResources() {
    // given
    loggedUserIs(new UserAuth(msnUser, null));
    // when & then
    assertFalse(loginUserContext.canAccessSellersResource("companyPid", Sets.newHashSet(1L)));
    assertTrue(loginUserContext.canAccessSellersResource("companyPid", Sets.newHashSet(2L)));
  }

  @Test
  void testCanAccessSellerResourcesAsSellerSeat() {
    // given
    loggedUserIs(new UserAuth(microsoftUser, null));
    // when & then
    assertTrue(loginUserContext.canAccessSellersResource("companyPid", Sets.newHashSet(1L, 2L)));
    assertFalse(loginUserContext.canAccessSellersResource("name", Sets.newHashSet(1L, 2L)));
  }

  @Test
  void testCanAccessSite() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(Collections.singleton(1L));
    when(siteRepository.findCompanyPidByPidWithStatusNotDeleted(anyLong())).thenReturn(1L);
    when(userRestrictedSiteRepository.findPidByUserIdAndSiteId(anyLong(), anyLong()))
        .thenReturn(Optional.empty());
    loginUserContext.siteRepository = siteRepository;

    // when & then
    assertTrue(loginUserContext.canAccessSite(1L));
  }

  @Test
  void testCannotAccessSite() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(Collections.singleton(2L));
    when(siteRepository.findCompanyPidByPidWithStatusNotDeleted(anyLong())).thenReturn(1L);
    loginUserContext.siteRepository = siteRepository;
    // when & then
    assertFalse(loginUserContext.canAccessSite(1L));
  }

  @Test
  void testCanAccessCompanyRules() {
    // given
    CompanyRule companyRule1 = TestObjectsFactory.createCompanyRule();
    CompanyRule companyRule2 = TestObjectsFactory.createCompanyRule();
    companyRule1.setOwnerCompanyPid(1L);
    companyRule1.setOwnerCompanyPid(2L);

    List<CompanyRule> rules = ImmutableList.of(companyRule1, companyRule2);
    Set<Long> ruleIds = ImmutableSet.of(1L, 2L);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(new HashSet<>(Arrays.asList(1L, 2L)));
    loginUserContext.companyRuleRepository = companyRuleRepository;
    when(loginUserContext.getCompanyPids()).thenReturn(new HashSet<>(Arrays.asList(1L, 2L)));
    when(companyRuleRepository.findRulesByPidsAndOwnerCompanyPids(anySet(), anySet()))
        .thenReturn(rules);

    // when & then
    assertTrue(loginUserContext.canAccessCompanyRules(ruleIds));
  }

  @Test
  void testCannotAccessCompanyRules() {
    // given
    CompanyRule companyRule1 = TestObjectsFactory.createCompanyRule();
    List<CompanyRule> rules = ImmutableList.of(companyRule1);
    Set<Long> ruleIds = ImmutableSet.of(1L, 2L);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(new HashSet<>(Arrays.asList(1L, 2L)));
    loginUserContext.companyRuleRepository = companyRuleRepository;
    when(loginUserContext.getCompanyPids()).thenReturn(new HashSet<>(Arrays.asList(1L, 2L)));
    when(companyRuleRepository.findRulesByPidsAndOwnerCompanyPids(anySet(), anySet()))
        .thenReturn(rules);

    // when & then
    assertFalse(loginUserContext.canAccessCompanyRules(ruleIds));
  }

  @Test
  void testCanAccessSingleCompanyRule() {
    // given
    Long rulePid = 1L;
    CompanyRule companyRule1 = TestObjectsFactory.createCompanyRule();
    companyRule1.setOwnerCompanyPid(1L);

    List<CompanyRule> rules = ImmutableList.of(companyRule1);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids())
        .thenReturn(new HashSet<>(Collections.singletonList(1L)));
    loginUserContext.companyRuleRepository = companyRuleRepository;
    when(loginUserContext.getCompanyPids())
        .thenReturn(new HashSet<>(Collections.singletonList(1L)));
    when(companyRuleRepository.findRulesByPidsAndOwnerCompanyPids(anySet(), anySet()))
        .thenReturn(rules);

    // when & then
    assertTrue(loginUserContext.canAccessCompanyRule(rulePid));
  }

  @Test
  void testCannotAccessSingleCompanyRule() {
    // given
    Long rulePid = 1L;
    List<CompanyRule> rules = ImmutableList.of();
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(new HashSet<>());
    loginUserContext.companyRuleRepository = companyRuleRepository;
    when(loginUserContext.getCompanyPids()).thenReturn(new HashSet<>());
    when(companyRuleRepository.findRulesByPidsAndOwnerCompanyPids(anySet(), anySet()))
        .thenReturn(rules);

    // when & then
    assertFalse(loginUserContext.canAccessCompanyRule(rulePid));
  }

  @Test
  void testCannotAccessCompanyRuleWithPlacementCompanyPidIsNull() {
    // given
    CompanyRule companyRule1 = TestObjectsFactory.createCompanyRule();
    List<CompanyRule> rules = ImmutableList.of(companyRule1);
    Set<Long> ruleIds = ImmutableSet.of(1L, 2L);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(new HashSet<>(Arrays.asList(1L, 2L)));
    loginUserContext.companyRuleRepository = companyRuleRepository;
    when(companyRuleRepository.findRulesByPidsAndOwnerCompanyPids(anySet(), anySet()))
        .thenReturn(rules);

    // when & then
    assertFalse(loginUserContext.canAccessCompanyRules(ruleIds));
  }

  @Test
  void testCannotAccessCompanyRuleWithCompanyPidNotIncludeRuleIds() {
    // given
    CompanyRule companyRule1 = TestObjectsFactory.createCompanyRule();
    List<CompanyRule> rules = ImmutableList.of(companyRule1);
    Set<Long> ruleIds = ImmutableSet.of(1L, 2L);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(new HashSet<>(Arrays.asList(3L, 4L)));
    loginUserContext.companyRuleRepository = companyRuleRepository;
    when(companyRuleRepository.findRulesByPidsAndOwnerCompanyPids(anySet(), anySet()))
        .thenReturn(rules);

    // when & then
    assertFalse(loginUserContext.canAccessCompanyRules(ruleIds));
  }

  @Test
  void testCanAccessPlacement() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(Collections.singleton(1L));
    when(positionRepository.findCompanyPidByPlacementPid(anyLong())).thenReturn(1L);
    loginUserContext.positionRepository = positionRepository;

    // when & then
    assertTrue(loginUserContext.canAccessPlacement(1L));
  }

  @Test
  void testCannotAccessPlacement() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(Collections.singleton(2L));
    when(positionRepository.findCompanyPidByPlacementPid(anyLong())).thenReturn(1L);
    loginUserContext.positionRepository = positionRepository;

    // when & then
    assertFalse(loginUserContext.canAccessPlacement(1L));
  }

  @Test
  void testCannotAccessPlacementWithPlacementCompanyPidIsNull() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(positionRepository.findCompanyPidByPlacementPid(anyLong())).thenReturn(null);
    loginUserContext.positionRepository = positionRepository;
    // when & then
    assertFalse(loginUserContext.canAccessPlacement(1L));
  }

  @Test
  void testCannotAccessPlacementWithPlacementCompanyPidIsNotNullAndCompanyPidIsNull() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(Collections.singleton(2L));
    when(positionRepository.findCompanyPidByPlacementPid(anyLong())).thenReturn(1L);
    loginUserContext.positionRepository = positionRepository;
    when(loginUserContext.getCompanyPids()).thenReturn(null);
    // when & then
    assertFalse(loginUserContext.canAccessPlacement(1L));
  }

  @Test
  void testCannotAccessPlacementWithContainsPlacementCompanyPid() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getCompanyPids()).thenReturn(Collections.singleton(2L));
    when(positionRepository.findCompanyPidByPlacementPid(anyLong())).thenReturn(1L);
    loginUserContext.positionRepository = positionRepository;
    when(loginUserContext.getCompanyPids()).thenReturn(new HashSet<>(Arrays.asList(1L, 2L)));
    // when & then
    assertTrue(loginUserContext.canAccessPlacement(1L));
  }

  @Test
  void testCannotAccessPlacementWithNotContainsPlacementCompanyPid() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(positionRepository.findCompanyPidByPlacementPid(anyLong())).thenReturn(1L);
    SpringUserDetails mockUserDetails = mock(SpringUserDetails.class);
    loginUserContext.positionRepository = positionRepository;
    when(loginUserContext.getCurrentUser()).thenReturn(mockUserDetails);
    when(loginUserContext.getCompanyPids()).thenReturn(Collections.emptySet());

    // when & then
    assertFalse(loginUserContext.canAccessPlacement(1L));
  }

  @Test
  void shouldBeNexageAdminOrManagerOnNexageAdmin() {
    // given
    SecurityContextHolder.setContext(securityContext);
    User adminNexage = createUser(Role.ROLE_ADMIN, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(adminNexage, buildOneCentralEntitlements(adminNexage)));

    // when
    boolean nexageAdminOrManagerOrYieldManager = loginUserContext.isNexageAdminOrManager();

    // then
    assertTrue(nexageAdminOrManagerOrYieldManager);
  }

  @Test
  void shouldBeManagerSmartexNexage() {
    // given
    SecurityContextHolder.setContext(securityContext);
    User adminNexage = createUser(Role.ROLE_MANAGER_SMARTEX, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(adminNexage, buildOneCentralEntitlements(adminNexage)));

    // when
    boolean nexageAdminOrManagerOrYieldManager = loginUserContext.isNexageAdminOrManager();

    // then
    assertTrue(nexageAdminOrManagerOrYieldManager);
  }

  @Test
  void shouldBeManagerNexage() {
    // given
    SecurityContextHolder.setContext(securityContext);
    User adminNexage = createUser(Role.ROLE_MANAGER, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(adminNexage, buildOneCentralEntitlements(adminNexage)));

    // when
    boolean nexageAdminOrManagerOrYieldManager = loginUserContext.isNexageAdminOrManager();

    // then
    assertTrue(nexageAdminOrManagerOrYieldManager);
  }

  @Test
  void shouldBeNexageAdminOrManagerOnNexageManager() {
    // given
    SecurityContextHolder.setContext(securityContext);
    User managerNexage = createUser(Role.ROLE_MANAGER, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(managerNexage, buildOneCentralEntitlements(managerNexage)));

    // when
    boolean nexageAdminOrManagerOrYieldManager = loginUserContext.isNexageAdminOrManager();

    // then
    assertTrue(nexageAdminOrManagerOrYieldManager);
  }

  @Test
  void shouldBeNexageAdminOrManagerOnNexageYieldManager() {
    // given
    SecurityContextHolder.setContext(securityContext);
    User managerYield = createUser(Role.ROLE_MANAGER_YIELD, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(managerYield, buildOneCentralEntitlements(managerYield)));

    // when
    boolean nexageAdminOrManagerOrYieldManager = loginUserContext.isNexageAdminOrManager();

    // then
    assertTrue(nexageAdminOrManagerOrYieldManager);
  }

  @Test
  void shouldBeNexageAdminOrManagerOnNexageSmartExchangeManager() {
    // given
    SecurityContextHolder.setContext(securityContext);
    User managerSmartexNexage = createUser(Role.ROLE_MANAGER_SMARTEX, CompanyType.NEXAGE);
    loggedUserIs(
        new UserAuth(managerSmartexNexage, buildOneCentralEntitlements(managerSmartexNexage)));

    // when
    boolean nexageAdminOrManagerOrYieldManagerOrSmartExchangeManager =
        loginUserContext.isNexageAdminOrManager();

    // then
    assertTrue(nexageAdminOrManagerOrYieldManagerOrSmartExchangeManager);
  }

  @Test
  void testIisNexageAdminOrManagerOnNone() {
    boolean nexageAdminOrManagerOrYieldManager = loginUserContext.isNexageAdminOrManager();

    assertFalse(nexageAdminOrManagerOrYieldManager);
  }

  @Test
  void shouldBeNexageUser() {
    // given
    nexageUser.setRole(Role.ROLE_USER);
    loggedUserIs(new UserAuth(nexageUser, null));
    assertFalse(loginUserContext.isNexageAdmin());

    createUser(Role.ROLE_MANAGER, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(nexageUser, null));
    // when & then
    assertFalse(loginUserContext.isNexageAdmin());

    // given
    nexageUser = createUser(Role.ROLE_ADMIN, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(nexageUser, buildOneCentralEntitlements(nexageUser)));
    // when & then
    assertTrue(loginUserContext.isNexageAdmin());
  }

  @Test
  void shouldBeSellerUser() {
    // given
    createUser(Role.ROLE_USER, CompanyType.SELLER);
    loggedUserIs(new UserAuth(sellerUser, null));
    // when & then
    assertFalse(loginUserContext.isSellerAdmin());

    // given
    createUser(Role.ROLE_MANAGER, CompanyType.SELLER);
    loggedUserIs(new UserAuth(sellerUser, null));
    // when & then
    assertFalse(loginUserContext.isSellerAdmin());

    // given
    var sellerUser = createUser(Role.ROLE_ADMIN, CompanyType.SELLER);
    loggedUserIs(new UserAuth(sellerUser, buildOneCentralEntitlements(sellerUser)));
    // when & then
    assertTrue(loginUserContext.isSellerAdmin());
  }

  @Test
  void ShouldBeAdminOrManagerWhenUserIsNull() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(null);
    // when & then
    assertFalse(loginUserContext.isAdminOrManager());
  }

  @Test
  void ShouldBeAdminOrManagerWhenUserRoleIsNull() {
    // given
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(springUserDetails.getRole()).thenReturn(null);

    // when & then
    assertFalse(loginUserContext.isAdminOrManager());
  }

  @Test
  void ShouldBeAdminOrManagerHavingRoleAdmin() {
    // given
    User adminNexage = createUser(Role.ROLE_ADMIN, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(adminNexage, buildOneCentralEntitlements(adminNexage)));
    // when & then
    assertTrue(loginUserContext.isAdminOrManager());
  }

  @Test
  void shouldBeAdminOrManagerHavingRoleManager() {
    // given
    SecurityContextHolder.setContext(securityContext);
    User managerNexage = createUser(Role.ROLE_MANAGER, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(managerNexage, buildOneCentralEntitlements(managerNexage)));
    // when & then
    assertTrue(loginUserContext.isAdminOrManager());
  }

  @Test
  void shouldBeAdminOrManagerHavingRoleManagerYield() {
    // given
    SecurityContextHolder.setContext(securityContext);
    User yieldManagerNexage = createUser(Role.ROLE_MANAGER_YIELD, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(yieldManagerNexage, buildOneCentralEntitlements(yieldManagerNexage)));
    // when & then
    assertTrue(loginUserContext.isAdminOrManager());
  }

  @Test
  void shouldBeAdminOrManagerHavingRoleManagerSmartExchange() {
    // given
    SecurityContextHolder.setContext(securityContext);
    User smartexManagerNexage = createUser(Role.ROLE_MANAGER_SMARTEX, CompanyType.NEXAGE);
    loggedUserIs(
        new UserAuth(smartexManagerNexage, buildOneCentralEntitlements(smartexManagerNexage)));
    // when & then
    assertTrue(loginUserContext.isAdminOrManager());
  }

  @Test
  void shouldEditSmartExchangeWhenHasRoleAdmin() {
    // given
    nexageUser = createUser(Role.ROLE_ADMIN, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(nexageUser, buildOneCentralEntitlements(nexageUser)));
    // when & then
    assertTrue(loginUserContext.canEditSmartExchange());
  }

  @Test
  void shouldEditSmartExchangeWhenHasRoleYieldManager() {
    // given
    nexageUser = createUser(Role.ROLE_MANAGER_YIELD, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(nexageUser, buildOneCentralEntitlements(nexageUser)));
    // when & then
    assertTrue(loginUserContext.canEditSmartExchange());
  }

  @Test
  void shouldEditSmartExchangeWhenHasRoleSmartExchangeManager() {
    // given
    nexageUser = createUser(Role.ROLE_MANAGER_SMARTEX, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(nexageUser, buildOneCentralEntitlements(nexageUser)));
    // when & then
    assertTrue(loginUserContext.canEditSmartExchange());
  }

  @Test
  void shouldNotEditSmartExchangeWhenHasRoleUser() {
    // given
    nexageUser.setRole(Role.ROLE_USER);
    loggedUserIs(new UserAuth(nexageUser, null));
    // when & then
    assertFalse(loginUserContext.canEditSmartExchange());
  }

  @Test
  void shouldReturnFalseAndThrowExceptionWhenUserDoesNotHaveAccessToSellerSeat() {
    // given
    Set<String> qf = new HashSet<>();
    String qt = "456";
    String keyName = "sellerSeatPid";
    qf.add(keyName);
    loggedUserIs(new UserAuth(msnUser, null));

    // when
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> loginUserContext.canAccessSellerSeat(qf, qt));

    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldReturnFalseWhenSellerSeatPidIsNotSet() {
    // given
    loggedUserIs(new UserAuth(msnUser, null));

    // when
    boolean result = loginUserContext.canAccessSellerSeat(new HashSet<>(), null);

    // then
    assertFalse(result, "User with the empty sellerSeatPid has no access");
  }

  @Test
  void shouldReturnTrueWhenSellerSeatPidIsSetAndIsSame() {
    // given
    SellerSeat sellerSeat = TestObjectsFactory.createSellerSeat();
    msnUser.setSellerSeat(sellerSeat);
    loggedUserIs(new UserAuth(msnUser, null));
    Set<String> qf = new HashSet<>();
    String qt = sellerSeat.getPid().toString();
    String keyName = "sellerSeatPid";
    qf.add(keyName);

    // when
    boolean result = loginUserContext.canAccessSellerSeat(qf, qt);

    // then
    assertTrue(result, "User with the same sellerSeatPid has access");
  }

  @Test
  void whenSellerSeatPidValueIsNotLong_thenExceptionIsThrown() {
    // given
    Set<String> qf = new HashSet<>();
    String qt = "invalid";
    String keyName = "sellerSeatPid";
    qf.add(keyName);
    loggedUserIs(new UserAuth(msnUser, null));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> loginUserContext.canAccessSellerSeat(qf, qt));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void whenCompanyPidValueIsNotLong_thenExceptionIsThrown() {
    // given
    Set<String> qf = new HashSet<>();
    qf.add("companyPid");
    String qt = "invalid";
    loggedUserIs(new UserAuth(msnUser, null));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> loginUserContext.canAccessSellerSeat(qf, qt));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldHaveWritePrivilegeOnSailpointUser() {
    // given
    User user = createUser(Role.ROLE_API_IIQ, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(user, buildOneCentralEntitlements(user)));
    SecurityContextHolder.setContext(securityContext);
    // when & then
    assertTrue(loginUserContext.writePrivilegeCheck(user));
  }

  @Test
  void shouldNotHaveWritePrivilegeOnSailpointUser() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    user.setRole(Role.ROLE_API_IIQ);
    loggedUserIs(new UserAuth(user, null));
    SecurityContextHolder.setContext(securityContext);

    // when & then
    assertFalse(loginUserContext.writePrivilegeCheck(user));
  }

  @Test
  void shouldBeIdentityIqUser() {
    // given
    User user = createUser(Role.ROLE_API_IIQ, CompanyType.NEXAGE);
    loggedUserIs(new UserAuth(user, buildOneCentralEntitlements(user)));
    SecurityContextHolder.setContext(securityContext);
    // when & then
    assertTrue(loginUserContext.isInternalIdentityIqUser());
  }

  @Test
  void shouldNotBeIdentityIqUser() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    user.setRole(Role.ROLE_API_IIQ);
    loggedUserIs(new UserAuth(user, null));
    SecurityContextHolder.setContext(securityContext);

    // when & then
    assertFalse(loginUserContext.isInternalIdentityIqUser());
  }

  @Test
  void shouldThrowExceptionOnGettingCurrentUserIfAuthenticationIsNull() {
    // given
    SecurityContextHolder.setContext(securityContext);
    // when
    when(securityContext.getAuthentication()).thenReturn(null);
    // then
    var exception =
        assertThrows(GenevaSecurityException.class, () -> loginUserContext.getCurrentUser());
    assertEquals(SecurityErrorCodes.SECURITY_BAD_PRINCIPAL, exception.getErrorCode());
  }

  @Test
  void shouldHaveAllEntitlements() {
    // given
    var entitlements = createEntitlements("e1", "e2");
    loggedUserIs(new UserAuth(sellerUser, entitlements));
    // when
    var result = loginUserContext.hasEntitlements("e1", "e2");

    // then
    assertTrue(result);
  }

  @Test
  void shouldNotHaveAllEntitlements() {
    // given
    var entitlements = createEntitlements("e1", "e2");
    loggedUserIs(new UserAuth(sellerUser, entitlements));
    // when
    var result = loginUserContext.hasEntitlements("e1", "e2", "e3");

    // then
    assertFalse(result);
  }

  @Test
  void shouldFailOnNoUser() {
    // given
    when(loginUserContext.getCurrentUser()).thenReturn(null);

    // when
    var result = loginUserContext.hasEntitlements("e1", "e2", "e3");

    // then
    assertFalse(result);
  }

  @Test
  void shouldFailOnNoUserEntitlements() {
    // given
    loggedUserIs(new UserAuth(sellerUser, null));
    // when
    var result = loginUserContext.hasEntitlements("e1", "e2", "e3");

    // then
    assertFalse(result);
  }

  @Test
  void shouldFailIsOcAdminNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.NEXAGE entitlement
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminNexage();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcAdminNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.NEXAGE.getValue(),
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminNexage();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcManagerYieldNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.NEXAGE entitlement
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue(),
            OneCentralEntitlement.SMARTEX.getValue(),
            OneCentralEntitlement.YIELD.getValue());

    loggedUserIs(new UserAuth(user, buildOneCentralEntitlements(user)));

    // when
    var result = loginUserContext.isOcManagerYieldNexage();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcManagerYieldNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.NEXAGE.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue(),
            OneCentralEntitlement.SMARTEX.getValue(),
            OneCentralEntitlement.YIELD.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerYieldNexage();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcManagerSmartexNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.NEXAGE entitlement
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue(),
            OneCentralEntitlement.SMARTEX.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerSmartexNexage();

    // then
    assertFalse(result);
  }

  @Test
  void shouldFailIsOcManagerSmartexNexageOnNoUser() {
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> loginUserContext.isOcManagerSmartexNexage());
    assertEquals(SecurityErrorCodes.SECURITY_BAD_PRINCIPAL, exception.getErrorCode());
  }

  @Test
  void shouldPassIsOcManagerSmartexNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.NEXAGE.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue(),
            OneCentralEntitlement.SMARTEX.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerSmartexNexage();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcManagerNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.NEXAGE entitlement
            OneCentralEntitlement.MANAGER.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerNexage();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcManagerNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.NEXAGE.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerNexage();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcUserNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.NEXAGE entitlement
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserNexage();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcUserNexage() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.NEXAGE.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserNexage();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcAdminSeller() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.SELLER entitlement
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminSeller();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcAdminSeller() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.SELLER.getValue(),
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminSeller();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcManagerSeller() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.SELLER entitlement
            OneCentralEntitlement.MANAGER.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerSeller();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcManagerSeller() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.SELLER.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerSeller();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcUserSeller() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.SELLER entitlement
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserSeller();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcUserSeller() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.SELLER.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserSeller();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcAdminBuyer() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.BUYER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.BUYER entitlement
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminBuyer();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcAdminBuyer() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.BUYER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.BUYER.getValue(),
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminBuyer();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcManagerBuyer() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.BUYER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.BUYER entitlement
            OneCentralEntitlement.MANAGER.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerBuyer();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcManagerBuyer() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.BUYER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.BUYER.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerBuyer();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcUserBuyer() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.BUYER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.BUYER entitlement
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserBuyer();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcUserBuyer() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.BUYER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.BUYER.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserBuyer();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcAdminSeatHolder() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SEATHOLDER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.SEAT_HOLDER entitlement
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminSeatHolder();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcAdminSeatHolder() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SEATHOLDER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.SEAT_HOLDER.getValue(),
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminSeatHolder();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcManagerSeatHolder() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SEATHOLDER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.SEAT_HOLDER entitlement
            OneCentralEntitlement.MANAGER.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerSeatHolder();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcManagerSeatHolder() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SEATHOLDER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.SEAT_HOLDER.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerSeatHolder();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcUserSeatHolder() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SEATHOLDER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.SEAT_HOLDER entitlement
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserSeatHolder();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcUserSeatHolder() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SEATHOLDER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.SEAT_HOLDER.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserSeatHolder();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcAdminSellerSeat() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.SELLER_SEAT entitlement
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminSellerSeat();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcAdminSellerSeat() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.SELLER_SEAT.getValue(),
            OneCentralEntitlement.ADMIN.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcAdminSellerSeat();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcManagerSellerSeat() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.SELLER_SEAT entitlement
            OneCentralEntitlement.MANAGER.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerSellerSeat();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcManagerSellerSeat() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.SELLER_SEAT.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcManagerSellerSeat();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcUserSellerSeat() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.SELLER_SEAT entitlement
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserSellerSeat();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcUserSellerSeat() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.SELLER_SEAT.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcUserSellerSeat();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcApiSeller() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.API entitlement
            "dummy");

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcApiSeller();

    // then
    assertFalse(result);
  }

  @Test
  void shouldFailIsOcApi() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.API entitlement
            "dummy");

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcApi();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcApi() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements = createEntitlements(OneCentralEntitlement.API.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcApi();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcApiBuyer() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.API entitlement
            "dummy");

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcApiBuyer();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcApiSeller() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.SELLER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.API.getValue(), OneCentralEntitlement.SELLER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcApiSeller();

    // then
    assertTrue(result);
  }

  @Test
  void shouldPassIsOcApiBuyer() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.BUYER));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.API.getValue(), OneCentralEntitlement.BUYER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcApiBuyer();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcApiIIQ() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.API_IIQ entitlement
            "dummy");

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcApiIIQ();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcApiIIQe() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements = createEntitlements(OneCentralEntitlement.API_IIQ.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcApiIIQ();

    // then
    assertTrue(result);
  }

  @Test
  void shouldFailIsOcDealManager() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            // missing: OneCentralEntitlement.DEAL entitlement
            OneCentralEntitlement.MANAGER.getValue(), OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcDealManager();

    // then
    assertFalse(result);
  }

  @Test
  void shouldPassIsOcDealManager() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    var entitlements =
        createEntitlements(
            OneCentralEntitlement.DEAL.getValue(),
            OneCentralEntitlement.MANAGER.getValue(),
            OneCentralEntitlement.USER.getValue());

    loggedUserIs(new UserAuth(user, entitlements));

    // when
    var result = loginUserContext.isOcDealManager();

    // then
    assertTrue(result);
  }

  @Test
  void shouldGetType() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    loggedUserIs(new UserAuth(user, null));

    // when & then
    assertNotNull(loginUserContext.getType());
  }

  @Test
  void shouldGetUserId() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    loggedUserIs(new UserAuth(user, null));

    // when & then
    assertNotNull(loginUserContext.getUserId());
  }

  @Test
  void shouldHasRole() {
    // given
    User user = createUser(TestObjectsFactory.createCompany(CompanyType.NEXAGE));
    loggedUserIs(new UserAuth(user, null));

    // when & then
    assertTrue(loginUserContext.hasRole(Role.ROLE_ADMIN));
  }

  private Company createCompany() {
    return createCompany(null);
  }

  private Company createCompany(String... mdmIds) {
    Company company = TestObjectsFactory.createCompany(CompanyType.SELLER);

    if (mdmIds != null) {
      var companyMdmIds = new ArrayList<CompanyMdmId>(mdmIds.length);
      for (String mdmId : mdmIds) {
        if (StringUtils.isBlank(mdmId)) {
          continue;
        }

        CompanyMdmId cMdmId = new CompanyMdmId();
        cMdmId.setId(mdmId);
        cMdmId.setCompany(company);
        companyMdmIds.add(cMdmId);
      }

      company.setMdmIds(companyMdmIds);
    }

    return company;
  }

  @Test
  void nexageUserIsCurrentUser() {
    // given
    loggedUserIs(new UserAuth(nexageUser, buildOneCentralEntitlements(nexageUser)));

    // when
    boolean isCurrentUser = loginUserContext.isCurrentUser(nexageUser.getPid());

    // then
    assertTrue(isCurrentUser);
  }

  @Test
  void shouldGetLoggedInUserPid() {
    // given
    loggedUserIs(new UserAuth(nexageUser, buildOneCentralEntitlements(nexageUser)));

    // when
    long expectedPid = loginUserContext.getPid();

    // then
    assertEquals(expectedPid, nexageUser.getPid());
  }

  @Test
  void loggedInUserIsNotGlobalUser() {
    // given
    loggedUserIs(new UserAuth(nexageUser, buildOneCentralEntitlements(nexageUser)));

    // when
    boolean isGlobalUser = loginUserContext.isGlobalUser();

    // then
    assertFalse(isGlobalUser);
  }

  private void enableSelfServe(Company company) {
    company.setSelfServeAllowed(true);
  }

  private User createUser(Company... companies) {
    return TestObjectsFactory.createUser(User.Role.ROLE_ADMIN, companies);
  }

  private User createUser(Role role, CompanyType companyType) {
    return TestObjectsFactory.createUser(role, TestObjectsFactory.createCompany(companyType));
  }

  private Company createCompany(long pid) {
    Company company = TestObjectsFactory.createCompany(CompanyType.SELLER);
    company.setPid(pid);
    return company;
  }

  private void loggedUserIs(UserAuth userAuth) {
    SpringUserDetails springUserDetails =
        ((SpringUserDetails) new UserDetailsServiceImpl(null, null, false).loadUser(userAuth));

    loginUserContext =
        new TestLoginUserContext(
            springUserDetails,
            positionRepository,
            siteRepository,
            companyRuleRepository,
            userRestrictedSiteRepository);
  }

  private List<Entitlement> createEntitlements(String... names) {
    return IntStream.range(0, names.length)
        .mapToObj(
            i -> new Entitlement(i, names[i], names[i].toUpperCase(), "app", "type", "permission"))
        .collect(Collectors.toList());
  }

  private static class TestLoginUserContext extends LoginUserContext {
    private final SpringUserDetails currentUser;

    public TestLoginUserContext(
        SpringUserDetails currentUser,
        PositionRepository positionRepository,
        SiteRepository siteRepository,
        CompanyRuleRepository companyRuleRepository,
        UserRestrictedSiteRepository userRestrictedSiteRepository) {
      super(
          positionRepository, siteRepository, companyRuleRepository, userRestrictedSiteRepository);
      this.currentUser = currentUser;
    }

    @Override
    public SpringUserDetails getCurrentUser() {
      return currentUser;
    }
  }
}
