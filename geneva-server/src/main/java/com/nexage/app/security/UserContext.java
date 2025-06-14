package com.nexage.app.security;

import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.Set;

public interface UserContext {

  CompanyType getType();

  long getPid();

  String getUserId();

  boolean isNexageUser();

  boolean isSellerAdmin();

  boolean isGlobalUser();

  boolean isNexageAdmin();

  boolean isNexageAdminOrManager();

  boolean writePrivilegeCheck(User user);

  boolean doSameOrNexageAffiliation(Site site);

  boolean doSameOrNexageAffiliation(User user);

  boolean doSameOrNexageAffiliation(Long companyPid);

  boolean hasAccessToSellerSeatOrHasNexageAffiliation(Long sellerSeatPid);

  boolean isCurrentUser(long userPID);

  /**
   * Checks if current user has specified role assigned
   *
   * @param role {@link Role}
   * @return boolean
   */
  @Deprecated(forRemoval = true)
  boolean hasRole(Role role);

  Set<Long> getCompanyPids();

  SpringUserDetails getCurrentUser();

  boolean canAccessSite(long sitePid);

  boolean isPublisherSelfServeEnabled(Long companyPid);

  boolean isAdminOrManager();

  /**
   * Checks if current user has ROLE_API assigned
   *
   * @return true if current user has ROLE_API assigned, false otherwise
   */
  boolean isApiUser();

  /**
   * Checks if current user can access publisher
   *
   * @param companyPid company id
   * @return true if current user can access given company, false otherwise
   */
  boolean canAccessPublisher(long companyPid);

  /**
   * Checks if current user can access Placement
   *
   * @param placementId placement id
   * @return true if current user can access given placement, false otherwise
   */
  boolean canAccessPlacement(long placementId);

  /**
   * Check if current user can access Sellers resources
   *
   * @param queryField query term used to fetch result
   * @param ids set of ids to be validated
   * @return boolean
   */
  boolean canAccessSellersResource(String queryField, Set<Long> ids);

  /**
   * Check if current user can access CompanyRules
   *
   * @param rulesPids set of rules pids
   * @return boolean
   */
  boolean canAccessCompanyRules(Set<Long> rulesPids);

  /**
   * Check if current user have access to a specific ruleId
   *
   * @param ruleId ruleId to verify
   * @return boolean
   */
  boolean canAccessCompanyRule(Long ruleId);

  /**
   * Check if current user is deal admin.
   *
   * @return {@code true} if user is deal admin, {@code false} otherwise
   */
  boolean isDealAdmin();

  /**
   * Check if current user can access Sellers resources. If sellerSeatPid is present and user does
   * not have access then an exception is thrown. If companyPid or sellerSeatPid does not have a
   * Long value then also a exception is thrown. If sellerSeatPid is present and user has access
   * then this method will return true. If sellerSeatPid is not present by default this method
   * returns false and the validations/permission checks for other fields are done in impl class
   *
   * @param qf query field used to fetch result
   * @param qt the seller seat pid to validated
   * @return boolean
   */
  boolean canAccessSellerSeat(Set<String> qf, String qt);

  /**
   * Checks if the current petitioner is an IdentityIQ user or not.
   *
   * @return true if internal IdentityIQ user, false otherwise.
   */
  boolean isInternalIdentityIqUser();

  boolean canEditSmartExchange();

  /**
   * Checks whether a user has all listed entitlements.
   *
   * @param entitlements list of entitlement names
   * @return <code>true</code> if user has all entitlements, <code>false</code> otherwise
   */
  boolean hasEntitlements(String... entitlements);

  /**
   * Checks whether User has OneCentral 'AdminNexage' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcAdminNexage();

  /**
   * Checks whether User has OneCentral 'ManagerYieldNexage' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcManagerYieldNexage();

  /**
   * Checks whether User has OneCentral 'ManagerSmartexNexage' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcManagerSmartexNexage();

  /**
   * Checks whether User has OneCentral 'ManagerNexage' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcManagerNexage();

  /**
   * Checks whether User has OneCentral 'UserNexage' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcUserNexage();

  /**
   * Checks whether User has OneCentral 'AdminSeller' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcAdminSeller();

  /**
   * Checks whether User has OneCentral 'ManagerSeller' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcManagerSeller();

  /**
   * Checks whether User has OneCentral 'UserSeller' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcUserSeller();

  /**
   * Checks whether User has OneCentral 'AdminBuyer' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcAdminBuyer();

  /**
   * Checks whether User has OneCentral 'ManagerBuyer' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcManagerBuyer();

  /**
   * Checks whether User has OneCentral 'UserBuyer' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcUserBuyer();

  /**
   * Checks whether User has OneCentral 'AdminSeatHolder' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcAdminSeatHolder();

  /**
   * Checks whether User has OneCentral 'ManagerSeatHolder' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcManagerSeatHolder();

  /**
   * Checks whether User has OneCentral 'UserSeatHolder' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcUserSeatHolder();

  /**
   * Checks whether User has OneCentral 'AdminSellerSeat' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcAdminSellerSeat();

  /**
   * Checks whether User has OneCentral 'ManagerSellerSeat' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcManagerSellerSeat();

  /**
   * Checks whether User has OneCentral 'UserSellerSeat' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcUserSellerSeat();

  boolean isOcApi();

  /**
   * Checks whether User has OneCentral 'ApiBuyer' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcApiBuyer();

  /**
   * Checks whether User has OneCentral 'ApiSeller' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcApiSeller();

  /**
   * Checks whether User has OneCentral 'ApiIIQ' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcApiIIQ();

  /**
   * Checks whether User has OneCentral 'DealManager' role.
   *
   * @return <code>true</code> only if User has all entitlements related to this role, <code>false
   *     </code> otherwise
   */
  boolean isOcDealManager();
}
