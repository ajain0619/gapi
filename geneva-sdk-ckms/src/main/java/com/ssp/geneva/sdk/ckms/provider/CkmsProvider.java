package com.ssp.geneva.sdk.ckms.provider;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.oath.auth.KeyRefresher;
import com.oath.auth.KeyRefresherException;
import com.oath.auth.Utils;
import com.ssp.geneva.sdk.ckms.config.CkmsSdkConfigProperties;
import com.ssp.geneva.sdk.ckms.util.CkmsFacade;
import com.ssp.geneva.sdk.ckms.util.CkmsInteractor;
import com.yahoo.ykeykey.client.YKeyKeyClient;
import com.yahoo.ykeykey.client.YKeyKeyEnvironment;
import com.yahoo.ykeykey.request.RequestedEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility to communicate with CKMS (Centralized Key Management System) running in AWS to fetch
 * secrets. <br>
 * CKMS (also known as yKeyKey), is a hosted secret-key management service under Oath. This service
 * automates the distribution of application secrets securely and reliably. <br>
 *
 * @author Suhas Subramanyam
 * @see <a href="https://git.ouryahoo.com/pages/ykeykey/ckms-guide/">CKMS User Guide</a>
 */
@Log4j2
public class CkmsProvider {

  private static final int HOUR_REFRESH_INTERVAL_IN_MILLISECONDS =
      60 * 60 * 1000; // 60 seconds * 60 minutes * 1000
  private final CkmsSdkConfigProperties ckmsSdkConfigProperties;
  private int retryCount;
  private int connectionTimeout;
  private final List<String> keyGroupsList = new ArrayList<>();
  private final AtomicBoolean isInitialized = new AtomicBoolean();
  protected YKeyKeyEnvironment yKeyKeyEnvironment;
  @Getter protected CkmsInteractor ckmsInteractor = null;

  protected YKeyKeyClient client;

  protected KeyRefresher keyRefresher;

  public CkmsProvider(final CkmsSdkConfigProperties ckmsSdkConfigProperties) {
    this.ckmsSdkConfigProperties = ckmsSdkConfigProperties;
  }

  @PostConstruct
  public void init() throws Exception {
    boolean isMockYKeyKey = ckmsSdkConfigProperties.getMockYKeyKey();
    if (!isMockYKeyKey) {
      String certPath = ckmsSdkConfigProperties.getCertPath();
      String trustStorePath = ckmsSdkConfigProperties.getTrustStorePath();
      String keyPath = ckmsSdkConfigProperties.getKeyPath();
      storeDefaultKeyGroups(ckmsSdkConfigProperties.getDefaultKeyGroups());
      String trustStorePassword = decodePassword(ckmsSdkConfigProperties.getTrustStorePassword());
      SSLContext context = createSSLContext(trustStorePath, trustStorePassword, certPath, keyPath);
      yKeyKeyEnvironment = getEnum(ckmsSdkConfigProperties.getYKeyKeyEnvironment());
      retryCount = Integer.parseInt(ckmsSdkConfigProperties.getConnectionRetryCount());
      connectionTimeout = Integer.parseInt(ckmsSdkConfigProperties.getConnectionTimeout());
      client = createYkeyKeyClient(context);
    }
    ckmsInteractor = CkmsFacade.getCkmsInteractor(client);
    refresh();
    isInitialized.compareAndSet(false, true);
  }

  @PreDestroy
  public void destroy() {
    if (Objects.nonNull(keyRefresher)) {
      keyRefresher.shutdown();
    }

    if (Objects.nonNull(client)) {
      client.close();
    }
  }

