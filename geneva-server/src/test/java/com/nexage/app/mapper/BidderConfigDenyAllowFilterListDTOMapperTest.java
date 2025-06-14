package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.filter.BidderConfigDenyAllowFilterList;
import com.nexage.admin.core.model.filter.FilterList;
import com.nexage.app.dto.BidderConfigDenyAllowFilterListDTO;
import com.nexage.app.dto.filter.FilterListDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BidderConfigDenyAllowFilterListDTOMapperTest {
  private BidderConfigDenyAllowFilterListDTOMapper mapper;

  @BeforeEach
  void setup() {
    mapper = Mappers.getMapper(BidderConfigDenyAllowFilterListDTOMapper.class);
  }

  @Test
  void shouldMapFilterListNonInclusiveInModelSameAsFilterListInDTO() {
    BidderConfigDenyAllowFilterListDTO bidderConfigDenyAllowFilterListDTO =
        new BidderConfigDenyAllowFilterListDTO();
    FilterListDTO filterListDTO = new FilterListDTO();
    filterListDTO.setPid(2);
    bidderConfigDenyAllowFilterListDTO.setFilterList(filterListDTO);
    BidderConfigDenyAllowFilterList model = mapper.map(bidderConfigDenyAllowFilterListDTO);
    assertEquals(2, model.getFilterListNonInclusive().getPid());
  }

  @Test
  void shouldMapFilterListInDTOSameAsFilterListNonInclusiveInModel() {
    BidderConfigDenyAllowFilterList bidderConfigDenyAllowFilterList =
        new BidderConfigDenyAllowFilterList();
    FilterList filterList = new FilterList();
    filterList.setPid(4);
    bidderConfigDenyAllowFilterList.setFilterListNonInclusive(filterList);
    BidderConfigDenyAllowFilterListDTO dto = mapper.map(bidderConfigDenyAllowFilterList);
    assertEquals(4, dto.getFilterList().getPid());
  }
}
