package com.nexage.app.mapper.deal;

import com.nexage.admin.core.model.DealInventory;
import com.nexage.app.dto.deals.DealInventoriesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DealInventoriesDTOMapper {

  DealInventoriesDTOMapper MAPPER = Mappers.getMapper(DealInventoriesDTOMapper.class);

  DealInventoriesDTO map(DealInventory dealInventory);

  DealInventory map(DealInventoriesDTO dealInventoriesDTO);
}
