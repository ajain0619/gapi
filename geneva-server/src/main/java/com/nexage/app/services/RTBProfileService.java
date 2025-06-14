package com.nexage.app.services;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.app.dto.RtbProfileLibsAndTagsDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherHierarchyDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import java.util.Set;

public interface RTBProfileService {

  PublisherDefaultRTBProfileDTO createDefaultRTBProfile(
      long publisherPid, PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile);

  PublisherDefaultRTBProfileDTO readDefaultRTBProfile(long publisherPid, long rtbProfilePid);

  PublisherDefaultRTBProfileDTO updateDefaultRTBProfile(
      long publisherPid,
      PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile,
      long rtbProfileId);

  void deleteDefaultRTBProfile(long publisherPid, long rtbProfilePid);

  PublisherDefaultRTBProfileDTO cloneDefaultRTBProfile(
      long publisherPid, long sourceRTBProfilePid, PublisherDefaultRTBProfileDTO source);

  /**
   * Ensure the passed in RTB profiles are valid
   *
   * @param site
   * @param company
   * @param publisherDefaultRTBProfile
   * @param dbRtbProfile
   * @param details
   * @return
   */
  RTBProfile processDefaultRtbProfile(
      Site site,
      Company company,
      PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile,
      RTBProfile dbRtbProfile,
      boolean details);

  void updateRTBProfileLibToRTBProfilesMap(
      long publisher, RtbProfileLibsAndTagsDTO rtbProfileLibAndTagList);

  RTBProfile createTagRTBProfile(PublisherTagDTO publisherTag, Tag tag, Site siteDTO);

  RTBProfile cloneTagRTBProfile(
      Site destinationSite,
      Tag newTag,
      Site originSite,
      Tag originTag,
      PublisherTagDTO publisherTag);

  Set<PublisherHierarchyDTO> getTagHierachy(long publisher, long rtbprofilegroup);

  RTBProfile updateTagRTBProfile(Site siteDTO, Tag tag, PublisherTagDTO publisherTag);

  RTBProfile processCompanyDefaultRtbProfile(
      Long publisherPid,
      PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile,
      RTBProfile dbRtbProfile,
      Company company);
}
