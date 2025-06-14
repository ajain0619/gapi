package com.ssp.geneva.common.security.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.util.UUIDGenerator;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestData {

  public static final String NEXAGE =
      "{\"id\":8,\"name\":\"Nexage\",\"displayName\":\"Nexage\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String SELLER =
      "{\"id\":9,\"name\":\"Seller\",\"displayName\":\"Seller\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String BUYER =
      "{\"id\":10,\"name\":\"Buyer\",\"displayName\":\"Buyer\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String SMARTEX =
      "{\"id\":11,\"name\":\"Smartex\",\"displayName\":\"Smartex\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String YIELD =
      "{\"id\":12,\"name\":\"Yield\",\"displayName\":\"Yield\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String SEATHOLDER =
      "{\"id\":13,\"name\":\"Seatholder\",\"displayName\":\"Seatholder\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String SELLERSEAT =
      "{\"id\":14,\"name\":\"Sellerseat\",\"displayName\":\"Sellerseat\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String API =
      "{\"id\":15,\"name\":\"Api\",\"displayName\":\"Api\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String APIIIQ =
      "{\"id\":16,\"name\":\"ApiIIQ\",\"displayName\":\"ApiIIQ\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String DEAL =
      "{\"id\":17,\"name\":\"Deal\",\"displayName\":\"Deal\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String ADMIN =
      "{\"id\":18,\"name\":\"Admin\",\"displayName\":\"Admin\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String MANAGER =
      "{\"id\":19,\"name\":\"Manager\",\"displayName\":\"Manager\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  public static final String USER =
      "{\"id\":20,\"name\":\"User\",\"displayName\":\"User\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";

  public static final ObjectMapper objectMapper = new ObjectMapper();

  public static User createUser() {
    return createUser(User.Role.ROLE_ADMIN, createCompany(CompanyType.NEXAGE));
  }

  public static User createUser(User.Role role, Company... companies) {
    Preconditions.checkNotNull(companies);
    if (companies.length > 1) {
      Preconditions.checkArgument(
          Arrays.stream(companies).map(Company::getType).allMatch(CompanyType.SELLER::equals));
    }
    User user = new User();
    user.setPid(randomLong());
    user.setEmail(randomEmail());
    user.setUserName(RandomStringUtils.randomAlphanumeric(12));
    if (companies.length > 1) {
      user.setSellerSeat(createSellerSeat(companies));
    }
    Arrays.stream(companies).forEach(user::addCompany);
    user.setRole(role);
    user.setEnabled(true);
    return user;
  }

  public static Company createCompany(CompanyType type) {
    Company company = new Company(RandomStringUtils.randomAlphanumeric(32), type);
    company.setId(new UUIDGenerator().generateUniqueId());
    company.setPid(randomLong());
    company.setStatus(Status.ACTIVE);
    company.setWebsite(randomUrl());
    return company;
  }

  public static String randomUrl() {
    String url = "http://";
    url += RandomStringUtils.randomAlphabetic(10);
    url += ".";
    url += RandomStringUtils.randomAlphabetic(3);
    return url;
  }

  public static long randomLong() {
    return new Random().nextLong();
  }

  public static String randomEmail() {
    String email = RandomStringUtils.randomAlphanumeric(10);
    email += "@";
    email += RandomStringUtils.randomAlphanumeric(8);
    email += RandomStringUtils.randomAlphanumeric(3);
    return email;
  }

  public static SellerSeat createSellerSeat(Company... companies) {
    Preconditions.checkNotNull(companies);
    SellerSeat sellerSeat = new SellerSeat();
    Arrays.stream(companies).forEach(sellerSeat::addSeller);
    sellerSeat.setPid(randomLong());
    sellerSeat.setStatus(true);
    sellerSeat.setName(RandomStringUtils.randomAlphanumeric(12));
    sellerSeat.setDescription(RandomStringUtils.randomAlphanumeric(40));
    return sellerSeat;
  }

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

  public static String createEntitlements(User.Role userRole, CompanyType companyType) {
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

  public static String userEntitlements() {
    return USER;
  }

  public static String managerEntitlements() {
    return String.format("%s,%s", MANAGER, userEntitlements());
  }

  public static String adminEntitlements() {
    return String.format("%s,%s", ADMIN, managerEntitlements());
  }

  public static String userEntitlements(CompanyType companyType) {
    return String.format("%s,%s", companyEntitlement(companyType), userEntitlements());
  }

  public static String managerEntitlements(CompanyType companyType) {
    return String.format("%s,%s", companyEntitlement(companyType), managerEntitlements());
  }

  public static String adminEntitlements(CompanyType companyType) {
    return String.format("%s,%s", companyEntitlement(companyType), adminEntitlements());
  }

  public static String managerSmartexEntitlements(CompanyType companyType) {
    return String.format(
        "%s,%s,%s", companyEntitlement(companyType), SMARTEX, managerEntitlements());
  }

  public static String managerYieldEntitlements(CompanyType companyType) {
    return String.format("%s,%s", managerSmartexEntitlements(companyType), YIELD);
  }

  public static String apiEntitlements(CompanyType companyType) {
    return String.format("%s,%s", companyEntitlement(companyType), API);
  }

  public static String sellerSeatEntitlements(User.Role userRole) {
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

  public static String companyEntitlement(CompanyType companyType) {
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
