package com.nexage.app.services.impl;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BDRAdvertiserRepository;
import com.nexage.admin.core.repository.BdrInsertionOrderRepository;
import com.nexage.admin.core.repository.BdrLineItemRepository;
import com.nexage.admin.core.repository.BdrTargetGroupRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BdrRelationshipValidationService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BdrRelationshipValidationServiceImpl implements BdrRelationshipValidationService {

  private final BdrTargetGroupRepository targetGroupRepository;
  private final BDRAdvertiserRepository bdrAdvertiserRepository;
  private final BdrInsertionOrderRepository bdrInsertionOrderRepository;
  private final BdrLineItemRepository bdrLineItemRepository;

  @Autowired
  public BdrRelationshipValidationServiceImpl(
      BdrTargetGroupRepository targetGroupRepository,
      BDRAdvertiserRepository bdrAdvertiserRepository,
      BdrInsertionOrderRepository bdrInsertionOrderRepository,
      BdrLineItemRepository bdrLineItemRepository) {
    this.targetGroupRepository = targetGroupRepository;
    this.bdrInsertionOrderRepository = bdrInsertionOrderRepository;
    this.bdrLineItemRepository = bdrLineItemRepository;
    this.bdrAdvertiserRepository = bdrAdvertiserRepository;
  }

  @Override
  public void validateRelationship(
      Long seatholderPid, Long insertionOrderPid, Long lineItemPid, Long targetGroupPid) {
    if (targetGroupPid != null) {
      var bdrTargetGroupOriginal =
          targetGroupRepository
              .findById(targetGroupPid)
              .orElseThrow(
                  () ->
                      new GenevaValidationException(
                          ServerErrorCodes.SERVER_TARGET_GROUP_NOT_FOUND));
      if (!Optional.of(bdrTargetGroupOriginal)
          .map(BdrTargetGroup::getLineitemPid)
          .map(pid -> pid.equals(lineItemPid))
          .orElse(false)) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_UNKNOWN_LINEITEM);
      }
    }

    if (lineItemPid != null) {
      BDRLineItem lineItem =
          bdrLineItemRepository
              .findById(lineItemPid)
              .orElseThrow(
                  () -> new GenevaValidationException(ServerErrorCodes.SERVER_LINEITEM_NOT_FOUND));
      if (!Optional.of(lineItem)
          .map(BDRLineItem::getInsertionOrderPid)
          .map(pid -> pid.equals(insertionOrderPid))
          .orElse(false)) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER);
      }
    }

    if (insertionOrderPid != null
        && !bdrInsertionOrderRepository
            .findById(insertionOrderPid)
            .map(BdrInsertionOrder::getAdvertiser)
            .map(BDRAdvertiser::getCompany)
            .map(Company::getPid)
            .map(pid -> pid.equals(seatholderPid))
            .orElse(false)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER);
    }
  }

  @Override
  public void validateRelationship(Long seatholderPid, Long advertiserPid) {
    if (!Optional.ofNullable(advertiserPid)
        .flatMap(bdrAdvertiserRepository::findById)
        .map(BDRAdvertiser::getCompany)
        .map(Company::getPid)
        .map(seatholderPid::equals)
        .orElse(false)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_NOT_RELATED);
    }
  }
}
