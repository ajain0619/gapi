package com.nexage.app.services.impl.user;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerSeatRule_;
import com.nexage.admin.core.model.Site_;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.admin.core.specification.UserSpecification;
import com.nexage.app.dto.user.CompanyViewDTO;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.UserDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.SellerSeatService;
import com.nexage.app.services.UserCompanyService;
import com.nexage.app.services.UserSellerSeatService;
import com.nexage.app.services.user.UserDTOService;
import com.nexage.app.services.user.UserOneCentralService;
import com.nexage.app.util.validator.UserUpdateValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Log4j2
@Transactional
@PreAuthorize(
    "@loginUserContext.isOcApiIIQ() or @loginUserContext.isOcUserNexage() or "
        + "@loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserBuyer() or "
        + "@loginUserContext.isOcUserSeatHolder()")
public class UserDTOServiceImpl implements UserDTOService {

  private final SellerSeatService sellerSeatService;
  private final UserSellerSeatService userSellerSeatService;
  private final CompanyService companyService;
  private final UserCompanyService userCompanyService;
  private final UserOneCentralService userOneCentralService;
  private final UserContext userContext;
  private final UserRepository userRepository;
  private final UserUpdateValidator userUpdateValidator;

  public UserDTOServiceImpl(
      SellerSeatService sellerSeatService,
      UserSellerSeatService userSellerSeatService,
      CompanyService companyService,
      UserCompanyService userCompanyService,
      UserOneCentralService userOneCentralService,
      UserContext userContext,
      UserRepository userRepository,
      UserUpdateValidator userUpdateValidator) {
    this.sellerSeatService = sellerSeatService;
    this.userSellerSeatService = userSellerSeatService;
    this.companyService = companyService;
    this.userCompanyService = userCompanyService;
    this.userOneCentralService = userOneCentralService;
    this.userContext = userContext;
    this.userRepository = userRepository;
    this.userUpdateValidator = userUpdateValidator;
  }

  private static final List<String> SUPPORTED_FIELDS =
      List.of(
          "sellerSeatPid",
          "companyPid",
          "userName",
          "email",
          "name",
          "oneCentralUserName",
          "onlyCurrent");

