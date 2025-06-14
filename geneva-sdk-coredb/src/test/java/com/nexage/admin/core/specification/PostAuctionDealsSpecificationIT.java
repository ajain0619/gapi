package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.DirectDealRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/specification/post-auction-deal-specification.sql",
    config = @SqlConfig(encoding = "utf-8"))
class PostAuctionDealsSpecificationIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private DirectDealRepository directDealRepository;

  @Test
  void shouldCorrectlySelectDealsWithSellerIdsAndBuyerSeats() {
    var deals =
        directDealRepository.findAll(
            PostAuctionDealsSpecification.withSellersAndDSPs(
                Set.of(1L, 2L),
                null,
                "\\{\"buyerCompany\":10205,[^{]*\"seats\":\\\\\\[\"1\"|\"2\"",
                "\\{\"buyerCompany\":10205,?"));
    assertEquals(1, deals.size());
  }

  @Test
  void shouldCorrectlySelectDealsWithSellerIdsAndBuyerSeatsAndDealIds() {
    var deals =
        directDealRepository.findAll(
            PostAuctionDealsSpecification.withSellersAndDSPs(
                Set.of(1L, 2L),
                Set.of("5"),
                "\\{\"buyerCompany\":10205,[^{]*\"seats\":\\\\\\[\"1\"|\"2\"",
                "\\{\"buyerCompany\":10205,?"));
    assertEquals("5", deals.get(0).getDealId());
  }
}
