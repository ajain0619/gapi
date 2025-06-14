package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.IsoLanguage;
import com.nexage.app.dto.IsoLanguageDTO;
import org.junit.jupiter.api.Test;

class IsoLanguageMapperTest {
  @Test
  void shouldMapModelToDTOWhenMapperIsApplied() {
    IsoLanguage isoLanguage = new IsoLanguage(1L, "English", "en");
    IsoLanguageDTO isoLanguageDTO = IsoLanguageDTOMapper.MAPPER.map(isoLanguage);
    assertEquals(isoLanguage.getPid(), isoLanguageDTO.getPid());
    assertEquals(isoLanguage.getLanguageName(), isoLanguageDTO.getLanguageName());
    assertEquals(isoLanguage.getLanguageCode(), isoLanguageDTO.getLanguageCode());
  }
}
