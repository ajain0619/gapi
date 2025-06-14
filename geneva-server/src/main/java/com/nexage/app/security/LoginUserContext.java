package com.nexage.app.security;

import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.SellerSeatRule_;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Site_;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRestrictedSiteRepository;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Helper class used to retrieve authenticated user from the Security Context. It also contains
 * handy methods performing various security validations.
 */
@Log4j2
@ConditionalOnExpression("'${geneva.server.login}'.matches('sso|oauth2')")
@Component("loginUserContext")
public class LoginUserContext implements UserContext {

  protected PositionRepository positionRepository;
  protected SiteRepository siteRepository;
  protected CompanyRuleRepository companyRuleRepository;
  protected UserRestrictedSiteRepository userRestrictedSiteRepository;

  @Autowired
  public LoginUserContext(
      PositionRepository positionRepository,
      SiteRepository siteRepository,
      CompanyRuleRepository companyRuleRepository,
      UserRestrictedSiteRepository userRestrictedSiteRepository) {
    this.positionRepository = positionRepository;
    this.siteRepository = siteRepository;
    this.companyRuleRepository = companyRuleRepository;
    this.userRestrictedSiteRepository = userRestrictedSiteRepository;
  }

  @Override
  public SpringUserDetails getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    // Authentication can be null if no authentication information is available
    if (auth != null) {
      Object principal = auth.getPrincipal();
      if (principal instanceof SpringUserDetails springUserDetails) {
        return springUserDetails;
      } else if (principal instanceof OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal) {
        return oAuth2AuthenticatedPrincipal.getAttribute("springUserDetails");
      }

    } else {
      log.error("Authorized user is null; This should never happen");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_BAD_PRINCIPAL);
    }
    return null;
  }

  @Override
  public long getPid() {
    return getCurrentUser().getPid();
  }

  @Override
  public boolean isNexageUser() {
    return hasEntitlements(
        OneCentralEntitlement.NEXAGE.getValue(), OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isSellerAdmin() {
    return hasEntitlements(
        OneCentralEntitlement.SELLER.getValue(),
        OneCentralEntitlement.ADMIN.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isNexageAdmin() {
    return hasEntitlements(
        OneCentralEntitlement.NEXAGE.getValue(),
        OneCentralEntitlement.ADMIN.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isGlobalUser() {
    return getCurrentUser().isGlobal();
  }

  @Override
  public boolean isNexageAdminOrManager() {
    return isOcAdminNexage()
        || isOcManagerNexage()
        || isOcManagerYieldNexage()
        || isOcManagerSmartexNexage();
  }

  @Override
  public CompanyType getType() {
    return getCurrentUser().getType();
  }

  @Override
  public boolean isPublisherSelfServeEnabled(Long companyPid) {
    return isNexageUser()
        || (doSameOrNexageAffiliation(companyPid)
            && getCurrentUser().isPublisherSelfServeEnabled(companyPid));
  }

  /**
   * For any user management operations that require privilege check:
   *
   * <p>1. If the given and logged in user belong to same company, then the logged in user should
   * have admin privileges 2. If the given and logged in user belong to different companies, then
   * the logged in should only be Nexage user 3. Only Nexage user can create/edit other admins
   */
  @Override
  public boolean writePrivilegeCheck(User user) {
    SpringUserDetails currentUser = getCurrentUser();
    return ((currentUser.canAccess(user) && currentUser.getRole() == Role.ROLE_ADMIN)
        || (user.getCompanyType() != CompanyType.NEXAGE
            && currentUser.getType() == CompanyType.NEXAGE
            && currentUser.getRole() != Role.ROLE_USER)
        || isInternalIdentityIqUser(currentUser));
  }

  /**
   * True if, 1. logged in user is affiliated to the company of a given site 2. logged in user is
   * affiliated to Nexage
   *
   * <p>No additional role check required as the restrictions defined in security.xml is enough.
   *
   * @param site site
   * @return true/false
   */
  @Override
  public boolean doSameOrNexageAffiliation(Site site) {
    return doSameOrNexageAffiliation((site.getCompany().getPid()));
  }

  @Override
  public boolean canAccessSite(long sitePid) {
    SpringUserDetails currentUser = getCurrentUser();
    if (currentUser.getType() == CompanyType.NEXAGE) return true;

    Long siteCompanyPid = siteRepository.findCompanyPidByPidWithStatusNotDeleted(sitePid);
    return !(getCompanyPids() == null
            || siteCompanyPid == null
            || !getCompanyPids().contains(siteCompanyPid))
        && userRestrictedSiteRepository
            .findPidByUserIdAndSiteId(getCurrentUser().getPid(), sitePid)
            .isEmpty();
  }

  /**
   * True if, 1. logged in user is affiliated to the company of a given site 2. logged in user is
   * affiliated to Nexage
   *
   * <p>No additional role check required as the restrictions defined in security.xml is enough.
   *
   * @param user authenticated user
   * @return true/false
   */
  @Override
  public boolean doSameOrNexageAffiliation(User user) {
    SpringUserDetails currentUser = getCurrentUser();
    return currentUser.canAccess(user) || (currentUser.getType() == CompanyType.NEXAGE);
  }

  @Override
  public boolean doSameOrNexageAffiliation(Long companyPid) {
    SpringUserDetails currentUser = getCurrentUser();
    return currentUser.canAccess(companyPid) || CompanyType.NEXAGE.equals(currentUser.getType());
  }

  @Override
  public boolean hasAccessToSellerSeatOrHasNexageAffiliation(Long sellerSeatPid) {
    SpringUserDetails currentUser = getCurrentUser();
    return currentUser.canAccessSellerSeat(sellerSeatPid)
        || CompanyType.NEXAGE.equals(currentUser.getType());
  }

  @Override
  public boolean isCurrentUser(long userPID) {
    return getCurrentUser().getPid() == userPID;
  }

  /** {@inheritDoc} */
  @Override
  public boolean canAccessSellersResource(String queryField, Set<Long> sellerIds) {
    if (Site_.COMPANY_PID.equals(queryField)) {
      SpringUserDetails currentUser = getCurrentUser();
      return currentUser.canAccess(sellerIds)
          || (currentUser.isGlobal() && currentUser.isAssociatedWithSellerSeat(sellerIds));
    }
    return false;
  }

  @Override
  public boolean hasRole(Role role) {
    return Optional.ofNullable(getCurrentUser())
        .map(SpringUserDetails::getRole)
        .filter(userRole -> userRole == role)
        .isPresent();
  }

  @Override
  public Set<Long> getCompanyPids() {
    return getCurrentUser().getCompanyPids();
  }

  @Override
  public String getUserId() {
    return getCurrentUser().getUsername();
  }

  @Override
  public boolean isAdminOrManager() {
    if (Objects.isNull(getCurrentUser()) || Objects.isNull(getCurrentUser().getRole())) {
      return false;
    }

    return hasEntitlements(OneCentralEntitlement.ADMIN.getValue())
        || hasEntitlements(OneCentralEntitlement.MANAGER.getValue())
        || hasEntitlements(OneCentralEntitlement.YIELD.getValue())
        || hasEntitlements(OneCentralEntitlement.SMARTEX.getValue());
  }

  /** {@inheritDoc} */
  @Override
  public boolean isApiUser() {
    return isOcApi();
  }

  /** {@inheritDoc} */
  @Override
  public boolean canAccessPublisher(long companyPid) {
    return getCompanyPids() != null && getCompanyPids().contains(companyPid);
  }

  /** {@inheritDoc} */
  @Override
  public boolean canAccessPlacement(long placementId) {
    Long placementCompanyPid = positionRepository.findCompanyPidByPlacementPid(placementId);
    return placementCompanyPid != null
        && getCompanyPids() != null
        && getCompanyPids().contains(placementCompanyPid);
  }

  /** {@inheritDoc} */
  @Override
  public boolean canAccessCompanyRules(Set<Long> rulesPids) {

    Set<Long> userCompanyPids = getCompanyPids();
    List<CompanyRule> accessibleRules =
        companyRuleRepository.findRulesByPidsAndOwnerCompanyPids(rulesPids, userCompanyPids);
    return accessibleRules != null
        && userCompanyPids != null
        && rulesPids.size() == accessibleRules.size();
  }

  /** {@inheritDoc} */
  @Override
  public boolean canAccessCompanyRule(Long ruleId) {
    Set<Long> ruleIdSet = Set.of(ruleId);
    return canAccessCompanyRules(ruleIdSet);
  }

  @Override
  public boolean isDealAdmin() {
    return getCurrentUser().isDealAdmin();
  }

  /** {@inheritDoc} */
  @Override
  public boolean canAccessSellerSeat(Set<String> qf, String qt) {
    if (!CollectionUtils.isEmpty(qf)
        && (qf.contains(Site_.COMPANY_PID) || qf.contains(SellerSeatRule_.SELLER_SEAT_PID))) {
      try {
        Long.valueOf(qt);
      } catch (NumberFormatException nfe) {
        log.error("NumberFormatException in reading property {}", qf);
        throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
      }
    }
    if (!CollectionUtils.isEmpty(qf) && qf.contains(SellerSeatRule_.SELLER_SEAT_PID)) {
      SpringUserDetails currentUser = getCurrentUser();
      boolean hasAccess =
          CompanyType.NEXAGE.equals(currentUser.getType())
              || currentUser.canAccessSellerSeat(Long.valueOf(qt));
      if (!hasAccess) {
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
      return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isInternalIdentityIqUser() {
    return isInternalIdentityIqUser(getCurrentUser());
  }

  private boolean isInternalIdentityIqUser(SpringUserDetails currentUser) {
    return hasEntitlements(OneCentralEntitlement.API_IIQ.getValue());
  }

  @Override
  public boolean canEditSmartExchange() {
    return hasEntitlements(OneCentralEntitlement.ADMIN.getValue())
        || hasEntitlements(OneCentralEntitlement.YIELD.getValue())
        || hasEntitlements(OneCentralEntitlement.SMARTEX.getValue());
  }

  @Override
  public boolean hasEntitlements(String... entitlements) {
    if (getCurrentUser() != null && getCurrentUser().getEntitlements() != null) {
      // avoid case-sensitive comparison, by lowering all cases
      var required =
          Arrays.stream(entitlements).map(String::toLowerCase).collect(Collectors.toList());

      return getCurrentUser().getEntitlements().stream()
          .map(e -> e.getName().toLowerCase())
          .collect(Collectors.toList())
          .containsAll(required);
    }
    return false;
  }

  @Override
  public boolean isOcAdminNexage() {
    return hasEntitlements(
        OneCentralEntitlement.NEXAGE.getValue(),
        OneCentralEntitlement.ADMIN.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcManagerYieldNexage() {
    return hasEntitlements(
        OneCentralEntitlement.NEXAGE.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue(),
        OneCentralEntitlement.SMARTEX.getValue(),
        OneCentralEntitlement.YIELD.getValue());
  }

  @Override
  public boolean isOcManagerSmartexNexage() {
    return hasEntitlements(
        OneCentralEntitlement.NEXAGE.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue(),
        OneCentralEntitlement.SMARTEX.getValue());
  }

  @Override
  public boolean isOcManagerNexage() {
    return hasEntitlements(
        OneCentralEntitlement.NEXAGE.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcUserNexage() {
    return hasEntitlements(
        OneCentralEntitlement.NEXAGE.getValue(), OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcAdminSeller() {
    return hasEntitlements(
        OneCentralEntitlement.SELLER.getValue(),
        OneCentralEntitlement.ADMIN.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcManagerSeller() {
    return hasEntitlements(
        OneCentralEntitlement.SELLER.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcUserSeller() {
    return hasEntitlements(
        OneCentralEntitlement.SELLER.getValue(), OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcAdminBuyer() {
    return hasEntitlements(
        OneCentralEntitlement.BUYER.getValue(),
        OneCentralEntitlement.ADMIN.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcManagerBuyer() {
    return hasEntitlements(
        OneCentralEntitlement.BUYER.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcUserBuyer() {
    return hasEntitlements(
        OneCentralEntitlement.BUYER.getValue(), OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcAdminSeatHolder() {
    return hasEntitlements(
        OneCentralEntitlement.SEAT_HOLDER.getValue(),
        OneCentralEntitlement.ADMIN.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcManagerSeatHolder() {
    return hasEntitlements(
        OneCentralEntitlement.SEAT_HOLDER.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcUserSeatHolder() {
    return hasEntitlements(
        OneCentralEntitlement.SEAT_HOLDER.getValue(), OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcAdminSellerSeat() {
    return hasEntitlements(
        OneCentralEntitlement.SELLER_SEAT.getValue(),
        OneCentralEntitlement.ADMIN.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcManagerSellerSeat() {
    return hasEntitlements(
        OneCentralEntitlement.SELLER_SEAT.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcUserSellerSeat() {
    return hasEntitlements(
        OneCentralEntitlement.SELLER_SEAT.getValue(), OneCentralEntitlement.USER.getValue());
  }

  @Override
  public boolean isOcApi() {
    return hasEntitlements(OneCentralEntitlement.API.getValue());
  }

  @Override
  public boolean isOcApiBuyer() {
    return hasEntitlements(
        OneCentralEntitlement.API.getValue(), OneCentralEntitlement.BUYER.getValue());
  }

  @Override
  public boolean isOcApiSeller() {
    return hasEntitlements(
        OneCentralEntitlement.API.getValue(), OneCentralEntitlement.SELLER.getValue());
  }

  @Override
  public boolean isOcApiIIQ() {
    return hasEntitlements(OneCentralEntitlement.API_IIQ.getValue());
  }

  @Override
  public boolean isOcDealManager() {
    return hasEntitlements(
        OneCentralEntitlement.DEAL.getValue(),
        OneCentralEntitlement.MANAGER.getValue(),
        OneCentralEntitlement.USER.getValue());
  }
}