  /**
   * {@inheritDoc}
   *
   * <ul>
   *   <li>if the user is authorized and onlyCurrent is not requested, a list of users is returned;
   *   <li>if the user is authorized and only current is requested (qf=onlyCurrent&qt=true), the
   *       current user is returned;
   *   <li>if the user is not authorized, the current user is returned regardless of qf/qt params.
   *       (userSpecification.withUser(pid))
   * </ul>
   */
  @PreAuthorize(
      "@loginUserContext.isOcApiIIQ() or @loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() "
          + "or @loginUserContext.isOcUserBuyer() or @loginUserContext.isOcUserSeatHolder()")
  @Transactional(readOnly = true)
  public Page<UserDTO> getAllUsers(Set<String> qf, String qt, Pageable pageable) {
    Specification<User> spec;
    // Check if authenticated user has the authority to retrieve list of all users (based on
    // OneCentral entitlements)
    if (userContext.canAccessSellerSeat(qf, qt)
        || userContext.isOcApiIIQ()
        || userContext.isOcUserNexage()
        || userContext.isOcAdminSeller()
        || userContext.isOcAdminBuyer()
        || userContext.isOcAdminSeatHolder()) {

      validateSearchParamRequest(qf, qt);
      spec =
          UserSpecification.buildUserSpecification(
              userContext.getCurrentUser().getType(),
              userContext.getCompanyPids(),
              userContext.getPid(),
              qf,
              qt);
    } else {
      log.debug(
          "User is not authorized to fetch a list of users. The current user only is returned.");
      spec = UserSpecification.withUser(userContext.getPid());
    }
    return userRepository.findAll(spec, pageable).map(UserDTOMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  public UserDTO getUser(Long userPid) {
    User user =
        userRepository
            .findById(userPid)
            .orElseThrow(
                () -> new GenevaValidationException(CommonErrorCodes.COMMON_USER_NOT_FOUND));
    if (!userContext.doSameOrNexageAffiliation(user)) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
    return UserDTOMapper.MAPPER.map(user);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcAdminBuyer() or @loginUserContext.isOcAdminSeatHolder() or "
          + "@loginUserContext.isOcApiIIQ()")
  public UserDTO createUser(UserDTO userDto) {
    User user = UserDTOMapper.MAPPER.map(userDto);
    if (nonNull(user.getSellerSeat())) {
      userSellerSeatService.updateUserWithVerifiedSellerSeat(user, user.getSellerSeat().getPid());
    } else {
      userCompanyService.updateUserWithVerifiedCompany(user, user.getCompanies(), Company::getPid);
    }
    checkPrivilege(user);
    return UserDTOMapper.MAPPER.map(userOneCentralService.createUser(user));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public UserDTO updateUser(UserDTO userDto, Long userPid) {
    userUpdateValidator.validatePid(userDto, userPid);
    validateEmailAndUserNameUniquenessForUpdate(userDto);
    User user =
        userRepository
            .findById(userDto.getPid())
            .orElseThrow(
                () -> new GenevaValidationException(CommonErrorCodes.COMMON_USER_NOT_FOUND));

    userUpdateValidator.validateEmail(userDto, user);

    UserDTOMapper.MAPPER.setUserDetails(userDto, user, userContext);

    if (isUserAffiliationChanged(userDto, user)) {
      if (!userContext.isNexageAdminOrManager()) {
        log.error("Unprivileged user trying to change user affiliation.");
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
      if (nonNull(userDto.getSellerSeat())) {
        userSellerSeatService.updateUserWithVerifiedSellerSeat(
            user, userDto.getSellerSeat().getPid());
      } else {
        userCompanyService.updateUserWithVerifiedCompany(
            user, userDto.getCompanies(), CompanyViewDTO::getPid);
      }
    }
    return UserDTOMapper.MAPPER.map(userOneCentralService.updateUser(user));
  }

  private boolean isUserAffiliationChanged(UserDTO userDto, User user) {
    return !Objects.equals(
            nonNull(user.getSellerSeat()) ? user.getSellerSeat().getPid() : null,
            nonNull(userDto.getSellerSeat()) ? userDto.getSellerSeat().getPid() : null)
        || !Arrays.equals(
            user.getCompanies().stream().mapToLong(Company::getPid).toArray(),
            userDto.getCompanies().stream().mapToLong(CompanyViewDTO::getPid).toArray());
  }

  private void validateEmailAndUserNameUniquenessForUpdate(UserDTO userDto) {
    if (userRepository.existsByEmailAndPidNot(userDto.getEmail(), userDto.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_EMAIL);
    }
    if (userRepository.existsByUserNameAndPidNot(userDto.getUserName(), userDto.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_USER_NAME);
    }
  }

  void checkPrivilege(User user) {
    if (!userContext.writePrivilegeCheck(user)) {
      log.error("Logged in user should have right affiliation or be Nexage manager to create user");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  private void validateSearchParamRequest(Set<String> qf, String qt) {
    if (!CollectionUtils.isEmpty(qf)) {
      if (qf.stream().noneMatch(SUPPORTED_FIELDS::contains)) {
        throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
      }
      if (qf.contains(Site_.COMPANY_PID) && qf.contains(SellerSeatRule_.SELLER_SEAT_PID)) {
        throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
      }
      if (qf.contains(SellerSeatRule_.SELLER_SEAT_PID)
          && isNull(sellerSeatService.getSellerSeat(Long.valueOf(qt)))) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND);
      }
      if (qf.contains(Site_.COMPANY_PID) && isNull(companyService.getCompany(Long.valueOf(qt)))) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
      }
    }
  }
}
