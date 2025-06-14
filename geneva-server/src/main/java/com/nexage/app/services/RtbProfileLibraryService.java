package com.nexage.app.services;

import com.nexage.app.dto.RTBProfileLibraryCloneDataDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import java.util.List;

public interface RtbProfileLibraryService {

  PublisherRTBProfileLibraryDTO create(Long publisherPid, PublisherRTBProfileLibraryDTO library);

  PublisherRTBProfileLibraryDTO get(Long publisherPid, long libraryPid);

  PublisherRTBProfileLibraryDTO get(long libraryPid);

  List<PublisherRTBProfileLibraryDTO> getAll();

  PublisherRTBProfileLibraryDTO update(long libraryPid, PublisherRTBProfileLibraryDTO library);

  PublisherRTBProfileLibraryDTO update(
      Long publisherPid, long libraryPid, PublisherRTBProfileLibraryDTO library);

  void delete(Long publisherPid, long libraryPid);

  void delete(long libraryPid);

  PublisherRTBProfileLibraryDTO clone(Long publisherPid, RTBProfileLibraryCloneDataDTO data);
}
