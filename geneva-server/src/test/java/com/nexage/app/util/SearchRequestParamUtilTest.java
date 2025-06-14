package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class SearchRequestParamUtilTest {
  List<String> qfForSellers = ImmutableList.of("sellerId");
  List<String> qtForSellers = ImmutableList.of("100|200");
  List<String> qfForSellersAndSites = ImmutableList.of("sellerId", "siteId");
  List<String> qtForSellersAndSites = ImmutableList.of("100", "1|4|5");

  @Test
  void getSellers() {
    Map<String, List<Long>> map =
        SearchRequestParamUtil.getSellersSites(qfForSellers, qtForSellers);
    assertEquals(1, map.size());
    assertEquals(Arrays.asList(100L, 200L), map.get("sellerId"));
  }

  @Test
  void getSellersAndSites() {
    Map<String, List<Long>> map =
        SearchRequestParamUtil.getSellersSites(qfForSellersAndSites, qtForSellersAndSites);
    assertEquals(2, map.size());
    assertEquals(Arrays.asList(100L), map.get("sellerId"));
    assertEquals(Arrays.asList(1L, 4L, 5L), map.get("siteId"));
  }

  @Test
  void getSellersAndSitesEmpty() {
    Map<String, List<Long>> map =
        SearchRequestParamUtil.getSellersSites(ImmutableList.of(), ImmutableList.of());
    assertEquals(0, map.size());
  }

  @Test
  void getSellersAndSitesForQueryParams() {
    MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
    multiValueMap.addAll("siteId", List.of("1", "4", "5"));
    multiValueMap.add("sellerId", "100");
    Map<String, List<Long>> map = SearchRequestParamUtil.getSellersSites(multiValueMap);
    assertEquals(2, map.size());
    assertEquals(List.of(100L), map.get("sellerId"));
    assertEquals(List.of(1L, 4L, 5L), map.get("siteId"));
  }

  @Test
  void getSellersAndSitesEmptyForQueryParams() {
    MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
    Map<String, List<Long>> map = SearchRequestParamUtil.getSellersSites(multiValueMap);
    assertEquals(0, map.size());
  }
}
