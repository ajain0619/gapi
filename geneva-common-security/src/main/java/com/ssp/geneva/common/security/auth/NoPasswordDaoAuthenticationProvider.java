package com.ssp.geneva.common.security.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class NoPasswordDaoAuthenticationProvider extends DaoAuthenticationProvider {

  @Override
  protected void additionalAuthenticationChecks(
      UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    // This method is responsible for password checking, by overriding it we're skipping
    // password verification.
    // @see:
    // org.springframework.security.authentication.dao.DaoAuthenticationProvider.additionalAuthenticationChecks
  }
}
