package com.nexage.app.services.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.repository.RTBProfileLibraryRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.app.dto.RTBProfileLibraryCloneDataDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.RtbProfileLibraryService;
import com.nexage.app.services.impl.limit.RtbProfileLimitChecker;
import com.nexage.app.util.assemblers.PublisherRTBProfileLibraryAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("rtbProfileLibraryService")
@Transactional
// unsecured, so it can be called by nexage only or pss services
public class RtbProfileLibraryServiceImpl implements RtbProfileLibraryService {

  private static final Splitter SPLITTER =
      Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings();

  private static final Joiner JOINER = Joiner.on(",").skipNulls();

  private final PublisherRTBProfileLibraryAssembler publisherRTBProfileLibraryAssembler;
  private final RTBProfileLibraryRepository rtbProfileLibraryRepository;
  private final RTBProfileRepository rtbProfileRepository;
  private final RtbProfileLimitChecker rtbProfileLimitChecker;

  public RtbProfileLibraryServiceImpl(
      PublisherRTBProfileLibraryAssembler publisherRTBProfileLibraryAssembler,
      RTBProfileLibraryRepository rtbProfileLibraryRepository,
      RTBProfileRepository rtbProfileRepository,
      RtbProfileLimitChecker rtbProfileLimitChecker) {
    this.publisherRTBProfileLibraryAssembler = publisherRTBProfileLibraryAssembler;
    this.rtbProfileLibraryRepository = rtbProfileLibraryRepository;
    this.rtbProfileRepository = rtbProfileRepository;
    this.rtbProfileLimitChecker = rtbProfileLimitChecker;
  }

  @Override
  public PublisherRTBProfileLibraryDTO create(
      Long publisherPid, PublisherRTBProfileLibraryDTO library) {
    rtbProfileLimitChecker.checkLimitsLibrary(publisherPid, library);
    var newLibrary = publisherRTBProfileLibraryAssembler.apply(new RtbProfileLibrary(), library);
    var coreLibrary = rtbProfileLibraryRepository.save(newLibrary);
    // once library is created, can create groups and linking table entries
    coreLibrary = publisherRTBProfileLibraryAssembler.applyGroups(coreLibrary, library);
    coreLibrary = rtbProfileLibraryRepository.save(coreLibrary);

    return publisherRTBProfileLibraryAssembler.make(coreLibrary);
  }

