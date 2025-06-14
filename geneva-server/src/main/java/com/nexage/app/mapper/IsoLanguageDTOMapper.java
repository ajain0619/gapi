package com.nexage.app.mapper;

import com.nexage.admin.core.model.IsoLanguage;
import com.nexage.app.dto.IsoLanguageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IsoLanguageDTOMapper {
  IsoLanguageDTOMapper MAPPER = Mappers.getMapper(IsoLanguageDTOMapper.class);

  IsoLanguageDTO map(IsoLanguage source);
}
