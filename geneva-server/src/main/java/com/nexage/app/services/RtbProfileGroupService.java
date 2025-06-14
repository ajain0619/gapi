package com.nexage.app.services;

import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;

public interface RtbProfileGroupService {

  PublisherRTBProfileGroupDTO create(Long publisherPid, PublisherRTBProfileGroupDTO group);

  PublisherRTBProfileGroupDTO get(long groupPid);

  PublisherRTBProfileGroupDTO update(long groupPid, PublisherRTBProfileGroupDTO group);

  void delete(long groupPid);

  void separateIndividualsGroups(PublisherAttributes attributes);

  void mergeIndividualsGroups(PublisherAttributes attributes);
}
