package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.sparta.jpa.model.SellerAdSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/seller-ad-source-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class SellerAdSourceRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired SellerAdSourceRepository sellerAdSourceRepository;

  @Test
  void shouldFindSellerAdSource() {
    SellerAdSource sellerAdSource = sellerAdSourceRepository.findById(3L).orElseThrow();
    assertNotNull(sellerAdSource.getAdSource());
    assertNotNull(sellerAdSource.getSeller());
    assertEquals(2L, sellerAdSource.getAdSourcePid());
  }

  @Test
  void shouldFindAllBySellerPid() {
    assertEquals(2, sellerAdSourceRepository.findAllBySellerPid(1L).size());
  }

  @Test
  void shouldFindBySellerPidAndAdSourcePid() {
    SellerAdSource sellerAdSource =
        sellerAdSourceRepository.findBySellerPidAndAdSourcePid(1L, 1L).orElseThrow();
    assertEquals(1L, sellerAdSource.getAdSource().getPid());
    assertEquals(1L, sellerAdSource.getSeller().getPid());
  }

  @Test
  void shouldDeleteByAdSourcePidAndSellerPid() {
    // when
    sellerAdSourceRepository.deleteBySellerPidAndAdSourcePid(1L, 1L);
    // then
    assertTrue(sellerAdSourceRepository.findBySellerPidAndAdSourcePid(1L, 1L).isEmpty());
  }

  @Test
  void shouldReturnTrueWhenSellerAdSourceExists() {
    assertTrue(sellerAdSourceRepository.existsBySellerPidAndAdSourcePid(2L, 1L));
  }

  @Test
  void shouldReturnFalseWhenSellerAdSourceNotExists() {
    assertFalse(sellerAdSourceRepository.existsBySellerPidAndAdSourcePid(2L, 2L));
  }
}
