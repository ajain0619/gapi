package com.nexage.app.services.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.nexage.app.services.RtbProfileLibraryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NexageRtbProfileLibraryServiceImplTest {

  @Mock private RtbProfileLibraryService rtbProfileLibraryService;
  @InjectMocks private NexageRtbProfileLibraryServiceImpl nexageRTBProfileLibraryService;

  @Test
  void shouldInvokeCorrectServiceWhenGettingRTBProfileLibrary() {
    // given
    long libraryPid = 1L;
    // when
    nexageRTBProfileLibraryService.get(libraryPid);
    // then
    verify(rtbProfileLibraryService).get(libraryPid);
    verifyNoMoreInteractions(rtbProfileLibraryService);
  }

  @Test
  void shouldInvokeCorrectServiceWhenDeletingRTBProfileLibrary() {
    // given
    long libraryPid = 1L;
    // when
    nexageRTBProfileLibraryService.delete(libraryPid);
    // then
    verify(rtbProfileLibraryService).delete(libraryPid);
    verifyNoMoreInteractions(rtbProfileLibraryService);
  }
}
