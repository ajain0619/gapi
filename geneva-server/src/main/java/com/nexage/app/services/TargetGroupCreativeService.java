package com.nexage.app.services;

import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.app.dto.support.AssociatedCreativeDTO;
import java.util.Set;

/**
 * @author Eugeny Yurko
 * @since 22.10.2014
 */
public interface TargetGroupCreativeService {

  /**
   * Associates creatives with target group. Creatives added to passed target group.
   *
   * @param seatholderPid
   * @param lineItem
   * @param targetGroup
   * @param associatedCreatives
   */
  void associateCreatives(
      long seatholderPid,
      BDRLineItem lineItem,
      BdrTargetGroup targetGroup,
      Set<AssociatedCreativeDTO> associatedCreatives);
}
