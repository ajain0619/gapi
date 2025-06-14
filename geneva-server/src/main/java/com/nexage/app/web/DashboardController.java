package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.services.DashboardService;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric;
import com.nexage.dw.geneva.util.ISO8601Util;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.text.ParseException;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Log4j2
@RestController
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/dashboard")
  @ResponseBody
  public DashboardMetric getDashboard(
      @RequestParam(value = "type", required = true) String type,
      @RequestParam(value = "start", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String startDate,
      @RequestParam(value = "stop", required = true) @DateTimeFormat(iso = ISO.DATE_TIME)
          String endDate,
      @RequestParam(value = "trend", required = false, defaultValue = "false") boolean trend) {
    Date start = null;
    Date stop = null;
    try {
      start = ISO8601Util.parse(startDate);
      stop = ISO8601Util.parse(endDate);
    } catch (ParseException e) {
      log.error("Error Parsing start/end dates :" + e.getMessage());
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    if (!(type.equalsIgnoreCase("NEXAGE")
        || type.equalsIgnoreCase("BUYER")
        || type.equalsIgnoreCase("SELLER"))) {
      log.error("Requested type is not one of NEXAGE/BUYER/SELLER. Type requested was : " + type);
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }

    if (type.equalsIgnoreCase("SELLER")) {
      return this.getSellerDashboard(start, stop, trend);
    }

    if (type.equalsIgnoreCase("BUYER")) {
      return this.getBuyerDashboard(start, stop, trend);
    } else {
      return this.getNexageDashboard(start, stop, trend);
    }
  }

  private DashboardMetric getSellerDashboard(Date start, Date stop, boolean trend) {
    return dashboardService.getSellerDashboard(start, stop, trend);
  }

  private DashboardMetric getBuyerDashboard(Date start, Date stop, boolean trend) {
    return dashboardService.getBuyerDashboard(start, stop, trend);
  }

  private DashboardMetric getNexageDashboard(Date start, Date stop, boolean trend) {
    return dashboardService.getNexageDashboard(start, stop, trend);
  }
}
