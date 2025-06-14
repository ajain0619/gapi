package com.nexage.app.services.validation;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.publisher.PublisherSiteDealTermDTO;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class RevenueShareUpdateValidator {

  /**
   * Checks whether revenue share or/and rtb fee are updated
   *
   * @param currentRevenueShare current revenue share value (can be null)
   * @param updatedRevenueShare updated revenue share value (can be null)
   * @return true if any of the values were updated or false if not
   */
  public boolean isRevenueShareUpdated(
      BigDecimal currentRevenueShare,
      BigDecimal updatedRevenueShare,
      BigDecimal currentRtbFee,
      BigDecimal updatedRtbFee) {
    return isUpdated(currentRevenueShare, updatedRevenueShare)
        || isUpdated(currentRtbFee, updatedRtbFee);
  }

  /**
   * Checks whether seller attributes's revenue share data is updated
   *
   * @param original current company info
   * @param updated updated company info (can be null)
   * @return true if any of the values were updated or false if not
   */
  public boolean isRevenueShareUpdated(Company original, Company updated) {
    if (updated == null || updated.getSellerAttributes() == null) return false;
    SellerAttributes originalSellerAttributes = original.getSellerAttributes();
    SellerAttributes updatedSellerAttributes = updated.getSellerAttributes();

    return isRevenueShareUpdated(
        originalSellerAttributes.getRevenueShare(),
        updatedSellerAttributes.getRevenueShare(),
        originalSellerAttributes.getRtbFee(),
        updatedSellerAttributes.getRtbFee());
  }

  /**
   * Checks whether deal term revenue share data is updated
   *
   * @param siteDealTerm current site's deal term (can be null)
   * @param currentDealTerm updated site's deal term (can be null)
   * @return true if any of the values were updated or false if not
   */
  public boolean isRevenueShareUpdate(
      SiteDealTerm siteDealTerm, PublisherSiteDealTermDTO currentDealTerm) {
    if (siteDealTerm == null) {
      if (currentDealTerm != null
          && (currentDealTerm.getRtbFee() != null
              || currentDealTerm.getNexageRevenueShare() != null)) {
        return true;
      }
      return false;
    }

    return isRevenueShareUpdated(
        siteDealTerm.getNexageRevenueShare(),
        currentDealTerm.getNexageRevenueShare(),
        siteDealTerm.getRtbFee(),
        currentDealTerm.getRtbFee());
  }

  /**
   * Checks whether seller attributes revenue share data is overridden by site
   *
   * @param sellerAttributes current seller attributes (can be null)
   * @param currentDealTerm updated site deal term (can be null)
   * @return true if any of the values were updated or false if not
   */
  public boolean isRevenueShareUpdate(
      SellerAttributes sellerAttributes, PublisherSiteDealTermDTO currentDealTerm) {
    if (sellerAttributes == null) {
      return currentDealTerm != null
          && (currentDealTerm.getRtbFee() != null
              || currentDealTerm.getNexageRevenueShare() != null);
    }

    return isRevenueShareUpdated(
        sellerAttributes.getRevenueShare(),
        currentDealTerm.getNexageRevenueShare(),
        sellerAttributes.getRtbFee(),
        currentDealTerm.getRtbFee());
  }

  /**
   * Checks whether seller attributes revenue share data is overridden
   *
   * @param sellerAttributes current seller attributes (can be null)
   * @param attributes updated publisher attributes (can be null)
   * @return true if any of the values were updated or false if not
   */
  public boolean isRevenueShareUpdate(
      SellerAttributes sellerAttributes, PublisherAttributes attributes) {
    if (sellerAttributes == null) {
      return attributes != null
          && (attributes.getRtbFee() != null || attributes.getRevenueShare() != null);
    }

    return isRevenueShareUpdated(
        sellerAttributes.getRevenueShare(),
        attributes.getRevenueShare(),
        sellerAttributes.getRtbFee(),
        attributes.getRtbFee());
  }

  private boolean isUpdated(BigDecimal current, BigDecimal updated) {
    if (current == null && updated != null) {
      return true;
    }

    if (updated == null) {
      return false;
    }

    return current.compareTo(updated) != 0;
  }
}
