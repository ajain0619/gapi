package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.nexage.admin.core.bidder.model.BDRCredit;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class CompanyTest {

  private final Company company = new Company();

  @Test
  void shouldSetContactWhenPreviouslyNull() {
    // given
    var contact = new User();
    contact.addCompany(company);

    // when
    company.setContact(contact);

    // then
    assertSame(contact, company.getContact());
  }

  @Test
  void shouldSetContactWhenPreviouslyNotNull() {
    // given
    var previousContact = new User();
    var newContact = new User();

    previousContact.addCompany(company);
    company.setContact(previousContact);

    // when
    newContact.addCompany(company);
    company.setContact(newContact);

    // then
    assertSame(newContact, company.getContact());
  }

  @Test
  void shouldRetrieveCorrectCreditValueWhenNotNull() {
    // given
    var credit = new BigDecimal("99.99");
    company.setCredit(credit);

    // when
    var result = company.getCredit();

    // then
    assertEquals(credit, result);
  }

  @Test
  void shouldRetrieveCorrectCreditValueWhenCreditNullButCreditsNotNull() {
    // given
    var bdrCredit = new BDRCredit();
    var otherBdrCredit = new BDRCredit();
    bdrCredit.setAmount(new BigDecimal("1.1"));
    otherBdrCredit.setAmount(new BigDecimal("2.2"));
    company.setCredits(Set.of(bdrCredit, otherBdrCredit));

    // when
    var result = company.getCredit();

    // then
    assertEquals(new BigDecimal("3.3"), result);
  }

  @Test
  void shouldRetrieveNullValueWhenCreditNullAndCreditsNull() {
    // when
    var result = company.getCredit();

    // then
    assertNull(result);
  }

  @Test
  void shouldSetHeaderBiddingPartnerCompaniesCorrectly() {
    // given
    var oldHbPartnerCompany = new HbPartnerCompany();
    var newHbPartnerCompany = new HbPartnerCompany();

    oldHbPartnerCompany.setPid(1L);
    newHbPartnerCompany.setPid(2L);

    // when
    company.setHbPartnerCompany(Set.of(oldHbPartnerCompany));
    company.setHbPartnerCompany(Set.of(newHbPartnerCompany));

    // then
    assertEquals(
        Set.of(2L),
        company.getHbPartnerCompany().stream()
            .map(HbPartnerCompany::getPid)
            .collect(Collectors.toSet()));
  }

  @Test
  void shouldRetrieveContactUserPidWhenPidFieldNotSet() {
    // given
    var contact = new User();
    contact.setPid(1L);
    contact.addCompany(company);
    company.setContact(contact);

    // when
    var result = company.getContactUserPid();

    // then
    assertEquals(1L, result);
  }

  @Test
  void shouldAssignThisCompanyAsPublisherWhenEligibleBidderAdded() {
    // given
    var bidder = new SellerEligibleBidders();

    // when
    company.addEligibleBidders(bidder);

    // then
    assertSame(company, bidder.getPublisher());
  }

  @Test
  void shouldGenerateCorrectStringRepresentation() {
    // given
    company.setId("id");
    company.setName("name");

    // when
    var result = company.toString();

    // then
    assertEquals("Company [name=name, id=id]", result);
  }

  @Test
  void shouldReturnFalseWhenAdServingEnabledNull() {
    // when
    var result = company.isAdServingEnabled();

    // then
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenRestrictDrillDownNull() {
    // when
    var result = company.isRestrictDrillDown();

    // then
    assertFalse(result);
  }

  @Test
  void shouldSetThirdPartyFraudDetectionEnabledWhenPreviouslyNotNull() {
    // when
    company.setThirdPartyFraudDetectionEnabled(false);

    // then
    assertFalse(company.getThirdPartyFraudDetectionEnabled());
  }
}
