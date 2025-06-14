package com.nexage.app.manager;

import com.nexage.admin.core.model.User;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO.OneCentralUser;

public interface OneCentralUserManager {

  /**
   * Create user in One Central Service
   *
   * @param user {@link User} to be created.
   * @return created {@link OneCentralUser}
   */
  OneCentralUser createOneCentralUser(User user);

  /**
   * Update user in One Central Service
   *
   * @param user {@link User} to be updated.
   * @return updated {@link OneCentralUser}
   */
  OneCentralUser updateOneCentralUser(User user);

  /**
   * Reset password for a given user in One Central Service
   *
   * @param user {@link User} to be updated.
   * @param oldPassword old password to be changed.
   * @param newPassword new password to be used.
   * @return New password.
   */
  String resetPassword(User user, String oldPassword, String newPassword);

  /**
   * Verify if create user in One Central Service is enabled.
   *
   * @return true if enabled, false otherwise.
   */
  boolean createUserEnabled();
}
