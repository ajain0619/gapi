package com.nexage.app.util.assemblers;

import com.nexage.admin.core.model.RtbProfileGroup;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PublisherRTBProfileLibraryAssembler extends NoContextAssembler {

  private final PublisherRTBProfileGroupAssembler publisherRTBProfileGroupAssembler;

  public PublisherRTBProfileLibraryDTO make(RtbProfileLibrary rtbProfileLibrary) {
    long pid = rtbProfileLibrary.getPid();
    String name = rtbProfileLibrary.getName();
    Integer version = rtbProfileLibrary.getVersion();
    String privilegeLevel = rtbProfileLibrary.getPrivilegeLevel().toString();
    Long publisherPid = rtbProfileLibrary.getPublisherPid();
    Boolean isDefaultEligible = rtbProfileLibrary.isDefaultEligible();
    PublisherRTBProfileLibraryDTO pubLib =
        new PublisherRTBProfileLibraryDTO(
            pid, name, version, privilegeLevel, publisherPid, isDefaultEligible);
    Set<PublisherRTBProfileGroupDTO> pubGroups = new HashSet<>();
    Set<RtbProfileGroup> coreGroups = rtbProfileLibrary.getGroups();
    for (RtbProfileGroup cg : coreGroups) {
      PublisherRTBProfileGroupDTO pg = publisherRTBProfileGroupAssembler.make(cg);
      pubGroups.add(pg);
    }
    pubLib.setGroups(pubGroups);
    return pubLib;
  }

  public RtbProfileLibrary apply(
      RtbProfileLibrary rtbProfileLibrary,
      PublisherRTBProfileLibraryDTO publisherRtbProfileLibrary) {

    rtbProfileLibrary.setName(publisherRtbProfileLibrary.getName());
    rtbProfileLibrary.setPrivilegeLevel(
        applyPrivilegeLevel(publisherRtbProfileLibrary.getPrivilegeLevel()));
    rtbProfileLibrary.setPublisherPid(publisherRtbProfileLibrary.getPublisherPid());
    rtbProfileLibrary.setDefaultEligible(publisherRtbProfileLibrary.getIsDefaultEligible());

    return rtbProfileLibrary;
  }

  public RtbProfileLibrary applyGroups(
      RtbProfileLibrary rtbProfileLibrary,
      PublisherRTBProfileLibraryDTO publisherRtbProfileLibrary) {

    Set<RtbProfileGroup> coreGroups = rtbProfileLibrary.getGroups();
    if (null == coreGroups) {
      coreGroups = new HashSet<>();
      rtbProfileLibrary.setGroups(coreGroups);
    }

    Map<Long, PublisherRTBProfileGroupDTO> publisherGroupMap = new HashMap<>();
    for (PublisherRTBProfileGroupDTO pg : publisherRtbProfileLibrary.getGroups()) {
      if (null != pg.getPid()) {
        publisherGroupMap.put(pg.getPid(), pg);
      } else {
        // new group
        var newGroup = publisherRTBProfileGroupAssembler.apply(new RtbProfileGroup(), pg);
        coreGroups.add(newGroup);
      }
    }

    // if a group does not exist in publisherGroups, remove it
    coreGroups.removeIf(p -> p.getPid() != null && !publisherGroupMap.containsKey(p.getPid()));

    // if a group is in publisherTagRules, update it
    rtbProfileLibrary.setGroups(
        coreGroups.stream()
            .map(
                p ->
                    publisherGroupMap.containsKey(p.getPid())
                        ? publisherRTBProfileGroupAssembler.apply(
                            p, publisherGroupMap.get(p.getPid()))
                        : p)
            .collect(Collectors.toSet()));
    return rtbProfileLibrary;
  }

  // TODO: merge with code from RTBProfileGroupAssembler
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
}
