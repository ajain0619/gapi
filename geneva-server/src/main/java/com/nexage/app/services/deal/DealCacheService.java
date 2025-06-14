package com.nexage.app.services.deal;

public interface DealCacheService {
  String getDescription(String id);

  boolean getVisibility(String id);

  void removeDeal(String id);

  boolean refreshCache();
}
