package com.nexage.app.services.impl;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.app.dto.RTBProfileLibraryCloneDataDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.services.CompanyRtbProfileLibraryService;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.PublisherRtbProfileLibrarySelfService;
import com.nexage.app.services.RtbProfileGroupService;
import com.nexage.app.services.RtbProfileLibraryService;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class was originally on {@link PublisherSelfServiceImpl}. it has been decouple to deal with
 * certain circular dependency injection.
 */
@Legacy
@Log4j2
@Service("publisherSelfRtbProfileLibraryService")
@Transactional
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() OR @loginUserContext.isUserSeller() OR isOcApiSeller()")
public class PublisherRtbProfileLibrarySelfServiceImpl
    implements PublisherRtbProfileLibrarySelfService {

  private final LoginUserContext userContext;
  private final BidderConfigRepository bidderConfigRepository;
  private final CompanyService companyService;
  private final RtbProfileLibraryService rtbProfileLibraryService;
  private final RtbProfileGroupService rtbProfileGroupService;
  private final CompanyRtbProfileLibraryService companyRtbProfileLibraryService;

  @Autowired
  public PublisherRtbProfileLibrarySelfServiceImpl(
      LoginUserContext userContext,
      BidderConfigRepository bidderConfigRepository,
      CompanyService companyService,
      RtbProfileLibraryService rtbProfileLibraryService,
      RtbProfileGroupService rtbProfileGroupService,
      CompanyRtbProfileLibraryService companyRtbProfileLibraryService) {
    this.userContext = userContext;
    this.bidderConfigRepository = bidderConfigRepository;
    this.companyService = companyService;
    this.rtbProfileLibraryService = rtbProfileLibraryService;
    this.rtbProfileGroupService = rtbProfileGroupService;
    this.companyRtbProfileLibraryService = companyRtbProfileLibraryService;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public PublisherRTBProfileGroupDTO createRTBProfileGroup(
      Long publisherPid, PublisherRTBProfileGroupDTO group) {
    return rtbProfileGroupService.create(publisherPid, group);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public PublisherRTBProfileGroupDTO getRTBProfileGroup(Long publisherPid, long groupPid) {
    return rtbProfileGroupService.get(groupPid);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public PublisherRTBProfileLibraryDTO createRTBProfileLibrary(
      Long publisherPid, PublisherRTBProfileLibraryDTO library) {
    return rtbProfileLibraryService.create(publisherPid, library);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public PublisherRTBProfileLibraryDTO getRTBProfileLibrary(Long publisherPid, long libraryPid) {
    return rtbProfileLibraryService.get(publisherPid, libraryPid);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.isNexageUser()")
  public List<PublisherRTBProfileLibraryDTO> getRTBProfileLibrariesForCompany(Long publisherPid) {
    return companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(
        publisherPid, userContext.isNexageUser());
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public List<PublisherRTBProfileLibraryDTO> getEligibleRTBProfileLibrariesForCompany(
      Long publisherPid) {
    boolean isInternalUser = userContext.isNexageUser();
    List<PublisherRTBProfileLibraryDTO> libraries =
        companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(
            publisherPid, isInternalUser);
    return filterForEligibleBidderGroups(publisherPid, libraries);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public void deleteRTBProfileLibrary(Long publisherPid, long libraryPid) {
    rtbProfileLibraryService.delete(publisherPid, libraryPid);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public PublisherRTBProfileGroupDTO updateRTBProfileGroup(
      Long publisherPid, long groupPid, PublisherRTBProfileGroupDTO group) {
    return rtbProfileGroupService.update(groupPid, group);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public void deleteRTBProfileGroup(Long publisherPid, long groupPid) {
    rtbProfileGroupService.delete(groupPid);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public PublisherRTBProfileLibraryDTO updateRTBProfileLibrary(
      Long publisherPid, long libraryPid, PublisherRTBProfileLibraryDTO library) {
    return rtbProfileLibraryService.update(publisherPid, libraryPid, library);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisherPid)")
  public PublisherRTBProfileLibraryDTO cloneRTBProfileLibraries(
      Long publisherPid, RTBProfileLibraryCloneDataDTO data) {
    data.setPublisherPid(publisherPid);
    data.setPublisherOwned();
    return rtbProfileLibraryService.clone(publisherPid, data);
  }

  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public Set<Long> getEligibleBidders(Long publisherPid) {
    Company company = companyService.getCompany(publisherPid);
    if (company == null) {
      throw new RuntimeException(String.format("Invalid company identifier %d", publisherPid));
    }

    return company.getEligibleBidders().stream()
        .flatMap(l -> l.getEligibleBidderGroups().stream())
        .flatMap(g -> Arrays.stream(rtbProfileGroupService.get(g).getData().split(",")))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(Long::parseLong)
        .collect(Collectors.toSet());
  }

  private List<PublisherRTBProfileLibraryDTO> filterForEligibleBidderGroups(
      Long publisherPid, List<PublisherRTBProfileLibraryDTO> libraries) {
    Set<Long> eligibleBidders = getEligibleBidders(publisherPid);
    if (eligibleBidders.isEmpty()) {
      return libraries;
    }

    Set<Long> inactiveBidders =
        findInactive().stream().map(BidderConfig::getPid).collect(Collectors.toSet());

    return libraries.stream()
        .filter(
            l ->
                l.getGroups().stream()
                    .allMatch(
                        g -> {
                          if (g.getItemType() != PublisherRTBProfileGroupDTO.ItemType.BIDDER) {
                            return true;
                          }
                          Set<Long> bidders =
                              Arrays.stream(g.getData().split(","))
                                  .map(String::trim)
                                  .filter(e -> !e.isEmpty())
                                  .map(Long::parseLong)
                                  .collect(Collectors.toSet());
                          if (bidders.isEmpty() || inactiveBidders.containsAll(bidders)) {
                            return false;
                          }
                          return eligibleBidders.containsAll(bidders);
                        }))
        .collect(Collectors.toList());
  }

  private List<BidderConfig> findInactive() {
    return bidderConfigRepository.findByTrafficStatus(false);
  }
}
