package com.nexage.admin.core.model.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

class PostAuctionDiscountTest {

  @Test
  void shouldSetCreatedOnAndUpdatedOnWhenBothAreNull() {
    // given
    PostAuctionDiscount postAuctionDiscount = new PostAuctionDiscount();
    Date beforePrePersist = Date.from(Instant.now());

    // when
    postAuctionDiscount.prePersist();

    // then
    Date afterPrePersist = Date.from(Instant.now());
    assertEquals(postAuctionDiscount.getCreationDate(), postAuctionDiscount.getLastUpdate());
    assertTrue(
        isDateBetween(postAuctionDiscount.getCreationDate(), beforePrePersist, afterPrePersist));
  }

  @Test
  void shouldSetUpdatedOnOnlyWhenCreatedOnIsNotNull() {
    // given
    PostAuctionDiscount postAuctionDiscount = new PostAuctionDiscount();
    postAuctionDiscount.setCreationDate(Date.from(Instant.EPOCH));
    Date beforePrePersist = Date.from(Instant.now());

    // when
    postAuctionDiscount.prePersist();

    // then
    Date afterPrePersist = Date.from(Instant.now());
    assertEquals(Date.from(Instant.EPOCH), postAuctionDiscount.getCreationDate());
    assertTrue(
        isDateBetween(postAuctionDiscount.getLastUpdate(), beforePrePersist, afterPrePersist));
  }

  private boolean isDateBetween(Date date, Date start, Date end) {
    return date.compareTo(start) >= 0 && date.compareTo(end) <= 0;
  }
}
