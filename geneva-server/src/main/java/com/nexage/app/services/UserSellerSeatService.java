package com.nexage.app.services;

import com.nexage.admin.core.model.User;

public interface UserSellerSeatService {

  /**
   * Update the supplied User (which contains a sellerSeat object with only the pid specified) with
   * a fully populated SellerSeat object pulled from the database.
   *
   * @param user to be updated
   */
  void updateUserWithVerifiedSellerSeat(User user, long sellerSeatId);
}
