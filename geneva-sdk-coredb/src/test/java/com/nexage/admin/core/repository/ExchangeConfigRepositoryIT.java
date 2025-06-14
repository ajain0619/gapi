package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.ExchangeConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/exchange-config-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class ExchangeConfigRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final String AUCTION_BID_REQUEST_CPM = "auction.bid.request.cpm";

  @Autowired ExchangeConfigRepository exchangeConfigRepository;

  @Test
  void shouldFindByProperty() {
    // given / when
    ExchangeConfig rxConfig = exchangeConfigRepository.findByProperty(AUCTION_BID_REQUEST_CPM);

    // then
    assertNotNull(rxConfig);
    assertEquals(1, rxConfig.getPid());
    assertEquals(AUCTION_BID_REQUEST_CPM, rxConfig.getProperty());
    assertEquals("0.01", rxConfig.getValue());
  }
}
