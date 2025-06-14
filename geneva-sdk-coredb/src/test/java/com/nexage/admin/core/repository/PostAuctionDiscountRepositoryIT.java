package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/post-auction-discount-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class PostAuctionDiscountRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private PostAuctionDiscountRepository repository;

  @Test
  void shouldFindPadsWithMatchingDspSeatSellerPair() {
    List<Long> dspSeatPids = List.of(1111L);
    List<Long> sellerPids = List.of(1114L);

    List<PostAuctionDiscount> returnedPads =
        repository.findByDspSellerPairIncludingRevenueGroups(dspSeatPids, sellerPids, null);

    assertEquals(2, returnedPads.size());
    assertEquals(
        Set.of(1L, 2L),
        returnedPads.stream().map(PostAuctionDiscount::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindPadsWithMatchingDspSeatRevenueGroupPair() {
    List<Long> dspSeatPids = List.of(1112L);
    List<Long> revGroupPids = List.of(1L);

    List<PostAuctionDiscount> returnedPads =
        repository.findByDspSellerPairIncludingRevenueGroups(dspSeatPids, null, revGroupPids);

    assertEquals(2, returnedPads.size());
    assertEquals(
        Set.of(3L, 4L),
        returnedPads.stream().map(PostAuctionDiscount::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindPadsWithMatchingDspSeatSellerThroughRevenueGroupPair() {
    List<Long> dspSeatPids = List.of(1113L);
    List<Long> sellerPids = List.of(1115L);

    List<PostAuctionDiscount> returnedPads =
        repository.findByDspSellerPairIncludingRevenueGroups(dspSeatPids, sellerPids, null);

    assertEquals(2, returnedPads.size());
    assertEquals(
        Set.of(5L, 6L),
        returnedPads.stream().map(PostAuctionDiscount::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindPadsWithMatchingDspSeatRevenueGroupThroughSellerPair() {
    List<Long> dspSeatPids = List.of(1114L);
    List<Long> revGroupPids = List.of(3L);

    List<PostAuctionDiscount> returnedPads =
        repository.findByDspSellerPairIncludingRevenueGroups(dspSeatPids, null, revGroupPids);

    assertEquals(2, returnedPads.size());
    assertEquals(
        Set.of(7L, 8L),
        returnedPads.stream().map(PostAuctionDiscount::getPid).collect(Collectors.toSet()));
  }
}
