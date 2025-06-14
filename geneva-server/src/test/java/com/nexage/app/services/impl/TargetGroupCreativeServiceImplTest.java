package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.repository.BdrCreativeRepository;
import com.nexage.app.dto.support.AssociatedCreativeDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TargetGroupCreativeServiceImplTest {

  private static final long CREATIVE_PID = 1L;
  private static final Long ADVERTISIER_PID = 1L;
  private static final Long SEATHOLDER_PID = 1L;

  @Mock BdrCreativeRepository bdrCreativeRepository;
  @InjectMocks TargetGroupCreativeServiceImpl targetGroupCreativeService;

  @Test
  void shouldAssociateCreatives() {
    // given
    BdrInsertionOrder insertionOrder = new BdrInsertionOrder();
    insertionOrder.setAdvertiserPid(ADVERTISIER_PID);
    BDRLineItem lineItem = new BDRLineItem();
    lineItem.setInsertionOrder(insertionOrder);
    BdrTargetGroup targetGroup = new BdrTargetGroup();
    AssociatedCreativeDTO associatedCreativeDTO =
        AssociatedCreativeDTO.newBuilder().withPid(CREATIVE_PID).build();
    BdrCreative creative = new BdrCreative();
    given(bdrCreativeRepository.findByPidAndAdvertiser_Pid(CREATIVE_PID, ADVERTISIER_PID))
        .willReturn(Optional.of(creative));

    // when
    targetGroupCreativeService.associateCreatives(
        SEATHOLDER_PID, lineItem, targetGroup, Set.of(associatedCreativeDTO));

    // then
    assertEquals(creative, targetGroup.getTargetGroupCreatives().iterator().next().getCreative());
  }

  @Test
  void shouldReturnNotFoundWhenAdvertiserIsEmpty() {
    // given
    BdrInsertionOrder insertionOrder = new BdrInsertionOrder();
    insertionOrder.setAdvertiserPid(ADVERTISIER_PID);
    BDRLineItem lineItem = new BDRLineItem();
    lineItem.setInsertionOrder(insertionOrder);
    BdrTargetGroup targetGroup = new BdrTargetGroup();
    AssociatedCreativeDTO associatedCreativeDTO =
        AssociatedCreativeDTO.newBuilder().withPid(CREATIVE_PID).build();
    when(bdrCreativeRepository.findByPidAndAdvertiser_Pid(CREATIVE_PID, ADVERTISIER_PID))
        .thenReturn(Optional.empty());
    var associatedCreatives = Set.of(associatedCreativeDTO);
    // when and then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                targetGroupCreativeService.associateCreatives(
                    SEATHOLDER_PID, lineItem, targetGroup, associatedCreatives));
    assertEquals(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND, exception.getErrorCode());
  }
}
