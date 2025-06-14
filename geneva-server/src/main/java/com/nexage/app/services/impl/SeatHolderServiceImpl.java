package com.nexage.app.services.impl;

import com.nexage.admin.core.bidder.model.BDRCredit;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SeatHolderService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Service
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatHolder()")
@Transactional
public class SeatHolderServiceImpl implements SeatHolderService {

  private final CompanyRepository companyRepository;
  private final UserContext userContext;

  private static final BigDecimal CREDIT_MAX = BigDecimal.valueOf(999999999.99);

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public Company addCreditToSeatHolder(long pid, BigDecimal credit) {
    Company seatholder =
        companyRepository
            .findById(pid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));
    return addCreditToSeatHolder(seatholder, credit);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public Company addCreditToSeatHolder(Company seatholder, BigDecimal credit) {
    if (!isCreditValid(credit)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_CREDIT_INVALID);
    }
    log.info("Set credit to: " + credit);
    BDRCredit bdrCredit = new BDRCredit();
    bdrCredit.setUser(userContext.getUserId());
    bdrCredit.setAmount(credit);
    bdrCredit.setCompany(seatholder);
    seatholder.getCredits().add(bdrCredit);
    return companyRepository.saveAndFlush(seatholder);
  }

  private boolean isCreditValid(BigDecimal credit) {
    if (credit != null && credit.compareTo(CREDIT_MAX) > 0) {
      return false;
    }
    return true;
  }
}
