package com.nexage.app.services.impl;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.UserRestrictedSite;
import com.nexage.admin.core.model.UserRestrictedSitePK;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.admin.core.repository.UserRestrictedSiteRepository;
import com.nexage.admin.core.specification.UserSpecification;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.manager.OneCentralUserManager;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.UserService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or "
        + "@loginUserContext.isOcUserBuyer() or @loginUserContext.isOcUserSeatHolder()")
public class UserServiceImpl implements UserService {
  private static final int pwdSize = 8;

  private final CompanyRepository companyRepository;
  private final SiteRepository siteRepository;
  private final UserRestrictedSiteRepository userRestrictedSiteRepository;
  private final UserContext userContext;
  private final OneCentralUserManager oneCentralUserManager;
  private final UserRepository userRepository;

  public UserServiceImpl(
      CompanyRepository companyRepository,
      SiteRepository siteRepository,
      UserRestrictedSiteRepository userRestrictedSiteRepository,
      UserContext userContext,
      OneCentralUserManager oneCentralUserManager,
      UserRepository userRepository) {
    this.companyRepository = companyRepository;
    this.siteRepository = siteRepository;
    this.userRestrictedSiteRepository = userRestrictedSiteRepository;
    this.userContext = userContext;
    this.oneCentralUserManager = oneCentralUserManager;
    this.userRepository = userRepository;
  }

  @Override
  public void changePassword(long userPid, String oldPasswd, String newPasswd) {
    // Only the account owner can change the password
    if (userContext.getPid() != userPid) {
      log.error("Cannot permit changing password for other users");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }

    User user = getUser(userPid);
    if (user.getOneCentralUserName() != null) {
      oneCentralUserManager.resetPassword(user, oldPasswd, newPasswd);
    } else {
      throw new UnsupportedOperationException("Not supported: change password for local user");
    }
  }

  /** {@inheritDoc} */
  @Override
  public User getUser(Long pid) {
    User user =
        userRepository
            .findById(pid)
            .orElseThrow(
                () -> {
                  log.info("User not found for Pid: [{}]", pid);
                  return new GenevaValidationException(CommonErrorCodes.COMMON_USER_NOT_FOUND);
                });
    if (!userContext.doSameOrNexageAffiliation(user)) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
    return postProcessUser(user);
  }

  private void initializeLazyFields(User user) {
    Hibernate.initialize(user.getSellerSeat());
    Hibernate.initialize(user.getCompanies());
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcAdminBuyer() or @loginUserContext.isOcAdminSeatHolder()")
  public List<User> getAllUsersByCompanyPid(long companyPid) {
    if (!companyRepository.existsById(companyPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }
    if (userContext.doSameOrNexageAffiliation(companyPid)) {
      Specification<User> spec =
          getSpecForUserWithoutApiRoleAndSuperadminAndCurrent()
              .and(UserSpecification.withCompany(companyPid));

      List<User> users = userRepository.findAll(spec);
      users.forEach(this::postProcessUser);
      return users;
    } else {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcAdminSeller()")
  public void restrictUserAccessToSites(long userPid, List<Long> sitePids) {
    User user = getUser(userPid);

    // Don't fail the complete operation for few individual site failures
    for (long sitePid : sitePids) {
      Long companyPid = findCompanyPidBySitePid(sitePid);
      if (!user.isBelongingToCompany(companyPid)) {
        // TODO check if this will be propogated as jdbc exception if there were few insertions
        // already
        log.warn("User and Site should belong to same company to apply restrictions");
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_USER_RESTRICTION_ON_INVALID_SITE);
      } else {
        // TODO Do batch insert
        userRestrictedSiteRepository.save(
            new UserRestrictedSite(new UserRestrictedSitePK(user.getPid(), sitePid)));
      }
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcAdminSeller()")
  public void allowUserAccessToSites(long userPid, List<Long> sitePids) {
    User user = getUser(userPid);

    // Don't fail the complete operation for few individual site failures
    for (long sitePid : sitePids) {
      Long companyPid = findCompanyPidBySitePid(sitePid);
      if (!user.isBelongingToCompany(companyPid)) {
        // TODO check if this will be propogated as jdbc exception if there were few insertions
        // already
        log.warn("User and Site should belong to same company to revoke restrictions");
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_REVOKE_USER_RESTRICTION_ON_INVALID_SITE);
      } else {
        // TODO Do batch insert
        userRestrictedSiteRepository.deleteByPkUserIdAndPkSiteId(user.getPid(), sitePid);
      }
    }
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcAdminBuyer()")
  public void deleteUser(long userPid) {
    if (userContext.isCurrentUser(userPid)) {
      log.error("User cannot delete self");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    } else {
      User user =
          userRepository
              .findById(userPid)
              .orElseThrow(
                  () -> {
                    log.info("User not found for Pid: [{}]", userPid);
                    return new GenevaValidationException(CommonErrorCodes.COMMON_USER_NOT_FOUND);
                  });
      if (userContext.writePrivilegeCheck(user)) {
        // remove user as primary contact if he is
        Company company = user.getCompany();
        if (company != null) {
          User primaryContact = company.getContact();
          if (primaryContact != null && primaryContact.equals(user)) {
            company.setContact(null);
            companyRepository.save(company);
          }
        }
        userRestrictedSiteRepository.deleteByPkUserId(userPid);
        userRepository.delete(user);
      } else {
        log.error("User is not allowed to delete user");
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
    }
  }

  private Long findCompanyPidBySitePid(long sitePid) {
    Long companyPid = siteRepository.findCompanyPidByPid(sitePid);
    if (companyPid == null) {
      log.info("Site companyPid not found for Pid: [{}]", sitePid);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND);
    }
    return companyPid;
  }

  private Specification<User> getSpecForUserWithoutApiRoleAndSuperadminAndCurrent() {
    return Specification.where(UserSpecification.withoutApiRole())
        .and(UserSpecification.withoutSuperAdmin())
        .and(UserSpecification.withoutUser(userContext.getPid()));
  }

  private User postProcessUser(User user) {
    initializeLazyFields(user);
    user.determinePrimaryContact();
    return user;
  }
}
