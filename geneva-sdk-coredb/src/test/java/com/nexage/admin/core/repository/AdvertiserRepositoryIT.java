package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.Advertiser;
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
    scripts = "/data/repository/advertiser-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class AdvertiserRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private AdvertiserRepository advertiserRepository;

  private static final String TEST_ADVERTISER_NAME = "Test Advertiser 1";
  private static final String TEST_ADVERTISER_NAME_UPDATED = "Test Advertiser 1 Updated";
  private static final long TEST_SELLER_PID = 1L;
  private static final long ADVERTISER_PID = 5L;
  private static final Advertiser.AdvertiserStatus TEST_ADVERTISER_STATUS =
      Advertiser.AdvertiserStatus.ACTIVE;
  private static final Advertiser.AdvertiserStatus TEST_ADVERTISER_STATUS_UPDATED =
      Advertiser.AdvertiserStatus.INACTIVE;

  @Test
  void shouldCreateAdvertiser() {
    // given
    Advertiser advertiser = getTestAdvertiser();

    // when
    advertiser = advertiserRepository.save(advertiser);

    // then
    assertNotNull(advertiser.getPid());
    assertEquals(TEST_ADVERTISER_NAME, advertiser.getName());
    assertEquals(TEST_SELLER_PID, advertiser.getSellerId());
    assertEquals(TEST_ADVERTISER_STATUS, advertiser.getStatus());
  }

  @Test
  void shouldUpdateAdvertiser() {
    // given
    Advertiser advertiser =
        advertiserRepository
            .findById(ADVERTISER_PID)
            .orElseThrow(() -> new EntityNotFoundException("Advertiser not found"));
    advertiser.setSellerId(TEST_SELLER_PID);
    advertiser.setName(TEST_ADVERTISER_NAME_UPDATED);
    advertiser.setStatus(TEST_ADVERTISER_STATUS_UPDATED);

    // when
    advertiser = advertiserRepository.save(advertiser);

    // then
    assertEquals(ADVERTISER_PID, advertiser.getPid());
    assertEquals(TEST_ADVERTISER_NAME_UPDATED, advertiser.getName());
    assertEquals(TEST_SELLER_PID, advertiser.getSellerId());
    assertEquals(TEST_ADVERTISER_STATUS_UPDATED, advertiser.getStatus());
  }

  @Test
  void shouldGetAllNotDeletedAdvertisersForCompany() {
    // given

    // when
    List<Advertiser> result =
        advertiserRepository.findAllBySellerIdAndStatusNotDeleted(TEST_SELLER_PID);

    // then
    assertEquals(2, result.size());
  }

  @Test
  void shouldGetAllAdvertisersBySellerId() {
    // given

    // when
    List<Advertiser> result = advertiserRepository.findAllBySellerId(TEST_SELLER_PID);

    // then
    assertEquals(4, result.size());
  }

  @Test
  void shouldDeleteAdvertiser() {
    // given
    Advertiser advertiser =
        advertiserRepository
            .findById(ADVERTISER_PID)
            .orElseThrow(() -> new EntityNotFoundException("Advertiser not found"));

    // when
    advertiserRepository.delete(advertiser);

    // then
    assertFalse(advertiserRepository.existsById(ADVERTISER_PID));
  }

  private Advertiser getTestAdvertiser() {
    Advertiser advertiser = new Advertiser();
    advertiser.setName(TEST_ADVERTISER_NAME);
    advertiser.setSellerId(TEST_SELLER_PID);
    advertiser.setStatus(TEST_ADVERTISER_STATUS);
    return advertiser;
  }
}
