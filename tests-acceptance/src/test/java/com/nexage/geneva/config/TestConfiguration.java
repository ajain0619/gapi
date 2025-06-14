package com.nexage.geneva.config;

import com.nexage.geneva.config.properties.GenevaCrudProperties;
import com.nexage.geneva.database.RestoreDatabaseUtils;
import com.nexage.geneva.wiremock.WiremockUtils;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Log4j2
@Configuration("testConfiguration")
public class TestConfiguration {
  @Autowired private WiremockUtils wiremockUtils;
  @Autowired private GenevaCrudProperties genevaCrudProperties;
  @Autowired private RestoreDatabaseUtils restoreDatabaseUtils;
  private TestSuiteProperties testSuiteProperties;

  private static final String RESTORE_AFTER_SHUTDOWN = "restoreAfterShutdown";

  @Bean(name = "testSuiteProperties", initMethod = "init")
  public TestSuiteProperties getTestSuiteProperties() {
    testSuiteProperties = new TestSuiteProperties(genevaCrudProperties);
    return testSuiteProperties;
  }

  /**
   * we can use 'restoreAfterShutdown' flag as JVM parameter (ex.: -DrestoreAfterShutdown=false) If
   * it set to true, restoring will be automatically run after tests passed Default value is true
   */
  @PreDestroy
  public void finalRestore() {
    String allowRestoreAfterShutdown = System.getProperty(RESTORE_AFTER_SHUTDOWN);
    if (StringUtils.isEmpty(allowRestoreAfterShutdown)
        || allowRestoreAfterShutdown.equals("true")) {
      restoreDatabaseUtils.restoreCrudCoreDatabase();
      restoreDatabaseUtils.restoreCrudDWDatabase();
    }
  }

  //    reload db before running any test, only applies for reporting api & reports test
  @PostConstruct
  public void initialRestore() {
    restoreDatabaseUtils.restoreCrudCoreDatabase();
    restoreDatabaseUtils.restoreCrudDWDatabase();
  }

  @PostConstruct
  public void startWiremockServer() {
    wiremockUtils.startWiremock(genevaCrudProperties);
  }

  @PreDestroy
  public void stopWiremockServer() {
    //	wiremockUtils.stopWiremock();
  }
}
