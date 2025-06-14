package com.nexage.app.services.impl;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BdrInsertionOrderRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.InsertionOrderService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("insertionOrderService")
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatHolder()")
public class InsertionOrderServiceImpl implements InsertionOrderService {

  private final BdrInsertionOrderRepository bdrInsertionOrderRepository;

  @Autowired
  public InsertionOrderServiceImpl(BdrInsertionOrderRepository bdrInsertionOrderRepository) {
    this.bdrInsertionOrderRepository = bdrInsertionOrderRepository;
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public BdrInsertionOrder getInsertionOrder(long seatholderPid, long insertionOrderPid) {
    BdrInsertionOrder insertionOrder =
        bdrInsertionOrderRepository
            .findById(insertionOrderPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER));
    validateOrderOwnership(insertionOrder, seatholderPid);
    return insertionOrder;
  }

  private void validateOrderOwnership(
      @NonNull BdrInsertionOrder insertionOrder, long seatholderPid) {
    if (!Optional.of(insertionOrder)
        .map(BdrInsertionOrder::getAdvertiser)
        .map(BDRAdvertiser::getCompany)
        .map(Company::getPid)
        .map(pid -> pid.equals(seatholderPid))
        .orElse(false)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_NOT_RELATED);
    }
  }
}
