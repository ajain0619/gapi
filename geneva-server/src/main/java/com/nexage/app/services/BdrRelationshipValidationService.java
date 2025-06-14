package com.nexage.app.services;

import com.ssp.geneva.common.error.exception.GenevaValidationException;

public interface BdrRelationshipValidationService {

  /**
   * Validates the relationships between seatholder, insertionOrder, lineItem and targetGroup.
   * Throws {@link GenevaValidationException} or {@link GenevaValidationException} when validation
   * fails.
   *
   * @param seatholderPid seatholder pid
   * @param insertionOrderPid insertion order pid
   * @param lineItemPid line item pid
   * @param targetGroupPid target group pid
   */
  void validateRelationship(
      Long seatholderPid, Long insertionOrderPid, Long lineItemPid, Long targetGroupPid);

  void validateRelationship(Long seatholderPid, Long advertiserPid);
}
