package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.RtbProfileGroup;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublisherRTBProfileGroupAssemblerTest {
  private final PublisherRTBProfileGroupAssembler publisherRTBProfileGroupAssembler =
      new PublisherRTBProfileGroupAssembler();

  private RtbProfileGroup rtbProfileGroup;

  @BeforeEach
  void setup() {
    rtbProfileGroup = new RtbProfileGroup();
    rtbProfileGroup.setPid(1L);
    rtbProfileGroup.setListType(RtbProfileGroup.ListType.BLOCKLIST);
    rtbProfileGroup.setItemType(RtbProfileGroup.ItemType.CATEGORY);
    rtbProfileGroup.setUICustomGroup(true);
    rtbProfileGroup.setName("profile-1");
    rtbProfileGroup.setVersion(1);
    rtbProfileGroup.setData("data");
    rtbProfileGroup.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.GLOBAL);
    rtbProfileGroup.setPublisherPid(2L);
  }

  @Test
  void shouldMakeDtoFromBaseObject() {

    PublisherRTBProfileGroupDTO publisherRTBProfileGroupDTO =
        publisherRTBProfileGroupAssembler.make(rtbProfileGroup);

    assertEquals(rtbProfileGroup.getPid(), publisherRTBProfileGroupDTO.getPid());
    assertEquals(
        rtbProfileGroup.getListType().getValue(),
        publisherRTBProfileGroupDTO.getListType().getExternalValue());
    assertEquals(
        rtbProfileGroup.getItemType().getValue(),
        publisherRTBProfileGroupDTO.getItemType().getExternalValue());
    assertEquals(rtbProfileGroup.getName(), publisherRTBProfileGroupDTO.getName());
    assertEquals(rtbProfileGroup.getData(), publisherRTBProfileGroupDTO.getData());
    assertEquals(rtbProfileGroup.getVersion(), publisherRTBProfileGroupDTO.getVersion());
    assertEquals(
        rtbProfileGroup.getPrivilegeLevel().name(),
        publisherRTBProfileGroupDTO.getPrivilegeLevel());
    assertEquals(rtbProfileGroup.getPublisherPid(), publisherRTBProfileGroupDTO.getPublisherPid());
  }

  @Test
  void shouldApplyDtoToBaseObject() {
    PublisherRTBProfileGroupDTO publisherRTBProfileGroupDTO =
        new PublisherRTBProfileGroupDTO(
            rtbProfileGroup.getPid(),
            rtbProfileGroup.getName(),
            rtbProfileGroup.getVersion(),
            rtbProfileGroup.getPrivilegeLevel().name(),
            rtbProfileGroup.getData(),
            rtbProfileGroup.getItemType().getValue(),
            rtbProfileGroup.getListType().getValue(),
            rtbProfileGroup.getPublisherPid(),
            rtbProfileGroup.isUICustomGroup());
    RtbProfileGroup rtbProfileGroupApply = new RtbProfileGroup();

    publisherRTBProfileGroupAssembler.apply(rtbProfileGroupApply, publisherRTBProfileGroupDTO);

    assertEquals(rtbProfileGroup.getListType(), rtbProfileGroupApply.getListType());
    assertEquals(rtbProfileGroup.getItemType(), rtbProfileGroupApply.getItemType());
    assertEquals(rtbProfileGroup.getName(), rtbProfileGroupApply.getName());
    assertEquals(rtbProfileGroup.getData(), rtbProfileGroupApply.getData());
    assertEquals(rtbProfileGroup.getPrivilegeLevel(), rtbProfileGroupApply.getPrivilegeLevel());
    assertEquals(rtbProfileGroup.getPublisherPid(), rtbProfileGroupApply.getPublisherPid());
  }
}
