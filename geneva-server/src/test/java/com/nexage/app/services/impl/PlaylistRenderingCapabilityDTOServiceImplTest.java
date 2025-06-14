package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.PlaylistRenderingCapability;
import com.nexage.admin.core.repository.PlaylistRenderingCapabilityRepository;
import com.nexage.app.dto.PlaylistRenderingCapabilityDTO;
import com.nexage.app.mapper.PlaylistRenderingCapabilityDTOMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class PlaylistRenderingCapabilityDTOServiceImplTest {

  @Mock private PlaylistRenderingCapabilityRepository playlistRenderingCapabilityRepository;
  @Mock private PlaylistRenderingCapabilityDTOMapper playlistRenderingCapabilityDTOMapper;

  @InjectMocks
  private PlaylistRenderingCapabilityDTOServiceImpl playlistRenderingCapabilityDTOService;

  @Test
  void shouldDelegatePageRequestToRepositoryAndMapToDTO() {
    var cap1 = new PlaylistRenderingCapability();
    var cap2 = new PlaylistRenderingCapability();
    var capDto1 = new PlaylistRenderingCapabilityDTO();
    var capDto2 = new PlaylistRenderingCapabilityDTO();
    var pageable = PageRequest.of(0, 10);

    when(playlistRenderingCapabilityRepository.findAllByStatus(same(pageable), eq(Status.ACTIVE)))
        .thenReturn(new PageImpl<>(List.of(cap1, cap2)));
    when(playlistRenderingCapabilityDTOMapper.map(same(cap1))).thenReturn(capDto1);
    when(playlistRenderingCapabilityDTOMapper.map(same(cap2))).thenReturn(capDto2);

    var result = playlistRenderingCapabilityDTOService.getPage(pageable);

    var content = result.getContent();
    assertEquals(2, content.size());
    assertSame(capDto1, content.get(0));
    assertSame(capDto2, content.get(1));
  }
}
