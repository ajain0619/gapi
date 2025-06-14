package com.ssp.geneva.common.security.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class NoPasswordDaoAuthenticationProviderTest {

  private static final String USERNAME = "user";
  private NoPasswordDaoAuthenticationProvider noPasswordDaoAuthenticationProvider;

  @Mock private UserDetailsService mockUserDetailsService;

  private static User createTestUser() {
    User user = new User();
    user.setUserName(USERNAME);
    user.setPid(1L);
    user.setEnabled(true);
    user.addCompany(createTestCompany());
    return user;
  }

  private static Company createTestCompany() {
    Company company = new Company();
    company.setPid(1L);
    company.setSelfServeAllowed(false);
    company.setType(CompanyType.SELLER);
    return company;
  }

  @BeforeEach
  void setup() {
    noPasswordDaoAuthenticationProvider = new NoPasswordDaoAuthenticationProvider();
    noPasswordDaoAuthenticationProvider.setUserDetailsService(mockUserDetailsService);
  }

  @Test
  void shouldIgnorePasswordForAuthentication() {
    // given
    String password = "thisIsARandomPassword";
    UserDetails userDetails =
        new SpringUserDetails(new UserAuth(createTestUser(), Collections.emptyList()));
    when(mockUserDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

    // when
    Authentication authenticate =
        noPasswordDaoAuthenticationProvider.authenticate(
            new UsernamePasswordAuthenticationToken(USERNAME, password));

    // then
    assertTrue(authenticate.isAuthenticated());
  }

  @Test
  void shouldNotLetInUnknownUser() {
    // given
    when(mockUserDetailsService.loadUserByUsername(USERNAME))
        .thenThrow(new UsernameNotFoundException(""));
    Authentication authRequest = new UsernamePasswordAuthenticationToken(USERNAME, "any");

    // when && then
    assertThrows(
        BadCredentialsException.class,
        () -> noPasswordDaoAuthenticationProvider.authenticate(authRequest));
  }
}
