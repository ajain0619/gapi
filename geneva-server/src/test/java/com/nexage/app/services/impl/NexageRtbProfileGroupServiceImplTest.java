package com.nexage.app.services.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NexageRtbProfileGroupServiceImplTest {

  @Mock private RtbProfileGroupServiceImpl rtbProfileGroupService;
  @InjectMocks private NexageRtbProfileGroupServiceImpl nexageRtbProfileGroupService;

  @Test
  void shouldInvokeCorrectServiceWhenUpdatingRTBProfileGroup() {
    // given
    PublisherRTBProfileGroupDTO group = new PublisherRTBProfileGroupDTO();
    // when
    nexageRtbProfileGroupService.update(1L, group);
    // then
    verify(rtbProfileGroupService).update(1L, group);
    verifyNoMoreInteractions(rtbProfileGroupService);
  }
}
