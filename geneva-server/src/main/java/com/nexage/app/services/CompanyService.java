package com.nexage.app.services;

import com.nexage.admin.core.model.Company;
import com.nexage.app.util.validator.SearchRequestParamConstraint;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;
import java.util.Set;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CompanyService {

  List<Company> getAllCompanies();

  /**
   * Retrieve information about a single company or Retrieve information about all companies by
   * types SELLER, SEATHOLDER, BUYER or NEXAGE
   *
   * @param type companies type of SELLER, SEATHOLDER, BUYER or NEXAGE
   * @param queryFields search fields allows name
   * @param queryTerm search term string
   */
  List<Company> getAllCompaniesByType(
      CompanyType type,
      @SearchRequestParamConstraint(allowedParams = "name") Set<String> queryFields,
      String queryTerm);

  Company getCompany(long companyPID);

  Company createCompany(Company company);

  Company updateCompany(Company company);

  void deleteCompany(long companyPID);

  void softDeleteCompany(long companyPID);

  Company updateCompanyAndReload(Company company);

  void addContact(Company company, Long userPID);

  int togglePfo(long companyPid, boolean isPfoEnabled);

  String getDefaultCurrencyCode();
}
