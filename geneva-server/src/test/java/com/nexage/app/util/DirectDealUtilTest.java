package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class DirectDealUtilTest {

  @Test
  void shouldCorrectlyReturnBuyerAndBuyerSeatPattern() {
    MultiValueMap multiValueMap = new LinkedMultiValueMap();
    multiValueMap.add("dspBuyerSeats", "1_%2_%3,4_%5_%6");
    var multiValueQueryParams = new MultiValueQueryParams(multiValueMap, null);
    var buyerAndBuyerSeatPattern =
        DirectDealUtil.getBuyerAndBuyerSeatPattern(multiValueQueryParams);
    assertEquals(
        "\\{\"buyerCompany\":1,[^{]*\"seats\":\\\\\\[[^}]*(\"2\"|\"3\")+|\\{\"buyerCompany\":4,[^{]*\"seats\":\\\\\\[[^}]*(\"5\"|\"6\")+",
        buyerAndBuyerSeatPattern);
  }

  @Test
  void shouldCorrectlyReturnSeatPattern() {
    var buyerSeatPattern = DirectDealUtil.getSeatPattern("1_%2_%3");
    assertEquals("\"1\"|\"2\"|\"3\",", buyerSeatPattern);
  }

  @Test
  void shouldCorrectlyReturnBuyerPattern() {
    MultiValueMap multiValueMap = new LinkedMultiValueMap();
    multiValueMap.add("dspBuyerSeats", "1_%2,4_%5");
    var multiValueQueryParams = new MultiValueQueryParams(multiValueMap, null);
    var buyerPattern = DirectDealUtil.getBuyerPattern(multiValueQueryParams);
    assertEquals("\\{\"buyerCompany\":1,?[^t]*}|\\{\"buyerCompany\":4,?[^t]*}", buyerPattern);
  }

  @Test
  void shouldCorrectlyReturnDealPids() {
    MultiValueMap multiValueMap = new LinkedMultiValueMap();
    multiValueMap.add("dealId", "1,2");
    var multiValueQueryParams = new MultiValueQueryParams(multiValueMap, null);
    var dealIds = DirectDealUtil.getDealIds(multiValueQueryParams);
    assertEquals(Set.of("1", "2"), dealIds);
  }

  @Test
  void shouldCorrectlyReturnSellerPids() {
    MultiValueMap multiValueMap = new LinkedMultiValueMap();
    multiValueMap.add("sellers", "1,2");
    var multiValueQueryParams = new MultiValueQueryParams(multiValueMap, null);
    var sellerPids = DirectDealUtil.getSellerPids(multiValueQueryParams);
    assertEquals(Set.of(1L, 2L), sellerPids);
  }
}
