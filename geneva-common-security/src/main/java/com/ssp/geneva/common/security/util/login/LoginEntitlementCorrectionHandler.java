package com.ssp.geneva.common.security.util.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
public class LoginEntitlementCorrectionHandler {

  private static final String NEXAGE =
      "{\"id\":8,\"name\":\"Nexage\",\"displayName\":\"Nexage\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String SELLER =
      "{\"id\":9,\"name\":\"Seller\",\"displayName\":\"Seller\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String BUYER =
      "{\"id\":10,\"name\":\"Buyer\",\"displayName\":\"Buyer\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String SMARTEX =
      "{\"id\":11,\"name\":\"Smartex\",\"displayName\":\"Smartex\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String YIELD =
      "{\"id\":12,\"name\":\"Yield\",\"displayName\":\"Yield\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String SEATHOLDER =
      "{\"id\":13,\"name\":\"Seatholder\",\"displayName\":\"Seatholder\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String SELLERSEAT =
      "{\"id\":14,\"name\":\"Sellerseat\",\"displayName\":\"Sellerseat\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String API =
      "{\"id\":15,\"name\":\"Api\",\"displayName\":\"Api\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String APIIIQ =
      "{\"id\":16,\"name\":\"ApiIIQ\",\"displayName\":\"ApiIIQ\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String ADMIN =
      "{\"id\":18,\"name\":\"Admin\",\"displayName\":\"Admin\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String MANAGER =
      "{\"id\":19,\"name\":\"Manager\",\"displayName\":\"Manager\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String USER =
      "{\"id\":20,\"name\":\"User\",\"displayName\":\"User\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String ENTITILEMENTS_FORMAT_SPECIFIER = "%s,%s";

  protected final UserRepository userRepository;
  @Getter protected final ObjectMapper objectMapper;

  public LoginEntitlementCorrectionHandler(
      UserRepository userRepository, ObjectMapper objectMapper) {
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
  }

  /**
   * Correct entitlements for a given username.
   *
   * @param username user identifier.
   * @return {@link User}
   */
  public UserAuth correctEntitlements(final String username) {
    if (username == null || username.isEmpty()) {
      log.error("The authentication is null in authentication success handler.");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_BAD_PRINCIPAL);
    }
    User user =
        userRepository
            .findByUserName(username)
            .orElseThrow(
                () -> {
                  log.error(
                      "No user found for principle={}. This should never happen!!!", username);
                  return new RuntimeException("No user is found in DB for username: " + username);
                });

    // this "hack" allows mocking 1C entitlements for lower envs,
    // so API pre-authorization can also be tested on dev/local
    List<Entitlement> entitlements = buildOneCentralEntitlements(user);
    return new UserAuth(user, entitlements);
  }

  public SpringUserDetails correctEntitlements(final SpringUserDetails springUserDetails) {
    if (springUserDetails == null) {
      log.error("The authentication is null in authentication success handler.");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_BAD_PRINCIPAL);
    }
    String username = springUserDetails.getUsername();
    User user =
        userRepository
            .findByUserName(springUserDetails.getUsername())
            .orElseThrow(
                () -> {
                  log.error(
                      "No user found for principle={}. This should never happen!!!", username);
                  return new RuntimeException("No user is found in DB for username: " + username);
                });

    // this "hack" allows mocking 1C entitlements for lower envs,
    // so API pre-authorization can also be tested on dev/local
    List<Entitlement> entitlements = buildOneCentralEntitlements(user);
    springUserDetails.setEntitlements(entitlements);
    return springUserDetails;
  }

  private List<Entitlement> buildOneCentralEntitlements(User user) {
    var userRole = user.getRole();
    var companyType = user.getCompanyType();
    if (userRole == null || companyType == null) {
      return new ArrayList<>();
    }
    String entitlements = createEntitlements(userRole, companyType);
    if (companyType == CompanyType.SELLER && user.getSellerSeat() != null) {
      entitlements = sellerSeatEntitlements(userRole); // patch for SELLER SEATS
    }
    // Note: entitlements for "DealManager" role should be mocked separately
    // or added always (for example in admin role) for the testing purposes
    List<Entitlement> result = new ArrayList<>();
    try {
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      result = objectMapper.readValue("[" + entitlements + "]", new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      log.error("Failed to generate list of 1C entitlements from string: " + entitlements, e);
    }
    return result;
  }

  private String createEntitlements(User.Role userRole, CompanyType companyType) {
    switch (userRole) {
      case ROLE_ADMIN:
        return adminEntitlements(companyType);
      case ROLE_MANAGER:
        return managerEntitlements(companyType);
      case ROLE_USER:
        return userEntitlements(companyType);
      case ROLE_API:
        return apiEntitlements(companyType);
      case ROLE_API_IIQ:
        return APIIIQ;
      case ROLE_MANAGER_SMARTEX:
        return managerSmartexEntitlements(companyType);
      case ROLE_MANAGER_YIELD:
        return managerYieldEntitlements(companyType);
      default:
        throw new IllegalArgumentException("Unsupported user role");
    }
  }

  private String userEntitlements() {
    return USER;
  }

  private String managerEntitlements() {
    return String.format(ENTITILEMENTS_FORMAT_SPECIFIER, MANAGER, userEntitlements());
  }

  private String adminEntitlements() {
    return String.format(ENTITILEMENTS_FORMAT_SPECIFIER, ADMIN, managerEntitlements());
  }

  private String userEntitlements(CompanyType companyType) {
    return String.format(
        ENTITILEMENTS_FORMAT_SPECIFIER, companyEntitlement(companyType), userEntitlements());
  }

  private String managerEntitlements(CompanyType companyType) {
    return String.format(
        ENTITILEMENTS_FORMAT_SPECIFIER, companyEntitlement(companyType), managerEntitlements());
  }

  private String adminEntitlements(CompanyType companyType) {
    return String.format(
        ENTITILEMENTS_FORMAT_SPECIFIER, companyEntitlement(companyType), adminEntitlements());
  }

  private String managerSmartexEntitlements(CompanyType companyType) {
    return String.format(
        "%s,%s,%s", companyEntitlement(companyType), SMARTEX, managerEntitlements());
  }

  private String managerYieldEntitlements(CompanyType companyType) {
    return String.format(
        ENTITILEMENTS_FORMAT_SPECIFIER, managerSmartexEntitlements(companyType), YIELD);
  }

  private String apiEntitlements(CompanyType companyType) {
    return String.format(ENTITILEMENTS_FORMAT_SPECIFIER, companyEntitlement(companyType), API);
  }

  private String sellerSeatEntitlements(User.Role userRole) {
    // Seller seats can only have one of these three roles (USER, MANAGER or ADMIN)
    // If this changes in the future, code below should be extended accordingly...
    String entitlements = userEntitlements();
    if (userRole == User.Role.ROLE_MANAGER) {
      entitlements = managerEntitlements();
    } else if (userRole == User.Role.ROLE_ADMIN) {
      entitlements = adminEntitlements();
    }
    return String.format("%s,%s", SELLERSEAT, entitlements);
  }

  private String companyEntitlement(CompanyType companyType) {
    switch (companyType) {
      case NEXAGE:
        return NEXAGE;
      case SELLER:
        return SELLER;
      case BUYER:
        return BUYER;
      case SEATHOLDER:
        return SEATHOLDER;
      default:
        throw new IllegalArgumentException("Unsupported company type");
    }
  }
}
