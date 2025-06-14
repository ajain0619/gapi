package com.nexage.admin.core.phonecast;

import java.util.List;
import java.util.Set;

public interface PhoneCastConfigService {

  List<String> getExchangeIdsAsList();

  List<String> getTestExchangeIdsAsList();

  Set<Long> getValidExchangeIds();

  Set<Long> getMMBuyerIdList();
}
