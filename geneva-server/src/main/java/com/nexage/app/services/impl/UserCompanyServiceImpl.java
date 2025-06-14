package com.nexage.app.services.impl;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.UserCompanyService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Set;
import java.util.function.ToLongFunction;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Log4j2
public class UserCompanyServiceImpl implements UserCompanyService {

  private final CompanyRepository companyRepository;

  public UserCompanyServiceImpl(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public <T> void updateUserWithVerifiedCompany(
      User user, Set<T> companies, ToLongFunction<T> pidSupplier) {
    if (user.isGlobal()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_ONLY_SELLER_SEAT_USER_CAN_BE_GLOBAL);
    }
    Long pid = getVerifiedCompanyPid(companies, pidSupplier);
    user.setSellerSeat(null);
    user.getCompanies().clear();
    user.getCompanies().add(getVerifiedCompany(pid));
  }

  private Company getVerifiedCompany(Long pid) {
    return companyRepository
        .findById(pid)
        .orElseThrow(
            () ->
                new GenevaValidationException(
                    ServerErrorCodes.SERVER_CREATE_USER_COMPANY_NOT_FOUND));
  }

  private <T> Long getVerifiedCompanyPid(Set<T> companies, ToLongFunction<T> mapper) {
    return mapper.applyAsLong(
        companies.stream()
            .reduce(
                (a, b) -> {
                  throw new GenevaValidationException(
                      ServerErrorCodes.SERVER_CREATE_USER_MULTIPLE_COMPANIES);
                })
            .orElseThrow(
                () -> {
                  throw new GenevaValidationException(
                      ServerErrorCodes.SERVER_CREATE_USER_MISSING_COMPANY_OR_SELLER_SEAT);
                }));
  }
}
