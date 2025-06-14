package com.nexage.geneva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectFile;

import java.io.PrintWriter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;

@Log4j2
@ContextConfiguration("classpath:cucumber.xml")
@Profile({"custom-runner", "!global-runner"})
public class CustomTestRunner {
  @Test
  void runSingleTest() {
    LauncherDiscoveryRequest request =
        LauncherDiscoveryRequestBuilder.request()
            .configurationParameter(
                "cucumber.plugin",
                "pretty,html:target/cucumber-custom.html,json:target/cucumber-custom.json,timeline:target/timeline")
            .selectors(
                selectFile(
                    "src/test/resources/com/nexage/geneva/identityprovider/1_identity_provider.feature"))
            .build();
    Launcher launcher = LauncherFactory.create();

    SummaryGeneratingListener listener = new SummaryGeneratingListener();
    launcher.registerTestExecutionListeners(listener);

    launcher.execute(request);
    TestExecutionSummary summary = listener.getSummary();
    log.info("Total tests: {}", summary.getTestsFoundCount());
    log.info("Test success: {}", summary.getTestsSucceededCount());
    log.info("Test failed: {}", summary.getTestsFailedCount());
    log.info("Test ignored: {}", summary.getTestsSkippedCount());
    log.info("Test aborted: {}", summary.getTestsAbortedCount());
    log.info("Test started: {}", summary.getTestsStartedCount());
    if (summary.getTestsFailedCount() > 0) {
      summary.printFailuresTo(new PrintWriter(System.out));
    }
    assertEquals(0, summary.getTestsFailedCount(), "Test failed:" + summary.getTestsFailedCount());
  }
}