  @Override
  public PublisherRTBProfileLibraryDTO get(Long publisherPid, long libraryPid) {
    var coreLibrary =
        rtbProfileLibraryRepository
            .findByPidAndPublisherPid(libraryPid, publisherPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND));
    return publisherRTBProfileLibraryAssembler.make(coreLibrary);
  }

  @Override
  public PublisherRTBProfileLibraryDTO get(long libraryPid) {
    var coreLibrary =
        rtbProfileLibraryRepository
            .findById(libraryPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND));
    return publisherRTBProfileLibraryAssembler.make(coreLibrary);
  }

  @Override
  public List<PublisherRTBProfileLibraryDTO> getAll() {
    List<RtbProfileLibrary> coreLibraries = rtbProfileLibraryRepository.findAll();

    List<PublisherRTBProfileLibraryDTO> returnLibraries = new ArrayList<>();
    convertRTBProfileLibraries(coreLibraries, returnLibraries);

    return returnLibraries;
  }

  @Override
  public void delete(Long publisherPid, long libraryPid) {
    var coreLibrary =
        rtbProfileLibraryRepository
            .findByPidAndPublisherPid(libraryPid, publisherPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND));
    clearRTBProfileLibraryAssociations(coreLibrary);
    // Reload the library now that we've removed the associations
    rtbProfileLibraryRepository
        .findByPidAndPublisherPid(libraryPid, publisherPid)
        .ifPresent(rtbProfileLibraryRepository::delete);
  }

  @Override
  public void delete(long libraryPid) {
    var coreLibrary =
        rtbProfileLibraryRepository
            .findById(libraryPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND));
    clearRTBProfileLibraryAssociations(coreLibrary);
    // Reload the library now that we've removed the associations
    rtbProfileLibraryRepository.findById(libraryPid).ifPresent(rtbProfileLibraryRepository::delete);
  }

  @Override
  public PublisherRTBProfileLibraryDTO update(
      long libraryPid, PublisherRTBProfileLibraryDTO library) {
    var coreLibrary =
        rtbProfileLibraryRepository
            .findById(libraryPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND));
    return update(library, coreLibrary);
  }

  @Override
  public PublisherRTBProfileLibraryDTO update(
      Long publisherPid, long libraryPid, PublisherRTBProfileLibraryDTO library) {
    var coreLibrary =
        rtbProfileLibraryRepository
            .findByPidAndPublisherPid(libraryPid, publisherPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND));
    return update(library, coreLibrary);
  }

  @Override
  public PublisherRTBProfileLibraryDTO clone(
      Long publisherPid, RTBProfileLibraryCloneDataDTO data) {

    if (null == data) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }

    Map<PublisherRTBProfileGroupDTO.ItemType, List<PublisherRTBProfileGroupDTO>> groupMap =
        new EnumMap<>(PublisherRTBProfileGroupDTO.ItemType.class);

    for (PublisherRTBProfileLibraryDTO library : data.getLibraries()) {
      Set<PublisherRTBProfileGroupDTO> groups = library.getGroups();
      for (PublisherRTBProfileGroupDTO group : groups) {
        var type = group.getItemType();
        List<PublisherRTBProfileGroupDTO> mapGroups = groupMap.get(type);
        if (null == mapGroups) {
          List<PublisherRTBProfileGroupDTO> list = new ArrayList<>();
          list.add(group);
          groupMap.put(type, list);
        } else {
          mapGroups.add(group);
        }
      }
    }

    Map<PublisherRTBProfileGroupDTO.ItemType, List<String>> nonGroupEntities =
        new EnumMap<>(PublisherRTBProfileGroupDTO.ItemType.class);
    if (isPopulatedList(data.getBidderPids())) {
      nonGroupEntities.put(PublisherRTBProfileGroupDTO.ItemType.BIDDER, data.getBidderPids());
    }
    if (isPopulatedList(data.getCategories())) {
      nonGroupEntities.put(PublisherRTBProfileGroupDTO.ItemType.CATEGORY, data.getCategories());
    }
    if (isPopulatedList(data.getAdomains())) {
      nonGroupEntities.put(PublisherRTBProfileGroupDTO.ItemType.ADOMAIN, data.getAdomains());
    }

    var nameGenerator = new UUIDGenerator();
    Set<PublisherRTBProfileGroupDTO> createdPublisherGroups = new HashSet<>();

    for (PublisherRTBProfileGroupDTO.ItemType type :
        PublisherRTBProfileGroupDTO.ItemType.values()) {
      Set<String> dataStrings = new HashSet<>();
      List<PublisherRTBProfileGroupDTO> mapGroups = groupMap.get(type);
      if (null != mapGroups) {
        for (PublisherRTBProfileGroupDTO dataGroup : mapGroups) {
          List<String> strings = Lists.newArrayList(SPLITTER.split(dataGroup.getData()));
          dataStrings.addAll(strings);
        }
      }

      List<String> singletonEntities = nonGroupEntities.get(type);
      if (isPopulatedList(singletonEntities)) {
        dataStrings.addAll(singletonEntities);
      }

      if (!dataStrings.isEmpty()) {
        Long pid = null;
        var newDataString = JOINER.join(dataStrings);
        var itemType = type.asInt();
        var version = 1;
        var name = (String) nameGenerator.generate();
        var newPublisherGroup =
            new PublisherRTBProfileGroupDTO(
                pid,
                name,
                version,
                data.getPrivilegeLevel(),
                newDataString,
                itemType,
                data.getListType(),
                data.getPublisherPid(),
                false);
        createdPublisherGroups.add(newPublisherGroup);
      }
    }

    if (!createdPublisherGroups.isEmpty()) {
      Long pid = null;
      var name = data.getName();
      var version = 1;
      var newLibrary =
          new PublisherRTBProfileLibraryDTO(
              pid, name, version, data.getPrivilegeLevel(), data.getPublisherPid(), false);
      newLibrary.setGroups(createdPublisherGroups);

      return create(publisherPid, newLibrary);
    } else {
      return null;
    }
  }

  private PublisherRTBProfileLibraryDTO update(
      PublisherRTBProfileLibraryDTO library, RtbProfileLibrary coreLibrary) {
    coreLibrary = publisherRTBProfileLibraryAssembler.apply(coreLibrary, library);
    coreLibrary = publisherRTBProfileLibraryAssembler.applyGroups(coreLibrary, library);
    coreLibrary = rtbProfileLibraryRepository.save(coreLibrary);
    return publisherRTBProfileLibraryAssembler.make(coreLibrary);
  }

  private void convertRTBProfileLibraries(
      List<RtbProfileLibrary> coreLibraries, List<PublisherRTBProfileLibraryDTO> destLibraries) {
    if (coreLibraries != null) {
      for (RtbProfileLibrary coreLibrary : coreLibraries) {
        destLibraries.add(publisherRTBProfileLibraryAssembler.make(coreLibrary));
      }
    }
  }

  private boolean isPopulatedList(List<String> list) {
    return list != null && !list.isEmpty();
  }

  private void clearRTBProfileLibraryAssociations(RtbProfileLibrary coreLibrary) {
    // The deletion process is a little convoluted thanks to Hibernate making things complex. We
    // need to remove the associations
    // between profile and library and save those changes and reload the library otherwise Hibernate
    // will go into
    // meltdown when we try to delete it
    if (coreLibrary.getProfileLibraryAssociations() != null) {
      for (RTBProfileLibraryAssociation association : coreLibrary.getProfileLibraryAssociations()) {
        var rtbProfile = association.getRtbprofile();
        rtbProfile.getLibraries().remove(association);

        rtbProfileRepository.save(rtbProfile);
      }
      coreLibrary.getProfileLibraryAssociations().clear();
    }
    rtbProfileLibraryRepository.save(coreLibrary);
  }
}
