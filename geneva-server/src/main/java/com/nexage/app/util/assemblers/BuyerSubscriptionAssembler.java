package com.nexage.app.util.assemblers;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.BidderSubscription;
import com.nexage.app.dto.buyer.BuyerSubscriptionDTO;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class BuyerSubscriptionAssembler extends NoContextAssembler {

  public static final Set<String> DEFAULT_FIELDS = Sets.newHashSet("name", "requiresData", "alias");

  public BuyerSubscriptionDTO make(BidderSubscription bidderRegionLimit) {
    return make(bidderRegionLimit, DEFAULT_FIELDS);
  }

  public BuyerSubscriptionDTO make(BidderSubscription bidderSubscription, Set<String> fields) {
    BuyerSubscriptionDTO.Builder buyerSubscription = BuyerSubscriptionDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "name":
          buyerSubscription.withName(bidderSubscription.getExternalDataProvider().getName());
          break;
        case "requiresData":
          buyerSubscription.withRequiresData(bidderSubscription.isRequiresDataToBid());
          break;
        case "alias":
          buyerSubscription.withAlias(bidderSubscription.getBidderAlias());
          break;
        default:
      }
    }

    return buyerSubscription.build();
  }

  public BidderSubscription apply(
      BidderSubscription bidderSubscription, BuyerSubscriptionDTO buyerSubscription) {
    return bidderSubscription;
  }
}
