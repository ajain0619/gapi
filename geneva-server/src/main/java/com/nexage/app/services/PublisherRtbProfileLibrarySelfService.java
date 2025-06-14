package com.nexage.app.services;

import com.nexage.app.dto.RTBProfileLibraryCloneDataDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.List;
import java.util.Set;

/**
 * @deprecated Although the logic associated to Publisher Self-Serve is highly used and active
 *     within the core of the app, we want to avoid developers following same practices put in here.
 *     This is part of the old legacy pss context, our plan is to slowly migrate each business logic
 *     to its own separated self-serve to reduce class complexity and to follow single
 *     responsibility principle.
 */
@Legacy
@Deprecated
public interface PublisherRtbProfileLibrarySelfService {

  PublisherRTBProfileGroupDTO createRTBProfileGroup(
      Long publisher, PublisherRTBProfileGroupDTO group);

  PublisherRTBProfileGroupDTO getRTBProfileGroup(Long publisherPid, long groupPid);

  PublisherRTBProfileLibraryDTO createRTBProfileLibrary(
      Long publisherPid, PublisherRTBProfileLibraryDTO group);

  PublisherRTBProfileLibraryDTO getRTBProfileLibrary(Long publisherPid, long libraryPid);

  List<PublisherRTBProfileLibraryDTO> getRTBProfileLibrariesForCompany(Long publisherPid);

  List<PublisherRTBProfileLibraryDTO> getEligibleRTBProfileLibrariesForCompany(Long publisherPid);

  void deleteRTBProfileLibrary(Long publisherId, long libraryPid);

  PublisherRTBProfileGroupDTO updateRTBProfileGroup(
      Long publisherPid, long groupPid, PublisherRTBProfileGroupDTO group);

  void deleteRTBProfileGroup(Long publisher, long groupPid);

  PublisherRTBProfileLibraryDTO updateRTBProfileLibrary(
      Long publisherPid, long libraryPid, PublisherRTBProfileLibraryDTO library);

  PublisherRTBProfileLibraryDTO cloneRTBProfileLibraries(
      Long publisherPid, RTBProfileLibraryCloneDataDTO data);

  Set<Long> getEligibleBidders(Long publisherPid);
}
