package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyMdmId;
import com.nexage.admin.core.model.CompanyMdmView;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.SellerSeatMdmId;
import com.nexage.admin.core.model.SellerSeatMdmView;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.repository.DealPositionRepository;
import com.nexage.admin.core.repository.DealPublisherRepository;
import com.nexage.admin.core.repository.DealSiteRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.MdmIdRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.InventoryMdmIdDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.util.InventoryMdmIdQueryFieldParameter;
import com.nexage.app.util.validator.InventoryMdmIdQueryFieldParams;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class InventoryMdmIdServiceImplTest {

  @Mock private LoginUserContext loginUserContext;
  @Mock private DirectDealRepository directDealRepository;
  @Mock private DealPublisherRepository dealPublisherRepository;
  @Mock private DealSiteRepository dealSiteRepository;
  @Mock private DealPositionRepository dealPositionRepository;
  @Mock private MdmIdRepository mdmIdRepository;
  @InjectMocks private InventoryMdmIdServiceImpl mdmIdService;

  private SpringUserDetails currentUser;

  @BeforeEach
  void setup() {
    currentUser = mock(SpringUserDetails.class);
    lenient().when(loginUserContext.getCurrentUser()).thenReturn(currentUser);
  }

  @Test
  void shouldGetCompanyMdmIdsForCurrentUser() {
    when(currentUser.getCompanyMdmIds()).thenReturn(Set.of("100"));

    InventoryMdmIdDTO result = mdmIdService.getMdmIdsForCurrentUser();

    assertNull(result.getSellerPid());
    assertEquals(result.getCompanyMdmIds(), Set.of("100"));
    assertTrue(result.getSellerSeatMdmIds().isEmpty());
  }

  @Test
  void shouldGetSellerSeatMdmIdsForCurrentUser() {
    when(currentUser.getSellerSeatMdmIds()).thenReturn(Set.of("43", "44"));

    InventoryMdmIdDTO result = mdmIdService.getMdmIdsForCurrentUser();

    assertNull(result.getSellerPid());
    assertTrue(result.getCompanyMdmIds().isEmpty());
    assertEquals(result.getSellerSeatMdmIds(), Set.of("43", "44"));
  }

  @Test
  void shouldReturnEmptyResponseIfNoDealAndNoSellerSpecified() {
    InventoryMdmIdQueryFieldParams queryFieldParams =
        new InventoryMdmIdQueryFieldParams(new LinkedMultiValueMap<>(), SearchQueryOperator.OR);

    Page<InventoryMdmIdDTO> result =
        mdmIdService.getMdmIdsForAssignedSellers(queryFieldParams, Pageable.unpaged());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenDealWithUserNotNexageAndSellerPidNotSpecified() {
    MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
    paramValues.add(InventoryMdmIdQueryFieldParameter.DEAL_PID.getName(), "123");
    InventoryMdmIdQueryFieldParams queryFieldParams =
        new InventoryMdmIdQueryFieldParams(paramValues, SearchQueryOperator.OR);

    when(loginUserContext.isNexageUser()).thenReturn(false);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> mdmIdService.getMdmIdsForAssignedSellers(queryFieldParams, Pageable.unpaged()));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_MDM_REQUEST_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenDealWithUserNotNexageAndGivenDealNotVisibleToSeller() {
    MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
    paramValues.add(InventoryMdmIdQueryFieldParameter.DEAL_PID.getName(), "123");
    paramValues.add(InventoryMdmIdQueryFieldParameter.SELLER_PID.getName(), "1000");
    InventoryMdmIdQueryFieldParams queryFieldParams =
        new InventoryMdmIdQueryFieldParams(paramValues, SearchQueryOperator.OR);

    when(loginUserContext.isNexageUser()).thenReturn(false);
    when(directDealRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> mdmIdService.getMdmIdsForAssignedSellers(queryFieldParams, Pageable.unpaged()));
    assertEquals(
        ServerErrorCodes.SERVER_INVENTORY_MDM_REQUEST_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void shouldReturnCompanyMdmIdsWhenDealWithUserNotNexageAndSellerPidSpecified() {
    long dealPid = 123L;
    long sellerPid = 1000L;
    String mdmId = "mdm1000";
    when(dealPublisherRepository.findByDealPid(dealPid))
        .thenReturn(List.of(setupDealPublisher(sellerPid)));

    CompanyMdmId companyMdmId = new CompanyMdmId();
    companyMdmId.setId(mdmId);
    CompanyMdmView companyMdmView = new CompanyMdmView(sellerPid, List.of(companyMdmId), null);
    when(mdmIdRepository.findMdmIdsForCompaniesIn(any(Set.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(companyMdmView), Pageable.unpaged(), 1));

    MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
    paramValues.add(InventoryMdmIdQueryFieldParameter.DEAL_PID.getName(), Long.toString(dealPid));
    paramValues.add(
        InventoryMdmIdQueryFieldParameter.SELLER_PID.getName(), Long.toString(sellerPid));
    InventoryMdmIdQueryFieldParams queryFieldParams =
        new InventoryMdmIdQueryFieldParams(paramValues, SearchQueryOperator.OR);

    when(loginUserContext.isNexageUser()).thenReturn(false);
    when(directDealRepository.findOne(any(Specification.class)))
        .thenReturn(Optional.of(new DirectDeal()));

    Page<InventoryMdmIdDTO> result =
        mdmIdService.getMdmIdsForAssignedSellers(queryFieldParams, Pageable.unpaged());

    assertEquals(1, result.getContent().size());

    InventoryMdmIdDTO result0 = result.getContent().get(0);
    assertEquals(sellerPid, result0.getSellerPid());
    assertEquals(Set.of(mdmId), result0.getCompanyMdmIds());
    assertTrue(result0.getSellerSeatMdmIds().isEmpty());
  }

  @Test
  void shouldReturnCompanyMdmIdsWhenDealWithUserIsNexage() {
    long dealPid = 123L;
    Set<Long> sellerPids = Set.of(10L, 20L, 30L, 40L, 50L, 60L);
    when(dealPublisherRepository.findByDealPid(dealPid))
        .thenReturn(List.of(setupDealPublisher(10L), setupDealPublisher(20L)));
    when(dealSiteRepository.findByDealPid(dealPid))
        .thenReturn(List.of(setupDealSite(dealPid, 30L), setupDealSite(dealPid, 40L)));
    when(dealPositionRepository.findByDealPid(dealPid))
        .thenReturn(List.of(setupDealPosition(dealPid, 50L), setupDealPosition(dealPid, 60L)));
    when(mdmIdRepository.findMdmIdsForCompaniesIn(sellerPids, Pageable.unpaged()))
        .thenReturn(setupCompanyMdmViewResponse(sellerPids, Pageable.unpaged()));

    MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
    paramValues.add(InventoryMdmIdQueryFieldParameter.DEAL_PID.getName(), Long.toString(dealPid));
    InventoryMdmIdQueryFieldParams queryFieldParams =
        new InventoryMdmIdQueryFieldParams(paramValues, SearchQueryOperator.OR);

    when(loginUserContext.isNexageUser()).thenReturn(true);

    Page<InventoryMdmIdDTO> result =
        mdmIdService.getMdmIdsForAssignedSellers(queryFieldParams, Pageable.unpaged());

    assertEquals(6, result.getContent().size());

    for (InventoryMdmIdDTO inventoryMdmIdDTO : result.getContent()) {
      assertTrue(sellerPids.contains(inventoryMdmIdDTO.getSellerPid()));
      assertEquals(1, inventoryMdmIdDTO.getCompanyMdmIds().size());
      assertEquals(
          "mdm_1" + inventoryMdmIdDTO.getSellerPid(),
          inventoryMdmIdDTO.getCompanyMdmIds().iterator().next());
      assertEquals(1, inventoryMdmIdDTO.getSellerSeatMdmIds().size());
      assertEquals(
          "mdm_9" + inventoryMdmIdDTO.getSellerPid(),
          inventoryMdmIdDTO.getSellerSeatMdmIds().iterator().next());
    }
  }

  @Test
  void shouldReturnCompanyMdmIdsWhenSellerPidAlone() {
    long sellerPid = 1000L;
    String mdmId = "mdm1000";

    CompanyMdmId companyMdmId = new CompanyMdmId();
    companyMdmId.setId(mdmId);
    CompanyMdmView companyMdmView = new CompanyMdmView(sellerPid, List.of(companyMdmId), null);
    when(mdmIdRepository.findMdmIdsForCompaniesIn(any(Set.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(companyMdmView), Pageable.unpaged(), 1));

    MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
    paramValues.add(
        InventoryMdmIdQueryFieldParameter.SELLER_PID.getName(), Long.toString(sellerPid));
    InventoryMdmIdQueryFieldParams queryFieldParams =
        new InventoryMdmIdQueryFieldParams(paramValues, SearchQueryOperator.OR);

    Page<InventoryMdmIdDTO> result =
        mdmIdService.getMdmIdsForAssignedSellers(queryFieldParams, Pageable.unpaged());

    assertEquals(1, result.getContent().size());

    InventoryMdmIdDTO result0 = result.getContent().get(0);
    assertEquals(sellerPid, result0.getSellerPid());
    assertEquals(Set.of(mdmId), result0.getCompanyMdmIds());
    assertTrue(result0.getSellerSeatMdmIds().isEmpty());
  }

  private DealPublisher setupDealPublisher(long companyPid) {
    CompanyView companyView = new CompanyView(companyPid, "company_" + companyPid);
    return new DealPublisher(companyPid, companyView);
  }

  private DealSite setupDealSite(long dealPid, long companyPid) {
    SiteView siteView = new SiteView(companyPid, "site_" + companyPid, companyPid, "seller");
    return new DealSite(dealPid, siteView);
  }

  private DealPosition setupDealPosition(long dealPid, long companyPid) {
    SiteView siteView = new SiteView(companyPid, "site_" + companyPid, companyPid, "seller");
    PositionView positionView = new PositionView(companyPid, "pos", Status.ACTIVE);
    positionView.setSiteView(siteView);
    return new DealPosition(dealPid, new DirectDeal(), 1, dealPid, positionView);
  }

  private Page<CompanyMdmView> setupCompanyMdmViewResponse(
      Set<Long> companyPids, Pageable pageable) {
    List<CompanyMdmView> companyMdmViews = new ArrayList<>();
    for (Long companyPid : companyPids) {
      SellerSeatMdmId sellerSeatMdmId = new SellerSeatMdmId();
      sellerSeatMdmId.setId("mdm_9" + companyPid);
      CompanyMdmId companyMdmId = new CompanyMdmId();
      companyMdmId.setId("mdm_1" + companyPid);

      SellerSeatMdmView sellerSeatMdmView =
          new SellerSeatMdmView(companyPid + 1, List.of(sellerSeatMdmId));
      CompanyMdmView companyMdmView =
          new CompanyMdmView(companyPid, List.of(companyMdmId), sellerSeatMdmView);
      companyMdmViews.add(companyMdmView);
    }
    return new PageImpl<>(companyMdmViews, pageable, companyMdmViews.size());
  }
}
