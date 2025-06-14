package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.specification.UserSpecification;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/user-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class UserRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired protected UserRepository userRepository;

  private static final long COMPANY_PID = 1L;
  private static final long SELLER_SEAT_PID = 1L;
  private static final long JDEER_PID = 3L;
  private static final String JDEER_LOGIN = "jdeer";
  private static final String JDEER_EMAIL = "jdeer@test.com";
  private static final String JDEER_ONE_CENTRAL_NAME = "onecentral3";
  private static final String SUPERADMIN_LOGIN = "superadmin";
  private static final String API_TEST_LOGIN = "api-test";
  private static final String JTEST_LOGIN = "jtest";

  @Test
  void shouldFindAllWithCompanySpec() {
    // when
    List<User> users = userRepository.findAll(UserSpecification.withCompany(COMPANY_PID));
    // then
    assertEquals(3, users.size());
    assertUserIsPresent(users, SUPERADMIN_LOGIN, JDEER_LOGIN, API_TEST_LOGIN);
  }

  @Test
  void shouldCountWithCompanyAndEnabledSpec() {
    // given
    Specification<User> spec =
        Specification.where(UserSpecification.withCompany(COMPANY_PID))
            .and(UserSpecification.isEnabled());
    // when
    long count = userRepository.count(spec);
    // then
    assertEquals(2L, count);
  }

  @Test
  void shouldFindAllWithCompanyAndNoRoleApiSpec() {
    // given
    Specification<User> spec =
        Specification.where(UserSpecification.withCompany(COMPANY_PID))
            .and(UserSpecification.withoutApiRole());
    // when
    List<User> users = userRepository.findAll(spec);
    // then
    assertEquals(2, users.size());
    assertUserIsPresent(users, SUPERADMIN_LOGIN, JDEER_LOGIN);
  }

  @Test
  void shouldFindAllWithCompanyAndNoApiRoleAndWithoutSuperadminSpec() {
    // given
    Specification<User> spec =
        Specification.where(UserSpecification.withCompany(COMPANY_PID))
            .and(UserSpecification.withoutApiRole())
            .and(UserSpecification.withoutSuperAdmin());
    // when
    List<User> users = userRepository.findAll(spec);
    // then
    assertEquals(1, users.size());
    assertUserIsPresent(users, JDEER_LOGIN);
  }

  @Test
  void shouldFindAllWithCompanies() {
    // given
    var spec = Specification.where(UserSpecification.withCompanies(Set.of(1L, 2L)));

    // when
    var users = userRepository.findAll(spec);

    // then
    assertEquals(
        List.of(1L, 3L, 40L, 2L), users.stream().map(User::getPid).collect(Collectors.toList()));
  }

  @Test
  void shouldFindAllWithSellerSeatAndNoApiRoleAndWithoutSuperadminSpec() {
    // given
    Specification<User> spec =
        Specification.where(UserSpecification.withSellerSeat(SELLER_SEAT_PID))
            .and(UserSpecification.withoutApiRole())
            .and(UserSpecification.withoutSuperAdmin());
    // when
    List<User> users = userRepository.findAll(spec);
    // then
    assertEquals(1, users.size());
    assertUserIsPresent(users, JTEST_LOGIN);
  }

  @Test
  void shouldFindAllWithSellerSeatAndNoApiRoleAndWithoutSuperadminAndWithoutUserSpec() {
    // given
    long jtestPid = 4L;
    Specification<User> spec =
        Specification.where(UserSpecification.withSellerSeat(SELLER_SEAT_PID))
            .and(UserSpecification.withoutUser(jtestPid))
            .and(UserSpecification.withoutApiRole())
            .and(UserSpecification.withoutSuperAdmin());
    // when
    List<User> users = userRepository.findAll(spec);
    // then
    assertTrue(users.isEmpty());
  }

  @Test
  void shouldFinalAllWithUserSpec() {
    // given
    var spec = Specification.where(UserSpecification.withUser(1L));

    // when
    var users = userRepository.findAll(spec);

    // then
    assertEquals("super admin", users.get(0).getName());
  }

  @Test
  void shouldFindAllBySellerSeatPid() {
    // when
    List<User> users = userRepository.findAllBySellerSeat_Pid(SELLER_SEAT_PID);
    // then
    assertFalse(users.isEmpty());
    for (User user : users) {
      assertEquals(SELLER_SEAT_PID, user.getSellerSeat().getPid());
    }
  }

  @Test
  void shouldProperlyCheckIfUserExistsByUserNameAndPid() {
    assertTrue(userRepository.existsByUserNameAndPid(JDEER_LOGIN, JDEER_PID));
    assertFalse(userRepository.existsByUserNameAndPid("not existing username", 123456789L));
  }

  @Test
  void shouldFindByUserName() {
    // when
    Optional<User> user = userRepository.findByUserName(JDEER_LOGIN);
    // then
    assertEquals(JDEER_LOGIN, user.get().getUserName());
  }

  @Test
  void shouldFindByEmail() {
    // when
    Optional<User> user = userRepository.findByEmail(JDEER_EMAIL);
    // then
    assertEquals(JDEER_EMAIL, user.get().getEmail());
  }

  @Test
  void shouldFindByOneCentralUserName() {
    // when
    Optional<User> user = userRepository.findByOneCentralUserName(JDEER_ONE_CENTRAL_NAME);
    // then
    assertEquals(JDEER_ONE_CENTRAL_NAME, user.get().getOneCentralUserName());
  }

  @Test
  void shouldProperlyCheckIfUserWithTheSameEmailButOtherPidExists() {
    // when & then
    assertTrue(userRepository.existsByEmailAndPidNot(JDEER_EMAIL, 123L));
    assertFalse(userRepository.existsByEmailAndPidNot("otheremail@email.com", 123L));
  }

  @Test
  void shouldProperlyCheckIfUserWithTheSameUserNameButOtherPidExists() {
    // when & then
    assertTrue(userRepository.existsByUserNameAndPidNot(JDEER_LOGIN, 123L));
    assertFalse(userRepository.existsByUserNameAndPidNot("otherUserName", 123L));
  }

  private static void assertUserIsPresent(List<User> users, String... usernames) {
    Set<String> set = users.stream().map(User::getUserName).collect(Collectors.toSet());
    for (String u : usernames) {
      assertTrue(set.contains(u), "Query result should contains user with login: " + u);
    }
  }
}
