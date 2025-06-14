package com.nexage.app.web.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.model.User;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EntitlementsTestUtil {

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
  private static final String DEAL =
      "{\"id\":17,\"name\":\"Deal\",\"displayName\":\"Deal\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String ADMIN =
      "{\"id\":18,\"name\":\"Admin\",\"displayName\":\"Admin\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String MANAGER =
      "{\"id\":19,\"name\":\"Manager\",\"displayName\":\"Manager\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String USER =
      "{\"id\":20,\"name\":\"User\",\"displayName\":\"User\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  private static final String ADSCREENING =
      "{\"id\":21,\"name\":\"Adscreening\",\"displayName\":\"Adscreening\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static List<Entitlement> buildOneCentralEntitlements(User user) {
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

  private static String createEntitlements(User.Role userRole, CompanyType companyType) {
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

  private static String userEntitlements() {
    return USER;
  }

  private static String managerEntitlements() {
    return String.format("%s,%s", MANAGER, userEntitlements());
  }

  private static String adminEntitlements() {
    return String.format("%s,%s", ADMIN, managerEntitlements());
  }

  private static String userEntitlements(CompanyType companyType) {
    return String.format("%s,%s", companyEntitlement(companyType), userEntitlements());
  }

  private static String managerEntitlements(CompanyType companyType) {
    return String.format("%s,%s", companyEntitlement(companyType), managerEntitlements());
  }

  private static String adminEntitlements(CompanyType companyType) {
    return String.format("%s,%s", companyEntitlement(companyType), adminEntitlements());
  }

  private static String managerSmartexEntitlements(CompanyType companyType) {
    return String.format(
        "%s,%s,%s", companyEntitlement(companyType), SMARTEX, managerEntitlements());
  }

  private static String managerYieldEntitlements(CompanyType companyType) {
    return String.format("%s,%s", managerSmartexEntitlements(companyType), YIELD);
  }

  private static String apiEntitlements(CompanyType companyType) {
    return String.format("%s,%s", companyEntitlement(companyType), API);
  }

  private static String adReviewerEntitlements(CompanyType companyType) {
    return String.format("%s,%s", companyEntitlement(companyType), ADSCREENING);
  }

  private static String sellerSeatEntitlements(User.Role userRole) {
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

  private static String companyEntitlement(CompanyType companyType) {
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
