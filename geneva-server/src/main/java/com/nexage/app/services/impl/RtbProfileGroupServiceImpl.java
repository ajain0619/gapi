package com.nexage.app.services.impl;

import com.nexage.admin.core.model.RtbProfileGroup;
import com.nexage.admin.core.repository.RtbProfileGroupRepository;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.RtbProfileGroupService;
import com.nexage.app.services.impl.limit.RtbProfileLimitChecker;
import com.nexage.app.util.assemblers.PublisherRTBProfileGroupAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("rtbProfileGroupService")
@Transactional
// unsecured, so it can be called by nexage only or pss services
public class RtbProfileGroupServiceImpl implements RtbProfileGroupService {

  private final PublisherRTBProfileGroupAssembler publisherRTBProfileGroupAssembler;
  private final RtbProfileGroupRepository rtbProfileGroupRepository;
  private final RtbProfileLimitChecker rtbProfileLimitChecker;
  private final UserContext userContext;

  public RtbProfileGroupServiceImpl(
      PublisherRTBProfileGroupAssembler publisherRTBProfileGroupAssembler,
      RtbProfileGroupRepository rtbProfileGroupRepository,
      RtbProfileLimitChecker rtbProfileLimitChecker,
      UserContext userContext) {
    this.publisherRTBProfileGroupAssembler = publisherRTBProfileGroupAssembler;
    this.rtbProfileGroupRepository = rtbProfileGroupRepository;
    this.rtbProfileLimitChecker = rtbProfileLimitChecker;
    this.userContext = userContext;
  }

  @Override
  public PublisherRTBProfileGroupDTO create(Long publisherPid, PublisherRTBProfileGroupDTO group) {
    rtbProfileLimitChecker.checkLimitsGroup(publisherPid, group);
    var newGroup = publisherRTBProfileGroupAssembler.apply(new RtbProfileGroup(), group);
    var coreGroup = rtbProfileGroupRepository.save(newGroup);
    return publisherRTBProfileGroupAssembler.make(coreGroup);
  }

  @Override
  public PublisherRTBProfileGroupDTO get(long groupPid) {
    return rtbProfileGroupRepository
        .findById(groupPid)
        .map(publisherRTBProfileGroupAssembler::make)
        .orElse(null);
  }

  @Override
  public PublisherRTBProfileGroupDTO update(long groupPid, PublisherRTBProfileGroupDTO group) {

    if (!userContext.doSameOrNexageAffiliation(group.getPublisherPid())) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }

    var coreGroup =
        rtbProfileGroupRepository
            .findById(groupPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_RTB_PROFILE_GROUP_NOT_FOUND));
    publisherRTBProfileGroupAssembler.apply(coreGroup, group);
    var updatedCoreGroup = rtbProfileGroupRepository.save(coreGroup);
    return publisherRTBProfileGroupAssembler.make(updatedCoreGroup);
  }

  @Override
  public void delete(long groupPid) {
    rtbProfileGroupRepository.deleteById(groupPid);
  }

  @Override
  public void separateIndividualsGroups(PublisherAttributes attributes) {
    Set<Long> defaultBidderGroups = attributes.getDefaultBidderGroups();
    Set<Long> defaultBlock = attributes.getDefaultBlock();

    if (defaultBidderGroups != null) {
      Set<Long> individualsDefaultBidderGroups = filterGroup(defaultBidderGroups);
      attributes.setIndividualsDefaultBidderGroups(individualsDefaultBidderGroups);
      defaultBidderGroups.removeAll(individualsDefaultBidderGroups);
    }

    if (defaultBlock != null) {
      Set<Long> individualsDefaultBlock = filterGroup(defaultBlock);
      attributes.setIndividualsDefaultBlock(individualsDefaultBlock);
      defaultBlock.removeAll(individualsDefaultBlock);
    }
  }

  @Override
  public void mergeIndividualsGroups(PublisherAttributes attributes) {
    mergeGroups(
        attributes.getIndividualsDefaultBidderGroups(), attributes.getDefaultBidderGroups());
    mergeGroups(attributes.getIndividualsDefaultBlock(), attributes.getDefaultBlock());
  }

  private void mergeGroups(Set<Long> from, Set<Long> to) {
    if (from != null) {
      to.addAll(from);
      from.clear();
    }
  }

  private Set<Long> filterGroup(Set<Long> targetGroups) {
    return targetGroups.stream()
        .filter(id -> rtbProfileGroupRepository.existsByPidAndIsUICustomGroup(id, true))
        .collect(Collectors.toSet());
  }
}
