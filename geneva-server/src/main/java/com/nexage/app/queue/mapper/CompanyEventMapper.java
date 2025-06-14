package com.nexage.app.queue.mapper;

import com.nexage.admin.core.model.Company;
import com.nexage.app.queue.model.CompanyEventMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyEventMapper {

  CompanyEventMapper MAPPER = Mappers.getMapper(CompanyEventMapper.class);

  @Mapping(source = "pid", target = "id")
  @Mapping(target = "status", constant = "CREATE")
  CompanyEventMessage map(Company company);
}
