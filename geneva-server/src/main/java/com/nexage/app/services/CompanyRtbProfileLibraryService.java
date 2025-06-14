package com.nexage.app.services;

import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import java.util.List;

public interface CompanyRtbProfileLibraryService {

  List<PublisherRTBProfileLibraryDTO> getRTBProfileLibrariesForCompany(Long publisherPid);

  List<PublisherRTBProfileLibraryDTO> getRTBProfileLibrariesForCompany(
      Long publisherPid, boolean isInternalUser);

  List<PublisherRTBProfileLibraryDTO> getRTBProfileLibrariesForCompany(
      Long publisherPid,
      boolean isBidderLibrary,
      boolean removeDefaultEligible,
      boolean removeExchangeDefault);
}
