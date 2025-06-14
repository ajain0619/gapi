package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.SellerAttributes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/seller-attributes-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class SellerAttributesRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private SellerAttributesRepository sellerAttributesRepository;

  @Test
  void testGetSellerAttributesBySellerId_ValidSellerId() {
    long sellerPid = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    Page<SellerAttributes> result =
        sellerAttributesRepository.findAllBySellerPid(sellerPid, pageable);

    assertEquals(1, result.getTotalPages());
    assertEquals(1, result.getContent().size());

    SellerAttributes sellerAttributes = result.getContent().get(0);
    assertTrue(sellerAttributes.isRawResponse());
  }

  @Test
  void testGetSellerAttributesBySellerId_InValidSellerId() {
    long sellerPid = 2L;
    Pageable pageable = PageRequest.of(0, 10);

    Page<SellerAttributes> result =
        sellerAttributesRepository.findAllBySellerPid(sellerPid, pageable);
    assertEquals(0, result.getContent().size());
  }

  @Test
  void shouldProperlyCheckIfSellerAttributesExistBySellerPid() {
    assertTrue(sellerAttributesRepository.existsBySellerPid(1L));
    assertFalse(sellerAttributesRepository.existsBySellerPid(2L));
  }
}
