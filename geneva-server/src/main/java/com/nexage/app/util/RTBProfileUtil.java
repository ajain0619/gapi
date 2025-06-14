package com.nexage.app.util;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.repository.RTBProfileLibraryRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.admin.core.sparta.jpa.model.TagRule;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.app.util.validator.RTBProfileValidator;
import com.nexage.app.util.validator.TagValidator;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RTBProfileUtil {
  private final UUIDGenerator uuidGen = new UUIDGenerator();

  private final RTBProfileLibraryRepository rtbProfileLibraryRepository;
  private final RTBProfileRepository rtbProfileRepository;
  private final TagValidator tagValidator;
  private final RTBProfileValidator rtbProfileValidator;

  public RTBProfile prepareDefaultRtbProfile(RTBProfile rtbProfile) {
    rtbProfileValidator.validateRtbProfile(rtbProfile);
    adjustReservesWithDealTerm(rtbProfile);

    Set<RtbProfileLibrary> candidateLibraries = getRTBProfileLibraries(rtbProfile.getLibraryPids());

    if (rtbProfile.getPid() == null) {
      createDefaultRtbProfile(rtbProfile);
      syncRTBProfileLibraries(rtbProfile, candidateLibraries, null);
    } else {
      syncRTBProfileLibraries(rtbProfile, candidateLibraries, rtbProfile.getLibraries());
    }

    if (rtbProfile.getTag() != null) {
      Tag tag = rtbProfile.getTag();
      tagValidator.validateTag(tag);
      if (tag.getRules() != null && !tag.getRules().isEmpty()) {
        for (TagRule rule : tag.getRules()) {
          rule.setTag(tag);
        }
      }
      tag.setRtbProfile(rtbProfile);
      rtbProfile.setTag(tag);
    }

    return rtbProfile;
  }

  public void syncRTBProfileLibraries(
      RTBProfile targetRtbProfile,
      Set<RtbProfileLibrary> candidateLibraries,
      Set<RTBProfileLibraryAssociation> currentAssociations) {
    Set<Long> currentLibraryIds = new HashSet<>();
    Set<Long> targetLibraryIds =
        targetRtbProfile.getLibraryPids() != null
            ? targetRtbProfile.getLibraryPids()
            : new HashSet<>();

    if (currentAssociations != null) {
      for (RTBProfileLibraryAssociation currentAssocation : currentAssociations) {
        currentLibraryIds.add(currentAssocation.getLibrary().getPid());
      }
    }

    Set<Long> keepLibraryIds = Sets.intersection(targetLibraryIds, currentLibraryIds);
    Set<Long> addLibraryIds = Sets.difference(targetLibraryIds, currentLibraryIds);

    // Move libraries we want to keep over to the target profile
    if (currentAssociations != null) {
      for (RTBProfileLibraryAssociation currentAssocation : currentAssociations) {
        if (keepLibraryIds.contains(currentAssocation.getLibrary().getPid())) {
          targetRtbProfile.getLibraries().add(currentAssocation);
        }
      }
    }

    // Add new libraries
    addNewLibraries(addLibraryIds, candidateLibraries, targetRtbProfile);
  }

  public Set<RtbProfileLibrary> getRTBProfileLibraries(Set<Long> pids) {
    return pids != null
        ? new HashSet<>(rtbProfileLibraryRepository.findAllById(pids))
        : new HashSet<>();
  }

  public void setAlterReserveForTag(Company company, RTBProfile rtbProfile) {
    // if AlterReserve not specified in rtbProfile - use parameter from company level
    if (company != null && rtbProfile != null && rtbProfile.getAlterReserve() == null) {
      if (company.getSellerAttributes() != null && company.getSellerAttributes().isPfoEnabled()) {
        rtbProfile.setAlterReserve(AlterReserve.ONLY_IF_HIGHER);
      } else {
        rtbProfile.setAlterReserve(AlterReserve.OFF);
      }
    }
  }

  public void populateRTBProfileLibraryPids(Site site) {
    if (site.getRtbProfiles() != null) {
      for (RTBProfile rtbProfile : site.getRtbProfiles()) {
        populateRTBProfileLibraryPids(rtbProfile);
      }
    }
  }

  public RTBProfile adjustReservesWithDealTerm(Site site, Tag tag, RTBProfile rtbProfile) {
    if (site != null && rtbProfile != null && tag != null) {
      if (rtbProfile.getPubNetReserve() != null) {
        rtbProfile.setDefaultReserve(
            RevenueUtils.calculateRevShares(site, tag, rtbProfile.getPubNetReserve()));
      }
      if (rtbProfile.getPubNetLowReserve() != null) {
        rtbProfile.setLowReserve(
            RevenueUtils.calculateRevShares(site, tag, rtbProfile.getPubNetLowReserve()));
      }
      if (rtbProfile.getPubNetLowReserve() == null) {
        rtbProfile.setLowReserve(null);
      }
    }
    return rtbProfile;
  }

  /*
   * When a RTBTag is archived or inactivated, RTBProfile needs to be updated.
   * Otherwise archived / inactivated tags are never removed from hazelcast cache.
   */
  public void updateRTBProfileForTag(Long tagPid) {
    var optionalRTBProfile = rtbProfileRepository.findByTagPid(tagPid);
    if (optionalRTBProfile.isPresent()) {
      var rtbProfile = optionalRTBProfile.get();
      rtbProfile.setLastUpdate(Calendar.getInstance().getTime());
      rtbProfileRepository.save(rtbProfile);
    }
  }

  private void createDefaultRtbProfile(RTBProfile rtbProfile) {
    String id = uuidGen.generateUniqueId();
    rtbProfile.setExchangeSiteTagId(id);
    rtbProfile.setStatus(Status.ACTIVE);

    if (rtbProfile.getTag() != null && rtbProfile.getTag().getPid() == null) {
      Tag tag = rtbProfile.getTag();
      String tagIdentifier = uuidGen.generateUniqueId();
      tag.setIdentifier(tagIdentifier);
      tag.setPrimaryId(id);
      setAlterReserveForTag(rtbProfile.getOwnerCompany(), rtbProfile);
      tag.setRtbProfile(rtbProfile);
      rtbProfile.setTag(tag);
    }
  }

  private void addNewLibraries(
      Set<Long> addLibraryIds,
      Set<RtbProfileLibrary> candidateLibraries,
      RTBProfile targetRtbProfile) {
    for (Long addLibraryId : addLibraryIds) {
      for (RtbProfileLibrary rtbProfileLibrary : candidateLibraries) {
        if (rtbProfileLibrary.getPid().equals(addLibraryId)) {
          RTBProfileLibraryAssociation association = new RTBProfileLibraryAssociation();
          association.setLibrary(rtbProfileLibrary);
          association.setRtbprofile(targetRtbProfile);
          targetRtbProfile.getLibraries().add(association);
        }
      }
    }
  }

  private RTBProfile adjustReservesWithDealTerm(RTBProfile rtbProfile) {
    if (rtbProfile.getDefaultReserve() == null) rtbProfile.setDefaultReserve(BigDecimal.ZERO);
    if (rtbProfile.getLowReserve() == null) rtbProfile.setLowReserve(BigDecimal.ZERO);
    return rtbProfile;
  }

  private void populateRTBProfileLibraryPids(RTBProfile rtbProfile) {
    Set<Long> libraryPids = new HashSet<>();

    if (rtbProfile.getLibraries() != null) {
      for (RTBProfileLibraryAssociation association : rtbProfile.getLibraries()) {
        libraryPids.add(association.getLibrary().getPid());
      }
    }

    rtbProfile.setLibraryPids(libraryPids);
  }
}
