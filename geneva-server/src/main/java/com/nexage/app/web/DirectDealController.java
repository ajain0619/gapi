package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealBuyerDTO;
import com.nexage.app.dto.deal.PublisherSitePositionDTO;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.services.DirectDealService;
import com.nexage.app.services.SellerDealService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/deals")
@RestController
@RequestMapping(value = "/deals")
public class DirectDealController {

  private final DirectDealService directDealService;
  private final SellerDealService sellerDealService;

  public DirectDealController(
      DirectDealService directDealService, SellerDealService sellerDealService) {
    this.directDealService = directDealService;
    this.sellerDealService = sellerDealService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{dealPID}/publisher_map")
  public List<PublisherSitePositionDTO> getPublisherMapForDeal(
      @PathVariable(value = "dealPID") long pid) {
    return directDealService.getPublisherMapForDeal(pid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{dealPID}")
  public DirectDealDTO getDeal(@PathVariable(value = "dealPID") long pid) {
    return directDealService.getDeal(pid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  public List<DirectDealDTO> getAllDeals() {
    return directDealService.getAllDeals();
  }

  @Timed
  @ExceptionMetered
  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
  public DirectDealDTO createDeal(@RequestBody DirectDealDTO deal) {
    return directDealService.createDeal(deal);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{dealPID}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public DirectDealDTO updateDeal(
      @PathVariable(value = "dealPID") long dealPid, @RequestBody DirectDealDTO deal) {
    return directDealService.updateDeal(dealPid, deal);
  }

  /*Please note this endpoint is only being used with the old UI. The update method is used for any changes in the new UI.
  If we want this to be used for the new UI, work will need to take place to sync the deal and rule status*/
  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{dealPID}/activate",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void activateDeal(@PathVariable(value = "dealPID") long dealPid) {
    directDealService.updateDealStatus(dealPid, DirectDeal.DealStatus.Active);
  }

  /*Please note this endpoint is only being used with the old UI. The update method is used for any changes in the new UI.
  If we want this to be used for the new UI, work will need to take place to sync the deal and rule status*/
  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{dealPID}/inactivate",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void inactivateDeal(@PathVariable(value = "dealPID") long dealPid) {
    directDealService.updateDealStatus(dealPid, DirectDeal.DealStatus.Inactive);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/suppliers/{profilePID}")
  public RTBProfileDTO getSupplier(@PathVariable(value = "profilePID") long pid) {
    return directDealService.getSupplier(pid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/suppliers")
  public List<RTBProfileDTO> getAvailableSuppliers() {
    return directDealService.getAllNonarchivedSuppliers();
  }

  @Deprecated
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/bidders")
  public List<DealBuyerDTO> getAvailableBuyers() {
    return directDealService.getAllBuyers();
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{dealPID}/rule")
  public List<SellerRuleDTO> getRulesAssosiatedWithDeal(@PathVariable(value = "dealPID") Long pid) {
    return directDealService.findRulesByDealPid(pid);
  }

  /**
   * Get sites names and pids plus positions names and pids assigned to the given {@link DirectDeal}
   * and belonging to the publisher {@link Company}.
   *
   * @param publisherId PID of the {@link Company}
   * @param dealPid PID of the {@link DirectDeal}
   */
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{dealPID}/publishers/{publisherId}/publisher_map")
  public List<PublisherSitePositionDTO> getPublisherMapForDeal(
      @PathVariable Long publisherId, @PathVariable(value = "dealPID") Long dealPid) {
    return sellerDealService.getPublisherMapForDeal(publisherId, dealPid);
  }
}
