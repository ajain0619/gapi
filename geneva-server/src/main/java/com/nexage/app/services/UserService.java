package com.nexage.app.services;

import com.nexage.admin.core.model.User;
import java.util.List;

public interface UserService {

  void changePassword(long user, String oldPasswd, String newPasswd);

  /**
   * @deprecated Use {@link com.nexage.app.services.user.UserDTOService#getUser(Long)} instead
   * @param pid the pid of the user
   * @return {@link User} object that matches the pid
   */
  User getUser(Long pid);

  List<User> getAllUsersByCompanyPid(long company);

  void restrictUserAccessToSites(long user, List<Long> sites);

  void allowUserAccessToSites(long user, List<Long> sites);

  void deleteUser(long user);
}
