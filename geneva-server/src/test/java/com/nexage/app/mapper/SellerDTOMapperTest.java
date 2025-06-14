package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.app.dto.seller.SellerDTO;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SellerDTOMapperTest {

  @Test
  void shouldMap() {
    final long pid = new Random().nextLong();
    final String id = UUID.randomUUID().toString();
    final String name = UUID.randomUUID().toString();
    final long revenueGroupPid = new Random().nextLong();
    Company source = new Company();
    source.setPid(pid);
    source.setId(id);
    source.setName(name);
    SellerAttributes sa = new SellerAttributes();
    sa.setRevenueGroupPid(revenueGroupPid);
    source.setSellerAttributes(sa);
    SellerDTO result = SellerDTOMapper.MAPPER.map(source);
    assertNotNull(result);
    assertEquals(source.getPid(), result.getPid());
    assertEquals(source.getId(), result.getId());
    assertEquals(source.getName(), result.getName());
    assertEquals(source.getSellerAttributes().getRevenueGroupPid(), result.getRevenueGroupPid());
  }
}
