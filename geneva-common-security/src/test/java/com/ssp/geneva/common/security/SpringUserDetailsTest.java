package com.ssp.geneva.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class SpringUserDetailsTest {

  private static final long TEST_USER_PID = 100L;
  private static final String TEST_USER_NAME = "testuser";

  private static final User USER = createTestUser();

  private static User createTestUser() {
    var user = new User();
    user.setPid(TEST_USER_PID);
    user.setUserName(TEST_USER_NAME);
    user.setRole(Role.ROLE_ADMIN);
    user.addCompany(createTestCompany());
    return user;
  }

  private static Company createTestCompany() {
    var company = new Company();
    company.setPid(1L);
    company.setSelfServeAllowed(true);
    company.setType(CompanyType.SELLER);
    return company;
  }

  @Test
  void shouldCreateCorrectUserDetails() {
    // given
    var entitlements = createEntitlements(OneCentralEntitlement.ADMIN.getValue());
    var userAuth = new UserAuth(USER, entitlements);

    // when
    var userDetails = new SpringUserDetails(userAuth);

    // then
    assertEquals(TEST_USER_PID, userDetails.getPid());
    assertEquals(TEST_USER_NAME, userDetails.getUsername());
    assertNull(userDetails.getPassword());
    assertEquals(Role.ROLE_ADMIN, userDetails.getRole());
    assertEquals(CompanyType.SELLER, userDetails.getType());
  }

  @Test
  void shouldIgnoreEntitlementsWhenComparingSpringUserDetails() {
    // given
    var auth1 = new UserAuth(USER, null);
    var auth2 = new UserAuth(USER, List.of());

    // when
    var details1 = new SpringUserDetails(auth1);
    var details2 = new SpringUserDetails(auth2);

    // then
    assertEquals(details1, details2);
  }

  private List<Entitlement> createEntitlements(String... names) {
    return IntStream.range(0, names.length)
        .mapToObj(
            i -> new Entitlement(i, names[i], names[i].toUpperCase(), "app", "type", "permission"))
        .collect(Collectors.toList());
  }
}
