package com.nexage.app.services.crs;

import com.aol.crs.cdk.CdkClient;
import com.aol.crs.cdk.SyncedConfig;
import com.codahale.metrics.MetricRegistry;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@Log4j2
@Getter
@ManagedResource(description = "CdkClient Managed Resource Selector")
public class CdkClientResource {

  private final SyncedConfig syncedConfig;
  private final MetricRegistry metricRegistry;
  private volatile CdkClient cdkClient;

  @Autowired
  public CdkClientResource(
      @NotNull SyncedConfig syncedConfig, @NotNull MetricRegistry metricRegistry) {
    this.syncedConfig = syncedConfig;
    this.metricRegistry = metricRegistry;
  }

  @PostConstruct
  public void init() {
    initialize(false);
  }

  @PreDestroy
  public void close() {
    closeCdkClient(true);
  }

  @ManagedOperation(description = "Reload CdkClient Implementation")
  public void reloadCdkClient() {
    initialize(false);
  }

  @ManagedOperation(description = "Destroy and Reload CdkClient Implementation")
  public void destroyAndReloadCdkClient() {
    initialize(true);
  }

  public CdkClient getCdkClient() {
    if (this.cdkClient == null) {
      initialize(false);
    }
    return this.cdkClient;
  }

  private void initialize(final boolean destroyPersistence) {
    createCdkClient(destroyPersistence);
  }

  private void createCdkClient(final boolean destroyPersistence) {
    if (cdkClient != null) {
      closeCdkClient(destroyPersistence);
    }
    try {
      this.cdkClient =
          new CdkClient(
              this.syncedConfig,
              new DefaultAsyncHttpClientConfig.Builder().build(),
              this.metricRegistry);
    } catch (Exception e) {
      log.error("Unable to initialize CdkClient", e);
    }
  }

  private void closeCdkClient(boolean destroyPersistence) {
    try {
      CdkClient cdkClient2 = cdkClient;
      cdkClient = null;
      if (cdkClient2 != null) {
        cdkClient2.close(destroyPersistence);
      }
    } catch (Exception ex) {
      log.error("Unable to close CdkClient", ex);
    }
  }
}
