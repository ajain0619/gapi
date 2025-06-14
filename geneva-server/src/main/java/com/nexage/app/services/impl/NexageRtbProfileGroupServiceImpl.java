package com.nexage.app.services.impl;

import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.services.NexageRtbProfileGroupService;
import com.nexage.app.services.RtbProfileGroupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("nexageRtbProfileGroupService")
@Transactional
@PreAuthorize("@loginUserContext.isOcUserNexage()")
public class NexageRtbProfileGroupServiceImpl implements NexageRtbProfileGroupService {

  private final RtbProfileGroupService rtbProfileGroupService;

  public NexageRtbProfileGroupServiceImpl(RtbProfileGroupService rtbProfileGroupService) {
    this.rtbProfileGroupService = rtbProfileGroupService;
  }

  @Override
  public PublisherRTBProfileGroupDTO create(Long publisherPid, PublisherRTBProfileGroupDTO group) {
    return rtbProfileGroupService.create(publisherPid, group);
  }

  @Override
  public PublisherRTBProfileGroupDTO get(Long publisherPid, long groupPid) {
    return rtbProfileGroupService.get(groupPid);
  }

  @Override
  public PublisherRTBProfileGroupDTO update(long groupPid, PublisherRTBProfileGroupDTO group) {
    return rtbProfileGroupService.update(groupPid, group);
  }

  @Override
  public void delete(long groupPid) {
    rtbProfileGroupService.delete(groupPid);
  }
}
