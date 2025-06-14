package com.nexage.app.mapper;

import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RTBProfileView;
import com.nexage.app.dto.RTBProfileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RTBProfileDTOMapper {
  RTBProfileDTOMapper MAPPER = Mappers.getMapper(RTBProfileDTOMapper.class);

  RTBProfileDTO map(RTBProfileView rtbProfileView);

  void map(RTBProfileDTO defaultRTBProfileDTO, @MappingTarget RTBProfile dbProfile);

  RTBProfileDTO map(RTBProfile rtbProfile);
}
