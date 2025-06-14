package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BuyerGroupDTOMapperTest {

  @Test
  void shouldDoNothingEntityToDTO() {
    BuyerGroup source = null;
    BuyerGroupDTO result = BuyerGroupDTOMapper.MAPPER.map(source);
    assertNull(result);
  }

  @Test
  void shouldDoNothingDTOtoEntity() {
    BuyerGroupDTO source = null;
    BuyerGroup result = BuyerGroupDTOMapper.MAPPER.map(source);
    assertNull(result);
  }

  @Test
  void shouldMapEntityToDTO() {
    final long pid = new Random().nextLong();
    final long companyPid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();
    final Company company = mock(Company.class);
    when(company.getPid()).thenReturn(companyPid);
    BuyerGroup source = new BuyerGroup();
    source.setPid(pid);
    source.setName(name);
    source.setCompany(company);

    BuyerGroupDTO result = BuyerGroupDTOMapper.MAPPER.map(source);
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getName(), source.getName());
    assertEquals(result.getCompanyPid(), source.getCompany().getPid());
  }

  @Test
  void shouldMapDTOtoEntity() {
    final long pid = new Random().nextLong();
    final long companyPid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();
    final Company company = mock(Company.class);
    when(company.getPid()).thenReturn(companyPid);
    BuyerGroupDTO source =
        BuyerGroupDTO.builder().pid(pid).name(name).companyPid(companyPid).build();

    BuyerGroup result = BuyerGroupDTOMapper.MAPPER.map(source);
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getName(), source.getName());
    assertEquals(result.getCompany().getPid(), source.getCompanyPid());
  }

  @Test
  void shouldManualMap() {
    final long pid = new Random().nextLong();
    final long companyPid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();
    final Company company = mock(Company.class);
    when(company.getPid()).thenReturn(companyPid);
    BuyerGroup source = new BuyerGroup();
    source.setPid(pid);
    source.setName(name);
    source.setCompany(company);

    BuyerGroupDTO result = BuyerGroupDTOMapper.MAPPER.manualMap(source);
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getName(), source.getName());
    assertEquals(result.getCompanyPid(), source.getCompany().getPid());
  }
}
