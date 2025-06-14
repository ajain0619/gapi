package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.bidder.model.BDRExchange;
import com.nexage.admin.core.bidder.model.BdrExchangeCompany;
import com.nexage.admin.core.bidder.model.BdrExchangeCompanyPk;
import com.nexage.admin.core.model.Company;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/bdr-exchange-company-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BdrExchangeCompanyRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired BdrExchangeCompanyRepository repository;
  @Autowired BdrExchangeRepository exchangeRepository;
  @Autowired CompanyRepository companyRepository;
  private static final long COMPANY_PID = 1L;

  @Test
  void shouldCreateExchangeCompany() {
    // given
    BDRExchange exchange =
        exchangeRepository
            .findById(2L)
            .orElseThrow(() -> new EntityNotFoundException("Exchange not found in DB"));
    Company company =
        companyRepository
            .findById(2L)
            .orElseThrow(() -> new EntityNotFoundException("Company not found in DB"));
    BdrExchangeCompany bdrExchangeCompany = new BdrExchangeCompany(exchange, company);

    // when
    BdrExchangeCompany result = repository.save(bdrExchangeCompany);

    // then
    assertEquals(bdrExchangeCompany, result);
  }

  @Test
  void shouldGetExchangeCompaniesByCompany() {
    // when
    List<BdrExchangeCompany> result = repository.findByExchangeCompanyPk_Company_Pid(COMPANY_PID);

    // then
    assertEquals(3, result.size());
    result.forEach(
        bdrExCo -> assertEquals(COMPANY_PID, bdrExCo.getExchangeCompanyPk().getCompany().getPid()));
  }

  @Test
  void shouldDeleteExchangeCompany() {
    // given
    BdrExchangeCompany bdrExchangeCompany =
        repository.findByExchangeCompanyPk_Company_Pid(2L).get(0);
    BdrExchangeCompanyPk pk = bdrExchangeCompany.getExchangeCompanyPk();

    // when
    repository.deleteByExchangeCompanyPk_Company_PidAndExchangeCompanyPk_BidderExchange_Pid(
        pk.getCompany().getPid(), pk.getBidderExchange().getPid());

    // then
    assertTrue(repository.findById(pk).isEmpty());
  }
}
