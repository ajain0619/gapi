package com.nexage.app.services.user;

import com.nexage.admin.core.model.User;

public interface UserOneCentralService {
  /**
   * Create new user
   *
   * @param user {@link User} to be created.
   * @return user of created user
   */
  User createUser(User user);

  /**
   * Update user details
   *
   * @param user {@link User} to be updated.
   * @return updated {@link User}
   */
  User updateUser(User user);
}
