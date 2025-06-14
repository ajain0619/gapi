package com.nexage.app.web.buyer;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.buyer.BuyerSeatDTO;
import com.nexage.app.services.BuyerService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/buyers")
@RestController
@RequestMapping(value = "/buyers")
public class BuyerSeatController {

  private final BuyerService buyerService;

  public BuyerSeatController(BuyerService buyerService) {
    this.buyerService = buyerService;
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{companyPid}/buyerseats",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public BuyerSeatDTO createBuyerSeat(
      @PathVariable("companyPid") @NotNull Long companyPid, @RequestBody @Valid BuyerSeatDTO dto) {
    return buyerService.createBuyerSeat(companyPid, dto);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{companyPid}/buyerseats", produces = APPLICATION_JSON_VALUE)
  public List<BuyerSeatDTO> getAllBuyerSeatsForCompanyAndName(
      @PathVariable(value = "companyPid") @NotNull Long companyPid,
      @Parameter(name = "A set of post auction discount fields to search for the query term.")
          @RequestParam(value = "qf", required = false)
          Set<String> qf,
      @Parameter(name = "A query search term.") @RequestParam(value = "qt", required = false)
          String qt,
      @RequestParam(value = "name", required = false) String name) {
    return buyerService.getAllBuyerSeatsForCompanyAndName(companyPid, name, qf, qt);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{companyPid}/buyerseats/{seatPid}",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public BuyerSeatDTO updateBuyerSeat(
      @PathVariable("companyPid") @NotNull Long companyPid,
      @PathVariable("seatPid") @NotNull Long seatPid,
      @RequestBody @Valid BuyerSeatDTO dto) {
    return buyerService.updateBuyerSeat(companyPid, seatPid, dto);
  }
}
