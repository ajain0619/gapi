package com.nexage.admin.core.phonecast.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.PhoneCastConfiguration;
import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.nexage.admin.core.repository.PhoneCastConfigurationRepository;
import com.ssp.geneva.common.error.exception.GenevaDatabaseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Cacheable("phoneCastConfiguration")
@Log4j2
public class PhoneCastConfigServiceImpl implements PhoneCastConfigService {

  private static final Splitter SPLIT_ON_COMMA =
      Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings();
  private static final String EXCHANGE_IDS_CONFIG_KEY = "valid.rtb.ids";
  private static final String MM_BUYER_IDS_CONFIG_KEY = "valid.mm.buyer.pids";
  private static final String TEST_EXCHANGE_IDS_CONFIG_KEY = "valid.rtb.testids";

  private final PhoneCastConfigurationRepository phoneCastConfigurationRepository;

  public PhoneCastConfigServiceImpl(
      PhoneCastConfigurationRepository phoneCastConfigurationRepository) {
    this.phoneCastConfigurationRepository = phoneCastConfigurationRepository;
  }

  @Override
  public List<String> getExchangeIdsAsList() {
    return phoneCastConfigurationRepository
        .findByConfigKey(EXCHANGE_IDS_CONFIG_KEY)
        .map(PhoneCastConfiguration::getConfigValue)
        .map(exchangeIds -> Lists.newArrayList(Splitter.on(",").split(exchangeIds)))
        .orElseThrow(
            () -> new GenevaDatabaseException(CoreDBErrorCodes.CORE_DB_EXCHANGE_ID_MISSING));
  }

  @Override
  public List<String> getTestExchangeIdsAsList() {
    return phoneCastConfigurationRepository
        .findByConfigKey(TEST_EXCHANGE_IDS_CONFIG_KEY)
        .map(PhoneCastConfiguration::getConfigValue)
        .map(exchangeIds -> Lists.newArrayList(Splitter.on(",").split(exchangeIds)))
        .orElseGet(ArrayList::new);
  }

  @Override
  public Set<Long> getValidExchangeIds() {
    return phoneCastConfigurationRepository
        .findByConfigKey(EXCHANGE_IDS_CONFIG_KEY)
        .map(PhoneCastConfiguration::getConfigValue)
        .map(
            exchangeIds ->
                parseAsLongsAndDropMalformed(
                        Streams.stream(SPLIT_ON_COMMA.split(exchangeIds)),
                        id -> log.warn("could not parse exchangeId string: " + id))
                    .collect(Collectors.toSet()))
        .orElseThrow(
            () -> new GenevaDatabaseException(CoreDBErrorCodes.CORE_DB_EXCHANGE_ID_MISSING));
  }

  @Override
  public Set<Long> getMMBuyerIdList() {
    return phoneCastConfigurationRepository
        .findByConfigKey(MM_BUYER_IDS_CONFIG_KEY)
        .map(PhoneCastConfiguration::getConfigValue)
        .map(
            mmBuyerIds ->
                parseAsLongsAndDropMalformed(
                        Streams.stream(SPLIT_ON_COMMA.split(mmBuyerIds)),
                        id -> log.warn("could not parse mmBuyerId string: " + id))
                    .collect(Collectors.toSet()))
        .orElseThrow(
            () -> new GenevaDatabaseException(CoreDBErrorCodes.CORE_DB_MMBUYER_ID_MISSING));
  }

  private Stream<Long> parseAsLongsAndDropMalformed(
      Stream<String> strings, Consumer<String> onMalformed) {
    return strings
        .map(
            str -> {
              Long parsedId = null;
              try {
                parsedId = Long.valueOf(str);
              } catch (NumberFormatException e) {
                onMalformed.accept(str);
              }
              return parsedId;
            })
        .filter(Objects::nonNull);
  }
}
