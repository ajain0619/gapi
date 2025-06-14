package com.nexage.app.mapper;

import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.dsp.DspDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DspDTOMapper {

  DspDTOMapper MAPPER = Mappers.getMapper(DspDTOMapper.class);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "pid", target = "pid")
  DspDTO map(Company source);
}
