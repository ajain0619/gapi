package com.nexage.app.mapper;

import com.nexage.admin.core.model.DeviceType;
import com.nexage.app.dto.DeviceTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeviceTypeDTOMapper {
  DeviceTypeDTOMapper MAPPER = Mappers.getMapper(DeviceTypeDTOMapper.class);

  DeviceTypeDTO map(DeviceType source);
}
