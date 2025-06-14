package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.BuyerGroup;
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
    scripts = "/data/repository/buyer-group-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BuyerGroupRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private BuyerGroupRepository buyerGroupRepository;
  @Autowired private CompanyRepository companyRepository;

  private static final long COMPANY_PID = 1L;
  private static final String NAME = "test-name";
  private static final String SFDC_LINE_ID = "test-sfdcLineId";
  private static final String SFDC_IO_ID = "sfdcIoId";
  private static final String CURRENCY = "USD";
  private static final String BILLING_COUNTRY = "USA";
  private static final boolean BILLABLE = true;

  @Test
  void shouldFindById() {
    // given
    long buyerGroupPid = 1L;

    // when
    BuyerGroup buyerGroup =
        buyerGroupRepository
            .findById(buyerGroupPid)
            .orElseThrow(() -> new EntityNotFoundException("BuyerGroup not found in db"));

    // then
    assertEquals(buyerGroupPid, buyerGroup.getPid());
  }

  @Test
  void shouldGetAllBuyerGroupsByCompanyPid() {
    // when
    List<BuyerGroup> buyerGroups = buyerGroupRepository.findAllByCompanyPid(COMPANY_PID);

    // then
    assertEquals(3, buyerGroups.size());
    buyerGroups.forEach(bg -> assertEquals(COMPANY_PID, bg.getCompany().getPid()));
  }

  @Test
  void shouldCreateBuyerGroup() {
    // given
    Company company =
        companyRepository
            .findById(COMPANY_PID)
            .orElseThrow(() -> new EntityNotFoundException("Company not found in db"));
    BuyerGroup buyerGroup =
        new BuyerGroup(NAME, SFDC_LINE_ID, SFDC_IO_ID, CURRENCY, BILLING_COUNTRY, BILLABLE);
    buyerGroup.setCompany(company);

    // when
    BuyerGroup saved = buyerGroupRepository.save(buyerGroup);
    long dbEntries = buyerGroupRepository.count();

    // then
    assertNotNull(saved.getPid());
    assertEquals(0, saved.getVersion());
    assertEquals(COMPANY_PID, saved.getCompany().getPid());
    assertEquals(NAME, saved.getName());
    assertEquals(SFDC_LINE_ID, saved.getSfdcLineId());
    assertEquals(SFDC_IO_ID, saved.getSfdcIoId());
    assertEquals(CURRENCY, saved.getCurrency());
    assertEquals(BILLING_COUNTRY, saved.getBillingCountry());
    assertEquals(BILLABLE, saved.isBillable());
    assertEquals(5L, dbEntries);
  }

  @Test
  void shouldUpdateBuyerGroup() {
    // given
    Company company =
        companyRepository
            .findById(2L)
            .orElseThrow(() -> new EntityNotFoundException("Company not found in db"));
    BuyerGroup buyerGroup =
        buyerGroupRepository
            .findById(1L)
            .orElseThrow(() -> new EntityNotFoundException("BuyerGroup not found in db"));
    buyerGroup.setName("new-name");
    buyerGroup.setCompany(company);
    buyerGroup.setSfdcLineId("new-line-id");
    buyerGroup.setSfdcIoId("new-io-id");
    buyerGroup.setCurrency("EUR");
    buyerGroup.setBillingCountry("RO");
    buyerGroup.setBillable(false);

    // when
    BuyerGroup updated = buyerGroupRepository.saveAndFlush(buyerGroup);

    // then
    assertEquals(buyerGroup, updated);
  }

  @Test
  void shouldCreateBuyerGroupsWithSameName() {
    // given
    Company company =
        companyRepository
            .findById(COMPANY_PID)
            .orElseThrow(() -> new EntityNotFoundException("Company not found in db"));
    BuyerGroup buyerGroup =
        new BuyerGroup(NAME, SFDC_LINE_ID, SFDC_IO_ID, CURRENCY, BILLING_COUNTRY, BILLABLE);
    buyerGroup.setCompany(company);
    BuyerGroup buyerGroup2 =
        new BuyerGroup(NAME, SFDC_LINE_ID, SFDC_IO_ID, CURRENCY, BILLING_COUNTRY, BILLABLE);
    buyerGroup2.setCompany(company);

    // when
    BuyerGroup saved = buyerGroupRepository.save(buyerGroup);
    BuyerGroup saved2 = buyerGroupRepository.save(buyerGroup2);

    // then
    assertNotNull(saved.getPid());
    assertNotNull(saved2.getPid());
    assertNotEquals(saved.getPid(), saved2.getPid());
    assertNotEquals(saved, saved2);
    assertNotEquals(saved.hashCode(), saved2.hashCode());
  }
}
