package com.nexage.app.mapper.deal;

import com.nexage.admin.core.sparta.jpa.model.DealTermView;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DealTermViewMapper {

  DealTermViewMapper MAPPER = Mappers.getMapper(DealTermViewMapper.class);

  SiteDealTerm map(DealTermView dealTermView);
}
