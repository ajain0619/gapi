package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.CrsSecureStatusBlock;
import com.nexage.admin.core.repository.CompanyRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/seller-attributes.sql", config = @SqlConfig(encoding = "utf-8"))
class SellerAttributesIT extends CoreDbSdkIntegrationTestBase {

  private static Map<String, SellerAttributes> attributes = new HashMap<>();
  private static List<Company> companies = new ArrayList<>();

  @Autowired protected CompanyRepository companyRepository;

  @Test
  @Rollback
  void validateSellerInfo() {
    Optional<Company> companyOptional = companyRepository.findById(666L);
    assertTrue(companyOptional.isPresent());
    Company company = companyOptional.get();
    companies.add(company);
    attributes.put(company.getName(), generateAttribute(1));
    addSellerAttributesThenFlushAndRefresh(company);
    SellerAttributes attr = company.getSellerAttributes();
    assertNotNull(company.getPid(), "Company pid is NULL");
    validateNotNullColumns(attr);

    assertNull(attr.getSellerType(), "Company SellerType is NULL");
    assertEquals(1L, attr.getRevenueGroupPid().longValue());
    assertEquals(20.00f, attr.getExternalAdVerificationSamplingRate());
    assertEquals("a8a87c69-b518-4868-8e79-45c147356716", attr.getExternalAdVerificationPolicyKey());
    assertTrue(attr.isRawResponse());

    Optional<Company> seller1Optional = companyRepository.findById(company.getPid());
    assertTrue(seller1Optional.isPresent());
    Company seller1 = seller1Optional.get();
    assertSame(company, seller1);
    assertSame(attr, seller1.getSellerAttributes());
    assertTrue(attr.isDefaultBiddersAllowList());
    assertTrue(attr.isEnableCtvSelling());

    // protected by @prePersist and @postLoad against null
    validatePostLoad(attr);
  }

  private void validateNotNullColumns(SellerAttributes attr) {
    assertNotNull(attr.getDefaultBidderGroups(), "Company DefaultBidderGroups is NULL");
    assertNotNull(attr.getDefaultBlock(), "Company DefaultBlock is NULL");
    assertNotNull(attr.getAdFeedbackOptOut(), "Company AdFeedbackOptOut is NULL");
    assertNotNull(attr.getBuyerTransparencyOptOut(), "Company BuyerTransparencyOptOut is NULL");
    assertNotNull(attr.getHumanOptOut(), "Company HumanOptOut is NULL");
    assertNotNull(attr.getSmartQPSEnabled(), "Company SmartQPSEnabled is NULL");
  }

  private void validatePostLoad(SellerAttributes attr) {
    assertSame(
        CrsSecureStatusBlock.ALLOW_ALL,
        attr.getSecureStatusBlock(),
        "Company CrsSecureStatusBlock is not CrsSecureStatusBlock.ALLOW_ALL");
  }

  private SellerAttributes generateAttribute(int size) {
    SellerAttributes attrib = new SellerAttributes();
    final long[] blockedPidList = new long[] {79454, 459087, 98120, 220987, 890432, 89048};
    final long[] bidderGroupList = new long[] {79454, 459087, 98120, 220987, 890432, 89048};
    Set<Long> setBlockedPidList = new HashSet<>(List.of(ArrayUtils.toObject(blockedPidList)));
    Set<Long> setBidderGroupList = new HashSet<>(List.of(ArrayUtils.toObject(bidderGroupList)));
    setBlockedPidList.add(null);
    setBidderGroupList.add(null);
    attrib.setDefaultBidderGroups(setBidderGroupList);
    attrib.setDefaultBlock(setBlockedPidList);
    attrib.setRevenueShare(new BigDecimal(String.valueOf(size / 100)));
    attrib.setDefaultBiddersAllowList(true);
    attrib.setRtbFee(new BigDecimal(String.valueOf(size)));
    attrib.setAdFeedbackOptOut(false);
    attrib.setBuyerTransparencyOptOut(false);
    attrib.setRevenueGroupPid(1L);
    attrib.setHumanOptOut(false);
    attrib.setSmartQPSEnabled(false);
    attrib.setExternalAdVerificationSamplingRate(20.00f);
    attrib.setExternalAdVerificationPolicyKey("a8a87c69-b518-4868-8e79-45c147356716");
    attrib.setSspDealRevShare(new BigDecimal(String.valueOf(size / 100)));
    attrib.setJointDealRevShare(new BigDecimal(String.valueOf(size / 100)));
    attrib.setSellerDealRevShare(new BigDecimal(String.valueOf(size / 100)));
    attrib.setEnableCtvSelling(true);
    attrib.setRawResponse(true);
    return attrib;
  }

  protected void addSellerAttributesThenFlushAndRefresh(Company company) {
    SellerAttributes attr = attributes.get(company.getName());
    company.setSellerAttributes(attr);
    attr.setSeller(company);
    assertNull(company.getSellerAttributes().getSellerPid(), "sellerPid is NOT NULL");
    companyRepository.saveAndFlush(company);
    assertNotNull(company.getSellerAttributes().getSellerPid(), "sellerPid is NULL");
  }
}
