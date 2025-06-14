package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.app.services.BDRAdvertiserService;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@RestController
public class BDRAdvertiserController {

  private final BDRAdvertiserService advService;

  public BDRAdvertiserController(BDRAdvertiserService advService) {
    this.advService = advService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/seatholders/{seatholderPid}/advertisers")
  @ResponseBody
  public List<BDRAdvertiser> getAdvertisersForCompany(@PathVariable long seatholderPid) {
    return advService.getAdvertisersForCompany(seatholderPid);
  }
}
