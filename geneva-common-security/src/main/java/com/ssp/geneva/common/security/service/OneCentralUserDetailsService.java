package com.ssp.geneva.common.security.service;

import com.ssp.geneva.common.security.model.UserAuth;
import java.io.Serializable;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface OneCentralUserDetailsService extends UserDetailsService, Serializable {

  UserDetails loadUserDetailsBy1CUsername(
      final Map<String, Object> authenticationData, boolean bearerAuthentication);

  UserAuth loadUserBy1CUsername(
      Map<String, Object> authenticationData, boolean bearerAuthentication);

  void updateUserMigrated(Long userPid);
}
