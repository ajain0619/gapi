package com.nexage.app.web.buyer;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.services.BuyerService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Log4j2
@Tag(name = "/buyers")
@RestController
@RequestMapping(value = "/buyers")
@Deprecated(since = "SSP-30166, SSP-30167, SSP-30168", forRemoval = true)
public class BuyerGroupController {

  private final BuyerService buyerService;

  public BuyerGroupController(BuyerService buyerService) {
    this.buyerService = buyerService;
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{companyPid}/buyergroups",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public BuyerGroupDTO createBuyerGroup(
      @PathVariable("companyPid") @NotNull Long companyPid, @RequestBody @Valid BuyerGroupDTO dto) {
    return buyerService.createBuyerGroup(companyPid, dto);
  }

  /**
   * @param companyPid Company unique Pid
   * @return {@link List} of {@link BuyerGroupDTO}
   * @deprecated use {@link BuyerGroupDTOController#findAll(Long, Set, String, Pageable)} instead.
   */
  @Deprecated
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{companyPid}/buyergroups", produces = APPLICATION_JSON_VALUE)
  public List<BuyerGroupDTO> getAllBuyerGroupsForCompany(
      @PathVariable(value = "companyPid") @NotNull Long companyPid) {
    return buyerService.getAllBuyerGroupsForCompany(companyPid);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{companyPid}/buyergroups/{buyerGroupPid}",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public BuyerGroupDTO updateBuyerGroup(
      @PathVariable("companyPid") @NotNull Long companyPid,
      @PathVariable("buyerGroupPid") @NotNull Long buyerGroupPid,
      @RequestBody @Valid BuyerGroupDTO dto) {
    return buyerService.updateBuyerGroup(companyPid, buyerGroupPid, dto);
  }
}
