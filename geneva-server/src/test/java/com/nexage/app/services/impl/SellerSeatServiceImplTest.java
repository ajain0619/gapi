package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.SellerSeatRepository;
import com.nexage.app.dto.SellerSeatDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class SellerSeatServiceImplTest {

  private static final long PID = 123L;
  private static final long PID_2 = 123L;
  private static final Long USER_PID = 111L;
  private static final String SEAT_NAME = "seatName";
  private static final String SEAT_DESC = "desc";
  private static final String SEAT_DESC_2 = "desc2";
  private static final Boolean SEAT_STATUS = Boolean.TRUE;
  private static final String SELLER_SEAT_NAME_2 = "sellerSeatName2";
  private static final PageRequest PAGE_REQUEST =
      PageRequest.of(0, 1000, Sort.by(Direction.ASC, "name"));
  private static final Set<String> QUERY_FIELDS = Sets.newHashSet();

  @Mock private SellerSeatRepository sellerSeatRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private UserContext userContext;
  @InjectMocks private SellerSeatServiceImpl sellerSeatService;

  @Test
  void shouldSuccessfullyFetchSellerSeat() {
    // given
    SellerSeat expectedResult = createSellerSeat(PID, SEAT_NAME, SEAT_DESC, SEAT_STATUS);

    given(sellerSeatRepository.findById(PID)).willReturn(Optional.of(expectedResult));
    // when
    SellerSeatDTO result = sellerSeatService.getSellerSeat(PID);
    // then
    verify(sellerSeatRepository).findById(PID);
    assertThat(result.getPid(), Matchers.is(PID));
  }

  @Test
  void shouldThrowAnExceptionWhenGivenIdNotFound() {
    long someNodExistingId = 111L;
    given(sellerSeatRepository.findById(someNodExistingId)).willReturn(Optional.empty());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSeatService.getSellerSeat(someNodExistingId));

    assertEquals(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldUpdateSellerSeat() {
    Set<Company> newSellers = new HashSet<>();
    Company newSeller1 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    Company newSeller2 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    newSellers.add(newSeller1);
    newSellers.add(newSeller2);

    Set<Company> oldSellers = new HashSet<>();
    Company oldSeller = TestObjectsFactory.createCompany(CompanyType.SELLER);
    oldSellers.add(oldSeller);

    when(companyRepository.findSellersWithSpecificPids(anyCollection())).thenReturn(newSellers);

    SellerSeatDTO seatDTO =
        TestObjectsFactory.createSellerSeatDTOWithSellers(SEAT_NAME, SEAT_DESC_2, SEAT_STATUS);
    seatDTO.setPid(PID);
    seatDTO.setStatus(Boolean.FALSE);

    SellerSeat expectedResult =
        createSellerSeat(PID, SEAT_NAME, SEAT_DESC, SEAT_STATUS, oldSellers);
    when(sellerSeatRepository.findById(PID)).thenReturn(Optional.of(expectedResult));

    sellerSeatService.updateSellerSeat(PID, seatDTO);

    ArgumentCaptor<SellerSeat> sellerSeatCaptor = ArgumentCaptor.forClass(SellerSeat.class);
    verify(sellerSeatRepository).saveAndFlush(sellerSeatCaptor.capture());
    assertEquals(SEAT_NAME, sellerSeatCaptor.getValue().getName());
    assertEquals(SEAT_DESC_2, sellerSeatCaptor.getValue().getDescription());
    assertEquals(Boolean.FALSE, sellerSeatCaptor.getValue().isStatus());
    assertEquals(newSellers, sellerSeatCaptor.getValue().getSellers());
    assertEquals(PID, newSeller1.getSellerSeat().getPid().longValue());
    assertEquals(PID, newSeller2.getSellerSeat().getPid().longValue());
  }

  @Test
  void shouldCreateSellerSeat() {
    Set<Company> sellers = new HashSet<>();
    sellers.add(TestObjectsFactory.createCompany(CompanyType.SELLER));
    sellers.add(TestObjectsFactory.createCompany(CompanyType.SELLER));

    when(companyRepository.findSellersWithSpecificPids(anyCollection())).thenReturn(sellers);
    when(userContext.getPid()).thenReturn(USER_PID);

    SellerSeatDTO sellerSeat =
        TestObjectsFactory.createSellerSeatDTOWithSellers(SEAT_NAME, SEAT_DESC, SEAT_STATUS);
    sellerSeatService.createSellerSeat(sellerSeat);

    ArgumentCaptor<SellerSeat> sellerSeatCaptor = ArgumentCaptor.forClass(SellerSeat.class);
    verify(sellerSeatRepository).save(sellerSeatCaptor.capture());
    assertEquals(SEAT_NAME, sellerSeatCaptor.getValue().getName());
    assertEquals(SEAT_DESC, sellerSeatCaptor.getValue().getDescription());
    assertEquals(SEAT_STATUS, sellerSeatCaptor.getValue().isStatus());
    assertEquals(sellerSeatCaptor.getValue().getSellers(), sellers);
    assertEquals(USER_PID, sellerSeatCaptor.getValue().getCreatedBy());
  }

  @Test
  void shouldCreateSellerSeatWithNoSellers() {
    when(userContext.getPid()).thenReturn(USER_PID);

    SellerSeatDTO sellerSeat =
        TestObjectsFactory.createSellerSeatDTO(null, SEAT_STATUS, SEAT_NAME, SEAT_DESC);
    sellerSeatService.createSellerSeat(sellerSeat);

    ArgumentCaptor<SellerSeat> sellerSeatCaptor = ArgumentCaptor.forClass(SellerSeat.class);
    verify(sellerSeatRepository).save(sellerSeatCaptor.capture());
    assertEquals(SEAT_NAME, sellerSeatCaptor.getValue().getName());
    assertEquals(SEAT_DESC, sellerSeatCaptor.getValue().getDescription());
    assertEquals(SEAT_STATUS, sellerSeatCaptor.getValue().isStatus());
    assertTrue(sellerSeatCaptor.getValue().getSellers().isEmpty());
    assertEquals(USER_PID, sellerSeatCaptor.getValue().getCreatedBy());
  }

  @Test
  void shouldNotCreateSellerSeatWhenSellerSeatDoesHaveNonSellerCompanies() {
    List<Company> sellers = new ArrayList<>();
    sellers.add(TestObjectsFactory.createCompany(CompanyType.SELLER));

    SellerSeatDTO sellerSeat =
        TestObjectsFactory.createSellerSeatDTOWithSellers(SEAT_NAME, SEAT_DESC, SEAT_STATUS);
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerSeatService.createSellerSeat(sellerSeat));

    assertEquals(ServerErrorCodes.SERVER_NOT_ALL_SELLERS_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldSuccessfullyFetchAllSellerSeats() {
    // given
    List<SellerSeat> expectedResults = prepareSellerSeatsList();
    given(sellerSeatRepository.findAll((Specification) ArgumentMatchers.isNull(), eq(PAGE_REQUEST)))
        .willReturn(new PageImpl<>(expectedResults));

    // when
    Page<SellerSeatDTO> result = sellerSeatService.findAll(false, null, null, PAGE_REQUEST);

    // then
    verify(sellerSeatRepository)
        .findAll((Specification) ArgumentMatchers.isNull(), eq(PAGE_REQUEST));
    List<SellerSeatDTO> content = result.getContent();
    assertEquals(2, result.getTotalElements());
    assertThat(content.get(0).getPid(), Matchers.is(PID));
    assertThat(content.get(1).getPid(), Matchers.is(PID_2));
  }

  @Test
  void shouldSuccessfullyFetchAllAssignableSellerSeats() {
    // given
    List<SellerSeat> expectedResults = new ArrayList<>();
    given(sellerSeatRepository.findAll(any(Specification.class), eq(PAGE_REQUEST)))
        .willReturn(new PageImpl<>(expectedResults));

    // when
    Page<SellerSeatDTO> result = sellerSeatService.findAll(true, null, null, PAGE_REQUEST);

    // then
    verify(sellerSeatRepository).findAll(any(Specification.class), eq(PAGE_REQUEST));
    assertEquals(0, result.getTotalElements());
  }

  @Test
  void shouldSuccessfullyFetchLikeSellerSeatsWhenSearchingByName() {
    // given
    QUERY_FIELDS.add("name");
    List<SellerSeat> sellerSeats = prepareSellerSeatsList();
    sellerSeats.remove(0);
    Page<SellerSeat> page = new PageImpl<>(sellerSeats);

    given(sellerSeatRepository.findAll(any(Specification.class), eq(PAGE_REQUEST)))
        .willReturn(page);

    // when
    Page<SellerSeatDTO> result =
        sellerSeatService.findAll(true, QUERY_FIELDS, SELLER_SEAT_NAME_2, PAGE_REQUEST);

    // then
    verify(sellerSeatRepository).findAll(any(Specification.class), eq(PAGE_REQUEST));
    List<SellerSeatDTO> content = result.getContent();
    assertEquals(1, result.getTotalElements());
    assertThat(content.get(0).getPid(), Matchers.is(PID_2));
  }

  @Test
  void shouldSuccessfullyFetchLikeSellerSeatsWhenAssignableIsFalseSearchingByName() {
    // given
    QUERY_FIELDS.add("name");
    List<SellerSeat> sellerSeats = prepareSellerSeatsList();
    sellerSeats.remove(0);
    Page<SellerSeat> page = new PageImpl<>(sellerSeats);

    given(sellerSeatRepository.findAll(any(Specification.class), eq(PAGE_REQUEST)))
        .willReturn(page);

    // when
    Page<SellerSeatDTO> result =
        sellerSeatService.findAll(false, QUERY_FIELDS, SELLER_SEAT_NAME_2, PAGE_REQUEST);

    // then
    verify(sellerSeatRepository).findAll(any(Specification.class), eq(PAGE_REQUEST));
    List<SellerSeatDTO> content = result.getContent();
    assertEquals(1, result.getTotalElements());
    assertThat(content.get(0).getPid(), Matchers.is(PID_2));
  }

  private List<SellerSeat> prepareSellerSeatsList() {
    List<SellerSeat> expectedResults = new ArrayList<>();
    expectedResults.add(
        TestObjectsFactory.createSellerSeat(PID, SEAT_NAME, SEAT_DESC, SEAT_STATUS));
    expectedResults.add(
        TestObjectsFactory.createSellerSeat(PID_2, SELLER_SEAT_NAME_2, SEAT_DESC, SEAT_STATUS));
    return expectedResults;
  }
}
