package com.nexage.app.util;

import com.ssp.geneva.common.security.error.GenevaSecurityException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Log4j2
public class UserAuthenticationUtils {

  private UserAuthenticationUtils() {}

  public static void setAuthenticationForJob(
      UserDetailsService userDetailsService, String jobUserName) {
    UserDetails principal;

    try {
      principal = userDetailsService.loadUserByUsername(jobUserName);
    } catch (UsernameNotFoundException | GenevaSecurityException e) {
      log.error(
          "Unable to set authentication/authorization for the job to proceed. Reason : {}. Aborting...",
          e.getMessage());
      throw e;
    }

    SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), principal.getAuthorities()));
  }

  public static void clearAuthentication() {
    SecurityContextHolder.getContext().setAuthentication(null);
    SecurityContextHolder.clearContext();
  }
}
