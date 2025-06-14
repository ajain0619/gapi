package com.nexage.app.services.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.publisher.PublisherSiteDealTermDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RevenueShareUpdateValidatorTest {
  private RevenueShareUpdateValidator revenueShareUpdateValidator =
      new RevenueShareUpdateValidator();

  @Test
  void givenRevenueShareValuesUpdated_whenRevenueShareDataIsUpdated_thenTrue() {
    BigDecimal currentRevenueShare = new BigDecimal("0.3");
    BigDecimal updatedRevenueShare = new BigDecimal("0.35");
    BigDecimal currentRtbFee = new BigDecimal("0.1");
    BigDecimal updatedRtbFee = new BigDecimal("0.05");

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdated(
            currentRevenueShare, updatedRevenueShare, currentRtbFee, updatedRtbFee),
        is(true));
  }

  @Test
  void givenRevenueShareValuesNotUpdated_whenRevenueShareDataIsUpdated_thenFalse() {
    BigDecimal currentRevenueShare = new BigDecimal("0.3");
    BigDecimal updatedRevenueShare = new BigDecimal("0.300");
    BigDecimal currentRtbFee = new BigDecimal("0.1");
    BigDecimal updatedRtbFee = new BigDecimal("0.1");

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdated(
            currentRevenueShare, updatedRevenueShare, currentRevenueShare, updatedRevenueShare),
        is(false));
  }

  @Test
  void givenCurrentRevenueShareIsNullAndUpdatedIsNot_whenRevenueShareDataIsUpdated_thenTrue() {
    BigDecimal currentRevenueShare = null;
    BigDecimal updatedRevenueShare = new BigDecimal("0.300");
    BigDecimal currentRtbFee = new BigDecimal("0.1");
    BigDecimal updatedRtbFee = new BigDecimal("0.1");

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdated(
            currentRevenueShare, updatedRevenueShare, currentRtbFee, updatedRtbFee),
        is(true));
  }

  @Test
  void givenCurrentRevenueShareIsNullAndUpdatedIsNull_whenRevenueShareDataIsUpdated_thenFalse() {
    BigDecimal currentRevenueShare = null;
    BigDecimal updatedRevenueShare = null;
    BigDecimal currentRtbFee = new BigDecimal("0.1");
    BigDecimal updatedRtbFee = new BigDecimal("0.1");

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdated(
            currentRevenueShare, updatedRevenueShare, currentRtbFee, updatedRtbFee),
        is(false));
  }

  @Test
  void givenCurrentRtbFeeIsNullAndUpdatedIsNot_whenRevenueShareDataIsUpdated_thenTrue() {
    BigDecimal currentRevenueShare = new BigDecimal("0.3");
    BigDecimal currentRtbFee = null;
    BigDecimal updatedRevenueShare = new BigDecimal("0.300");
    BigDecimal updatedRtbFee = new BigDecimal("0.1");

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdated(
            currentRevenueShare, updatedRevenueShare, currentRtbFee, updatedRtbFee),
        is(true));
  }

  @Test
  void givenCurrentRtbFeeIsNullAndUpdatedIsNot_whenRevenueShareDataIsUpdated_thenFalse() {
    BigDecimal currentRevenueShare = new BigDecimal("0.3");
    BigDecimal updatedRevenueShare = new BigDecimal("0.300");
    BigDecimal currentRtbFee = null;
    BigDecimal updatedRtbFee = null;

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdated(
            currentRevenueShare, updatedRevenueShare, currentRtbFee, updatedRtbFee),
        is(false));
  }

  @Test
  void givenCompanyUpdatedWithoutRevenueShareValues_whenRevenueShareDataIsUpdated_thenFalse() {
    Company originalCompany = new Company();
    Company updatedCompany = new Company();
    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdated(originalCompany, updatedCompany),
        is(false));
  }

  @Test
  void givenCompanyUpdatedWithNewRevenueShareValues_whenRevenueShareDataIsUpdated_thenTrue() {
    Company originalCompany = new Company();
    SellerAttributes originalSellerAttributes = new SellerAttributes();
    originalSellerAttributes.setRevenueShare(BigDecimal.valueOf(0.3));
    originalSellerAttributes.setRtbFee(BigDecimal.valueOf(0.1));
    originalCompany.setSellerAttributes(originalSellerAttributes);
    Company updatedCompany = new Company();
    SellerAttributes updatedSellerAttributes = new SellerAttributes();
    updatedSellerAttributes.setRevenueShare(BigDecimal.valueOf(0.35));
    updatedSellerAttributes.setRtbFee(BigDecimal.valueOf(0.05));
    updatedCompany.setSellerAttributes(updatedSellerAttributes);
    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdated(originalCompany, updatedCompany),
        is(true));
  }

  @Test
  void givenCompanyUpdatedWithoutNewRevenueShareValues_whenRevenueShareDataIsUpdated_thenFalse() {
    Company originalCompany = new Company();
    SellerAttributes originalSellerAttributes = new SellerAttributes();
    originalSellerAttributes.setRevenueShare(BigDecimal.valueOf(0.3));
    originalSellerAttributes.setRtbFee(BigDecimal.valueOf(0.1));
    originalCompany.setSellerAttributes(originalSellerAttributes);
    Company updatedCompany = new Company();
    SellerAttributes updatedSellerAttributes = new SellerAttributes();
    updatedSellerAttributes.setRevenueShare(BigDecimal.valueOf(0.3));
    updatedSellerAttributes.setRtbFee(BigDecimal.valueOf(0.1));
    updatedCompany.setSellerAttributes(updatedSellerAttributes);
    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdated(originalCompany, updatedCompany),
        is(false));
  }

  @Test
  void givenSiteDealTermUpdatedWithNewRevenueShareValues_whenRevenueShareDataIsUpdated_thenTrue() {
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    PublisherSiteDealTermDTO publisherSiteDealTerm = new PublisherSiteDealTermDTO();
    siteDealTerm.setNexageRevenueShare(BigDecimal.valueOf(0.3));
    siteDealTerm.setRtbFee(BigDecimal.valueOf(0.1));
    publisherSiteDealTerm.setNexageRevenueShare(BigDecimal.valueOf(0.4));
    publisherSiteDealTerm.setRtbFee(BigDecimal.valueOf(0.0));

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdate(siteDealTerm, publisherSiteDealTerm),
        is(true));
  }

  @Test
  void
      givenSiteDealTermUpdatedWithoutNewRevenueShareValues_whenRevenueShareDataIsUpdated_thenFalse() {
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    PublisherSiteDealTermDTO publisherSiteDealTerm = new PublisherSiteDealTermDTO();
    siteDealTerm.setNexageRevenueShare(BigDecimal.valueOf(0.3));
    siteDealTerm.setRtbFee(BigDecimal.valueOf(0.1));
    publisherSiteDealTerm.setNexageRevenueShare(BigDecimal.valueOf(0.3));
    publisherSiteDealTerm.setRtbFee(BigDecimal.valueOf(0.1));

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdate(siteDealTerm, publisherSiteDealTerm),
        is(false));
  }

  @Test
  void
      givenNoSiteDealTermUpdatedWithNewRevenueShareValues_whenRevenueShareDataIsUpdated_thenTrue() {
    SiteDealTerm siteDealTerm = null;
    PublisherSiteDealTermDTO publisherSiteDealTerm = new PublisherSiteDealTermDTO();
    publisherSiteDealTerm.setNexageRevenueShare(BigDecimal.valueOf(0.4));
    publisherSiteDealTerm.setRtbFee(BigDecimal.valueOf(0.0));

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdate(siteDealTerm, publisherSiteDealTerm),
        is(true));
  }

  @Test
  void
      givenSiteDealTermOverridesSellerAttributesWithNewRevenueShareValues_whenRevenueShareDataIsUpdated_thenTrue() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setRevenueShare(BigDecimal.valueOf(0.4));
    sellerAttributes.setRtbFee(BigDecimal.valueOf(0.0));
    PublisherSiteDealTermDTO publisherSiteDealTerm = new PublisherSiteDealTermDTO();
    publisherSiteDealTerm.setNexageRevenueShare(BigDecimal.valueOf(0.3));
    publisherSiteDealTerm.setRtbFee(BigDecimal.valueOf(0.1));

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdate(sellerAttributes, publisherSiteDealTerm),
        is(true));
  }

  @Test
  void
      givenSiteDealTermOverridesSellerAttributesWithoutNewRevenueShareValues_whenRevenueShareDataIsUpdated_thenFalse() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setRevenueShare(BigDecimal.valueOf(0.4));
    sellerAttributes.setRtbFee(BigDecimal.valueOf(0.0));
    PublisherSiteDealTermDTO publisherSiteDealTerm = new PublisherSiteDealTermDTO();
    publisherSiteDealTerm.setNexageRevenueShare(BigDecimal.valueOf(0.4));
    publisherSiteDealTerm.setRtbFee(BigDecimal.valueOf(0.0));

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdate(sellerAttributes, publisherSiteDealTerm),
        is(false));
  }

  @Test
  void
      givenSiteDealTermOverridesSellerAttributesWithNewRevenueShareValuesWithNoSellerAttributes_whenRevenueShareDataIsUpdated_thenTrue() {
    SellerAttributes sellerAttributes = null;
    PublisherSiteDealTermDTO publisherSiteDealTerm = new PublisherSiteDealTermDTO();
    publisherSiteDealTerm.setNexageRevenueShare(BigDecimal.valueOf(0.3));
    publisherSiteDealTerm.setRtbFee(BigDecimal.valueOf(0.1));

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdate(sellerAttributes, publisherSiteDealTerm),
        is(true));
  }

  @Test
  void
      givenPublisherCreatesSiteWithNewUpdatedRevenueShare_whenRevenueShareDataIsUpdated_thenTrue() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setRevenueShare(BigDecimal.valueOf(0.4));
    sellerAttributes.setRtbFee(BigDecimal.valueOf(0.0));
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setRevenueShare(BigDecimal.valueOf(0.3));
    publisherAttributes.setRtbFee(BigDecimal.valueOf(0.1));

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdate(sellerAttributes, publisherAttributes),
        is(true));
  }

  @Test
  void
      givenPublisherCreatesSiteWithoutNewUpdatedRevenueShare_whenRevenueShareDataIsUpdated_thenFalse() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setRevenueShare(BigDecimal.valueOf(0.4));
    sellerAttributes.setRtbFee(BigDecimal.valueOf(0.0));
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setRevenueShare(BigDecimal.valueOf(0.4));
    publisherAttributes.setRtbFee(BigDecimal.valueOf(0.0));

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdate(sellerAttributes, publisherAttributes),
        is(false));
  }

  @Test
  void givenPublisherCreatesSiteWithoutDefaultValues_whenRevenueShareDataIsUpdated_thenTrue() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    PublisherAttributes publisherAttributes = new PublisherAttributes();
    publisherAttributes.setRevenueShare(BigDecimal.valueOf(0.3));
    publisherAttributes.setRtbFee(BigDecimal.valueOf(0.1));

    assertThat(
        revenueShareUpdateValidator.isRevenueShareUpdate(sellerAttributes, publisherAttributes),
        is(true));
  }
}
