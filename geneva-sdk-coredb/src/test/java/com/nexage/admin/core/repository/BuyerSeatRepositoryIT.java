package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Sets;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/buyer-seat-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BuyerSeatRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private BuyerSeatRepository buyerSeatRepository;

  @Test
  void shouldCountSeatsByExistingCompanyPidAndExistingSeatsCorrectly() {
    final long count =
        buyerSeatRepository.countByCompanyPidAndSeatIn(
            500L, Sets.newHashSet("seat_1", "seat_2", "seat_99", "seat_whatever"));

    assertEquals(2L, count);
    assertEquals("name_1", buyerSeatRepository.findBySeatAndCompanyPid("seat_1", 500L).getName());
  }
}
