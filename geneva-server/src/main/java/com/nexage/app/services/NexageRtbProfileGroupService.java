package com.nexage.app.services;

import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;

public interface NexageRtbProfileGroupService {

  PublisherRTBProfileGroupDTO create(Long publisher, PublisherRTBProfileGroupDTO group);

  PublisherRTBProfileGroupDTO get(Long publisherPid, long groupPid);

  PublisherRTBProfileGroupDTO update(long groupPid, PublisherRTBProfileGroupDTO group);

  void delete(long groupPid);
}
