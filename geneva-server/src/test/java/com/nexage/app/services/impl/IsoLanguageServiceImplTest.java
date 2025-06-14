package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.IsoLanguage;
import com.nexage.admin.core.repository.IsoLanguageRepository;
import com.nexage.app.dto.IsoLanguageDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class IsoLanguageServiceImplTest {
  @Mock private IsoLanguageRepository isoLanguageRepository;

  @Mock private Pageable pageable;

  @InjectMocks private IsoLanguageServiceImpl isoLanguageService;

  Page<IsoLanguage> pagedEntity;

  @BeforeEach
  void setup() {
    pagedEntity = new PageImpl<>(List.of(new IsoLanguage(1L, "English", "en")));
  }

  @Test
  void shouldFindAllLanguages() {
    when(isoLanguageRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);
    Page<IsoLanguageDTO> returnedPage = isoLanguageService.findAll("", null, pageable);

    assertEquals(1, returnedPage.getTotalElements());
    assertEquals("English", returnedPage.stream().findFirst().get().getLanguageName());
    assertEquals("en", returnedPage.stream().findFirst().get().getLanguageCode());
    assertEquals(1L, returnedPage.stream().findFirst().get().getPid());
  }
}
