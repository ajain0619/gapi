package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.bdr.BdrExchangeCompanyDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.ExchangeCompanyService;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/companies")
@RestController
@RequestMapping(value = "/companies")
public class ExchangeCompanyController {

  private final ExchangeCompanyService exchangeCompanyService;

  public ExchangeCompanyController(ExchangeCompanyService exchangeCompanyService) {
    this.exchangeCompanyService = exchangeCompanyService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{seatholderPID}/exchanges")
  public List<BdrExchangeCompanyDTO> getExchangesForCompany(
      @PathVariable(value = "seatholderPID") long seatholderPid) {
    return exchangeCompanyService.getAllForSeatholder(seatholderPid);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{seatholderPID}/exchanges",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public BdrExchangeCompanyDTO createExchangeCompany(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @RequestBody BdrExchangeCompanyDTO exchangeCompany) {
    if (seatholderPid != exchangeCompany.getExchangeCompanyPk().getCompany().getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    return exchangeCompanyService.create(exchangeCompany);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{seatholderPID}/exchanges/{exchangePID}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public BdrExchangeCompanyDTO updateExchangeCompany(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "exchangePID") long exchangePid,
      @RequestBody BdrExchangeCompanyDTO exchangeCompany) {
    if (seatholderPid != exchangeCompany.getExchangeCompanyPk().getCompany().getPid()
        || exchangePid != exchangeCompany.getExchangeCompanyPk().getBidderExchange().getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    return exchangeCompanyService.update(exchangeCompany);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/{seatholderPID}/exchanges/{exchangePID}")
  public void deleteExchangeCompany(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "exchangePID") long exchangePid) {
    exchangeCompanyService.delete(seatholderPid, exchangePid);
  }
}
