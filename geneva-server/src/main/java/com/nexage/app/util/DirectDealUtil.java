package com.nexage.app.util;

import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.util.MapParamDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectDealUtil {
  public static final String QF_DEAL_ID = "dealId";
  public static final String QF_SELLERS = "sellers";
  public static final String QF_DSP_BUYER_SEAT = "dspBuyerSeats";

  /**
   * Accepts MultiValueQueryParams object to extract BuyerandBuyerSeat pattern
   *
   * @param queryParams
   * @return regexp BuyerandBuyerSeat pattern
   */
  public static String getBuyerAndBuyerSeatPattern(MultiValueQueryParams queryParams) {
    Map<String, String> queryMap = MapParamDecoder.decodeMap(queryParams.getFields());
    String buyerCompanyBuyerSeats = queryMap.getOrDefault(QF_DSP_BUYER_SEAT, null);
    return Arrays.stream(buyerCompanyBuyerSeats.split(","))
        .reduce(
            "",
            (acc, item) -> {
              var companyBuyerSeatItems = item.split("_%", 2);
              var company = String.format("\\{\"buyerCompany\":%s", companyBuyerSeatItems[0]);
              if (companyBuyerSeatItems.length > 1) {
                var seats = getSeatPattern(companyBuyerSeatItems[1]).replaceAll(",$", "");
                return acc.concat(
                    company.concat(String.format(",[^{]*\"seats\":\\\\\\[[^}]*(%s)+|", seats)));
              }
              return acc.concat(company.concat(",|"));
            })
        .replaceAll("\\|$", "");
  }

  /**
   * Accepts MultiValueQueryParams object to extract seatPattern
   *
   * @param seat
   * @return regexp seatPattern
   */
  public static String getSeatPattern(String seat) {
    return "\"".concat(seat.replace("_%", "\"|\"")).concat("\",");
  }

  /**
   * Accepts MultiValueQueryParams object to extract buyerPattern
   *
   * @param queryParams
   * @return regexp buyerPattern
   */
  public static String getBuyerPattern(MultiValueQueryParams queryParams) {
    Map<String, String> queryMap = MapParamDecoder.decodeMap(queryParams.getFields());
    String buyerCompanyBuyerSeats = queryMap.getOrDefault(QF_DSP_BUYER_SEAT, null);
    return Arrays.stream(buyerCompanyBuyerSeats.split(","))
        .reduce(
            "",
            (acc, item) -> {
              var companyBuyerSeatItems = item.split("_%", 2);
              var company = String.format("\\{\"buyerCompany\":%s", companyBuyerSeatItems[0]);
              return acc.concat(company.concat(",?[^t]*}|"));
            })
        .replaceAll("\\|$", "");
  }

  /**
   * Accepts MultiValueQueryParams object to extract dealsPids
   *
   * @param queryParams
   * @return set of Deal Pids
   */
  public static Set<String> getDealIds(MultiValueQueryParams queryParams) {
    Map<String, String> queryMap = MapParamDecoder.decodeMap(queryParams.getFields());
    String buyerCompanyBuyerSeats = queryMap.getOrDefault(QF_DEAL_ID, null);
    if (buyerCompanyBuyerSeats == null) {
      return Collections.emptySet();
    }
    return Arrays.stream(buyerCompanyBuyerSeats.split(",")).collect(Collectors.toSet());
  }

  /**
   * Accepts MultiValueQueryParams object to extract sellerPids
   *
   * @param queryParams MultiValueQueryParams object
   * @return Set of SellerPids
   */
  public static Set<Long> getSellerPids(MultiValueQueryParams queryParams) {
    Map<String, String> queryMap = MapParamDecoder.decodeMap(queryParams.getFields());
    String sellers = queryMap.getOrDefault(QF_SELLERS, null);
    return Arrays.stream(sellers.split(",")).map(Long::valueOf).collect(Collectors.toSet());
  }
}
