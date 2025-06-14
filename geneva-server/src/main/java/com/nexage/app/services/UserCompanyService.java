package com.nexage.app.services;

import com.nexage.admin.core.model.User;
import java.util.Set;
import java.util.function.ToLongFunction;

public interface UserCompanyService {

  /**
   * Update the supplied User (which contains companies with only the pid specified) with fully
   * populated set of Companies pulled from the database.
   *
   * @param user to be updated
   * @param pidSupplier pid supplier
   */
  <T> void updateUserWithVerifiedCompany(
      User user, Set<T> companies, ToLongFunction<T> pidSupplier);
}
