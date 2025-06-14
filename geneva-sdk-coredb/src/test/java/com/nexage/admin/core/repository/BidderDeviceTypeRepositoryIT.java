package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.BidderDeviceType;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/bidder-config-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class BidderDeviceTypeRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private BidderDeviceTypeRepository bidderDeviceTypeRepository;
  @Autowired private BidderConfigRepository bidderConfigRepository;

  @Test
  void shouldTestWhenDeviceTypeIdIsNull() {
    BidderDeviceType bidderDeviceType = createDefaultBidderDeviceType();
    bidderDeviceType.setDeviceTypeId(null);
    assertThrows(
        ConstraintViolationException.class,
        () -> bidderDeviceTypeRepository.save(bidderDeviceType));
  }

  @Test
  void shouldTestWhenBidderConfigIsInvalid() {
    BidderDeviceType bidderDeviceType = createDefaultBidderDeviceType();
    BidderConfig bidderConfig = bidderConfigRepository.getOne(2L);
    bidderDeviceType.setBidderConfig(bidderConfig);
    assertThrows(
        DataIntegrityViolationException.class,
        () -> bidderDeviceTypeRepository.save(bidderDeviceType));
  }

  @Test
  void shouldTestWhenBidderConfigIsValid() {
    Long bidderPid = 100L;
    BidderDeviceType bidderDeviceType = createDefaultBidderDeviceType();
    BidderDeviceType savedBidderDeviceType = bidderDeviceTypeRepository.save(bidderDeviceType);
    assertEquals(bidderPid, savedBidderDeviceType.getBidderConfig().getPid());
  }

  @Test
  void shouldTestWhenDeviceTypeIsInvalid() {
    BidderDeviceType bidderDeviceType = createDefaultBidderDeviceType();
    bidderDeviceType.setDeviceTypeId(12);
    assertThrows(
        DataIntegrityViolationException.class,
        () -> bidderDeviceTypeRepository.save(bidderDeviceType));
  }

  @Test
  void shouldTestGetAllowedDeviceTypesForBidderConfig() {
    Long bidderPid = 100L;
    Set<Integer> allowedDeviceTypes =
        bidderDeviceTypeRepository.getAllowedDeviceTypesForBidderConfig(bidderPid);
    assertEquals(2, allowedDeviceTypes.size());
    assertTrue(allowedDeviceTypes.containsAll(List.of(2, 4)));
  }

  private BidderDeviceType createDefaultBidderDeviceType() {
    BidderConfig bidderConfig = bidderConfigRepository.getOne(100L);
    BidderDeviceType bidderDeviceType = new BidderDeviceType();
    bidderDeviceType.setBidderConfig(bidderConfig);
    bidderDeviceType.setDeviceTypeId(2);

    return bidderDeviceType;
  }
}
