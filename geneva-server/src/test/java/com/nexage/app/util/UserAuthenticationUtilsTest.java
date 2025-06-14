package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.User;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.service.UserDetailsServiceImpl;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationUtilsTest {

  @Mock UserDetailsServiceImpl userDetailsService;

  @Test
  void shouldSetAuthenticationForJob() {
    // given
    User user = new User();
    user.setUserName("admin");
    user.setSellerSeat(new SellerSeat());
    when(userDetailsService.loadUserByUsername("admin")).thenReturn(user);

    // when
    UserAuthenticationUtils.setAuthenticationForJob(userDetailsService, "admin");

    // then
    assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
  }

  @ParameterizedTest
  @MethodSource("getExceptions")
  void shouldRethrowExceptions(Class<? extends RuntimeException> exception) {
    // given
    when(userDetailsService.loadUserByUsername("admin")).thenThrow(exception);

    // when
    assertThrows(
        exception,
        () -> UserAuthenticationUtils.setAuthenticationForJob(userDetailsService, "admin"));
  }

  @Test
  void shouldClearAuthentication() {
    // when
    UserAuthenticationUtils.clearAuthentication();

    // then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  private static Stream<Class<? extends RuntimeException>> getExceptions() {
    return Stream.of(GenevaSecurityException.class, UsernameNotFoundException.class);
  }
}
