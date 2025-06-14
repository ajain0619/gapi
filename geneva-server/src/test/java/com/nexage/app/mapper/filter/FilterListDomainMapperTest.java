package com.nexage.app.mapper.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.filter.Domain;
import com.nexage.admin.core.model.filter.FiilterListStatus;
import com.nexage.admin.core.model.filter.FilterListDomain;
import com.nexage.app.dto.filter.FilterListDomainDTO;
import com.nexage.app.dto.filter.MediaStatusDTO;
import com.nexage.app.mapper.FilterListDomainMapper;
import org.junit.jupiter.api.Test;

class FilterListDomainMapperTest {

  @Test
  void shouldMapToDTO() {
    FilterListDomain filterListDomain = new FilterListDomain();
    Domain domain = new Domain();
    domain.setDomain("bucknell.edu");
    filterListDomain.setPid(73);
    filterListDomain.setDomain(domain);
    filterListDomain.setStatus(FiilterListStatus.INVALID);

    FilterListDomainDTO filterListDomainDTO = FilterListDomainMapper.INSTANCE.map(filterListDomain);
    assertEquals(filterListDomain.getPid(), filterListDomainDTO.getPid());
    assertEquals(filterListDomain.getDomain().getDomain(), filterListDomainDTO.getDomain());
    assertEquals(
        filterListDomain.getStatus().toString(), filterListDomainDTO.getStatus().toString());
  }

  @Test
  void shouldMapToEntity() {
    FilterListDomainDTO filterListDomainDTO = new FilterListDomainDTO();
    filterListDomainDTO.setDomain("myDomain.com");
    filterListDomainDTO.setPid(72);
    filterListDomainDTO.setStatus(MediaStatusDTO.VALID);

    FilterListDomain filterListDomain = FilterListDomainMapper.INSTANCE.map(filterListDomainDTO);
    assertEquals(filterListDomainDTO.getPid(), filterListDomain.getPid());
    assertEquals(filterListDomainDTO.getDomain(), filterListDomain.getDomain().getDomain());
    assertEquals(
        filterListDomainDTO.getStatus().toString(), filterListDomain.getStatus().toString());
  }
}
