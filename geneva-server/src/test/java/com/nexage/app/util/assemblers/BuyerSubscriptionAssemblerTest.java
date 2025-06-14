package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.BidderSubscription;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuyerSubscriptionAssemblerTest {

  @InjectMocks private BuyerSubscriptionAssembler buyerSubscriptionAssembler;

  @Test
  void shouldMakeDtoCorrectly() {
    // given
    var bidderSubscription = new BidderSubscription();
    bidderSubscription.setRequiresDataToBid(true);
    var fields = Set.of("requiresData");

    // when
    var result = buyerSubscriptionAssembler.make(bidderSubscription, fields);

    // then
    assertTrue(result.getRequiresData());
  }
}
