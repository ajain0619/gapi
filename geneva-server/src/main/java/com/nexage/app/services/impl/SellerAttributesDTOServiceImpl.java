package com.nexage.app.services.impl;

import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.app.dto.seller.SellerAttributesDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.SellerAttributesDTOMapper;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.services.SellerAttributesDTOService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class SellerAttributesDTOServiceImpl implements SellerAttributesDTOService {

  private final SellerAttributesRepository sellerAttributesRepo;
  private final LoginUserContext userContext;

  @Autowired
  public SellerAttributesDTOServiceImpl(
      SellerAttributesRepository sellerAttributesRepo, LoginUserContext userContext) {
    this.sellerAttributesRepo = sellerAttributesRepo;
    this.userContext = userContext;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "((@loginUserContext.isOcAdminNexage() OR @loginUserContext.isOcManagerNexage() OR @loginUserContext.isOcUserNexage() "
          + " OR @loginUserContext.isOcManagerYieldNexage() OR @loginUserContext.isOcManagerSmartexNexage()) "
          + " AND @loginUserContext.doSameOrNexageAffiliation(#sellerPid)) "
          + " OR (hasAnyRole('ROLE_ADMIN_SELLER', 'ROLE_MANAGER_SELLER', 'ROLE_USER_SELLER') AND @loginUserContext.doSameOrNexageAffiliation(#sellerPid))")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Page<SellerAttributesDTO> getSellerAttribute(Long sellerPid, Pageable pageable) {
    Page<SellerAttributes> sellerAttributes =
        sellerAttributesRepo.findAllBySellerPid(sellerPid, pageable);
    return sellerAttributes.map(SellerAttributesDTOMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isNexageAdminOrManager() && @loginUserContext.isNexageUser()")
  @Transactional(propagation = Propagation.REQUIRED)
  public SellerAttributesDTO updateSellerAttribute(SellerAttributesDTO sellerAttributesDTO) {
    SellerAttributes sellerAttributes =
        sellerAttributesRepo
            .findById(sellerAttributesDTO.getSellerPid())
            .orElseThrow(
                () ->
                    new GenevaValidationException(ServerErrorCodes.SERVER_NON_EXISTENT_TARGET_PID));

    sellerAttributes.setHumanOptOut(sellerAttributesDTO.getHumanOptOut());

    if (sellerAttributesDTO.getSmartQPSEnabled() != null) {
      validateSmartQPSEnabledUpdate(sellerAttributesDTO, sellerAttributes);
      sellerAttributes.setSmartQPSEnabled(sellerAttributesDTO.getSmartQPSEnabled());
    }

    if (sellerAttributesDTO.getHumanPrebidSampleRate() != null) {
      sellerAttributes.setHumanPrebidSampleRate(sellerAttributesDTO.getHumanPrebidSampleRate());
    }
    if (sellerAttributesDTO.getHumanPostbidSampleRate() != null) {
      sellerAttributes.setHumanPostbidSampleRate(sellerAttributesDTO.getHumanPostbidSampleRate());
    }

    sellerAttributes.setCustomDealFloorEnabled(sellerAttributesDTO.isCustomDealFloorEnabled());

    return SellerAttributesDTOMapper.MAPPER.map(sellerAttributesRepo.save(sellerAttributes));
  }

  private void validateSmartQPSEnabledUpdate(
      SellerAttributesDTO sellerAttributesDTO, SellerAttributes sellerAttributes) {
    if (!sellerAttributesDTO.getSmartQPSEnabled().equals(sellerAttributes.getSmartQPSEnabled())
        && !userContext.canEditSmartExchange()) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }
}
