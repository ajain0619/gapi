package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostAuctionDiscountRepository
    extends JpaRepository<PostAuctionDiscount, Long>,
        JpaSpecificationExecutor<PostAuctionDiscount> {

  /**
   * Find {@code PostAuctionDiscount}s that have matching {@code DspSeat}/{@code Seller} or {@code
   * DspSeat}/{@code RevenueGroup} pairs - that includes the {@code Seller}s that belong to the
   * given {@code RevenueGroup}s.
   *
   * @param dspSeatPids the {@code DspSeat} pids to match against
   * @param sellerPids the {@code Seller} pids to match against
   * @param revenueGroupPids the {@code RevenueGroup} pids to match against
   * @return a list of matching {@code PostAuctionDiscount}s
   */
  @Query(
      """
  select distinct pad
  from PostAuctionDiscount pad,
       SellerAttributes sellerAttr
  join pad.dsps as padDsp
  join padDsp.dsp as dspSeat
  left join pad.sellers as padSeller
  left join padSeller.seller as seller
  left join pad.revenueGroups as padRevGroup
  left join padRevGroup.revenueGroup as revGroup
  where dspSeat.pid in (:dspSeatPids)
    and (seller.pid in (:sellerPids)
      or revGroup.pid in (:revenueGroupPids)
      or (sellerAttr.revenueGroupPid in (:revenueGroupPids) and sellerAttr.sellerPid = seller.pid)
      or (revGroup.pid = sellerAttr.revenueGroupPid and sellerAttr.sellerPid in (:sellerPids)))
  """)
  List<PostAuctionDiscount> findByDspSellerPairIncludingRevenueGroups(
      Collection<Long> dspSeatPids, Collection<Long> sellerPids, Collection<Long> revenueGroupPids);
}
