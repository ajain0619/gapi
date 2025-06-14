package com.nexage.app.mapper;

import com.nexage.admin.core.model.aggregation.CompanyMetricsAggregation;
import com.nexage.app.dto.seller.SellerSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SellerSummaryDTOMapper {

  SellerSummaryDTOMapper MAPPER = Mappers.getMapper(SellerSummaryDTOMapper.class);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "pid", target = "pid")
  SellerSummaryDTO map(CompanyMetricsAggregation source);
}
