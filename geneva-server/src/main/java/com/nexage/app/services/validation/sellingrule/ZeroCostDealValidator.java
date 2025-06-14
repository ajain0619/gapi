package com.nexage.app.services.validation.sellingrule;

import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deals.DealPlacementDTO;
import com.nexage.app.dto.deals.DealPositionDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.dto.deals.DealSellerDTO;
import com.nexage.app.dto.deals.DealSiteDTO;
import com.nexage.app.dto.deals.FormulaAssignedInventoryDTO;
import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.FormulaInventoryService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ZeroCostDealValidator {
  private final GlobalConfigService globalConfigService;
  private final SiteRepository siteRepository;
  private final PositionRepository positionRepository;

  private final FormulaInventoryService formulaInventoryService;

  @Autowired
  public ZeroCostDealValidator(
      GlobalConfigService globalConfigService,
      SiteRepository siteRepository,
      PositionRepository positionRepository,
      FormulaInventoryService formulaInventoryService) {
    this.globalConfigService = globalConfigService;
    this.siteRepository = siteRepository;
    this.positionRepository = positionRepository;
    this.formulaInventoryService = formulaInventoryService;
  }

  /**
   * Throws an exception if the given direct deal DTO is a 0 cost (i.e. less than 0.01 in cost) deal
   * of a restricted {@link DealPriorityType} which is associated with a seller that isn't allowed.
   * This method also checks the sites and positions for associated sellers.
   *
   * @param directDealDTO The deal to check.
   */
  public void validateZeroCostDeals(DirectDealDTO directDealDTO) {
    if (shouldValidate(directDealDTO)) {
      if (directDealDTO.isAllSellers()) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED);
      }

      validateAutoUpdate(directDealDTO.getAutoUpdate());

      Stream<Long> associatedSellers =
          Stream.of(
                  directDealDTO.getSellers().stream().map(DealPublisherDTO::getPublisherPid),
                  directDealDTO.getSites().stream()
                      .map(com.nexage.app.dto.deal.DealSiteDTO::getSitePid)
                      .map(siteRepository::findCompanyPidByPidWithStatusNotDeleted),
                  directDealDTO.getPositions().stream()
                      .map(DealPositionDTO::getPositionPid)
                      .map(positionRepository::findCompanyPidByPlacementPid),
                  Optional.ofNullable(directDealDTO.getPlacementFormula())
                      .map(
                          placementFormula ->
                              formulaInventoryService.findPlacementsByFormula(
                                  placementFormula, Pageable.unpaged()))
                      .map(Page::getContent)
                      .stream()
                      .flatMap(List::stream)
                      .map(RuleFormulaPositionView::getPid)
                      .map(positionRepository::findCompanyPidByPlacementPid))
              .flatMap(Function.identity());

      validateAssociatedSellers(associatedSellers);
    }
  }

  /**
   * Throws an exception if the given direct deal DTO is a 0 cost (i.e. less than 0.01 in cost) deal
   * of a restricted {@link DealPriorityType} which is associated with a seller that isn't allowed.
   * This method also checks the sites and positions for associated sellers.
   *
   * @param directDeal The deal associated with this assigned inventory.
   * @param specificAssignedInventoryDTO The inventory that is being assigned to the deal.
   */
  public void validateZeroCostDeals(
      DirectDeal directDeal, SpecificAssignedInventoryDTO specificAssignedInventoryDTO) {
    if (shouldValidate(directDeal)) {
      Stream<Long> associatedSellers =
          Stream.of(
                  specificAssignedInventoryDTO.getContent().stream()
                      .map(DealSellerDTO::getSellerPid),
                  specificAssignedInventoryDTO.getContent().stream()
                      .flatMap(dealSellerDTO -> dealSellerDTO.getSites().stream())
                      .map(DealSiteDTO::getSitePid)
                      .map(siteRepository::findCompanyPidByPidWithStatusNotDeleted),
                  specificAssignedInventoryDTO.getContent().stream()
                      .flatMap(dealSellerDTO -> dealSellerDTO.getSites().stream())
                      .flatMap(dealSiteDTO -> dealSiteDTO.getPlacements().stream())
                      .map(DealPlacementDTO::getPlacementPid)
                      .map(positionRepository::findCompanyPidByPlacementPid))
              .flatMap(Function.identity());

      validateAssociatedSellers(associatedSellers);
    }
  }

  /**
   * Throws an exception if the given direct deal DTO is a 0 cost (i.e. less than 0.01 in cost) deal
   * of a restricted {@link DealPriorityType} which is associated with a seller that isn't allowed.
   * This method also checks the sites and positions for associated sellers.
   *
   * @param directDeal The deal which is associated with this placement formula.
   * @param formulaAssignedInventoryDTO The inventory that is being assigned to the deal.
   */
  public void validateZeroCostDeals(
      DirectDeal directDeal, FormulaAssignedInventoryDTO formulaAssignedInventoryDTO) {
    if (shouldValidate(directDeal)) {
      validateAutoUpdate(formulaAssignedInventoryDTO.getAutoUpdate());

      Stream<Long> associatedSellers =
          formulaInventoryService
              .findPlacementsByFormula(
                  formulaAssignedInventoryDTO.getPlacementFormula(), Pageable.unpaged())
              .stream()
              .map(RuleFormulaPositionView::getPid)
              .map(positionRepository::findCompanyPidByPlacementPid);

      validateAssociatedSellers(associatedSellers);
    }
  }

  private boolean shouldValidate(DirectDeal directDeal) {
    return directDeal.getPriorityType() == DealPriorityType.OPEN
        && directDeal.getFloor() != null
        && directDeal.getFloor().compareTo(BigDecimal.valueOf(0.01)) < 0;
  }

  private boolean shouldValidate(DirectDealDTO directDealDTO) {
    return directDealDTO.getPriorityType() == DealPriorityType.OPEN
        && directDealDTO.getFloor() != null
        && directDealDTO.getFloor().compareTo(BigDecimal.valueOf(0.01)) < 0;
  }

  private void validateAutoUpdate(Boolean autoUpdate) {
    if (Boolean.TRUE.equals(autoUpdate)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DEAL_ZERO_COST_AUTO_UPDATE_DISALLOWED);
    }
  }

  private void validateAssociatedSellers(Stream<Long> associatedSellers) {
    Set<Long> sellerAllowList =
        new HashSet<>(
            globalConfigService.getLongListValue(
                GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST));

    if (associatedSellers.anyMatch(Predicate.not(sellerAllowList::contains))) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED);
    }
  }
}
