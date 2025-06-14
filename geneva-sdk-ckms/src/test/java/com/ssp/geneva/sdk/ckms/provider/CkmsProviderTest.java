package com.ssp.geneva.sdk.ckms.provider;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.oath.auth.KeyRefresher;
import com.ssp.geneva.sdk.ckms.config.CkmsSdkConfigProperties;
import com.ssp.geneva.sdk.ckms.util.YkeyKeyClientWrapper;
import com.yahoo.ykeykey.client.YKeyKeyClient;
import com.yahoo.ykeykey.client.YKeyKeyEnvironment;
import com.yahoo.ykeykey.client.data.KeyData;
import com.yahoo.ykeykey.client.data.KeyGroupData;
import com.yahoo.ykeykey.client.data.ResponseData;
import com.yahoo.ykeykey.client.data.VersionData;
import com.yahoo.ykeykey.response.AccessResponseData;
import com.yahoo.ykeykey.response.KeyGroup;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import javax.net.ssl.SSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class CkmsProviderTest {

  private static final String DEFAULT_CKMS_RETRY_COUNT = "3";
  private static final String DEFAULT_CKMS_TIMEOUT_MS = "5000";
  private static final String VALID_KEY_GROUP = "com.test.keygroup";
  private static final String VALID_KEY = "encryptKey";
  private static final String SECRET = "This is a secret";
  private static final String VALID_KEY_GROUP_NAME = "com.test.keygroup";
  private static final String DUMMY_KEY_NAME = "dummykey";
  private static final String ENCODED_PASSWORD = "ZHVtbXlQYXNzd29yZA==";
  private static final String DUMMY_KEY_GROUP_NAME = "com.test.dummygroup";
  private CkmsProvider ckmsUtil;
  @Mock private YKeyKeyClient client;

  @Nested
  class CkmsProviderWithMockedYkeyKeyClientTest {
    @BeforeEach
    void setup() throws Exception {
      CkmsSdkConfigProperties config = createCkmsSdkConfigProperties(true, ENCODED_PASSWORD, "aws");

      TreeMap<String, KeyData> keys = minimalKeysMap();
      createMocks(keys);

      ckmsUtil = new CkmsProvider(config);
      ckmsUtil.client = client;
      ckmsUtil.init();
    }

    @Test
    @DisplayName("Should return valid secret when ckms fetches secret")
    void shouldReturnValidSecretWhenCkmsFetchesSecret() {

      // when
      String result = assertDoesNotThrow(() -> ckmsUtil.getSecret(VALID_KEY, VALID_KEY_GROUP_NAME));

      // then
      assertEquals(SECRET, result, "CKMS Key returned doesn't match");
    }

    @Test
    @DisplayName("Should return null when getting secret with invalid key")
    void shouldReturnNullWhenGettingSecretWithInvalidKey() {

      // when
      String result =
          assertDoesNotThrow(() -> ckmsUtil.getSecret(DUMMY_KEY_NAME, DUMMY_KEY_GROUP_NAME));

      // then
      assertNull(result, "CKMS Key returned must be null");
    }

    @Test
    @DisplayName("Should return null when fetching secret with valid group and invalid key")
    void shouldReturnNullWhenFetchingSecretWithValidGroupAndInvalidKey() {

      // when
      String result =
          assertDoesNotThrow(() -> ckmsUtil.getSecret(DUMMY_KEY_NAME, VALID_KEY_GROUP_NAME));

      // then
      assertNull(result, "CKMS Key returned must be null");
    }

    private void createMocks(TreeMap<String, KeyData> keys) throws IOException {
      Mockito.when(client.getCachedSecrets())
          .thenReturn(
              new ResponseData(
                  true,
                  AccessResponseData.fromJsonString("{}"),
                  AccessResponseData.fromJsonString("{}"),
                  "error",
                  createKeyGroups(keys)));
      doCallRealMethod().when(client).refreshCacheNow();
      doCallRealMethod().when(client).setEntitiesToCache(anyList());
    }
  }

  @Nested
  class CkmsProviderWithRealYkeyKeyClientTest {

    @BeforeEach
    void setUp() {
      CkmsSdkConfigProperties config = createCkmsSdkConfigProperties(false, "", "aws");
      ckmsUtil = Mockito.spy(new CkmsProvider(config));
    }

    @Test
    @DisplayName("Should Create YKeyKeyClient and return YKeyKeyClient")
    void shouldCreateYKeyKeyClientAndReturnYkeyKeyClient() {

      // given
      ckmsUtil.yKeyKeyEnvironment = YKeyKeyEnvironment.aws;

      // when
      YKeyKeyClient client =
          assertDoesNotThrow(() -> ckmsUtil.createYkeyKeyClient(createSslContext()));

      // then
      assertNotNull(client);
    }

    @Test
    @DisplayName("Should create YKeyKeyClient with wrong Environment and return null")
    void shouldCreateYKeyKeyClientWithWrongEnvironmentAndReturnNull()
        throws NoSuchAlgorithmException {

      // when
      YKeyKeyClient client = ckmsUtil.createYkeyKeyClient(createSslContext());

      // then
      Assertions.assertNull(client);
    }

    @Test
    @DisplayName("Should Throw Exception When Refreshing Cache and catch it")
    void shouldThrowExceptionWhenRefreshingCacheAndCatchIt() {

      // given
      ckmsUtil.ckmsInteractor = mock(YkeyKeyClientWrapper.class);
      doThrow(new RuntimeException()).when(ckmsUtil.ckmsInteractor).refresh();

      // when
      ckmsUtil.refresh();

      // then
      verify(ckmsUtil.ckmsInteractor, times(1)).refresh();
    }

    @Test
    @DisplayName("Should call destroy Provider and verify shutdown and close")
    void shouldCallDestroyProviderAndVerifyShutdownAndClose() {

      // given
      ckmsUtil.client = client;
      ckmsUtil.keyRefresher = mock(KeyRefresher.class);
      doNothing().when(ckmsUtil.keyRefresher).shutdown();

      // when
      ckmsUtil.destroy();

      // then
      verify(ckmsUtil.keyRefresher, times(1)).shutdown();
      verify(ckmsUtil.client, times(1)).close();
    }

    @Test
    @DisplayName("Should call destroy Provider and not call shutdown or close")
    void shouldCallDestroyProviderAndNotCallShutdownOrClose() {

      // when && then
      assertDoesNotThrow(() -> ckmsUtil.destroy());
    }
  }

  @ParameterizedTest
  @CsvSource({
    "'', azure",
    "ZHVtbXlQYXNzd29yZA==, aws", // encoded password
    "'', aws"
  })
  @DisplayName("Should immaculately init provider with different passwords and environments")
  void shouldImmaculatelyInitProviderWithPasswordWhenUsingTrustStorePassword(
      final String password, final String environment) throws Exception {

    // given
    CkmsSdkConfigProperties config = createCkmsSdkConfigProperties(false, password, environment);
    ckmsUtil = Mockito.spy(new CkmsProvider(config));
    createMocks();

    // when
    assertDoesNotThrow(() -> ckmsUtil.init());

    // then
    verify(ckmsUtil, times(1)).refresh();
  }

  private void createMocks() throws Exception {
    doReturn(createSslContext())
        .when(ckmsUtil)
        .createSSLContext(anyString(), anyString(), anyString(), anyString());
    doReturn(client).when(ckmsUtil).createYkeyKeyClient(any());
  }

  private SSLContext createSslContext() throws NoSuchAlgorithmException {
    return SSLContext.getDefault();
  }

  private CkmsSdkConfigProperties createCkmsSdkConfigProperties(
      boolean isMockYKeyKey, String password, String environment) {
    return CkmsSdkConfigProperties.builder()
        .mockYKeyKey(isMockYKeyKey)
        .certPath("/dummy_path")
        .keyPath("/dummy_path")
        .yKeyKeyEnvironment(environment)
        .defaultKeyGroups(VALID_KEY_GROUP_NAME + " ," + DUMMY_KEY_GROUP_NAME + " ")
        .trustStorePassword(password)
        .trustStorePath("/dummy_path")
        .connectionRetryCount(DEFAULT_CKMS_RETRY_COUNT)
        .connectionTimeout(DEFAULT_CKMS_TIMEOUT_MS)
        .build();
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
    return new TreeMap<>(
        Map.of(1, new VersionData("keyGroupName", "key1", 1, null, null, SECRET.getBytes())));
  }
}
