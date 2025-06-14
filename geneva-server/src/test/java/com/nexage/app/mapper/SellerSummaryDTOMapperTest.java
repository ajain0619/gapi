package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.aggregation.CompanyMetricsAggregation;
import com.nexage.app.dto.seller.SellerSummaryDTO;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SellerSummaryDTOMapperTest {

  @Test
  void shouldMap() {
    final long pid = new Random().nextLong();
    final String id = UUID.randomUUID().toString();
    CompanyMetricsAggregation source = CompanyMetricsAggregation.builder().pid(pid).id(id).build();
    SellerSummaryDTO result = SellerSummaryDTOMapper.MAPPER.map(source);
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getId(), source.getId());
  }
}
