package com.nexage.admin.core.specification;

import static com.nexage.admin.core.specification.PositionDealsSpecification.withDealPidAndSellerIds;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.DealPositionRepository;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {
      "/data/repository/deals-common.sql",
      "/data/repository/site-repository.sql",
      "/data/repository/deal-position-repository.sql"
    },
    config = @SqlConfig(encoding = "utf-8"))
class PositionDealsSpecificationIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private DealPositionRepository dealPositionRepository;

  private static Stream<Arguments> data() {
    return Stream.of(Arguments.of(1L, List.of(11L), 2), Arguments.of(1L, List.of(2L), 0));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldCorrectlySelectDealsWithDealPidAndSellerIds(
      Long dealId, List<Long> sellerIds, int expected) {
    var dealsCount = dealPositionRepository.count(withDealPidAndSellerIds(dealId, sellerIds));
    Assertions.assertEquals(expected, dealsCount);
  }
}
