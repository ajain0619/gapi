package com.nexage.app.mapper.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.filter.FilterList;
import com.nexage.admin.core.model.filter.FilterListUploadStatus;
import com.nexage.app.dto.filter.FilterListDTO;
import com.nexage.app.dto.filter.FilterListTypeDTO;
import com.nexage.app.dto.filter.FilterListUploadStatusDTO;
import com.nexage.app.mapper.FilterListMapper;
import java.sql.Date;
import org.junit.jupiter.api.Test;

class FilterListMapperTest {

  private FilterListMapper filterListMapper = FilterListMapper.MAPPER;

  @Test
  void shouldMapToEntity() {

    FilterListDTO filterListDTO =
        FilterListDTO.builder()
            .buyerId(72L)
            .uploadStatus(FilterListUploadStatusDTO.PENDING)
            .type(FilterListTypeDTO.DOMAIN)
            .name("FilterListName")
            .invalid(1)
            .error(2)
            .duplicate(3)
            .total(7)
            .pid(16)
            .created(new Date(111111111L))
            .updated(new Date(222222222L))
            .version(16)
            .build();

    FilterList filterList = filterListMapper.map(filterListDTO);

    assertEquals(FilterListUploadStatus.PENDING, filterList.getUploadStatus());
    assertEquals(filterListDTO.getBuyerId(), filterList.getCompanyId());
    assertEquals(filterListDTO.getName(), filterList.getName());
    assertEquals(filterListDTO.getInvalid(), filterList.getInvalid());
    assertEquals(filterListDTO.getError(), filterList.getError());
    assertEquals(filterListDTO.getDuplicate(), filterList.getDuplicate());
    assertEquals(filterListDTO.getTotal(), filterList.getTotal());
    assertEquals(filterListDTO.getPid(), filterList.getPid());
    assertEquals(filterListDTO.getCreated(), filterList.getCreated());
    assertEquals(filterListDTO.getUpdated(), filterList.getUpdated());
    assertEquals(filterListDTO.getVersion(), filterList.getVersion());
  }

  @Test
  void shouldMapToDTO() {
    FilterList filterList = new FilterList();
    filterList.setCompanyId(72L);
    filterList.setName("FilterListName");
    filterList.setUploadStatus(FilterListUploadStatus.READY);
    filterList.setInvalid(1);
    filterList.setError(2);
    filterList.setDuplicate(3);
    filterList.setTotal(7);
    filterList.setPid(16);
    filterList.setCreated(new Date(333333333333L));
    filterList.setUpdated(new Date(444444444444L));
    filterList.setVersion(72);

    FilterListDTO filterListDTO = filterListMapper.map(filterList);
    int validCount =
        filterList.getTotal()
            - (filterList.getError() + filterList.getInvalid() + filterList.getDuplicate());
    assertEquals(filterList.getCompanyId(), filterListDTO.getBuyerId());
    assertEquals(filterList.getName(), filterListDTO.getName());
    assertEquals(FilterListUploadStatusDTO.READY, filterListDTO.getUploadStatus());
    assertEquals(validCount, filterListDTO.getValid());
    assertEquals(filterList.getInvalid(), filterListDTO.getInvalid());
    assertEquals(filterList.getError(), filterListDTO.getError());
    assertEquals(filterList.getDuplicate(), filterListDTO.getDuplicate());
    assertEquals(filterList.getTotal(), filterListDTO.getTotal());
    assertEquals(filterList.getPid(), filterListDTO.getPid());
    assertEquals(filterList.getCreated(), filterListDTO.getCreated());
    assertEquals(filterList.getUpdated(), filterListDTO.getUpdated());
    assertEquals(filterList.getVersion(), filterListDTO.getVersion());
  }
}
