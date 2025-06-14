package com.nexage.admin.core.model;

import static com.nexage.admin.core.util.TestUtil.getTestBidderConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.BidderDeviceTypeRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
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
class BidderDeviceTypeIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private BidderConfigRepository bidderConfigRepository;
  @Autowired private BidderDeviceTypeRepository bidderDeviceTypeRepository;

  private BidderConfig bidderConfig;
  private BidderDeviceType bidderDeviceType1, bidderDeviceType2, bidderDeviceType3;

  @BeforeEach
  void setUp() {
    bidderConfig = getTestBidderConfig();
    bidderDeviceType1 = initializeBidderDeviceType(bidderConfig, 2);
    bidderDeviceType2 = initializeBidderDeviceType(bidderConfig, 3);
    bidderDeviceType3 = initializeBidderDeviceType(bidderConfig, 6);
  }

  @Test
  void shouldGetBidderConfigWithAllowedDeviceTypes() {
    // given
    BidderConfig bidderConfig = bidderConfigRepository.findById(100L).orElseThrow();

    // when
    Set<BidderDeviceType> allowedDeviceTypes = bidderConfig.getAllowedDeviceTypes();

    // then
    assertEquals(2, allowedDeviceTypes.size());
    Set<Integer> deviceTypeIds =
        allowedDeviceTypes.stream()
            .map(BidderDeviceType::getDeviceTypeId)
            .collect(Collectors.toSet());
    assertTrue(deviceTypeIds.containsAll(Set.of(2, 4)));
  }

  @Test
  void shouldCreateBidderConfigWithoutAllowedDeviceTypes() {
    // given
    bidderConfig.setAllowedDeviceTypes(null);

    // when
    bidderConfig = bidderConfigRepository.save(bidderConfig);

    // then
    assertTrue(bidderConfig.getAllowedDeviceTypes().isEmpty());
  }

  @Test
  void shouldCreateBidderConfigWithAllowedDeviceTypes() {
    // given
    bidderConfig.setAllowedDeviceTypes(
        new HashSet<>(Arrays.asList(bidderDeviceType1, bidderDeviceType2)));

    // when
    bidderConfig = bidderConfigRepository.save(bidderConfig);

    // then
    Set<BidderDeviceType> allowedDeviceTypes = bidderConfig.getAllowedDeviceTypes();
    assertEquals(2, allowedDeviceTypes.size());
    Set<Integer> deviceTypeIds =
        allowedDeviceTypes.stream()
            .map(BidderDeviceType::getDeviceTypeId)
            .collect(Collectors.toSet());
    assertTrue(
        deviceTypeIds.containsAll(
            Set.of(bidderDeviceType1.getDeviceTypeId(), bidderDeviceType2.getDeviceTypeId())));
  }

  @Test
  void shouldUpdateBidderConfigByAddingAllowedDeviceTypes() {
    // given
    bidderConfig.setAllowedDeviceTypes(
        new HashSet<>(Arrays.asList(bidderDeviceType1, bidderDeviceType2)));
    bidderConfig = bidderConfigRepository.save(bidderConfig);
    bidderConfig.getAllowedDeviceTypes().add(bidderDeviceType3);

    // when
    BidderConfig returnedBidderConfig = bidderConfigRepository.save(bidderConfig);

    // then
    Set<BidderDeviceType> allowedDeviceTypes = returnedBidderConfig.getAllowedDeviceTypes();
    assertEquals(3, allowedDeviceTypes.size());
    Set<Integer> deviceTypeIds =
        allowedDeviceTypes.stream()
            .map(BidderDeviceType::getDeviceTypeId)
            .collect(Collectors.toSet());
    assertTrue(
        deviceTypeIds.containsAll(
            Set.of(
                bidderDeviceType1.getDeviceTypeId(),
                bidderDeviceType2.getDeviceTypeId(),
                bidderDeviceType3.getDeviceTypeId())));
  }

  @Test
  void shouldUpdateBidderConfigByRemovingAndAddingAllowedDeviceTypes() {
    // given
    bidderDeviceTypeRepository.save(bidderDeviceType1);
    bidderDeviceTypeRepository.save(bidderDeviceType2);
    bidderDeviceTypeRepository.save(bidderDeviceType3);
    bidderConfig.setAllowedDeviceTypes(
        new HashSet<>(List.of(bidderDeviceType1, bidderDeviceType2)));
    bidderConfig = bidderConfigRepository.save(bidderConfig);
    bidderConfig.getAllowedDeviceTypes().remove(bidderDeviceType1);
    bidderConfig.getAllowedDeviceTypes().add(bidderDeviceType3);

    // when
    BidderConfig returnedBidderConfig = bidderConfigRepository.save(bidderConfig);

    // then
    Set<BidderDeviceType> allowedDeviceTypes = returnedBidderConfig.getAllowedDeviceTypes();
    assertEquals(2, allowedDeviceTypes.size());
    Set<Integer> deviceTypeIds =
        allowedDeviceTypes.stream()
            .map(BidderDeviceType::getDeviceTypeId)
            .collect(Collectors.toSet());
    assertTrue(
        deviceTypeIds.containsAll(
            Set.of(bidderDeviceType2.getDeviceTypeId(), bidderDeviceType3.getDeviceTypeId())));
  }

  @Test
  void shouldUpdateBidderConfigByWithNullAllowedDeviceTypes() {
    // given
    bidderConfig.setAllowedDeviceTypes(
        new HashSet<>(Arrays.asList(bidderDeviceType1, bidderDeviceType2)));
    bidderConfig = bidderConfigRepository.save(bidderConfig);
    bidderConfig.setAllowedDeviceTypes(null);

    // when
    BidderConfig returnedBidderConfig = bidderConfigRepository.save(bidderConfig);

    // then
    assertNull(returnedBidderConfig.getAllowedDeviceTypes());
  }

  @Test
  void shouldNotCreateBidderConfigByWithInvalidDeviceType() {
    // given
    bidderDeviceType1.setDeviceTypeId(12);
    bidderConfig.setAllowedDeviceTypes(
        new HashSet<>(List.of(bidderDeviceType1, bidderDeviceType2)));

    // when
    assertThrows(
        DataIntegrityViolationException.class, () -> bidderConfigRepository.save(bidderConfig));
  }

  BidderDeviceType initializeBidderDeviceType(BidderConfig bidderConfig, Integer deviceType) {
    BidderDeviceType bidderDeviceType = new BidderDeviceType();
    bidderDeviceType.setDeviceTypeId(deviceType);
    bidderDeviceType.setBidderConfig(bidderConfig);
    return bidderDeviceType;
  }
}
