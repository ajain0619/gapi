package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.dto.CompanySearchSummaryDTO;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.SearchService;
import com.nexage.app.services.SeatHolderService;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/companies")
@RestController
@RequestMapping(value = "/companies")
public class CompanyController {

  private final CompanyService companyService;

  private final SearchService<CompanySearchSummaryDTO> searchService;

  private final SeatHolderService seatholderService;

  public CompanyController(
      CompanyService companyService,
      SearchService<CompanySearchSummaryDTO> searchService,
      SeatHolderService seatholderService) {
    this.companyService = companyService;
    this.searchService = searchService;
    this.seatholderService = seatholderService;
  }

  @Timed
  @ExceptionMetered
  @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Company createCompany(@RequestBody Company company) {
    return companyService.createCompany(company);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{companyPID}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Company updateCompany(
      @PathVariable(value = "companyPID") long pid, @RequestBody Company company) {
    if (company.getPid() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    if (pid != company.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    return companyService.updateCompany(company);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{companyPID}")
  public Company getCompany(@PathVariable(value = "companyPID") long pid) {
    return companyService.getCompany(pid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  public List<Company> getAllCompanies() {
    return companyService.getAllCompanies();
  }

  /**
   * Retrieves all companies by type
   *
   * @param type company type of SELLER, SEATHOLDER, BUYER or NEXAGE
   * @param queryFields search fields allows name
   * @param queryTerm search term string
   * @return requested {@link SellerSeat} instance
   */
  @Timed
  @ExceptionMetered
  @GetMapping(params = {"type"})
  public List<Company> getAllCompaniesByType(
      @RequestParam(value = "type") CompanyType type,
      @RequestParam(required = false, name = "qf") Set<String> queryFields,
      @RequestParam(required = false, name = "qt") String queryTerm) {
    return companyService.getAllCompaniesByType(type, queryFields, queryTerm);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(params = {"type=SELLER", "prefix"})
  public List<CompanySearchSummaryDTO> getSellerCompaniesByPrefix(
      @RequestParam(value = "prefix", required = true) String prefix) {
    return searchService.findCompanySearchDtosByTypeAndNamePrefix(prefix, CompanyType.SELLER);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(params = {"type=BUYER", "prefix"})
  public List<CompanySearchSummaryDTO> getBuyerCompaniesByPrefix(
      @RequestParam(value = "prefix", required = true) String prefix) {
    return searchService.findCompanySearchDtosByTypeAndNamePrefix(prefix, CompanyType.BUYER);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(params = {"type=SEATHOLDER", "prefix"})
  public List<CompanySearchSummaryDTO> getSeatHolderCompaniesByPrefix(
      @RequestParam(value = "prefix", required = true) String prefix) {
    return searchService.findCompanySearchDtosByTypeAndNamePrefix(prefix, CompanyType.SEATHOLDER);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/{companyPID}")
  public void deleteCompany(@PathVariable(value = "companyPID") long companyPid) {
    companyService.deleteCompany(companyPid);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(value = "/{companyPID}")
  public Company addCreditToSeatHolder(
      @PathVariable(value = "companyPID") long pid, @RequestParam BigDecimal credit) {
    return seatholderService.addCreditToSeatHolder(pid, credit);
  }
}
