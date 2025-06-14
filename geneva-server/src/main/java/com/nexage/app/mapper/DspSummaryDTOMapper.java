package com.nexage.app.mapper;

import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.dsp.DspSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DspSummaryDTOMapper {

  DspSummaryDTOMapper MAPPER = Mappers.getMapper(DspSummaryDTOMapper.class);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "pid", target = "pid")
  DspSummaryDTO map(Company source);
}
