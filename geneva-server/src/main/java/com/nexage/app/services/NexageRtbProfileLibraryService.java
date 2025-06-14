package com.nexage.app.services;

import com.nexage.app.dto.RTBProfileLibraryCloneDataDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import java.util.List;

public interface NexageRtbProfileLibraryService {

  PublisherRTBProfileLibraryDTO create(PublisherRTBProfileLibraryDTO library);

  PublisherRTBProfileLibraryDTO get(Long publisherPid, long libraryPid);

  List<PublisherRTBProfileLibraryDTO> getAllByCompany(Long publisherPid);

  PublisherRTBProfileLibraryDTO get(long libraryPid);

  List<PublisherRTBProfileLibraryDTO> getAll();

  PublisherRTBProfileLibraryDTO update(long libraryPid, PublisherRTBProfileLibraryDTO library);

  void delete(long libraryPid);

  PublisherRTBProfileLibraryDTO cloneRTBProfileLibraries(RTBProfileLibraryCloneDataDTO data);
}
