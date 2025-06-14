package com.nexage.app.services.impl;

import com.nexage.admin.core.model.AdserverConfiguration;
import com.nexage.admin.core.model.AdserverConfiguration.AdserverConfigurationProperty;
import com.nexage.admin.core.model.Creative;
import com.nexage.admin.core.repository.AdserverConfigurationRepository;
import com.nexage.admin.core.repository.CampaignRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.CreativeRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CampaignCreativeService;
import com.nexage.app.util.EnvironmentUtil;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() OR @loginUserContext.isOcUserSeller()"
        + " OR @loginUserContext.isOcUserBuyer() OR @loginUserContext.isOcUserSeatHolder()")
public class CampaignCreativeServiceImpl implements CampaignCreativeService {

  private static final String CREATIVE_DOMAIN_PROTOCOL = "http://";

  @Value("${spp.geneva.static.base.url}")
  private String staticBaseUrl;

  private final CompanyRepository companyRepository;
  private final CampaignRepository campaignRepository;
  private final UserContext userContext;
  private final CreativeRepository creativeRepository;
  private final EnvironmentUtil environmentUtil;
  private final AdserverConfigurationRepository adserverConfigurationRepository;

  @Override
  public Set<Creative> getAllCreatives(long sellerPid, long campaignPid) {
    Set<Creative> creatives = new HashSet<>();
    if (userContext.doSameOrNexageAffiliation(sellerPid)) {
      validateCampaignForSeller(campaignPid, sellerPid);
      List<Creative> clist = creativeRepository.findAllNonDeletedByCampaignPid(campaignPid);
      clist.forEach(creative -> creatives.add(createCopyWithAbsoluteUrls(creative)));
    }
    return creatives;
  }

  /**
   * Creates a shallow-copy clone of a given creative with absolute urls.
   *
   * @param original original creative
   * @return creative with absolute urls
   */
  private Creative createCopyWithAbsoluteUrls(Creative original) {

    Creative creative = null;
    try {
      creative = original.clone();
    } catch (CloneNotSupportedException e) {
      log.error("Cannot clone Creative to make absolute urls for client", e);
      return creative;
    }
    String domainUrl = environmentUtil.isAwsEnvironment() ? staticBaseUrl : getCreativeDomainUrl();

    if (creative.getBanner() != null) {
      creative.setBanner(domainUrl + creative.getBanner());
    }
    if (creative.getMma120x20() != null) {
      creative.setMma120x20(domainUrl + creative.getMma120x20());
    }
    if (creative.getMma168x28() != null) {
      creative.setMma168x28(domainUrl + creative.getMma168x28());
    }
    if (creative.getMma216x36() != null) {
      creative.setMma216x36(domainUrl + creative.getMma216x36());
    }
    if (creative.getMma300x50() != null) {
      creative.setMma300x50(domainUrl + creative.getMma300x50());
    }
    if (creative.getMma320x50() != null) {
      creative.setMma320x50(domainUrl + creative.getMma320x50());
    }
    return creative;
  }

  /**
   * Gets the creative domain url from database.
   *
   * @return the creative domain url
   */
  private String getCreativeDomainUrl() {
    String creativeDomainUrl = null;

    AdserverConfiguration adserverConfiguration =
        adserverConfigurationRepository.findByProperty(
            AdserverConfigurationProperty.CREATIVE_HOST_PROPERTIES.getPropertyName());
    if (adserverConfiguration != null) {
      creativeDomainUrl = CREATIVE_DOMAIN_PROTOCOL + adserverConfiguration.getValue();
    }
    return creativeDomainUrl;
  }

  private void validateCampaignForSeller(long campaignPid, long sellerPid) {
    if (!companyRepository.existsById(sellerPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_USER_RESTRICTION_INVALID_SELLER);
    }
    if (!campaignRepository.existsByPidAndSellerId(campaignPid, sellerPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_CAMPAIGN_FOR_SELLER);
    }
  }
}
