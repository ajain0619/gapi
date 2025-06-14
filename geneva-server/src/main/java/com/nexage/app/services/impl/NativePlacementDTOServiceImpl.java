package com.nexage.app.services.impl;

import static com.nexage.app.dto.CrudOperation.CREATE;
import static com.nexage.app.dto.CrudOperation.UPDATE;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.app.dto.CrudOperation;
import com.nexage.app.dto.NativePlacementRequestParamsDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.NativePlacementDTOMapper;
import com.nexage.app.queue.model.event.SyncEvent;
import com.nexage.app.queue.producer.PlacementSyncProducer;
import com.nexage.app.services.NativePlacementDTOService;
import com.nexage.app.services.NativePlacementHbPartnerService;
import com.nexage.app.services.NativePlacementTierService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.validation.NativePlacementsParameterValidator;
import com.nexage.app.util.PositionValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Log4j2
@PreAuthorize(
    "hasAnyRole("
        + "'ROLE_ADMIN_NEXAGE', "
        + "'ROLE_MANAGER_NEXAGE', "
        + "'ROLE_MANAGER_NEXAGE', "
        + "'ROLE_USER_NEXAGE', "
        + "'ROLE_ADMIN_SELLER', "
        + "'ROLE_MANAGER_SELLER',"
        + "'ROLE_USER_SELLER', "
        + "'ROLE_API_SELLER' )")
public class NativePlacementDTOServiceImpl implements NativePlacementDTOService {

  @Value("${geneva.server.native.placement.gemini.id}")
  private Long geminiCompanyId;

  private final NativePlacementDTOMapper nativePlacementDTOMapper;
  private final NativePlacementHbPartnerService nativePlacementHbPartnerService;
  private final NativePlacementsParameterValidator nativePlacementsParameterValidator;
  private final NativePlacementTierService nativePlacementTierService;
  private final PositionRepository positionRepository;
  private final PositionValidator positionValidator;
  private final PlacementSyncProducer publisher;
  private final SellerSiteService sellerSiteService;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "hasAnyRole('ROLE_ADMIN_NEXAGE', 'ROLE_MANAGER_NEXAGE', 'ROLE_MANAGER_YIELD_NEXAGE')")
  public NativePlacementDTO createPlacement(NativePlacementRequestParamsDTO requestParams) {
    Position position = internalSave(requestParams, CrudOperation.CREATE);
    publisher.publishEvent(SyncEvent.createOf(position));
    return nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "hasRole('ROLE_ADMIN_NEXAGE') or hasRole('ROLE_MANAGER_NEXAGE') "
          + " or (@loginUserContext.canAccessSite(#siteId) and hasAnyRole('ROLE_ADMIN_SELLER', 'ROLE_MANAGER_SELLER'))")
  public NativePlacementDTO getNativePlacementById(
      @NotNull Long sellerId, @NotNull Long siteId, @NotNull Long positionId) {
    return sellerSiteService.getSite(siteId).getPositions().stream()
        .filter(p -> p.getPid().equals(positionId))
        .findFirst()
        .map(position -> nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService))
        .orElseThrow(
            () ->
                new GenevaValidationException(ServerErrorCodes.SERVER_PLACEMENT_NOT_FOUND_IN_SITE));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "hasAnyRole('ROLE_ADMIN_NEXAGE', 'ROLE_MANAGER_NEXAGE', 'ROLE_MANAGER_YIELD_NEXAGE')")
  public NativePlacementDTO updatePlacement(NativePlacementRequestParamsDTO requestParams) {
    Position position = internalSave(requestParams, UPDATE);
    return nativePlacementDTOMapper.map(position, nativePlacementHbPartnerService);
  }

  private Position saveOrUpdatePosition(
      NativePlacementRequestParamsDTO requestParams, CrudOperation crudOperation) {
    Site site = sellerSiteService.getSite(requestParams.getSiteId());
    NativePlacementDTO nativePlacementDTO = requestParams.getNativePlacement();
    Position position;
    if (UPDATE.equals(crudOperation)) {
      Position positionToUpdate =
          site.getPositions().stream()
              .filter(p -> p.getPid().equals(requestParams.getPlacementId()))
              .findFirst()
              .orElseThrow(
                  () -> new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS));
      positionValidator.validateVersion(positionToUpdate, nativePlacementDTO.getVersion());
      nativePlacementTierService.update(nativePlacementDTO, site, positionToUpdate);
      position =
          nativePlacementDTOMapper.map(
              nativePlacementDTO, positionToUpdate, nativePlacementHbPartnerService, site);
    } else {
      position =
          nativePlacementDTOMapper.map(nativePlacementDTO, nativePlacementHbPartnerService, site);
    }

    Optional.ofNullable(position.getPositionBuyer())
        .ifPresent(positionBuyer -> positionBuyer.setCompanyPid(geminiCompanyId));

    if (CREATE.equals(crudOperation) && StringUtils.isBlank(position.getName())) {
      position.setName(new UUIDGenerator().generateUniqueId());
    }

    position.setSite(site);
    return positionRepository.saveAndFlush(position);
  }

  private Position internalSave(
      NativePlacementRequestParamsDTO requestParams, CrudOperation crudOperation) {
    nativePlacementsParameterValidator.validateByOperation(crudOperation, requestParams);
    Position updatePosition = saveOrUpdatePosition(requestParams, crudOperation);
    log.debug("position [{}] was [{}] successfully ", updatePosition.getPid(), crudOperation);
    return updatePosition;
  }
}
