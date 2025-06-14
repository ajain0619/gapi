package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BdrInsertionOrderRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InsertionOrderServiceImplTest {

  private static final Long SEATHOLDER_PID = 1L;
  private static final Long INSERTION_ORDER_PID = 2L;
  private static final Long WRONG_PID = 3L;

  @Mock private BdrInsertionOrderRepository bdrInsertionOrderRepository;
  @InjectMocks private InsertionOrderServiceImpl insertionOrderService;

  @Mock private Company company;
  @Mock private BDRAdvertiser advertiser;
  @Mock private BdrInsertionOrder insertionOrder;

  @BeforeEach
  void setUp() {
    lenient().when(company.getPid()).thenReturn(SEATHOLDER_PID);
    lenient().when(advertiser.getCompany()).thenReturn(company);
    lenient().when(insertionOrder.getAdvertiser()).thenReturn(advertiser);
  }

  @Test
  void shouldGetInsertionOrder() {
    // given
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));

    // when
    BdrInsertionOrder result =
        insertionOrderService.getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID);

    // then
    assertEquals(insertionOrder, result);
    verify(bdrInsertionOrderRepository).findById(INSERTION_ORDER_PID);
    verifyNoMoreInteractions(bdrInsertionOrderRepository);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenInsertionOrderNotFound() {
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> insertionOrderService.getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER, ex.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenInsertionOrderFoundButHasNoAdvertiser() {
    given(insertionOrder.getAdvertiser()).willReturn(null);
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));

    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> insertionOrderService.getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID));
    assertEquals(ServerErrorCodes.SERVER_PIDS_NOT_RELATED, ex.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenInsertionOrderFoundButAdvertiserHasNoCompany() {
    given(advertiser.getCompany()).willReturn(null);
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));

    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> insertionOrderService.getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID));
    assertEquals(ServerErrorCodes.SERVER_PIDS_NOT_RELATED, ex.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenInsertionOrderFoundButAdvertiserCompanyHasNoPid() {
    given(company.getPid()).willReturn(null);
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));

    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> insertionOrderService.getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID));
    assertEquals(ServerErrorCodes.SERVER_PIDS_NOT_RELATED, ex.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenInsertionOrderFoundButAdvertiserCompanyHasWrongPid() {
    given(company.getPid()).willReturn(WRONG_PID);
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));

    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> insertionOrderService.getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID));
    assertEquals(ServerErrorCodes.SERVER_PIDS_NOT_RELATED, ex.getErrorCode());
  }
}
