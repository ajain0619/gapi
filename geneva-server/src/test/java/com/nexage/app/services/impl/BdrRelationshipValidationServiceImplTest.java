package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BDRAdvertiserRepository;
import com.nexage.admin.core.repository.BdrInsertionOrderRepository;
import com.nexage.admin.core.repository.BdrLineItemRepository;
import com.nexage.admin.core.repository.BdrTargetGroupRepository;
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
class BdrRelationshipValidationServiceImplTest {

  private static final Long SEATHOLDER_PID = 1L;
  private static final Long INSERTION_ORDER_PID = 2L;
  private static final Long LINE_ITEM_PID = 3L;
  private static final Long TARGET_GROUP_PID = 4L;
  private static final Long WRONG_PID = 5L;
  private static final Long COMPANY_PID = 101L;
  private static final Long ADVERTISIER_PID = 200L;

  @Mock private Company company;
  @Mock private BDRAdvertiser advertiser;
  @Mock private BdrTargetGroup targetGroup;
  @Mock private BDRLineItem lineItem;
  @Mock private BdrInsertionOrder insertionOrder;

  @Mock private BdrTargetGroupRepository targetGroupRepository;
  @Mock private BdrInsertionOrderRepository bdrInsertionOrderRepository;
  @Mock private BdrLineItemRepository lineItemRepository;
  @Mock private BDRAdvertiserRepository bdrAdvertiserRepository;
  @InjectMocks private BdrRelationshipValidationServiceImpl relationshipValidationService;

  @BeforeEach
  void setUp() {
    lenient().when(company.getPid()).thenReturn(SEATHOLDER_PID);
    lenient().when(advertiser.getCompany()).thenReturn(company);
    lenient().when(targetGroup.getLineitemPid()).thenReturn(LINE_ITEM_PID);
    lenient().when(lineItem.getInsertionOrderPid()).thenReturn(INSERTION_ORDER_PID);
    lenient().when(insertionOrder.getAdvertiser()).thenReturn(advertiser);
  }

  @Test
  void shouldSuccessfullyValidateAllNulls() {
    relationshipValidationService.validateRelationship(null, null, null, null);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenTargetGroupNotFound() {
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, null, null, TARGET_GROUP_PID));
    assertEquals(ServerErrorCodes.SERVER_TARGET_GROUP_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenTargetGroupExistsAndHasNoLineItemPid() {
    given(targetGroupRepository.findById(TARGET_GROUP_PID)).willReturn(Optional.of(targetGroup));
    given(targetGroup.getLineitemPid()).willReturn(null);

    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, null, null, TARGET_GROUP_PID));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_LINEITEM, ex.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenTargetGroupFoundButHasWrongLineItemPid() {
    given(targetGroupRepository.findById(TARGET_GROUP_PID)).willReturn(Optional.of(targetGroup));
    given(targetGroup.getLineitemPid()).willReturn(WRONG_PID);

    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, null, LINE_ITEM_PID, TARGET_GROUP_PID));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_LINEITEM, ex.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenLineItemNotFound() {
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, null, LINE_ITEM_PID, null));
    assertEquals(ServerErrorCodes.SERVER_LINEITEM_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenLineItemFoundButHasNoInsertionOrderPid() {
    given(lineItemRepository.findById(LINE_ITEM_PID)).willReturn(Optional.of(lineItem));
    given(lineItem.getInsertionOrderPid()).willReturn(null);

    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, null, LINE_ITEM_PID, null));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER, ex.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenLineItemFoundButHasWrongInsertionOrderPid() {
    given(lineItemRepository.findById(LINE_ITEM_PID)).willReturn(Optional.of(lineItem));
    given(lineItem.getInsertionOrderPid()).willReturn(WRONG_PID);

    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, INSERTION_ORDER_PID, LINE_ITEM_PID, null));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER, ex.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenInsertionOrderNotFound() {
    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, INSERTION_ORDER_PID, null, null));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER, ex.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenInsertionOrderFoundButHasNoAdvertiser() {
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));
    given(insertionOrder.getAdvertiser()).willReturn(null);

    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, INSERTION_ORDER_PID, null, null));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER, ex.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenInsertionOrderFoundButAdvertiserHasNoCompany() {
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));
    given(advertiser.getCompany()).willReturn(null);

    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, INSERTION_ORDER_PID, null, null));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER, ex.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenInsertionOrderFoundButAdvertiserCompanyHasNoPid() {
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));
    given(company.getPid()).willReturn(null);

    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    null, INSERTION_ORDER_PID, null, null));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER, ex.getErrorCode());
  }

  @Test
  void
      shouldThrowGenevaValidationExceptionWhenInsertionOrderFoundButAdvertiserCompanyHasWrongPid() {
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));
    given(company.getPid()).willReturn(WRONG_PID);

    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                relationshipValidationService.validateRelationship(
                    SEATHOLDER_PID, INSERTION_ORDER_PID, null, null));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_INSERTION_ORDER, ex.getErrorCode());
  }

  @Test
  void shouldValidateFullInputSuccessfully() {
    given(targetGroupRepository.findById(TARGET_GROUP_PID)).willReturn(Optional.of(targetGroup));
    given(lineItemRepository.findById(LINE_ITEM_PID)).willReturn(Optional.of(lineItem));
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));

    relationshipValidationService.validateRelationship(
        SEATHOLDER_PID, INSERTION_ORDER_PID, LINE_ITEM_PID, TARGET_GROUP_PID);
  }

  @Test
  void shouldThrowExceptionWhenPidsAreNotRelated() {
    // given
    BDRAdvertiser bdrAdvertiser = new BDRAdvertiser();
    Company company = new Company();
    company.setPid(COMPANY_PID);
    bdrAdvertiser.setCompany(company);

    when(bdrAdvertiserRepository.findById(ADVERTISIER_PID)).thenReturn(Optional.of(bdrAdvertiser));

    // when
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> relationshipValidationService.validateRelationship(WRONG_PID, ADVERTISIER_PID));
    // then
    assertEquals(ServerErrorCodes.SERVER_PIDS_NOT_RELATED, ex.getErrorCode());
  }
}
