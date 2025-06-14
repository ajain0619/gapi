package com.nexage.app.util.assemblers;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.nexage.admin.core.model.BidderRegionLimit;
import com.nexage.app.dto.buyer.BuyerRegionLimitDTO;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BuyerRegionLimitAssembler extends NoContextAssembler {

  public static final Set<String> DEFAULT_FIELDS =
      Set.of("pid", "version", "name", "countries", "maximumQps");

  public BuyerRegionLimitDTO make(BidderRegionLimit bidderRegionLimit) {
    return make(bidderRegionLimit, DEFAULT_FIELDS);
  }

  public BuyerRegionLimitDTO make(BidderRegionLimit bidderRegionLimit, Set<String> fields) {
    BuyerRegionLimitDTO.Builder buyerRegionLimitBuilder = BuyerRegionLimitDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          buyerRegionLimitBuilder.withPid(bidderRegionLimit.getPid());
          break;
        case "version":
          buyerRegionLimitBuilder.withVersion(bidderRegionLimit.getVersion());
          break;
        case "name":
          buyerRegionLimitBuilder.withName(bidderRegionLimit.getName());
          break;
        case "maximumQps":
          buyerRegionLimitBuilder.withMaximumQps(bidderRegionLimit.getRequestRate());
          break;
        case "countries":
          if (!StringUtils.isBlank(bidderRegionLimit.getCountriesFilter())) {
            Iterator<String> iter =
                Splitter.on(",")
                    .omitEmptyStrings()
                    .trimResults()
                    .split(bidderRegionLimit.getCountriesFilter())
                    .iterator();
            while (iter.hasNext()) {
              buyerRegionLimitBuilder.withCountry(iter.next());
            }
          }
          break;

        default:
      }
    }

    return buyerRegionLimitBuilder.build();
  }

  public BidderRegionLimit apply(
      BidderRegionLimit bidderRegionLimit, BuyerRegionLimitDTO buyerRegionLimit) {

    if (buyerRegionLimit.getMaximumQps() != null)
      bidderRegionLimit.setRequestRate(buyerRegionLimit.getMaximumQps());
    if (buyerRegionLimit.getCountries() != null) {
      bidderRegionLimit.setCountriesFilter(
          Joiner.on(",").join(buyerRegionLimit.getCountries().keySet()));
    }
    if (bidderRegionLimit.getPid() == null) {
      bidderRegionLimit.setName(bidderRegionLimit.getCountriesFilter());
    }
    return bidderRegionLimit;
  }
}
