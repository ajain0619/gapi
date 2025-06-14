package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.model.RtbProfileGroup;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherRtbProfileLibraryAssemblerTest {
  @Mock PublisherRTBProfileGroupAssembler publisherRTBProfileGroupAssembler;
  @InjectMocks PublisherRTBProfileLibraryAssembler publisherRTBProfileLibraryAssembler;

  private RtbProfileLibrary rtbProfileLibrary;

  @BeforeEach
  void setup() {
    rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(1L);
    rtbProfileLibrary.setName("name");
    rtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.GLOBAL);
    rtbProfileLibrary.setPublisherPid(1L);
    RtbProfileGroup rtbProfileGroup = new RtbProfileGroup();
    rtbProfileGroup.setPid(1L);
    rtbProfileLibrary.setGroups(Set.of(rtbProfileGroup));
  }

  @Test
  void shouldReturnDTOFromBaseObject() {

    given(publisherRTBProfileGroupAssembler.make(any()))
        .willReturn(new PublisherRTBProfileGroupDTO());

    PublisherRTBProfileLibraryDTO resultProfileLibrary =
        publisherRTBProfileLibraryAssembler.make(rtbProfileLibrary);

    assertEquals(rtbProfileLibrary.getPid(), resultProfileLibrary.getPid());
    assertEquals(rtbProfileLibrary.getName(), resultProfileLibrary.getName());
    assertEquals(
        rtbProfileLibrary.getPrivilegeLevel().name(), resultProfileLibrary.getPrivilegeLevel());
    assertEquals(rtbProfileLibrary.getPublisherPid(), resultProfileLibrary.getPublisherPid());
  }

  @Test
  void shouldApplyDtoToBaseObject() {
    PublisherRTBProfileLibraryDTO publisherRTBProfileLibraryDTO =
        new PublisherRTBProfileLibraryDTO(
            rtbProfileLibrary.getPid(),
            rtbProfileLibrary.getName(),
            rtbProfileLibrary.getVersion(),
            rtbProfileLibrary.getPrivilegeLevel().name(),
            rtbProfileLibrary.getPublisherPid(),
            rtbProfileLibrary.isDefaultEligible());

    RtbProfileLibrary rtbProfileLibraryApply = new RtbProfileLibrary();
    publisherRTBProfileLibraryAssembler.apply(
        rtbProfileLibraryApply, publisherRTBProfileLibraryDTO);

    assertEquals(rtbProfileLibrary.getName(), rtbProfileLibraryApply.getName());
    assertEquals(rtbProfileLibrary.getPrivilegeLevel(), rtbProfileLibraryApply.getPrivilegeLevel());
    assertEquals(rtbProfileLibrary.getPublisherPid(), rtbProfileLibraryApply.getPublisherPid());
    assertEquals(rtbProfileLibrary.isDefaultEligible(), rtbProfileLibraryApply.isDefaultEligible());
  }

  @Test
  void shouldApplyGroups() {
    PublisherRTBProfileLibraryDTO publisherRTBProfileLibraryDTO =
        new PublisherRTBProfileLibraryDTO();
    PublisherRTBProfileGroupDTO publisherRtbProfileGroup =
        new PublisherRTBProfileGroupDTO(1L, "name", 0, "none", "data", 0, 0, 1L, true);
    RtbProfileGroup rtbProfileGroup = new RtbProfileGroup();
    rtbProfileGroup.setPid(1L);
    rtbProfileGroup.setName("testName");
    publisherRTBProfileLibraryDTO.setGroups(Set.of(publisherRtbProfileGroup));
    given(publisherRTBProfileGroupAssembler.apply(any(), any())).willReturn(rtbProfileGroup);

    RtbProfileLibrary resultProfileLibrary =
        publisherRTBProfileLibraryAssembler.applyGroups(
            rtbProfileLibrary, publisherRTBProfileLibraryDTO);

    for (RtbProfileGroup resultGroup : resultProfileLibrary.getGroups()) {
      assertEquals(rtbProfileGroup.getName(), resultGroup.getName());
    }
  }
}
