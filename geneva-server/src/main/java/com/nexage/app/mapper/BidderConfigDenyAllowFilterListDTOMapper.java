package com.nexage.app.mapper;

import com.nexage.admin.core.model.filter.BidderConfigDenyAllowFilterList;
import com.nexage.app.dto.BidderConfigDenyAllowFilterListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface BidderConfigDenyAllowFilterListDTOMapper {

  /**
   * Map {@link BidderConfigDenyAllowFilterList} to {@link BidderConfigDenyAllowFilterListDTO}
   * representation.
   *
   * @param bidderConfigDenyAllowFilterList {@link BidderConfigDenyAllowFilterList} representation
   * @return {@link BidderConfigDenyAllowFilterListDTO} representation
   */
  @Mappings(
      value = {
        @Mapping(source = "filterListNonInclusive", target = "filterList"),
      })
  BidderConfigDenyAllowFilterListDTO map(
      BidderConfigDenyAllowFilterList bidderConfigDenyAllowFilterList);

  /**
   * Map {@link BidderConfigDenyAllowFilterListDTO} to {@link BidderConfigDenyAllowFilterList}
   * representation.
   *
   * @param bidderConfigDenyAllowFilterListDTO {@link BidderConfigDenyAllowFilterListDTO}
   *     representation
   * @return {@link BidderConfigDenyAllowFilterList} representation
   */
  @Mappings(
      value = {
        @Mapping(source = "filterList", target = "filterListNonInclusive"),
      })
  BidderConfigDenyAllowFilterList map(
      BidderConfigDenyAllowFilterListDTO bidderConfigDenyAllowFilterListDTO);
}
