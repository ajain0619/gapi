package com.nexage.admin.core.specification;

import static com.nexage.admin.core.specification.PublisherDealsSpecification.withDealPidAndSellerIds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.DealPublisherRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = {
      "/data/repository/deals-common.sql",
      "/data/repository/deal-publisher-repository.sql"
    },
    config = @SqlConfig(encoding = "utf-8"))
class PublisherDealsSpecificationIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private DealPublisherRepository dealPublisherRepository;

  @Test
  void shouldCorrectlySelectDealsWithDealPidAndSellerIds() {
    var dealPublishers =
        dealPublisherRepository.findAll(withDealPidAndSellerIds(1L, List.of(2L, 3l)));
    assertFalse(dealPublishers.isEmpty());
    assertEquals(2, dealPublishers.size());
  }
}
