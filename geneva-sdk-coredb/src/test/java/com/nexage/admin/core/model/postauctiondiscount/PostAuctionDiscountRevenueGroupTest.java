package com.nexage.admin.core.model.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

class PostAuctionDiscountRevenueGroupTest {

  @Test
  void shouldSetCreatedOnAndUpdatedOnWhenBothAreNull() {
    // given
    PostAuctionDiscountRevenueGroup padRevGroup = new PostAuctionDiscountRevenueGroup();
    Date beforePrePersist = Date.from(Instant.now());

    // when
    padRevGroup.setTimestamps();

    // then
    Date afterPrePersist = Date.from(Instant.now());
    assertEquals(padRevGroup.getCreatedOn(), padRevGroup.getUpdatedOn());
    assertTrue(isDateBetween(padRevGroup.getCreatedOn(), beforePrePersist, afterPrePersist));
  }

  @Test
  void shouldSetUpdatedOnOnlyWhenCreatedOnIsNotNull() {
    // given
    PostAuctionDiscountRevenueGroup padRevGroup = new PostAuctionDiscountRevenueGroup();
    padRevGroup.setCreatedOn(Date.from(Instant.EPOCH));
    Date beforePrePersist = Date.from(Instant.now());

    // when
    padRevGroup.setTimestamps();

    // then
    Date afterPrePersist = Date.from(Instant.now());
    assertEquals(Date.from(Instant.EPOCH), padRevGroup.getCreatedOn());
    assertTrue(isDateBetween(padRevGroup.getUpdatedOn(), beforePrePersist, afterPrePersist));
  }

  private boolean isDateBetween(Date date, Date start, Date end) {
    return date.compareTo(start) >= 0 && date.compareTo(end) <= 0;
  }
}
