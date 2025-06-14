package com.nexage.app.web;

import static java.util.stream.Collectors.toList;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.admin.core.enums.AdSizeFilter;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.ExchangeProduction;
import com.nexage.admin.core.model.ExchangeRegional;
import com.nexage.app.services.BuyerService;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@RestController
public class BuyerController {

  private final BuyerService buyerService;

  public BuyerController(BuyerService buyerService) {
    this.buyerService = buyerService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/buyers/adsourcesummaries")
  @ResponseBody
  public List<AdSourceSummaryDTO> getAllAdSourceSummaries() {
    return buyerService.getAllAdSourceSummaries();
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/buyers/bidders/getIdNameMap")
  @ResponseBody
  public List<BidderSummaryDTO> getBiddersPidNameMap() {
    return buyerService.getAllBidderSummaries();
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/buyers/{buyerPID}/bidderconfigs")
  @ResponseBody
  public List<BidderConfig> getAllBidderConfigsByCompanyPid(
      @PathVariable(value = "buyerPID") long buyerPid) {
    return buyerService.getAllBidderConfigsByCompanyPid(buyerPid);
  }

  public static class AdSizeJson {
    private final String id;
    private final String text;

    @JsonCreator
    public AdSizeJson(@JsonProperty("id") String id, @JsonProperty("text") String text) {
      this.id = id;
      this.text = text;
    }

    public String getId() {
      return id;
    }

    public String getText() {
      return text;
    }
  }

  private static final List<AdSizeJson> ALL_AD_SIZES =
      ImmutableList.copyOf(
          Arrays.stream(AdSizeFilter.values())
              .map(size -> new AdSizeJson(size.name(), size.asActual()))
              .collect(toList()));

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/buyers/bidderconfigs/adsizes")
  @ResponseBody
  public List<AdSizeJson> getAllAdSizes() {
    return ALL_AD_SIZES;
  }

  @Timed
  @ExceptionMetered
  @ResponseBody
  @DeleteMapping(value = "/buyers/bidderconfigs/{bidderconfigPID}")
  public void deleteBidderConfig(@PathVariable(value = "bidderconfigPID") long bidderConfigPid) {
    buyerService.deleteBidderConfig(bidderConfigPid);
  }

  @Timed
  @ExceptionMetered
  @ResponseBody
  @GetMapping(value = "/buyers/{buyerPID}/adsources")
  public List<AdSource> getAllAdSourcesByCompanyPid(
      @PathVariable(value = "buyerPID") Long buyerPid) {
    return buyerService.getAllAdSourcesByCompanyPid(buyerPid);
  }

  @Timed
  @ExceptionMetered
  @ResponseBody
  @GetMapping(value = "/buyers/adsources/{adsourcePID}")
  public AdSource getAdSource(@PathVariable(value = "adsourcePID") Long adsourcePid) {
    return buyerService.getAdSource(adsourcePid);
  }

  @Timed
  @ExceptionMetered
  @ResponseBody
  @PutMapping(
      value = "/buyers/{buyerPID}/adsources/",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public AdSource createAdSource(
      @PathVariable("buyerPID") Long buyerPID, @RequestBody AdSource adSource) {
    return buyerService.createAdSource(buyerPID, adSource);
  }

  /**
   * Update AdSource with provided object.
   *
   * @param buyerPid associated buyer PID
   * @param adSourcePid associated ad source PID
   * @param adSource updated ad source object
   * @return requested {@link AdSource} confirmed updated ad source object
   */
  @Timed
  @ExceptionMetered
  @ResponseBody
  @PutMapping(
      value = "/buyers/{buyerPID}/adsources/{adsourcePID}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public AdSource updateAdSource(
      @PathVariable("buyerPID") Long buyerPid,
      @PathVariable(value = "adsourcePID") Long adSourcePid,
      @RequestBody AdSource adSource) {
    return buyerService.updateAdSource(buyerPid, adSource, adSourcePid);
  }

  @Timed
  @ExceptionMetered
  @ResponseBody
  @DeleteMapping(
      value = "/buyers/adsources/{adsourcePID}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public void deleteAdSource(@PathVariable(value = "adsourcePID") Long adsourcePid) {
    buyerService.deleteAdSource(adsourcePid);
  }

  @Timed
  @ExceptionMetered
  @ResponseBody
  @GetMapping(value = "/buyers/exchanges/regions")
  public List<ExchangeRegional> getExchangeRegions() {
    return buyerService.getAllExchangeRegions();
  }

  @Timed
  @ExceptionMetered
  @ResponseBody
  @GetMapping(value = "/buyers/exchanges/deployments")
  public List<ExchangeProduction> getExchangeProductions() {
    return buyerService.getAllExchangeProductions();
  }
}
