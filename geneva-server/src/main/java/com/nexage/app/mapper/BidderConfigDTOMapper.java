package com.nexage.app.mapper;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.IdentityProviderView;
import com.nexage.admin.core.model.NativeVersion;
import com.nexage.app.dto.BidderConfigDTO;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/** Defines bean mappings between {@link BidderConfigDTO} and {@link BidderConfig}. */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = BidderConfigDenyAllowFilterListDTOMapper.class)
public interface BidderConfigDTOMapper {
  /**
   * Map {@link BidderConfigDTO} to {@link BidderConfig} representation.
   *
   * @param bidderConfigDTO {@link BidderConfigDTO} representation
   * @return {@link BidderConfig} representation
   */
  BidderConfig map(BidderConfigDTO bidderConfigDTO);

  /**
   * Map {@link BidderConfig} to {@link BidderConfigDTO} representation.
   *
   * @param bidderConfig {@link BidderConfig} representation
   * @return {@link BidderConfigDTO} representation
   */
  BidderConfigDTO map(BidderConfig bidderConfig);

  /**
   * Map {@link BidderConfigDTO#nativeVersion} to {@link BidderConfig#nativeVersions}.
   *
   * <p>If {@code bidderConfigDTO.nativeVersion} is {@code null}, set {@code
   * bidderConfig.nativeVersions} to an empty set. Otherwise, set it to a set containing that
   * non-{@code null} version.
   *
   * @param bidderConfigDTO {@link BidderConfigDTO} representation
   * @param bidderConfig {@link BidderConfig} representation
   */
  @AfterMapping
  default void mapNativeVersion(
      BidderConfigDTO bidderConfigDTO, @MappingTarget BidderConfig bidderConfig) {
    NativeVersion nativeVersion = NativeVersion.fromActual(bidderConfigDTO.getNativeVersion());
    Set<NativeVersion> nativeVersions =
        (nativeVersion == null) ? new HashSet<>() : Sets.newHashSet(nativeVersion);
    bidderConfig.setNativeVersions(nativeVersions);
  }

  /**
   * Map {@link BidderConfig#nativeVersions} to {@link BidderConfigDTO#nativeVersion}.
   *
   * <p>If {@code bidderConfig.nativeVersions} is {@code null} or empty, set {@code
   * bidderConfigDTO.nativeVersion} to {@code null}. Otherwise, set it to the highest version in
   * {@code bidderConfig.nativeVersions}.
   *
   * @param bidderConfig {@link BidderConfig} representation
   * @param bidderConfigDTO {@link BidderConfigDTO} representation
   */
  @AfterMapping
  default void mapNativeVersions(
      BidderConfig bidderConfig, @MappingTarget BidderConfigDTO bidderConfigDTO) {
    Set<NativeVersion> nativeVersions = bidderConfig.getNativeVersions();
    NativeVersion maxNativeVersion = NativeVersion.maxOfSet(nativeVersions);
    if (maxNativeVersion != null) {
      bidderConfigDTO.setNativeVersion(maxNativeVersion.asActual());
    }
  }

  /**
   * Inject {@link BidderConfig} into fields that contain a {@link
   * com.fasterxml.jackson.annotation.JsonBackReference} to it.
   *
   * @param bidderConfig {@link BidderConfig} to inject
   */
  @AfterMapping
  default void injectBackReferences(@MappingTarget BidderConfig bidderConfig) {
    if (bidderConfig.getAllowedDeviceTypes() != null) {
      bidderConfig
          .getAllowedDeviceTypes()
          .forEach(bidderDeviceType -> bidderDeviceType.setBidderConfig(bidderConfig));
    }
    if (bidderConfig.getRegionLimits() != null) {
      bidderConfig
          .getRegionLimits()
          .forEach(regionLimit -> regionLimit.setBidderConfig(bidderConfig));
    }
    if (bidderConfig.getBidderConfigDenyAllowFilterLists() != null) {
      bidderConfig
          .getBidderConfigDenyAllowFilterLists()
          .forEach(filterList -> filterList.setBidderConfig(bidderConfig));
    }
  }

  @AfterMapping
  default void mapHeaderBiddingEnabled(@MappingTarget BidderConfig bidderConfig) {
    if (bidderConfig.getHeaderBiddingEnabled() == null) {
      bidderConfig.setHeaderBiddingEnabled(false);
    }
  }

  default Set<IdentityProviderView> mapPidsToIdentityProviderViews(
      Set<Long> identityProviderPidSet) {
    if (identityProviderPidSet == null) {
      return new HashSet<>();
    }

    return identityProviderPidSet.stream()
        .map(IdentityProviderView::new)
        .collect(Collectors.toSet());
  }

  default Set<Long> mapIdentityProviderViewsToPids(
      Set<IdentityProviderView> identityProviderViewSet) {
    if (identityProviderViewSet == null) {
      return new HashSet<>();
    }

    return identityProviderViewSet.stream()
        .map(IdentityProviderView::getPid)
        .collect(Collectors.toSet());
  }
}
