package com.nexage.app.services.impl;

import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.repository.RTBProfileLibraryRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.services.CompanyRtbProfileLibraryService;
import com.nexage.app.util.assemblers.PublisherRTBProfileLibraryAssembler;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class was originally on {@link RtbProfileLibraryServiceImpl}. it has been decouple to deal
 * with certain circular dependency injection.
 */
@Legacy
@Service("companyRtbProfileLibraryService")
@Transactional(readOnly = true)
// unsecured, so it can be called by nexage only or pss services
public class CompanyRtbProfileLibraryServiceImpl implements CompanyRtbProfileLibraryService {

  private final PublisherRTBProfileLibraryAssembler publisherRTBProfileLibraryAssembler;
  private final RTBProfileLibraryRepository rtbProfileLibraryRepository;

  @Autowired
  public CompanyRtbProfileLibraryServiceImpl(
      PublisherRTBProfileLibraryAssembler publisherRTBProfileLibraryAssembler,
      RTBProfileLibraryRepository rtbProfileLibraryRepository) {
    this.publisherRTBProfileLibraryAssembler = publisherRTBProfileLibraryAssembler;
    this.rtbProfileLibraryRepository = rtbProfileLibraryRepository;
  }

  /** {@inheritDoc} */
  @Override
  public List<PublisherRTBProfileLibraryDTO> getRTBProfileLibrariesForCompany(Long publisherPid) {
    List<PublisherRTBProfileLibraryDTO> publisherRTBProfileLibraries = new ArrayList<>();
    List<RtbProfileLibrary> libraries =
        rtbProfileLibraryRepository.findAllByPublisherPidAndPrivilegeLevel(
            publisherPid, RTBProfileLibraryPrivilegeLevel.PUBLISHER);
    convertRTBProfileLibraries(libraries, publisherRTBProfileLibraries);
    return publisherRTBProfileLibraries;
  }

  /** {@inheritDoc} */
  @Override
  public List<PublisherRTBProfileLibraryDTO> getRTBProfileLibrariesForCompany(
      Long publisherPid, boolean isInternalUser) {
    List<RtbProfileLibrary> coreLibrariesCompany =
        rtbProfileLibraryRepository.findAllByPublisherPid(publisherPid);
    List<RtbProfileLibrary> coreLibrariesGlobal =
        rtbProfileLibraryRepository.findAllByPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.GLOBAL);

    List<RtbProfileLibrary> coreLibrariesInternalOnly = new ArrayList<>();
    if (isInternalUser) {
      coreLibrariesInternalOnly =
          rtbProfileLibraryRepository.findAllByPrivilegeLevel(
              RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY);
    }

    List<PublisherRTBProfileLibraryDTO> returnLibraries = new ArrayList<>();
    convertRTBProfileLibraries(coreLibrariesCompany, returnLibraries);
    convertRTBProfileLibraries(coreLibrariesGlobal, returnLibraries);
    convertRTBProfileLibraries(coreLibrariesInternalOnly, returnLibraries);

    return returnLibraries;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public List<PublisherRTBProfileLibraryDTO> getRTBProfileLibrariesForCompany(
      Long publisherPid,
      boolean isBidderLibrary,
      boolean removeDefaultEligible,
      boolean removeExchangeDefault) {
    List<PublisherRTBProfileLibraryDTO> libraries = getRTBProfileLibrariesForCompany(publisherPid);
    if (isBidderLibrary) {
      libraries =
          libraries.stream()
              .filter(
                  l ->
                      l.getGroups().stream()
                          .allMatch(
                              g -> g.getItemType() == PublisherRTBProfileGroupDTO.ItemType.BIDDER))
              .collect(Collectors.toList());
    } else {
      libraries =
          libraries.stream()
              .filter(
                  l ->
                      l.getGroups().stream()
                          .allMatch(
                              g ->
                                  g.getItemType() == PublisherRTBProfileGroupDTO.ItemType.ADOMAIN
                                      || g.getItemType()
                                          == PublisherRTBProfileGroupDTO.ItemType.CATEGORY))
              .collect(Collectors.toList());
    }
    if (removeDefaultEligible) {
      libraries =
          libraries.stream().filter(l -> !l.getIsDefaultEligible()).collect(Collectors.toList());
    }
    if (removeExchangeDefault) {
      libraries =
          libraries.stream()
              .filter(
                  l ->
                      l.getGroups().stream()
                          .noneMatch(PublisherRTBProfileGroupDTO::getIsIndividualsGroup))
              .collect(Collectors.toList());
    }
    return libraries;
  }

  private void convertRTBProfileLibraries(
      List<RtbProfileLibrary> coreLibraries, List<PublisherRTBProfileLibraryDTO> destLibraries) {
    if (coreLibraries != null) {
      for (RtbProfileLibrary coreLibrary : coreLibraries) {
        destLibraries.add(publisherRTBProfileLibraryAssembler.make(coreLibrary));
      }
    }
  }
}
