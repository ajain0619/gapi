package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.json.MapSerializer;
import com.nexage.app.dto.transparency.TransparencySettingsDTO;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class PublisherRTBProfileDTO {

  public enum AuctionType {
    EXCHANGE_DEFAULT,
    FIRST_PRICE,
    SECOND_PRICE;
  }

  private Long pid;
  private Integer version;
  private String id;
  private String name;
  private Set<String> blockedAdCategories;
  private Set<String> blockedAdvertisers;
  private BigDecimal pubNetReserve;
  private Set<PublisherRTBProfileLibraryDTO> libraries;
  private Set<String> bidderFilters;
  /** @deprecated use the inclusive term {@link #bidderFilterAllowlist} instead. */
  @Deprecated(forRemoval = true)
  private Boolean bidderFilterWhitelist;

  private Boolean bidderFilterAllowlist;
  private Boolean useDefaultBlock;
  private Boolean includeConsumerId;
  private Boolean includeDomainReferences;
  private Boolean includeConsumerProfile;
  private PublisherRTBProfileScreeningLevel screeningLevel;
  private Boolean useDefaultBidders;
  private Set<PublisherRTBProfileBidderDTO> rtbProfileBidders;
  private AuctionType auctionType;
  private BigDecimal lowReserve;
  private BigDecimal pubNetLowReserve;
  private AlterReserve alterReserve;
  private TransparencySettingsDTO siteTransparencySettings;
  private TransparencySettingsDTO publisherTransparencySettings;
  private String description;

  @JsonInclude(Include.NON_DEFAULT)
  private char siteType;

  private String blockedAdTypes;
  private String blockedAttributes;
  private BigDecimal defaultReserve;
  private Date creationDate;
  private Date lastUpdate;
  private Map<Long, String> blockedExternalDataProviderMap;

  @JsonSerialize(using = MapSerializer.class)
  private Map<Long, String> bidderFilterMap;

  private Set<Long> libraryPids;
  private Boolean includeGeoData;

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder<T extends PublisherRTBProfileDTO, B extends Builder> {

    protected T profile;
    protected B builder;

    public Builder() {
      this.profile = (T) new PublisherRTBProfileDTO();
      this.builder = (B) this;
    }

    public B withDescription(String description) {
      profile.setDescription(description);
      return builder;
    }

    public B withSiteType(char siteType) {
      profile.setSiteType(siteType);
      return builder;
    }

    public B withBlockedAdTypes(String blockedAdTypes) {
      profile.setBlockedAdTypes(blockedAdTypes);
      return builder;
    }

    public B withBlockedAttributes(String blockedAttributes) {
      profile.setBlockedAttributes(blockedAttributes);
      return builder;
    }

    public B withDefaultReserve(BigDecimal defaultReserve) {
      profile.setDefaultReserve(defaultReserve);
      return builder;
    }

    public B withCreationDate(Date creationDate) {
      profile.setCreationDate(creationDate);
      return builder;
    }

    public B withLastUpdate(Date lastUpdate) {
      profile.setLastUpdate(lastUpdate);
      return builder;
    }

    public B withBlockedExternalDataProviderMap(Map<Long, String> blockedExternalDataProviderMap) {
      profile.setBlockedExternalDataProviderMap(blockedExternalDataProviderMap);
      return builder;
    }

    public B withBidderFilterMap(Map<Long, String> bidderFilterMap) {
      profile.setBidderFilterMap(bidderFilterMap);
      return builder;
    }

    public B withLibraryPids(Set<Long> libraryPids) {
      profile.setLibraryPids(libraryPids);
      return builder;
    }

    public B withIncludeGeoData(Boolean includeGeoData) {
      profile.setIncludeGeoData(includeGeoData);
      return builder;
    }

    public B withPid(Long pid) {
      profile.setPid(pid);
      return builder;
    }

    public B withVersion(Integer version) {
      profile.setVersion(version);
      return builder;
    }

    public B withId(String id) {
      profile.setId(id);
      return builder;
    }

    public B withName(String name) {
      profile.setName(name);
      return builder;
    }

    /** @deprecated use the inclusive term {@link #bidderFilterAllowlist} instead. */
    @Deprecated(since = "SSP-17992", forRemoval = true)
    public B withBidderFilterWhitelist(Boolean bidderFilterWhitelist) {
      profile.setBidderFilterWhitelist(bidderFilterWhitelist);
      return builder;
    }

    public B withBidderFilterAllowlist(Boolean bidderFilterAllowlist) {
      profile.setBidderFilterAllowlist(bidderFilterAllowlist);
      return builder;
    }

    public B withBidderFilters(Set<String> bidderFilters) {
      profile.setBidderFilters(bidderFilters);
      return builder;
    }

    public B withBidderFilter(String bidderFilter) {
      if (profile.getBidderFilters() == null) {
        profile.setBidderFilters(new HashSet<>());
      }
      profile.getBidderFilters().add(bidderFilter);
      return builder;
    }

    public B withBlockedAdCategories(Set<String> blockedAdCategories) {
      profile.setBlockedAdCategories(blockedAdCategories);
      return builder;
    }

    public B withBlockedAdvertisers(Set<String> blockedAdvertisers) {
      profile.setBlockedAdvertisers(blockedAdvertisers);
      return builder;
    }

    public B withPubNetReserve(BigDecimal pubNetReserve) {
      profile.setPubNetReserve(pubNetReserve);
      return builder;
    }

    public B withLibraries(Set<PublisherRTBProfileLibraryDTO> libraries) {
      profile.setLibraries(libraries);
      return builder;
    }

    public B withUseDefaultBlock(Boolean useDefaultBlock) {
      profile.setUseDefaultBlock(useDefaultBlock);
      return builder;
    }

    public B withUseDefaultBidders(Boolean useDefaultBidders) {
      profile.setUseDefaultBidders(useDefaultBidders);
      return builder;
    }

    public B withRtbProfileBidders(Set<PublisherRTBProfileBidderDTO> rtbProfileBidders) {
      profile.setRtbProfileBidders(rtbProfileBidders);
      return builder;
    }

    public B withIncludeConsumerId(Boolean includeConsumerId) {
      profile.setIncludeConsumerId(includeConsumerId);
      return builder;
    }

    public B withIncludeDomainReferences(Boolean includeDomainReferences) {
      profile.setIncludeDomainReferences(includeDomainReferences);
      return builder;
    }

    public B withIncludeConsumerProfile(Boolean includeConsumerProfile) {
      profile.setIncludeConsumerProfile(includeConsumerProfile);
      return builder;
    }

    public B withScreeningLevel(PublisherRTBProfileScreeningLevel screeningLevel) {
      profile.setScreeningLevel(screeningLevel);
      return builder;
    }

    public B withAuctionType(AuctionType auctionType) {
      profile.setAuctionType(auctionType);
      return builder;
    }

    public B withLowReserve(BigDecimal lowReserve) {
      profile.setLowReserve(lowReserve);
      return builder;
    }

    public B withPubNetLowReserve(BigDecimal pubNetLowReserve) {
      profile.setPubNetLowReserve(pubNetLowReserve);
      return builder;
    }

    public B withAlterReserve(AlterReserve alterReserve) {
      profile.setAlterReserve(alterReserve);
      return builder;
    }

    public B withSiteTransparencySettings(TransparencySettingsDTO siteTransparencySettings) {
      profile.setSiteTransparencySettings(siteTransparencySettings);
      return builder;
    }

    public B withPublisherTransparencySettings(
        TransparencySettingsDTO publisherTransparencySettings) {
      profile.setPublisherTransparencySettings(publisherTransparencySettings);
      return builder;
    }

    public PublisherRTBProfileDTO build() {
      return profile;
    }
  }

  /** @deprecated use the inclusive term {@link #bidderFilterAllowlist} instead. */
  @Deprecated(since = "SSP-17992", forRemoval = true)
  public Boolean getBidderFilterWhitelist() {
    return bidderFilterAllowlist != null ? bidderFilterAllowlist : bidderFilterWhitelist;
  }

  /** @deprecated use the inclusive term {@link #bidderFilterAllowlist} instead. */
  @Deprecated(since = "SSP-17992", forRemoval = true)
  public void setBidderFilterWhitelist(Boolean bidderFilterWhitelist) {
    if (this.bidderFilterAllowlist != null) {
      this.bidderFilterWhitelist = this.bidderFilterAllowlist;
    } else if (bidderFilterWhitelist != null) {
      this.bidderFilterWhitelist = bidderFilterWhitelist;
      this.bidderFilterAllowlist = bidderFilterWhitelist;
    }
  }

  public Boolean getBidderFilterAllowlist() {
    return bidderFilterAllowlist != null ? bidderFilterAllowlist : bidderFilterWhitelist;
  }

  public void setBidderFilterAllowlist(Boolean bidderFilterAllowlist) {
    // To give precedence to allowlist over whitelist
    if ((this.bidderFilterAllowlist == null
            || this.bidderFilterWhitelist == null
            || !this.bidderFilterAllowlist.equals(bidderFilterAllowlist))
        && bidderFilterAllowlist != null) {
      this.bidderFilterWhitelist = bidderFilterAllowlist;
      this.bidderFilterAllowlist = bidderFilterAllowlist;
    }
  }
}
