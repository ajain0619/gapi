package com.nexage.app.services.impl;

import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.repository.AdSourceRepository;
import com.nexage.admin.core.repository.SellerAdSourceRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.SellerAdSource;
import com.nexage.app.dto.tag.TagPerformanceMetricsDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.util.HashUtils;
import com.nexage.app.util.RTBProfileUtil;
import com.nexage.app.util.TemplatingUtils;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Methods used in reference to Sellers.
 *
 * <p>NOTE: Please do not add any new functionality to this class or its implementations. The
 * business concepts associated to the different operations handled by the following functions use
 * strategies, concepts, or frameworks considered deprecated. Be sure you reach to the core team
 * before considering designing or implementing new functionality under this class.
 */
@Log4j2
@Service
@Transactional
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcManagerSeller()")
public class SellerServiceImpl implements SellerService {

  private final UserContext userContext;
  private final SellerSiteService sellerSiteService;
  private final SiteRepository siteRepository;
  private final SellerAdSourceRepository sellerAdSourceRepository;
  private final AdSourceRepository adSourceRepository;
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate coreNamedTemplate;
  private final NamedParameterJdbcTemplate dwNamedTemplate;
  private final RTBProfileUtil rtbProfileUtil;

  public SellerServiceImpl(
      UserContext userContext,
      SellerSiteService sellerSiteService,
      SiteRepository siteRepository,
      SellerAdSourceRepository sellerAdSourceRepository,
      AdSourceRepository adSourceRepository,
      @Qualifier("coreServicesJdbcTemplate") JdbcTemplate jdbcTemplate,
      @Qualifier("coreNamedJdbcTemplate") NamedParameterJdbcTemplate coreNamedTemplate,
      @Qualifier("dwNamedJdbcTemplate") NamedParameterJdbcTemplate dwNamedTemplate,
      RTBProfileUtil rtbProfileUtil) {
    this.userContext = userContext;
    this.sellerSiteService = sellerSiteService;
    this.siteRepository = siteRepository;
    this.sellerAdSourceRepository = sellerAdSourceRepository;
    this.adSourceRepository = adSourceRepository;
    this.jdbcTemplate = jdbcTemplate;
    this.coreNamedTemplate = coreNamedTemplate;
    this.dwNamedTemplate = dwNamedTemplate;
    this.rtbProfileUtil = rtbProfileUtil;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()) "
          + "or (@loginUserContext.canAccessSite(#rtbProfile.getSitePid()) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()))")
  public Site updateRTBProfile(RTBProfile rtbProfile) {
    Site site = sellerSiteService.getSite(rtbProfile.getSitePid());
    boolean matchFound = false;
    // remove the old rtb profile
    for (Iterator<RTBProfile> it = site.getRtbProfiles().iterator(); it.hasNext(); ) {
      RTBProfile oldRTBProfile = it.next();
      if (oldRTBProfile.getPid().equals(rtbProfile.getPid())) {
        matchFound = true;
        it.remove();
        break;
      }
    }

    if (!matchFound) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND);
    }
    for (Iterator<Tag> it = site.getTags().iterator(); it.hasNext(); ) {
      Tag tempTag = it.next();
      if (tempTag.getPrimaryId().equals(rtbProfile.getPid().toString())) {
        rtbProfileUtil.adjustReservesWithDealTerm(site, tempTag, rtbProfile);
        break;
      }
    }
    // add the updated rtb profile
    site.getRtbProfiles().add(rtbProfile);
    return siteRepository.save(site);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  public String calcHashForTagArchive(TagPerformanceMetricsDTO performanceMetrics) {
    return new HashUtils().calculateHash(performanceMetrics.hashCode());
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  public List<SellerAdSource> getAllAdsourceDefaults(Long sellerPid) {
    return sellerAdSourceRepository.findAllBySellerPid(sellerPid);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()")
  public List<AdSource> getPublisherSelfServeDefaultAdsources() {
    return adSourceRepository.findNonDeletedBySelfServeEnablement(
        AdSource.SelfServeEnablement.PUBLISHER);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller()")
  public boolean existsSellerAdSource(@NotNull Long publisherPid, @NotNull Long adsourceId) {
    return sellerAdSourceRepository.existsBySellerPidAndAdSourcePid(publisherPid, adsourceId);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller()")
  public SellerAdSource saveSellerAdSource(@NotNull SellerAdSource sellerAdSource) {
    return sellerAdSourceRepository.save(sellerAdSource);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller()")
  public Optional<SellerAdSource> getSellerAdSourceBySellerPidAndAdSourcePid(
      @NotNull Long publisherPid, @NotNull Long adsourceId) {
    return sellerAdSourceRepository.findBySellerPidAndAdSourcePid(publisherPid, adsourceId);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller()")
  public void deleteSellerAdSourceBySellerPidAndAdSourcePid(
      @NotNull Long publisherPid, @NotNull Long adsourceId) {
    sellerAdSourceRepository.deleteBySellerPidAndAdSourcePid(publisherPid, adsourceId);
  }

  protected String processNativeRequestMacros(String nativeRequest) {
    String cleanRequest;

    try {
      cleanRequest = TemplatingUtils.createFromString(nativeRequest, new HashMap<>());
    } catch (Exception e) {
      log.error("Error replacing macros for native request: " + nativeRequest, e);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_NATIVE_REQUEST_INVALID);
    }

    return cleanRequest;
  }
}
