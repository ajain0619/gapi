package com.nexage.app.util.assemblers;

import com.nexage.admin.core.model.RtbProfileGroup;
import com.nexage.admin.core.model.RtbProfileGroup.ItemType;
import com.nexage.admin.core.model.RtbProfileGroup.ListType;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import org.springframework.stereotype.Component;

@Component
public class PublisherRTBProfileGroupAssembler extends NoContextAssembler {

  public PublisherRTBProfileGroupDTO make(RtbProfileGroup rtbProfileGroup) {
    Long pid = rtbProfileGroup.getPid();
    String name = rtbProfileGroup.getName();
    Integer version = rtbProfileGroup.getVersion();
    String privilegeLevel = rtbProfileGroup.getPrivilegeLevel().toString();
    String data = rtbProfileGroup.getData();
    int itemType = rtbProfileGroup.getItemType().getValue();
    int listType = rtbProfileGroup.getListType().getValue();
    Long publisherPid = rtbProfileGroup.getPublisherPid();
    boolean isIndividualsGroup = rtbProfileGroup.isUICustomGroup();
    return new PublisherRTBProfileGroupDTO(
        pid,
        name,
        version,
        privilegeLevel,
        data,
        itemType,
        listType,
        publisherPid,
        isIndividualsGroup);
  }

  public RtbProfileGroup apply(
      RtbProfileGroup rtbProfileGroup, PublisherRTBProfileGroupDTO publisherRTBProfileGroup) {
    rtbProfileGroup.setName(publisherRTBProfileGroup.getName());
    rtbProfileGroup.setData(publisherRTBProfileGroup.getData());
    rtbProfileGroup.setItemType(applyItemType(publisherRTBProfileGroup.getItemType().asInt()));
    rtbProfileGroup.setListType(applyListType(publisherRTBProfileGroup.getListType().asInt()));
    rtbProfileGroup.setPrivilegeLevel(
        applyPrivilegeLevel(publisherRTBProfileGroup.getPrivilegeLevel()));
    rtbProfileGroup.setPublisherPid(publisherRTBProfileGroup.getPublisherPid());
    rtbProfileGroup.setUICustomGroup(publisherRTBProfileGroup.getIsIndividualsGroup());

    return rtbProfileGroup;
  }

  private RTBProfileLibraryPrivilegeLevel applyPrivilegeLevel(String privilegeLevel) {
    RTBProfileLibraryPrivilegeLevel level = RTBProfileLibraryPrivilegeLevel.GLOBAL;
    for (RTBProfileLibraryPrivilegeLevel pl : RTBProfileLibraryPrivilegeLevel.values()) {
      if (pl.toString().equalsIgnoreCase(privilegeLevel)) {
        level = pl;
        break;
      }
    }
    return level;
  }

  private RtbProfileGroup.ItemType applyItemType(int type) {
    RtbProfileGroup.ItemType returnType = ItemType.CATEGORY;

    for (ItemType it : ItemType.values()) {
      if (it.getValue() == type) {
        returnType = it;
        break;
      }
    }
    return returnType;
  }

  private RtbProfileGroup.ListType applyListType(int type) {
    RtbProfileGroup.ListType returnType = ListType.BLOCKLIST;

    for (ListType lt : ListType.values()) {
      if (lt.getValue() == type) {
        returnType = lt;
        break;
      }
    }
    return returnType;
  }
}
