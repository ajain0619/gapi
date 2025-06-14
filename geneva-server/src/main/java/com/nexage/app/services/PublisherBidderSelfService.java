package com.nexage.app.services;

import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.app.dto.RuleDSPBiddersDTO;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.List;

/**
 * @deprecated Although the logic associated to Publisher Self-Serve is highly used and active
 *     within the core of the app, we want to avoid developers following same practices put in here.
 *     This is part of the old legacy pss context, our plan is to slowly migrate each business logic
 *     to its own separated self-serve to reduce class complexity and to follow single
 *     responsibility principle.
 */
@Legacy
@Deprecated
public interface PublisherBidderSelfService {

  /**
   * Get BidderSummaryDTO by publisher pid
   *
   * @param publisherPid pid for a given Publisher.
   * @return A {@link List} of {@link BidderSummaryDTO}
   */
  List<BidderSummaryDTO> getBidders(long publisherPid);

  /**
   * Get RuleDSPBiddersDTO by publisher pid
   *
   * @param publisherPid
   * @return A {@link List} of {@link RuleDSPBiddersDTO}
   */
  List<RuleDSPBiddersDTO> getRuleDSPBidders(long publisherPid);
}
