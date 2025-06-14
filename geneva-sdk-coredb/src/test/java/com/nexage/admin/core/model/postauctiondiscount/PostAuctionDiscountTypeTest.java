package com.nexage.admin.core.model.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

class PostAuctionDiscountTypeTest {

  @Test
  void shouldSetCreatedOnAndUpdatedOnWhenBothAreNull() {
    // given
    PostAuctionDiscountType padType = new PostAuctionDiscountType();
    Date beforePrePersist = Date.from(Instant.now());

    // when
    padType.setTimestamps();

    // then
    Date afterPrePersist = Date.from(Instant.now());
    assertEquals(padType.getCreatedOn(), padType.getUpdatedOn());
    assertTrue(isDateBetween(padType.getCreatedOn(), beforePrePersist, afterPrePersist));
  }

  @Test
  void shouldSetUpdatedOnOnlyWhenCreatedOnIsNotNull() {
    // given
    PostAuctionDiscountType padType = new PostAuctionDiscountType();
    padType.setCreatedOn(Date.from(Instant.EPOCH));
    Date beforePrePersist = Date.from(Instant.now());

    // when
    padType.setTimestamps();

    // then
    Date afterPrePersist = Date.from(Instant.now());
    assertEquals(Date.from(Instant.EPOCH), padType.getCreatedOn());
    assertTrue(isDateBetween(padType.getUpdatedOn(), beforePrePersist, afterPrePersist));
  }

  private boolean isDateBetween(Date date, Date start, Date end) {
    return date.compareTo(start) >= 0 && date.compareTo(end) <= 0;
  }
}
