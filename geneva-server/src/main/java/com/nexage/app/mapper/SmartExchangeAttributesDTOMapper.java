package com.nexage.app.mapper;

import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.sparta.jpa.model.SmartExchangeAttributes;
import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface SmartExchangeAttributesDTOMapper {

  SmartExchangeAttributesDTOMapper SMART_EXCHANGE_ATTR_MAPPER =
      Mappers.getMapper(SmartExchangeAttributesDTOMapper.class);

  @Mapping(target = "version", ignore = true)
  @Mapping(source = "source.smartMarginEnabled", target = "smartMarginOverride")
  @Mapping(source = "sellerAttributes", target = "sellerAttributes")
  SmartExchangeAttributes map(SmartExchangeAttributesDTO source, SellerAttributes sellerAttributes);

  @Mapping(target = "pid", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "sellerAttributes", ignore = true)
  @Mapping(target = "createdOn", ignore = true)
  @Mapping(target = "updatedOn", ignore = true)
  void updateOriginal(
      SmartExchangeAttributes updated, @MappingTarget SmartExchangeAttributes original);

  @Mapping(source = "smartMarginOverride", target = "smartMarginEnabled")
  SmartExchangeAttributesDTO map(SmartExchangeAttributes source);
}
