package com.nexage.app.services.impl;

import static com.nexage.admin.core.enums.site.Type.DOOH;

import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.specification.PositionSpecification;
import com.nexage.admin.core.specification.SpecificationUtils;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.site.PlacementDTOMapper;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.PlacementVideoService;
import com.nexage.app.services.PlacementsService;
import com.nexage.app.util.validator.PlacementQueryTermValidator;
import com.nexage.app.util.validator.VideoSupportValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.search.util.MapParamDecoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@PreAuthorize(
    "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserNexage() or "
        + "@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcUserSeller() or "
        + "@loginUserContext.isOcApiSeller()")
public class PlacementsServiceImpl implements PlacementsService {

  private final PositionRepository positionRepository;
  private final SiteRepository siteRepository;
  private final PlacementVideoService placementVideoService;
  private final PositionViewRepository positionViewRepository;
  private final BeanValidationService beanValidationService;

  private static final String PLACEMENT_ASSIGNED_LEVEL = "3";

  public PlacementsServiceImpl(
      PositionRepository positionRepository,
      SiteRepository siteRepository,
      PlacementVideoService placementVideoService,
      PositionViewRepository positionViewRepository,
      BeanValidationService beanValidationService) {
    this.positionRepository = positionRepository;
    this.siteRepository = siteRepository;
    this.placementVideoService = placementVideoService;
    this.positionViewRepository = positionViewRepository;
    this.beanValidationService = beanValidationService;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#sellerId)")
  public Page<PlacementDTO> getPlacements(
      Pageable pageable,
      Optional<Long> siteIdOpt,
      Long sellerId,
      Optional<String> qt,
      Optional<List<String>> placementTypes,
      Optional<List<String>> statusOpt) {
    Optional<Specification<Position>> sellerIdSpec;
    Optional<Specification<Position>> siteIdSpec;
    Optional<Specification<Position>> positionTypeSpec;
    Optional<Specification<Position>> statusSpec;
    Optional<Specification<Position>> qtSpec;
    sellerIdSpec = Optional.of(sellerId).map(PositionSpecification::withSellerId);
    siteIdSpec = siteIdOpt.map(PositionSpecification::withSiteId);
    positionTypeSpec = placementTypes.map(PositionSpecification::withPositionTypes);
    statusSpec = statusOpt.map(PositionSpecification::withStatus);
    qtSpec = Optional.empty();
    if (qt.isPresent()) {
      Map<String, String> termsMap = MapParamDecoder.decodeString(qt.get());

      checkQueryTerm(termsMap);

      qtSpec = PositionSpecification.withQueryTerms(termsMap);
    }

    return SpecificationUtils.conjunction(
            sellerIdSpec, siteIdSpec, positionTypeSpec, statusSpec, qtSpec)
        .map(combinedSpecs -> positionRepository.findAll(combinedSpecs, pageable))
        .orElseThrow(
            () -> new GenevaValidationException(ServerErrorCodes.SERVER_ERROR_FETCHING_POSITIONS))
        .map(PlacementDTOMapper.MAPPER::map)
        .map(this::populatePlacementVideoForVideoPlacements);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  public Page<PlacementDTO> getPlacementsMinimalData(
      Pageable pageable, Long sitePid, Long sellerPid, String qt) {
    if (Objects.nonNull(qt) && !qt.isEmpty() && !qt.isBlank()) {
      return positionViewRepository
          .searchPlacementsByName(sitePid, qt, pageable)
          .map(PlacementDTOMapper.MAPPER::map);
    }
    return positionViewRepository
        .findAllPlacements(sitePid, pageable)
        .map(PlacementDTOMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#sellerId)")
  public PlacementDTO save(Long sellerId, PlacementDTO placementDTO) {
    beanValidationService.validate(placementDTO, Default.class, CreateGroup.class);

    if (StringUtils.isBlank(placementDTO.getName())) {
      placementDTO.setName(new UUIDGenerator().generateUniqueId());
    }
    PlacementVideoDTO placementVideoDTO = placementDTO.getPlacementVideo();

    // This logic should be removed after complete migration
    if (isVideoPlacement(placementDTO)) {
      placementVideoDTO = placementVideoService.populateVideoData(placementVideoDTO, placementDTO);
    }

    Site site = findSite(sellerId, placementDTO.getSite());

    if (isDooh(placementDTO)) {
      placementDTO.setDooh(new PlacementDoohDTO());
    }

    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(site);

    Position savedPosition = positionRepository.save(position);
    PlacementDTO savedPlacementDTO = PlacementDTOMapper.MAPPER.map(savedPosition);

    if (isVideoPlacement(placementDTO)) {
      PlacementVideoDTO savedPlacementVideoDTO =
          placementVideoService.save(placementVideoDTO, savedPosition.getPid());
      savedPlacementDTO.setPlacementVideo(savedPlacementVideoDTO);
    }
    return savedPlacementDTO;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#sellerId)")
  public PlacementDTO update(Long sellerId, PlacementDTO placementDTO) {
    beanValidationService.validate(placementDTO, Default.class, UpdateGroup.class);

    PlacementVideoDTO placementVideoDTO = placementDTO.getPlacementVideo();

    // This logic should be removed after complete migration
    if (isVideoPlacement(placementDTO)) {
      placementVideoDTO = placementVideoService.populateVideoData(placementVideoDTO, placementDTO);
    }

    SiteDTO siteDTO = placementDTO.getSite();
    Site site = findSite(sellerId, siteDTO);

    if (isDooh(placementDTO)) {
      placementDTO.setDooh(new PlacementDoohDTO());
    }

    Position position = PlacementDTOMapper.MAPPER.map(placementDTO);
    position.setSite(site);

    if (!positionRepository.existsByPid(placementDTO.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_FOUND_IN_SITE);
    }

    VideoSupport currentVideoSupport =
        positionRepository.findVideoSupportByPlacementPid(placementDTO.getPid());
    boolean insertPlacementVideo = false;

    if (VideoSupportValidator.isChangingFromVideoPosition(
        currentVideoSupport, placementDTO.getVideoSupport())) {
      position.removeVideo();
      placementVideoService.delete(placementDTO.getPid());
      placementVideoDTO = null;
    } else if (VideoSupportValidator.isChangingToVideoPosition(
        currentVideoSupport, placementDTO.getVideoSupport())) {
      insertPlacementVideo = true;
    }

    Position updatedPosition = positionRepository.saveAndFlush(position);

    PlacementDTO updatedPlacementDTO = PlacementDTOMapper.MAPPER.map(updatedPosition);
    if (isVideoPlacement(placementDTO) && Objects.nonNull(placementVideoDTO)) {
      updatedPlacementDTO.setPlacementVideo(
          placementVideoService.update(
              placementVideoDTO, updatedPosition.getPid(), insertPlacementVideo));
    }

    return updatedPlacementDTO;
  }

  private PlacementDTO populatePlacementVideoForVideoPlacements(PlacementDTO placementDTO) {
    if (isVideoPlacement(placementDTO)) {
      placementDTO.setPlacementVideo(
          placementVideoService.getPlacementVideo(placementDTO.getPid()));
    }
    return placementDTO;
  }

  private Site findSite(long sellerId, SiteDTO siteDTO) {
    return siteRepository
        .findById(siteDTO.getPid())
        .filter(site -> site.getCompanyPid() == sellerId)
        .filter(site -> site.getType() == siteDTO.getType())
        .orElseThrow(() -> new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND));
  }

  private void checkQueryTerm(Map<String, String> qt) {
    if (!PlacementQueryTermValidator.isValid(qt)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PLACEMENT_SEARCH_ERROR_WITH_QUERY_TERM_FORMAT);
    }
  }

  private <T> Optional<Specification<T>> with(Specification<T> spec) {
    return Optional.ofNullable(spec);
  }

  private boolean isDooh(PlacementDTO placementDTO) {
    return DOOH.equals(placementDTO.getSite().getType()) && Objects.isNull(placementDTO.getDooh());
  }

  private boolean isVideoPlacement(PlacementDTO placementDTO) {
    return placementDTO.getVideoSupport() != null
        && (VideoSupport.VIDEO_AND_BANNER == placementDTO.getVideoSupport()
            || VideoSupport.VIDEO == placementDTO.getVideoSupport());
  }
}