  /**
   * Get the secret key value corresponding to the current/latest version from RKS(Remote Key
   * Server). This is a time consuming method if the key group was not loaded at the application
   * startup. When the group is included at the application startup, then the secrets are returned
   * from the cache. Cache is refreshed asynchronously every x minutes (default: 1h)
   *
   * @param keyName the name of the key you want retrieved
   * @param keyGroupName the key group that the key is part of
   * @return {@code String} containing the key value if key found, {@code null} otherwise
   */
  public String getSecret(final String keyName, final String keyGroupName) {
    if (!keyGroupsList.contains(keyGroupName)) {
      loadKeyGroup(keyGroupName);
    }
    return ckmsInteractor.getSecret(keyName, keyGroupName);
  }

  /** For the loaded key groups, it refreshes all the keys and their values. */
  public void refresh() {
    int retries = 0;

    while (retries <= retryCount) {
      try {
        ckmsInteractor.refresh();
        break;
      } catch (Exception e) {
        log.warn("Failed to get all the keys from CKMS - attempt {} of {}", retries, retryCount);
        if (retries >= retryCount) {
          log.error("Failed to fetch keyGroups: \'" + keyGroupsList.stream().toString() + "\'", e);
        }
        retries++;
      }
    }
  }

  /** Loads the key group and forces asynchronously cache refresh. */
  public void loadKeyGroup(final String keyGroup) {
    keyGroupsList.add(keyGroup);
    ckmsInteractor.loadKeyGroup(keyGroup);
    refresh();
  }

  protected YKeyKeyClient createYkeyKeyClient(SSLContext context) {
    try {
      YKeyKeyClient yKeyKeyClient =
          new YKeyKeyClient.YKeyKeyClientBuilder()
              .environment(yKeyKeyEnvironment)
              .sslContext(context)
              .connectTimeOutInMS(connectionTimeout)
              .cache(HOUR_REFRESH_INTERVAL_IN_MILLISECONDS)
              .build();
      yKeyKeyClient.setEntitiesToCache(
          keyGroupsList.stream().map(this::buildCachedEntity).toList());

      return yKeyKeyClient;
    } catch (Exception e) {
      log.error("Could not initialize CKMS", e);
    }
    return null;
  }

  protected SSLContext createSSLContext(
      final String trustStorePath, final String trustStorePassword, String certPath, String keyPath)
      throws KeyRefresherException, IOException, InterruptedException {
    createAndStartKeyRefresher(trustStorePath, trustStorePassword, certPath, keyPath);
    return getSslContext();
  }

  private void createAndStartKeyRefresher(
      String trustStorePath, String trustStorePassword, String certPath, String keyPath)
      throws IOException, InterruptedException, KeyRefresherException {
    keyRefresher =
        Utils.generateKeyRefresher(trustStorePath, trustStorePassword, certPath, keyPath);
    keyRefresher.startup();
  }

  private SSLContext getSslContext() throws KeyRefresherException {
    return Utils.buildSSLContext(
        keyRefresher.getKeyManagerProxy(), keyRefresher.getTrustManagerProxy());
  }

  private RequestedEntity buildCachedEntity(final String keyGroup) {
    return new RequestedEntity.Builder().keyGroup(keyGroup).onlyLatestVersion().build();
  }

  private YKeyKeyEnvironment getEnum(final String name) {
    YKeyKeyEnvironment environment = YKeyKeyEnvironment.aws; // default
    try {
      if (isNotEmpty(name)) {
        environment = YKeyKeyEnvironment.valueOf(name);
      }
    } catch (IllegalArgumentException e) {
      log.warn("Invalid enum configured for YKeyKeyEnviroment: {}, using default: aws", name, e);
    }
    return environment;
  }

  private String decodePassword(final String encodedPassword) {
    if (isNotEmpty(encodedPassword)) {
      byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword); // Base 64 Decoding
      return new String(decodedBytes);
    }
    return StringUtils.EMPTY;
  }

  private void storeDefaultKeyGroups(final String defaultKeyGroups) {
    if (StringUtils.isNotBlank(defaultKeyGroups)) {
      String[] keyGroups = defaultKeyGroups.split(",");
      for (String keyGroup : keyGroups) {
        keyGroupsList.add(keyGroup.trim());
      }
    }
  }
}
