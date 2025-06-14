package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.bidder.model.BDRLineItem;
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
    scripts = {
      "/data/repository/bdr-insertion-order-repository.sql",
      "/data/repository/bdr-line-item-repository.sql"
    },
    config = @SqlConfig(encoding = "utf-8"))
class BdrLineItemRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private BdrLineItemRepository lineItemRepository;

  @Test
  void shouldFindById() {
    BDRLineItem lineItem = lineItemRepository.findById(1L).orElse(new BDRLineItem());
    assertEquals(1L, lineItem.getPid());
  }

  @Test
  void shouldFindAll() {
    // when
    List<BDRLineItem> bdrLineItems = lineItemRepository.findAll();

    // then
    assertEquals(
        Set.of(1L, 2L), bdrLineItems.stream().map(BDRLineItem::getPid).collect(Collectors.toSet()));
  }
}
