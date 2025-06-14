package com.nexage.app.mapper;

import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BuyerGroupDTOMapper {

  BuyerGroupDTOMapper MAPPER = Mappers.getMapper(BuyerGroupDTOMapper.class);

  @Mapping(source = "company.pid", target = "companyPid")
  BuyerGroupDTO map(BuyerGroup source);

  @Mapping(source = "companyPid", target = "company.pid")
  BuyerGroup map(BuyerGroupDTO source);

  BuyerGroup map(@MappingTarget BuyerGroup target, BuyerGroupDTO source);

  /**
   * This method substitutes old code where a {@link BuyerGroupDTO} had a constructor accepting an
   * instance of type {@link BuyerGroup} as parameter. That logic has been removed.
   *
   * @param source {@link BuyerGroup}
   * @return instance of type {@link BuyerGroupDTO}
   */
  default BuyerGroupDTO manualMap(final BuyerGroup source) {
    return BuyerGroupDTO.builder()
        .pid(source.getPid())
        .name(source.getName())
        .sfdcLineId(source.getSfdcLineId())
        .sfdcIoId(source.getSfdcIoId())
        .currency(source.getCurrency())
        .billingCountry(source.getBillingCountry())
        .billable(source.isBillable())
        .version(source.getVersion())
        .companyPid(source.getCompany().getPid())
        .build();
  }
}
