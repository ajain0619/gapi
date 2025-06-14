package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.TagView;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.app.dto.tag.TagDTO;
import com.nexage.app.mapper.TagDTOMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TagDTOServiceImplTest {
  @Mock TagRepository tagRepository;
  @Mock Pageable pageable;
  @InjectMocks private TagDTOServiceImpl tagSummaryDTOService;

  @Test
  void shouldGetTags() {
    Page<TagView> returnedAggregatedPage =
        new PageImpl<>(TestObjectsFactory.createTagAggregation(10));
    Page<TagDTO> expectedAggregatedPage =
        returnedAggregatedPage.map(tag -> TagDTOMapper.MAPPER.map(tag));
    when(tagRepository.findTags(eq(456L), eq(789L), any(Pageable.class)))
        .thenReturn(returnedAggregatedPage);
    Page<TagDTO> actualPage = tagSummaryDTOService.getTags(123L, 456L, 789L, pageable);
    assertEquals(
        expectedAggregatedPage.getContent().get(0).getPid(),
        actualPage.getContent().get(0).getPid());
  }
}
