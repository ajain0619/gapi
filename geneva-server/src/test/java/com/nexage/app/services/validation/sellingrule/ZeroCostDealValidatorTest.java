package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.FormulaInventoryService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ZeroCostDealValidatorTest {

  @Mock private GlobalConfigService globalConfigServiceMock;
  @Mock private SiteRepository siteRepositoryMock;
  @Mock private PositionRepository positionRepositoryMock;

  @Mock private FormulaInventoryService formulaInventoryServiceMock;
  @InjectMocks private ZeroCostDealValidator zeroCostDealValidator;

  @Test
  void shouldValidateZeroCostDealOfPriorityType() {
    DealPriorityType dealPriorityType = DealPriorityType.OPEN;
    var directDealDTOMock = mock(DirectDealDTO.class);

    when(directDealDTOMock.getPriorityType()).thenReturn(dealPriorityType);
    when(directDealDTOMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(directDealDTOMock.getSellers())
        .thenReturn(
            List.of(
                new DealPublisherDTO.Builder()
                    .setPublisherPid(1111L) // Seller not allowed.
                    .build()));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> zeroCostDealValidator.validateZeroCostDeals(directDealDTOMock));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void
      directDealDtoValidationShouldPassWhenSellersAndSitesAndPlacementsInAllowListForZeroCostDeals() {
    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L, 1112L, 1113L, 1114L));

    when(siteRepositoryMock.findCompanyPidByPidWithStatusNotDeleted(2223L)).thenReturn(1113L);
    when(siteRepositoryMock.findCompanyPidByPidWithStatusNotDeleted(2224L)).thenReturn(1114L);
    when(positionRepositoryMock.findCompanyPidByPlacementPid(3333L)).thenReturn(1113L);
    when(positionRepositoryMock.findCompanyPidByPlacementPid(3334L)).thenReturn(1114L);

    var directDealDTOMock = mock(DirectDealDTO.class);

    when(directDealDTOMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealDTOMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(directDealDTOMock.getSellers())
        .thenReturn(
            List.of(
                new DealPublisherDTO.Builder().setPublisherPid(1113L).build(),
                new DealPublisherDTO.Builder().setPublisherPid(1114L).build()));
    when(directDealDTOMock.getSites())
        .thenReturn(
            List.of(
                new com.nexage.app.dto.deal.DealSiteDTO.Builder().setSitePid(2223L).build(),
                new com.nexage.app.dto.deal.DealSiteDTO.Builder().setSitePid(2224L).build()));
    when(directDealDTOMock.getPositions())
        .thenReturn(
            List.of(
                new DealPositionDTO.Builder().setPositionPid(3333L).build(),
                new DealPositionDTO.Builder().setPositionPid(3334L).build()));

    zeroCostDealValidator.validateZeroCostDeals(directDealDTOMock);
  }

  @Test
  void directDealDtoValidationShouldThrowWhenSellerNotInAllowListForZeroCostDeals() {
    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L, 1112L, 1113L));

    var directDealDTOMock = mock(DirectDealDTO.class);

    when(directDealDTOMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealDTOMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(directDealDTOMock.getSellers())
        .thenReturn(
            List.of(
                new DealPublisherDTO.Builder().setPublisherPid(1113L).build(),
                new DealPublisherDTO.Builder()
                    .setPublisherPid(1114L) // Seller not allowed.
                    .build()));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> zeroCostDealValidator.validateZeroCostDeals(directDealDTOMock));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void directDealDtoValidationShouldThrowWhenSiteNotInAllowListForZeroCostDeals() {
    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L, 1112L, 1113L));

    var directDealDTOMock = mock(DirectDealDTO.class);

    when(directDealDTOMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealDTOMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(directDealDTOMock.getSites())
        .thenReturn(
            List.of(
                new com.nexage.app.dto.deal.DealSiteDTO.Builder().setSitePid(2223L).build(),
                new com.nexage.app.dto.deal.DealSiteDTO.Builder()
                    .setSitePid(2224L) // Site not allowed.
                    .build()));

    when(siteRepositoryMock.findCompanyPidByPidWithStatusNotDeleted(2223L)).thenReturn(1113L);
    when(siteRepositoryMock.findCompanyPidByPidWithStatusNotDeleted(2224L)).thenReturn(1114L);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> zeroCostDealValidator.validateZeroCostDeals(directDealDTOMock));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void directDealDtoValidationShouldThrowWhenPlacementNotInAllowListForZeroCostDeals() {
    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L, 1112L, 1113L));

    var directDealDTOMock = mock(DirectDealDTO.class);

    when(directDealDTOMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealDTOMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(directDealDTOMock.getPositions())
        .thenReturn(
            List.of(
                new DealPositionDTO.Builder().setPositionPid(3333L).build(),
                new DealPositionDTO.Builder()
                    .setPositionPid(3334L) // Position not allowed.
                    .build()));

    when(positionRepositoryMock.findCompanyPidByPlacementPid(3333L)).thenReturn(1113L);
    when(positionRepositoryMock.findCompanyPidByPlacementPid(3334L)).thenReturn(1114L);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> zeroCostDealValidator.validateZeroCostDeals(directDealDTOMock));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void directDealDtoValidationShouldThrowWhenFormulaPlacementNotInAllowListForZeroCostDeals() {
    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L, 1112L, 1113L));

    var directDealDTOMock = mock(DirectDealDTO.class);
    var placementFormulaMock = mock(PlacementFormulaDTO.class);

    when(directDealDTOMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealDTOMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(directDealDTOMock.getPlacementFormula()).thenReturn(placementFormulaMock);

    when(formulaInventoryServiceMock.findPlacementsByFormula(
            placementFormulaMock, Pageable.unpaged()))
        .thenReturn(
            new PageImpl(
                List.of(
                    RuleFormulaPositionView.builder().pid(3333L).build(),
                    RuleFormulaPositionView.builder().pid(3334L).build())));
    when(positionRepositoryMock.findCompanyPidByPlacementPid(3333L)).thenReturn(1113L);
    when(positionRepositoryMock.findCompanyPidByPlacementPid(3334L)).thenReturn(1114L);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> zeroCostDealValidator.validateZeroCostDeals(directDealDTOMock));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void directDealDtoValidationShouldThrowWhenAutoUpdateIsEnabledForZeroCostDeals() {
    var directDealDTOMock = mock(DirectDealDTO.class);

    when(directDealDTOMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealDTOMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(directDealDTOMock.getAutoUpdate()).thenReturn(true);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> zeroCostDealValidator.validateZeroCostDeals(directDealDTOMock));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_AUTO_UPDATE_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void directDealDtoValidationShouldThrowWhenAllSellersIsEnabledForZeroCostDeals() {
    var directDealDTOMock = mock(DirectDealDTO.class);

    when(directDealDTOMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealDTOMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(directDealDTOMock.isAllSellers()).thenReturn(true);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> zeroCostDealValidator.validateZeroCostDeals(directDealDTOMock));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void specificAssignedInventoryDtoValidationShouldThrowWhenSellerNotInAllowListForZeroCostDeals() {
    var directDealMock = mock(DirectDeal.class);
    var specificAssignedInventoryDTO =
        createSpecificAssignedInventoryDTO(List.of(createDealSellerDTO(1112L, List.of())));

    when(directDealMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealMock.getFloor()).thenReturn(BigDecimal.ZERO);

    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                zeroCostDealValidator.validateZeroCostDeals(
                    directDealMock, specificAssignedInventoryDTO));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void specificAssignedInventoryDtoValidationShouldThrowWhenSiteNotInAllowListForZeroCostDeals() {
    var directDealMock = mock(DirectDeal.class);
    var specificAssignedInventoryDTO =
        createSpecificAssignedInventoryDTO(
            List.of(createDealSellerDTO(1111L, List.of(createDealSiteDTO(2222L, List.of())))));

    when(directDealMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealMock.getFloor()).thenReturn(BigDecimal.ZERO);

    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L));
    when(siteRepositoryMock.findCompanyPidByPidWithStatusNotDeleted(2222L))
        .thenReturn(1112L); // Not in allow list.

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                zeroCostDealValidator.validateZeroCostDeals(
                    directDealMock, specificAssignedInventoryDTO));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void
      specificAssignedInventoryDtoValidationShouldThrowWhenPlacementNotInAllowListForZeroCostDeals() {
    var directDealMock = mock(DirectDeal.class);
    var specificAssignedInventoryDTO =
        createSpecificAssignedInventoryDTO(
            List.of(
                createDealSellerDTO(
                    1111L,
                    List.of(createDealSiteDTO(2221L, List.of(createDealPlacementDTO(3332L)))))));

    when(directDealMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealMock.getFloor()).thenReturn(BigDecimal.ZERO);

    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L));
    when(siteRepositoryMock.findCompanyPidByPidWithStatusNotDeleted(2221L)).thenReturn(1111L);
    when(positionRepositoryMock.findCompanyPidByPlacementPid(3332L))
        .thenReturn(1112L); // Not in allow list.

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                zeroCostDealValidator.validateZeroCostDeals(
                    directDealMock, specificAssignedInventoryDTO));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void placementFormulaDtoValidationShouldPassWhenPlacementsInAllowListForZeroCostDeals() {
    var directDealMock = mock(DirectDeal.class);
    var formulaAssignedInventoryDTO = mock(FormulaAssignedInventoryDTO.class);
    var placementFormulaDTOMock = mock(PlacementFormulaDTO.class);

    when(directDealMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(formulaAssignedInventoryDTO.getPlacementFormula()).thenReturn(placementFormulaDTOMock);

    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L, 1112L));
    when(formulaInventoryServiceMock.findPlacementsByFormula(
            placementFormulaDTOMock, Pageable.unpaged()))
        .thenReturn(
            new PageImpl(
                List.of(
                    RuleFormulaPositionView.builder().pid(1L).build(),
                    RuleFormulaPositionView.builder().pid(2L).build())));
    when(positionRepositoryMock.findCompanyPidByPlacementPid(1L)).thenReturn(1111L);
    when(positionRepositoryMock.findCompanyPidByPlacementPid(2L)).thenReturn(1112L);

    zeroCostDealValidator.validateZeroCostDeals(directDealMock, formulaAssignedInventoryDTO);
  }

  @Test
  void placementFormulaDtoValidationShouldThrowWhenPlacementsNotInAllowListForZeroCostDeals() {
    var directDealMock = mock(DirectDeal.class);
    var formulaAssignedInventoryDTO = mock(FormulaAssignedInventoryDTO.class);
    var placementFormulaDTOMock = mock(PlacementFormulaDTO.class);

    when(directDealMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(formulaAssignedInventoryDTO.getPlacementFormula()).thenReturn(placementFormulaDTOMock);

    when(globalConfigServiceMock.getLongListValue(
            GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST))
        .thenReturn(List.of(1111L));
    when(formulaInventoryServiceMock.findPlacementsByFormula(
            placementFormulaDTOMock, Pageable.unpaged()))
        .thenReturn(
            new PageImpl(
                List.of(
                    RuleFormulaPositionView.builder().pid(1L).build(),
                    RuleFormulaPositionView.builder().pid(2L).build())));
    when(positionRepositoryMock.findCompanyPidByPlacementPid(1L)).thenReturn(1111L);
    when(positionRepositoryMock.findCompanyPidByPlacementPid(2L)).thenReturn(1112L);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                zeroCostDealValidator.validateZeroCostDeals(
                    directDealMock, formulaAssignedInventoryDTO));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
  }

  @Test
  void placementFormulaDtoValidationShouldThrowWhenAutoUpdateIsEnabledForZeroCostDeals() {
    var directDealMock = mock(DirectDeal.class);
    var formulaAssignedInventoryDTO = mock(FormulaAssignedInventoryDTO.class);

    when(directDealMock.getPriorityType()).thenReturn(DealPriorityType.OPEN);
    when(directDealMock.getFloor()).thenReturn(BigDecimal.ZERO);
    when(formulaAssignedInventoryDTO.getAutoUpdate()).thenReturn(true);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                zeroCostDealValidator.validateZeroCostDeals(
                    directDealMock, formulaAssignedInventoryDTO));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_AUTO_UPDATE_DISALLOWED, exception.getErrorCode());
  }

  private SpecificAssignedInventoryDTO createSpecificAssignedInventoryDTO(
      List<DealSellerDTO> dealSellerDTOs) {
    var specificAssignedInventoryDTO = new SpecificAssignedInventoryDTO();

    specificAssignedInventoryDTO.setContent(dealSellerDTOs);
    return specificAssignedInventoryDTO;
  }

  private DealSellerDTO createDealSellerDTO(Long sellerPid, List<DealSiteDTO> dealSiteDTOs) {
    var dealSellerDTO = new DealSellerDTO();

    dealSellerDTO.setSellerPid(sellerPid);
    dealSellerDTO.setSites(dealSiteDTOs);
    return dealSellerDTO;
  }

  private DealSiteDTO createDealSiteDTO(Long sitePid, List<DealPlacementDTO> dealPlacementDTOs) {
    var dealSiteDTO = new DealSiteDTO();

    dealSiteDTO.setSitePid(sitePid);
    dealSiteDTO.setPlacements(dealPlacementDTOs);
    return dealSiteDTO;
  }

  private DealPlacementDTO createDealPlacementDTO(Long placementPid) {
    var dealPlacementDTO = new DealPlacementDTO();

    dealPlacementDTO.setPlacementPid(placementPid);
    return dealPlacementDTO;
  }
}
