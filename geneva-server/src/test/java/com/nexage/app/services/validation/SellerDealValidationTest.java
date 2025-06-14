package com.nexage.app.services.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.CompanyViewRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealSiteDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerDealValidationTest {

  @Mock UserContext userContext;
  @Mock SiteRepository siteRepository;
  @InjectMocks SellerDealValidator sellerDealValidator;
  @Mock private CompanyViewRepository companyViewRepository;
  private DirectDealDTO directDealDTO = new DirectDealDTO();
  @Mock private DirectDealDTO companyFormulaDirectDealDTO;

  @BeforeEach
  void setup() {
    sellerDealValidator =
        new SellerDealValidator(userContext, siteRepository, companyViewRepository);
  }

  @Test
  void shouldNotThrowExceptionForValidSeller() {
    assertDoesNotThrow(() -> sellerDealValidator.validateSeller(1l, directDealDTO));
  }

  @Test
  void shouldThrowMultiSellersExceptionForMoreThanOneSeller() {
    directDealDTO
        .getSellers()
        .add(new DealPublisherDTO.Builder().setPid(33L).setPublisherPid(3L).build());
    directDealDTO
        .getSellers()
        .add(new DealPublisherDTO.Builder().setPid(34L).setPublisherPid(4L).build());

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealValidator.validateSeller(1l, directDealDTO));
    assertEquals(ServerErrorCodes.SERVER_MULTI_SELLERS, exception.getErrorCode());
  }

  @Test
  void shouldThrowSellerPIDsMismatchExceptionWhenSellerIdMismatch() {
    directDealDTO
        .getSellers()
        .add(new DealPublisherDTO.Builder().setPid(33L).setPublisherPid(3L).build());

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealValidator.validateSeller(1l, directDealDTO));
    assertEquals(ServerErrorCodes.SERVER_SELLER_PIDS_MISMATCH, exception.getErrorCode());
  }

  @Test
  void shouldNotThrowExceptionForValidSites() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.NEXAGE);
    User user = TestObjectsFactory.createUser(User.Role.ROLE_ADMIN, company);
    SpringUserDetails loggedUserDetails = Mockito.mock(SpringUserDetails.class);
    given(loggedUserDetails.getPid()).willReturn(user.getPid());
    given(userContext.getCurrentUser()).willReturn(loggedUserDetails);
    directDealDTO.getSites().add(new DealSiteDTO.Builder().setSitePid(123L).build());
    directDealDTO.getSites().add(new DealSiteDTO.Builder().setSitePid(321L).build());
    given(
            siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                anyLong(), any()))
        .willReturn(Set.of(123L, 321L, 231L));

    // when & then
    assertDoesNotThrow(
        () -> sellerDealValidator.areAllSellerSitesAllowedForUser(1L, directDealDTO));
  }

  @Test
  void shouldNotThrowExceptionWhenSitesEmpty() {
    // when & then
    assertDoesNotThrow(
        () -> sellerDealValidator.areAllSellerSitesAllowedForUser(1L, directDealDTO));
  }

  @Test
  void shouldThrowUserRestrictionOnInvalidSite() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.NEXAGE);
    User user = TestObjectsFactory.createUser(User.Role.ROLE_ADMIN, company);
    SpringUserDetails loggedUserDetails = Mockito.mock(SpringUserDetails.class);
    given(loggedUserDetails.getPid()).willReturn(user.getPid());
    given(userContext.getCurrentUser()).willReturn(loggedUserDetails);
    directDealDTO.getSites().add(new DealSiteDTO.Builder().setPid(12L).build());
    directDealDTO.getSites().add(new DealSiteDTO.Builder().setSitePid(321L).build());
    given(
            siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                anyLong(), anySet()))
        .willReturn(Set.of(123L, 321L, 231L));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealValidator.areAllSellerSitesAllowedForUser(1L, directDealDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_USER_RESTRICTION_ON_INVALID_SITE, exception.getErrorCode());
  }

  @Test
  void shouldNotThrowExceptionWhenDealCategorySeller() {
    directDealDTO.setDealCategory(1);
    assertDoesNotThrow(() -> sellerDealValidator.validateDealCategory(directDealDTO));
  }

  @Test
  void shouldNotThrowExceptionForValidVisibility() {
    directDealDTO.setDealCategory(1);
    assertDoesNotThrow(() -> sellerDealValidator.validateVisibility(directDealDTO));
  }

  @Test
  void shouldNotThrowExceptionWhenUserNexageAdminORManagerWithValidVisibility() {
    given(userContext.isNexageAdminOrManager()).willReturn(true);
    assertDoesNotThrow(() -> sellerDealValidator.validateVisibility(directDealDTO));
  }

  @Test
  void shouldThrowNotAuthorizedExceptionWhenNotUserNexageAdminORManagerWithValidVisibility() {
    given(userContext.isNexageAdminOrManager()).willReturn(false);
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> sellerDealValidator.validateVisibility(directDealDTO));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenCompanyDoesNotMatchSellerRuleFormula()
      throws IOException {
    given(companyViewRepository.findById(123L))
        .willAnswer(
            invocationOnMock ->
                Optional.of(new CompanyView(123L, "Yahoo", CompanyType.SELLER, true)));

    PlacementFormulaDTO dto = getFormulaAssignedInventoryListDTO();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealValidator.validateSellerFormulaRuleDTOs(dto, 123L));
    assertEquals(
        ServerErrorCodes.SERVER_INVALID_SELLER_NAME_PLACEMENT_FORMULA, exception.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenCompanyDoesNotExist() throws IOException {
    when(companyViewRepository.findById(any())).thenReturn(Optional.empty());

    PlacementFormulaDTO dto = getFormulaAssignedInventoryListDTO();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealValidator.validateSellerFormulaRuleDTOs(dto, 123L));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenFormulaGroupsAreEmpty() throws IOException {
    given(companyViewRepository.findById(123L))
        .willAnswer(
            invocationOnMock ->
                Optional.of(new CompanyView(123L, "Yahoo", CompanyType.SELLER, true)));
    PlacementFormulaDTO dto = new PlacementFormulaDTO();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealValidator.validateSellerFormulaRuleDTOs(dto, 123L));
    assertEquals(
        ServerErrorCodes.SERVER_INVALID_SELLER_NAME_PLACEMENT_FORMULA, exception.getErrorCode());
  }

  @Test
  void shouldThrowInvalidSellerNameFormulaExceptionWhenCompanyDoesNotMatchSellerRuleFormula()
      throws IOException {
    given(companyViewRepository.findById(123L))
        .willAnswer(
            invocationOnMock ->
                Optional.of(new CompanyView(123L, "Yahoo", CompanyType.SELLER, true)));

    given(companyFormulaDirectDealDTO.getPlacementFormula())
        .willReturn(getFormulaAssignedInventoryListDTO());
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealValidator.validateSeller(123L, companyFormulaDirectDealDTO));
    assertEquals(
        ServerErrorCodes.SERVER_INVALID_SELLER_NAME_PLACEMENT_FORMULA, exception.getErrorCode());
  }

  @Test
  void shouldNotThrowExceptionWhenCompanyMatchesWithSellerRuleFormula() throws IOException {
    given(companyViewRepository.findById(123L))
        .willAnswer(
            invocationOnMock ->
                Optional.of(new CompanyView(123L, "Provision", CompanyType.SELLER, true)));

    given(companyFormulaDirectDealDTO.getPlacementFormula())
        .willReturn(getFormulaAssignedInventoryListDTO());
    given(companyFormulaDirectDealDTO.getSellers()).willReturn(directDealDTO.getSellers());
    assertDoesNotThrow(() -> sellerDealValidator.validateSeller(123L, companyFormulaDirectDealDTO));
  }

  private PlacementFormulaDTO getFormulaAssignedInventoryListDTO() throws IOException {

    PlacementFormulaDTO dto = new PlacementFormulaDTO();
    FormulaRuleDTO formula = new FormulaRuleDTO();
    List<FormulaRuleDTO> formulaList = new ArrayList<>();
    formulaList.add(formula);
    formula.setAttribute(FormulaAttributeDTO.PUBLISHER_NAME);
    formula.setOperator(FormulaOperatorDTO.EQUALS);
    formula.setRuleData("Provision");
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaList);
    formulaGroupDTO.setFormulaRules(formulaList);
    List<FormulaGroupDTO> formulaGroupDTOList = new ArrayList<>();
    formulaGroupDTOList.add(formulaGroupDTO);
    dto.setFormulaGroups(formulaGroupDTOList);

    return dto;
  }
}
