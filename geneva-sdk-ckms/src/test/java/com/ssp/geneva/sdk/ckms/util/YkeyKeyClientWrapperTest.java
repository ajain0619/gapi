package com.ssp.geneva.sdk.ckms.util;

import com.yahoo.ykeykey.client.YKeyKeyClient;
import com.yahoo.ykeykey.client.data.KeyData;
import com.yahoo.ykeykey.client.data.KeyGroupData;
import com.yahoo.ykeykey.client.data.ResponseData;
import com.yahoo.ykeykey.client.data.VersionData;
import com.yahoo.ykeykey.response.AccessResponseData;
import com.yahoo.ykeykey.response.KeyGroup;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YkeyKeyClientWrapperTest {

  private static final String VALID_KEY_GROUP = "com.test.keygroup";

  private static final String VALID_KEY = "encryptKey";
  private YkeyKeyClientWrapper wrapper;

  @Mock private YKeyKeyClient client;

  @BeforeEach
  void setUp() {
    wrapper = new YkeyKeyClientWrapper(client);
  }

  @Test
  @DisplayName("Should return secret from cache when fetching secret from client")
  void shouldReturnSecretFromCacheWhenFetchingSecretFromClient() throws IOException {

    // given
    TreeMap<String, KeyData> keys = minimalKeysMap();
    Mockito.when(client.getCachedSecrets())
        .thenReturn(
            new ResponseData(
                true,
                AccessResponseData.fromJsonString("{}"),
                AccessResponseData.fromJsonString("{}"),
                "error",
                createKeyGroups(keys)));

    // when
    String keySecret = wrapper.getSecret(VALID_KEY, VALID_KEY_GROUP);

    // then
    Mockito.verify(client, Mockito.times(1)).getCachedSecrets();
    Assertions.assertEquals("This is a secret", keySecret);
  }

  @Test
  @DisplayName("Should refresh cache when ckms refreshes cache now")
  void shouldRefreshCacheWhenCkmsRefreshesCacheNow() {

    // when
    wrapper.refresh();

    // then
    Mockito.verify(client, Mockito.times(1)).refreshCacheNow();
  }

  @Test
  @DisplayName("Should set new cache entity when ckms loads key group")
  void shouldSetNewCacheEntityWhenCkmsLoadsKeyGroup() {

    // given
    String keyGroup = "com.test.keygroup";
    Mockito.doCallRealMethod().when(client).setEntitiesToCache(Mockito.anyList());

    // when
    wrapper.loadKeyGroup(keyGroup);

    // then
    Mockito.verify(client, Mockito.times(1)).getEntitiesToCache();
    Mockito.verify(client, Mockito.times(1)).setEntitiesToCache(Mockito.anyList());
  }

  private TreeMap<String, KeyGroupData> createKeyGroups(TreeMap<String, KeyData> keys) {
    return new TreeMap<>(
        Map.of(
            VALID_KEY_GROUP,
            new KeyGroupData(
                "keyGroupName", KeyGroup.KeyGroupError.NOT_MODIFIED, "errorMessage", keys)));
  }

  private TreeMap<String, KeyData> minimalKeysMap() {
    return new TreeMap<>(
        Map.of(
            VALID_KEY,
            new KeyData(
                "keyGroupName", "key1", null, null, 1, 1, null, null, minimalVersionsMap())));
  }

  private TreeMap<Integer, VersionData> minimalVersionsMap() {
    String secret = "This is a secret";
    return new TreeMap<>(
        Map.of(1, new VersionData("keyGroupName", "key1", 1, null, null, secret.getBytes())));
  }
}
