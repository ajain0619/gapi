package com.nexage.app.services.impl;

import com.nexage.app.dto.RTBProfileLibraryCloneDataDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.services.CompanyRtbProfileLibraryService;
import com.nexage.app.services.NexageRtbProfileLibraryService;
import com.nexage.app.services.RtbProfileLibraryService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("nexageRtbProfileLibraryService")
@Transactional
@PreAuthorize("@loginUserContext.isOcUserNexage()")
public class NexageRtbProfileLibraryServiceImpl implements NexageRtbProfileLibraryService {

  private final RtbProfileLibraryService rtbProfileLibraryService;
  private final CompanyRtbProfileLibraryService companyRtbProfileLibraryService;

  public NexageRtbProfileLibraryServiceImpl(
      RtbProfileLibraryService rtbProfileLibraryService,
      CompanyRtbProfileLibraryService companyRtbProfileLibraryService) {
    this.rtbProfileLibraryService = rtbProfileLibraryService;
    this.companyRtbProfileLibraryService = companyRtbProfileLibraryService;
  }

  @Override
  public PublisherRTBProfileLibraryDTO create(PublisherRTBProfileLibraryDTO library) {
    return rtbProfileLibraryService.create(library.getPublisherPid(), library);
  }

  @Override
  public PublisherRTBProfileLibraryDTO get(Long publisherPid, long libraryPid) {
    return rtbProfileLibraryService.get(publisherPid, libraryPid);
  }

  @Override
  public List<PublisherRTBProfileLibraryDTO> getAllByCompany(Long publisherPid) {
    return companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(publisherPid, true);
  }

  @Override
  public PublisherRTBProfileLibraryDTO get(long libraryPid) {
    return rtbProfileLibraryService.get(libraryPid);
  }

  @Override
  public List<PublisherRTBProfileLibraryDTO> getAll() {
    return rtbProfileLibraryService.getAll();
  }

  @Override
  public PublisherRTBProfileLibraryDTO update(
      long libraryPid, PublisherRTBProfileLibraryDTO library) {
    return rtbProfileLibraryService.update(libraryPid, library);
  }

  @Override
  public void delete(long libraryPid) {
    rtbProfileLibraryService.delete(libraryPid);
  }

  @Override
  public PublisherRTBProfileLibraryDTO cloneRTBProfileLibraries(
      RTBProfileLibraryCloneDataDTO data) {
    return rtbProfileLibraryService.clone(data.getPublisherPid(), data);
  }
}
