package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.ContentGenre;
import com.nexage.admin.core.repository.ContentGenreRepository;
import com.nexage.app.dto.ContentGenreDTO;
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
class ContentGenreServiceImplTest {
  @Mock private ContentGenreRepository contentGenreRepository;

  @Mock private Pageable pageable;

  @InjectMocks private ContentGenreServiceImpl contentGenreService;

  Page<ContentGenre> pagedEntity;

  @BeforeEach
  void setup() {
    pagedEntity = new PageImpl<>(List.of(new ContentGenre(1L, "Action", true)));
  }

  @Test
  void shouldFindAllGenre() {
    when(contentGenreRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);
    Page<ContentGenreDTO> returnedPage = contentGenreService.findAll("", null, pageable);

    assertEquals(1, returnedPage.getTotalElements());
    assertEquals("Action", returnedPage.stream().findFirst().get().getGenre());
  }
}
