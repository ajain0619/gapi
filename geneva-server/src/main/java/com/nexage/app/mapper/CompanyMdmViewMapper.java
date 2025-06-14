package com.nexage.app.mapper;

import com.nexage.admin.core.model.CompanyMdmView;
import com.nexage.admin.core.model.MdmId;
import com.nexage.app.dto.InventoryMdmIdDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface CompanyMdmViewMapper {

  CompanyMdmViewMapper MAPPER = Mappers.getMapper(CompanyMdmViewMapper.class);

  @Mapping(source = "pid", target = "sellerPid")
  @Mapping(source = "mdmIds", target = "companyMdmIds")
  @Mapping(source = "sellerSeat.mdmIds", target = "sellerSeatMdmIds")
  InventoryMdmIdDTO map(CompanyMdmView companyMdmView);

  default String map(MdmId mdmId) {
    return mdmId.getId();
  }
}
