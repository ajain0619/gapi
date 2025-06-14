package com.nexage.admin.core.phonecast.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.PhoneCastConfiguration;
import com.nexage.admin.core.repository.PhoneCastConfigurationRepository;
import com.ssp.geneva.common.error.exception.GenevaDatabaseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PhoneCastConfigServiceImplTest {

  private static final String EXCHANGE_IDS_CONFIG_KEY = "valid.rtb.ids";
  private static final String TEST_EXCHANGE_IDS_CONFIG_KEY = "valid.rtb.testids";
  private static final String MM_BUYER_IDS_CONFIG_KEY = "valid.mm.buyer.pids";

  @Mock private PhoneCastConfigurationRepository configurationRepository;
  @InjectMocks private PhoneCastConfigServiceImpl phoneCastConfigService;

  @Test
  void shouldGetExchangeIdsAsList() {
    // given
    String exchangeIds = "123,456,789";
    PhoneCastConfiguration exchangeIdsConfig = mock(PhoneCastConfiguration.class);
    given(exchangeIdsConfig.getConfigValue()).willReturn(exchangeIds);
    given(configurationRepository.findByConfigKey(EXCHANGE_IDS_CONFIG_KEY))
        .willReturn(Optional.of(exchangeIdsConfig));

    // when
    List<String> returnedExchangeIdList = phoneCastConfigService.getExchangeIdsAsList();

    // then
    assertEquals(List.of("123", "456", "789"), returnedExchangeIdList);
  }

  @Test
  void shouldThrowOnGetExchangeIdsAsListWhenConfigIsNotFound() {
    // given
    given(configurationRepository.findByConfigKey(EXCHANGE_IDS_CONFIG_KEY))
        .willReturn(Optional.empty());

    // when
    GenevaDatabaseException exception =
        assertThrows(
            GenevaDatabaseException.class, () -> phoneCastConfigService.getExchangeIdsAsList());

    // then
    assertEquals(CoreDBErrorCodes.CORE_DB_EXCHANGE_ID_MISSING, exception.getErrorCode());
  }

  @Test
  void shouldGetTestExchangeIdsAsList() {
    // given
    String testExchangeIds = "123,456,789";
    PhoneCastConfiguration testExchangeIdsConfig = mock(PhoneCastConfiguration.class);
    given(testExchangeIdsConfig.getConfigValue()).willReturn(testExchangeIds);
    given(configurationRepository.findByConfigKey(TEST_EXCHANGE_IDS_CONFIG_KEY))
        .willReturn(Optional.of(testExchangeIdsConfig));

    // when
    List<String> returnedTestExchangeIdList = phoneCastConfigService.getTestExchangeIdsAsList();

    // then
    assertEquals(List.of("123", "456", "789"), returnedTestExchangeIdList);
  }

  @Test
  void shouldGetEmptyTestExchangeIdListWhenConfigIsNotFound() {
    // given
    given(configurationRepository.findByConfigKey(TEST_EXCHANGE_IDS_CONFIG_KEY))
        .willReturn(Optional.empty());

    // when
    List<String> returnedTestExchangeIdList = phoneCastConfigService.getTestExchangeIdsAsList();

    // then
    assertEquals(List.of(), returnedTestExchangeIdList);
  }

  @Test
  void shouldGetValidExchangeIds() {
    // given
    String exchangeIds = "123,fe4,456, ,789";
    PhoneCastConfiguration exchangeIdsConfig = mock(PhoneCastConfiguration.class);
    given(exchangeIdsConfig.getConfigValue()).willReturn(exchangeIds);
    given(configurationRepository.findByConfigKey(EXCHANGE_IDS_CONFIG_KEY))
        .willReturn(Optional.of(exchangeIdsConfig));

    // when
    Set<Long> returnedExchangeIdList = phoneCastConfigService.getValidExchangeIds();

    // then
    assertEquals(Set.of(123L, 456L, 789L), returnedExchangeIdList);
  }

  @Test
  void shouldThrowOnGetValidExchangeIdsWhenConfigIsNotFound() {
    // given
    given(configurationRepository.findByConfigKey(EXCHANGE_IDS_CONFIG_KEY))
        .willReturn(Optional.empty());

    // when
    GenevaDatabaseException exception =
        assertThrows(
            GenevaDatabaseException.class, () -> phoneCastConfigService.getValidExchangeIds());

    // then
    assertEquals(CoreDBErrorCodes.CORE_DB_EXCHANGE_ID_MISSING, exception.getErrorCode());
  }

  @Test
  void shouldGetMMBuyerIdList() {
    // given
    String mmBuyerIds = "123,fe4,456, ,789";
    PhoneCastConfiguration mmBuyerIdConfig = mock(PhoneCastConfiguration.class);
    given(mmBuyerIdConfig.getConfigValue()).willReturn(mmBuyerIds);
    given(configurationRepository.findByConfigKey(MM_BUYER_IDS_CONFIG_KEY))
        .willReturn(Optional.of(mmBuyerIdConfig));

    // when
    Set<Long> mmBuyerIdList = phoneCastConfigService.getMMBuyerIdList();

    // then
    assertEquals(Set.of(123L, 456L, 789L), mmBuyerIdList);
  }

  @Test
  void shouldThrowOnGetMMBuyerIdsWhenConfigIsNotFound() {
    // given
    given(configurationRepository.findByConfigKey(MM_BUYER_IDS_CONFIG_KEY))
        .willReturn(Optional.empty());

    // when
    GenevaDatabaseException exception =
        assertThrows(
            GenevaDatabaseException.class, () -> phoneCastConfigService.getMMBuyerIdList());

    // then
    assertEquals(CoreDBErrorCodes.CORE_DB_MMBUYER_ID_MISSING, exception.getErrorCode());
  }
}
