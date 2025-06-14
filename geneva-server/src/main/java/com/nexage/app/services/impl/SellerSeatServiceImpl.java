package com.nexage.app.services.impl;

import static com.nexage.admin.core.model.SellerSeat.ENABLED;
import static com.nexage.admin.core.specification.SellerSeatSpecification.withNameLike;
import static com.nexage.admin.core.specification.SellerSeatSpecification.withNonEmptySellers;
import static com.nexage.admin.core.specification.SellerSeatSpecification.withStatus;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.data.jpa.domain.Specification.where;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.SellerSeatRepository;
import com.nexage.app.dto.SellerSeatDTO;
import com.nexage.app.dto.user.CompanyViewDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.SellerSeatDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerSeatService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
public class SellerSeatServiceImpl implements SellerSeatService {

  private final SellerSeatRepository sellerSeatRepository;
  private final CompanyRepository companyRepository;
  private final UserContext userContext;

  public SellerSeatServiceImpl(
      SellerSeatRepository sellerSeatRepository,
      CompanyRepository companyRepository,
      UserContext userContext) {
    this.sellerSeatRepository = sellerSeatRepository;
    this.companyRepository = companyRepository;
    this.userContext = userContext;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(#sellerSeatPid)")
  public SellerSeatDTO getSellerSeat(Long sellerSeatPid) {
    SellerSeat sellerSeat = getOneFromRepository(sellerSeatPid);
    return mapToDto(sellerSeat);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage() "
          + "or @loginUserContext.isNexageUser()")
  public SellerSeatDTO createSellerSeat(SellerSeatDTO sellerSeatDTO) {
    Set<Company> sellers = fetchSellers(sellerSeatDTO);
    SellerSeat sellerSeat = SellerSeatDTOMapper.MAPPER.map(sellerSeatDTO);
    sellers.forEach(sellerSeat::addSeller);
    sellerSeat.setCreatedBy(userContext.getPid());
    return saveAndReturnDTO(sellerSeat);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage() "
          + "or @loginUserContext.isNexageUser()")
  public SellerSeatDTO updateSellerSeat(Long sellerSeatPid, SellerSeatDTO sellerSeatDTO) {
    SellerSeat sellerSeat = getOneFromRepository(sellerSeatPid);
    Set<Company> sellersFromDTO = fetchSellers(sellerSeatDTO);
    updateSellers(sellerSeat, sellersFromDTO);
    SellerSeatDTOMapper.MAPPER.updateEntityFromDTO(sellerSeatDTO, sellerSeat);
    sellerSeat = sellerSeatRepository.saveAndFlush(sellerSeat);
    return mapToDto(sellerSeat);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage() "
          + "or @loginUserContext.isNexageUser()")
  public Page<SellerSeatDTO> findAll(
      boolean assignable, Set<String> queryFields, String queryTerm, Pageable pageable) {
    Specification<SellerSeat> sellerSeatSpec = null;
    if (assignable) {
      sellerSeatSpec = where(withStatus(ENABLED)).and(withNonEmptySellers());
    }
    if (isNotEmpty(queryFields)) {
      sellerSeatSpec =
          sellerSeatSpec != null
              ? sellerSeatSpec.and(withNameLike(queryTerm))
              : where(withNameLike(queryTerm));
    }
    return sellerSeatRepository
        .findAll(sellerSeatSpec, pageable)
        .map(SellerSeatDTOMapper.MAPPER::map);
  }

  private void updateSellers(SellerSeat sellerSeat, Set<Company> sellersFromDTO) {
    Set<Company> detachedSellers =
        sellerSeat.getSellers().stream()
            .filter(seller -> !sellersFromDTO.contains(seller))
            .collect(Collectors.toSet());
    detachedSellers.forEach(sellerSeat::removeSeller);

    sellersFromDTO.stream()
        .filter(seller -> !sellerSeat.getSellers().contains(seller))
        .forEach(sellerSeat::addSeller);
  }

  private Set<Company> fetchSellers(SellerSeatDTO sellerSeatDTO) {
    Set<CompanyViewDTO> sellersFromDTO = sellerSeatDTO.getSellers();
    if (isEmpty(sellersFromDTO)) {
      return Collections.emptySet();
    }
    Set<Long> sellersPids =
        sellersFromDTO.stream().map(CompanyViewDTO::getPid).collect(Collectors.toSet());
    Set<Company> sellers =
        companyRepository.findSellersWithSpecificPids(ImmutableSet.copyOf(sellersPids));
    if (sellersPids.size() != sellers.size()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_NOT_ALL_SELLERS_FOUND);
    }
    return Collections.unmodifiableSet(sellers);
  }

  private SellerSeatDTO mapToDto(SellerSeat sellerSeat) {
    return SellerSeatDTOMapper.MAPPER.map(sellerSeat);
  }

  private SellerSeat getOneFromRepository(Long sellerSeatPid) {
    Optional<SellerSeat> sellerSeatOptional = sellerSeatRepository.findById(sellerSeatPid);
    return sellerSeatOptional.orElseThrow(
        () -> new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND));
  }

  private SellerSeatDTO saveAndReturnDTO(SellerSeat sellerSeat) {
    SellerSeat createdSellerSeat = sellerSeatRepository.save(sellerSeat);
    return mapToDto(createdSellerSeat);
  }
}
