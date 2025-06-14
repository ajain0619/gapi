package com.ssp.geneva.sdk.ckms.util;

import com.yahoo.ykeykey.client.YKeyKeyClient;
import com.yahoo.ykeykey.client.data.ResponseData;
import com.yahoo.ykeykey.request.RequestedEntity;
import java.util.List;
import java.util.Objects;

public class YkeyKeyClientWrapper implements CkmsInteractor {

  private final YKeyKeyClient client;

  public YkeyKeyClientWrapper(YKeyKeyClient client) {
    this.client = Objects.requireNonNull(client, "Client cannot be null!");
  }

  /** {@inheritDoc} */
  @Override
  public String getSecret(final String key, final String keyGroup) {
    ResponseData cachedSecrets = client.getCachedSecrets();

    return Objects.isNull(cachedSecrets)
        ? null
        : cachedSecrets.getCurrentSecret(keyGroup, key).secretUtf8;
  }

  /** {@inheritDoc} */
  @Override
  public void refresh() {
    client.refreshCacheNow();
  }

  /** {@inheritDoc} */
  @Override
  public void loadKeyGroup(String keyGroup) {
    List<RequestedEntity> entitiesToCache = client.getEntitiesToCache();
    entitiesToCache.add(
        new RequestedEntity.Builder().keyGroup(keyGroup).onlyLatestVersion().build());
    client.setEntitiesToCache(entitiesToCache);
  }
}
