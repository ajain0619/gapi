package com.nexage.app.services.impl;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.DealPositionRepository;
import com.nexage.admin.core.repository.DealPublisherRepository;
import com.nexage.admin.core.repository.DealSiteRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.MdmIdRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.specification.SellerDealSpecification;
import com.nexage.app.dto.InventoryMdmIdDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.CompanyMdmViewMapper;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.services.InventoryMdmIdService;
import com.nexage.app.util.InventoryMdmIdQueryFieldParameter;
import com.nexage.app.util.validator.InventoryMdmIdQueryFieldParams;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class InventoryMdmIdServiceImpl implements InventoryMdmIdService {

  private final LoginUserContext loginUserContext;
  private final DirectDealRepository directDealRepository;
  private final DealPublisherRepository dealPublisherRepository;
  private final DealSiteRepository dealSiteRepository;
  private final DealPositionRepository dealPositionRepository;
  private final MdmIdRepository mdmIdRepository;

  public InventoryMdmIdServiceImpl(
      LoginUserContext loginUserContext,
      DirectDealRepository directDealRepository,
      DealPublisherRepository dealPublisherRepository,
      DealSiteRepository dealSiteRepository,
      DealPositionRepository dealPositionRepository,
      MdmIdRepository mdmIdRepository) {
    this.loginUserContext = loginUserContext;
    this.directDealRepository = directDealRepository;
    this.dealPublisherRepository = dealPublisherRepository;
    this.dealSiteRepository = dealSiteRepository;
    this.dealPositionRepository = dealPositionRepository;
    this.mdmIdRepository = mdmIdRepository;
  }

  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or ((@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerYieldNexage()) "
          + "and @loginUserContext.isDealAdmin())")
  @Override
  public InventoryMdmIdDTO getMdmIdsForCurrentUser() {
    SpringUserDetails currentUser = loginUserContext.getCurrentUser();
    return InventoryMdmIdDTO.builder()
        .companyMdmIds(currentUser.getCompanyMdmIds())
        .sellerSeatMdmIds(currentUser.getSellerSeatMdmIds())
        .build();
  }

  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or ((@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerYieldNexage()) "
          + "and @loginUserContext.isDealAdmin())")
  @Override
  public Page<InventoryMdmIdDTO> getMdmIdsForAssignedSellers(
      InventoryMdmIdQueryFieldParams queryParams, Pageable pageable) {

    Optional<Long> dealPid =
        extractParamValue(queryParams, InventoryMdmIdQueryFieldParameter.DEAL_PID);
    Optional<Long> sellerPid =
        extractParamValue(queryParams, InventoryMdmIdQueryFieldParameter.SELLER_PID);

    if (dealPid.isEmpty() && sellerPid.isEmpty()) {
      return Page.empty(pageable);
    }

    Set<Long> inventorySellerPids =
        dealPid
            .map(
                dealPidValue ->
                    getAssignedInventorySellerPids(dealPidValue, sellerPid.orElse(null)))
            .orElseGet(() -> Set.of(sellerPid.get()));

    return mdmIdRepository
        .findMdmIdsForCompaniesIn(inventorySellerPids, pageable)
        .map(CompanyMdmViewMapper.MAPPER::map);
  }

  private <T> Optional<T> extractParamValue(
      MultiValueQueryParams queryParams, InventoryMdmIdQueryFieldParameter field) {
    T[] values = field.values(queryParams.getFields());
    return Stream.of(values).findFirst();
  }

  private Set<Long> getAssignedInventorySellerPids(long dealPid, Long sellerPid) {
    if (!loginUserContext.isNexageUser()) {
      if (sellerPid == null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_INVENTORY_MDM_REQUEST_NOT_ALLOWED);
      }

      Optional<DirectDeal> sellerDeal =
          directDealRepository.findOne(
              SellerDealSpecification.buildSpecification(sellerPid, dealPid, false));
      if (sellerDeal.isEmpty()) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_INVENTORY_MDM_REQUEST_NOT_ALLOWED);
      }
    }

    return getAssignedInventoryCompanyPids(dealPid);
  }

  private Set<Long> getAssignedInventoryCompanyPids(long dealPid) {
    Set<Long> companyPids = new HashSet<>();

    List<DealPublisher> dealPublishers = dealPublisherRepository.findByDealPid(dealPid);
    dealPublishers.forEach(dealPublisher -> companyPids.add(dealPublisher.getPubPid()));

    List<DealSite> dealSites = dealSiteRepository.findByDealPid(dealPid);
    dealSites.forEach(dealSite -> companyPids.add(dealSite.getSiteView().getCompanyPid()));

    List<DealPosition> dealPositions = dealPositionRepository.findByDealPid(dealPid);
    dealPositions.forEach(
        dealPosition ->
            companyPids.add(dealPosition.getPositionView().getSiteView().getCompanyPid()));

    return companyPids;
  }
}
