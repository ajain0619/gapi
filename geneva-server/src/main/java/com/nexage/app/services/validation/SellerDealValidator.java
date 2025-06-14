package com.nexage.app.services.validation;

import com.nexage.admin.core.repository.CompanyViewRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealSiteDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SellerDealValidator {
  private final UserContext userContext;
  private final SiteRepository siteRepository;
  private final CompanyViewRepository companyViewRepository;

  @Autowired
  public SellerDealValidator(
      UserContext userContext,
      SiteRepository siteRepository,
      CompanyViewRepository companyViewRepository) {
    this.userContext = userContext;
    this.siteRepository = siteRepository;
    this.companyViewRepository = companyViewRepository;
  }

  /**
   * validate sellers in DirectDealDTO contains only one publisherPid and matches sellerId param
   *
   * @param sellerId seller ID
   * @param directDealDTO DirectDealDTO
   */
  @Transactional(readOnly = true)
  public void validateSeller(Long sellerId, DirectDealDTO directDealDTO) {
    if (directDealDTO.getPlacementFormula() != null) {
      validateSellerFormulaRuleDTOs(directDealDTO.getPlacementFormula(), sellerId);
    }
    int size = directDealDTO.getSellers().size();
    if (size > 1) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_MULTI_SELLERS);
    }
    if (size == 1 && !directDealDTO.getSellers().get(0).getPublisherPid().equals(sellerId)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_PIDS_MISMATCH);
    }
  }

  /**
   * validate sites in DirectDealDTO are allowed for user
   *
   * @param sellerId seller ID
   * @param directDealDTO DirectDealDTO
   */
  public void areAllSellerSitesAllowedForUser(Long sellerId, DirectDealDTO directDealDTO) {
    if (CollectionUtils.isNotEmpty(directDealDTO.getSites())) {
      List<Long> allowedSitePidsForUser =
          new ArrayList<>(
              siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                  userContext.getCurrentUser().getPid(), Set.of(sellerId)));
      if (!allowedSitePidsForUser.containsAll(
          directDealDTO.getSites().stream()
              .map(DealSiteDTO::getSitePid)
              .collect(Collectors.toSet()))) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_USER_RESTRICTION_ON_INVALID_SITE);
      }
    }
  }

  /**
   * validate deal category in DirectDealDTO is valid
   *
   * @param directDealDTO DirectDealDTO
   */
  public void validateDealCategory(DirectDealDTO directDealDTO) {
    if (directDealDTO.getDealCategory() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_INVALID_DEAL_CATEGORY);
    }
  }

  /**
   * validate visibility in DirectDealDTO set to right boolean value for given dealCategory
   *
   * @param directDealDTO DirectDealDTO
   */
  public void validateVisibility(DirectDealDTO directDealDTO) {
    if ((directDealDTO.getDealCategory() == null
        && Boolean.FALSE.equals(userContext.isNexageAdminOrManager()))) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  /***
   * validate sellerPid matching the seller from the placement formula
   *
   * @param placementFormula
   * @param sellerPid
   *
   */
  @Transactional(readOnly = true)
  public void validateSellerFormulaRuleDTOs(PlacementFormulaDTO placementFormula, Long sellerPid) {
    var company =
        companyViewRepository
            .findById(sellerPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));
    var formulaGroups =
        placementFormula.getFormulaGroups().stream()
            .filter(
                formulaGroup ->
                    !(formulaGroup.getFormulaRules().stream()
                        .filter(
                            formulaRule ->
                                formulaRule
                                        .getAttribute()
                                        .equals(FormulaAttributeDTO.PUBLISHER_NAME)
                                    && formulaRule.getOperator().equals(FormulaOperatorDTO.EQUALS)
                                    && formulaRule.getRuleData().equals(company.getName()))
                        .collect(Collectors.toList())
                        .isEmpty()))
            .collect(Collectors.toList());

    if (formulaGroups.size() != 1) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVALID_SELLER_NAME_PLACEMENT_FORMULA);
    }
  }
}
