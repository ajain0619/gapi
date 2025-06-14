package com.ssp.geneva.sdk.ckms.util;

public interface CkmsInteractor {

  /**
   * Get Secret from cache.
   *
   * @param keyName key name
   * @param keyGroupName key group name
   * @return secret from client
   */
  String getSecret(final String keyName, final String keyGroupName);

  /** Refresh cache. */
  void refresh();

  /**
   * Load Ckms Key Group.
   *
   * @param keyGroup to be loaded.
   */
  void loadKeyGroup(String keyGroup);
}
