package com.nexage.app.util;

import com.google.common.collect.ImmutableMap;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchRequestParamUtil {

  public static final String SELLER_ID = ScreenedAdsQueryFieldParameter.SELLER_ID.getName();
  public static final String SITE_ID = ScreenedAdsQueryFieldParameter.SITE_ID.getName();

  @Deprecated(forRemoval = true)
  /**
   * Given a qf and qt lists.
   *
   * @param qf queryField list
   * @param qt queryTerm list
   * @return An Map<Key,List<Long Values> containing the values found in the qt for sellerId or/and
   *     siteId
   * @deprecated to be removed as qf and qt as lists is no longer used.
   */
  public static Map<String, List<Long>> getSellersSites(List<String> qf, List<String> qt) {
    Map<String, List<Long>> map = new HashMap<>();
    if (SearchRequestParamValidator.isValid(qf, qt) && qf.contains(SELLER_ID)) {
      for (String field : qf) {
        int idx = qf.indexOf(field);
        switch (field) {
          case "sellerId":
            List<Long> sellerIds = DelimitedStringDecoder.decodeString(qt.get(idx));
            map.put(SELLER_ID, sellerIds);
            break;
          case "siteId":
            List<Long> siteIds = DelimitedStringDecoder.decodeString(qt.get(idx));
            map.put(SITE_ID, siteIds);
            break;
          default:
            break;
        }
      }
    }
    return ImmutableMap.copyOf(map);
  }

  /**
   * Given a multi map extract sellerId and siteId in a Map<String,List<Long>> and return it.
   *
   * @param multi queryField Map
   * @return An Map<Key,List<Long Values> containing the values found in the multi for sellerId
   *     or/and siteId
   */
  public static Map<String, List<Long>> getSellersSites(MultiValueMap<String, String> multi) {
    if (CollectionUtils.isEmpty(multi)) {
      return Map.of();
    }
    Map<String, List<Long>> map;
    map =
        multi.entrySet().stream()
            .filter(
                e -> e.getKey().equalsIgnoreCase(SELLER_ID) || e.getKey().equalsIgnoreCase(SITE_ID))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().stream().map(Long::parseLong).collect(Collectors.toList())));

    return ImmutableMap.copyOf(map);
  }
}
