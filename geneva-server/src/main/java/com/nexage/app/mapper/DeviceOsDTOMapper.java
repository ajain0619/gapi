package com.nexage.app.mapper;

import com.nexage.admin.core.model.DeviceOs;
import com.nexage.app.dto.DeviceOsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeviceOsDTOMapper {
  DeviceOsDTOMapper MAPPER = Mappers.getMapper(DeviceOsDTOMapper.class);

  DeviceOsDTO map(DeviceOs source);
}
