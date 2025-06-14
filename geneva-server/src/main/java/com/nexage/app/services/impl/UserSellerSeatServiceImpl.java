package com.nexage.app.services.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.SellerSeatRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.UserSellerSeatService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Log4j2
public class UserSellerSeatServiceImpl implements UserSellerSeatService {

  private final SellerSeatRepository sellerSeatRepository;
  private final UserContext userContext;

  public UserSellerSeatServiceImpl(
      SellerSeatRepository sellerSeatRepository, UserContext userContext) {
    this.sellerSeatRepository = sellerSeatRepository;
    this.userContext = userContext;
  }

  @Override
  @Transactional
  public void updateUserWithVerifiedSellerSeat(User user, long sellerSeatPid) {
    checkSellerSeatAffiliation(sellerSeatPid);
    SellerSeat sellerSeat = getValidSellerSeat(sellerSeatPid);
    user.setSellerSeat(sellerSeat);
    user.getCompanies().clear();
    sellerSeat.getSellers().forEach(user::addCompany);
  }

  private void checkSellerSeatAffiliation(Long pid) {
    if (!userContext.hasAccessToSellerSeatOrHasNexageAffiliation(pid)) {
      log.error(
          "Logged in user should be assigned to the same seller seat as created user or be Nexage user");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  private SellerSeat getValidSellerSeat(Long pid) {
    SellerSeat sellerSeat =
        sellerSeatRepository
            .findById(pid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND));
    if (isEmpty(sellerSeat.getSellers())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_SELLER_SEAT_WITHOUT_SELLERS_NOT_ALLOWED);
    }
    if (sellerSeat.isDisabled()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_ENABLED);
    }
    return sellerSeat;
  }
}
