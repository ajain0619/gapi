package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.ContentRating;
import com.nexage.admin.core.repository.ContentRatingRepository;
import com.nexage.app.dto.ContentRatingDTO;
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
class ContentRatingServiceImplTest {
  @Mock private ContentRatingRepository contentRatingRepository;

  @Mock private Pageable pageable;

  @InjectMocks private ContentRatingServiceImpl contentRatingService;

  Page<ContentRating> pagedEntity;

  @BeforeEach
  void setup() {
    pagedEntity = new PageImpl<>(List.of(new ContentRating(1L, "NR", true)));
  }

  @Test
  void shouldFindAllRating() {
    when(contentRatingRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);
    Page<ContentRatingDTO> returnedPage = contentRatingService.findAll("", null, pageable);

    assertEquals(1, returnedPage.getTotalElements());
    assertEquals("NR", returnedPage.stream().findFirst().get().getRating());
  }
}
