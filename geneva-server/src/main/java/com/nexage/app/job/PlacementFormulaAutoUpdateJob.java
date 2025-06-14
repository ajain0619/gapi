package com.nexage.app.job;

import com.nexage.app.util.UserAuthenticationUtils;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetailsService;

@Log4j2
public class PlacementFormulaAutoUpdateJob {

  private final UserDetailsService userDetailsService;
  private final PlacementFormulaUpdateService ruleFormulaUpdateService;
  private final String jobUserName;

  public PlacementFormulaAutoUpdateJob(
      UserDetailsService userDetailsService,
      PlacementFormulaUpdateService ruleFormulaUpdateService,
      String jobUserName) {
    this.userDetailsService = userDetailsService;
    this.ruleFormulaUpdateService = ruleFormulaUpdateService;
    this.jobUserName = jobUserName;
  }

  public synchronized boolean runJob() {
    long start = System.currentTimeMillis();

    try {
      log.debug("Authenticating placement formula auto update job...");
      UserAuthenticationUtils.setAuthenticationForJob(userDetailsService, jobUserName);

      boolean ruleResults = execAutoUpdateTask(ruleFormulaUpdateService);

      return ruleResults;
    } catch (Exception e) {
      log.error("Placement formula auto update job failed because: {}", e.getMessage(), e);
      return false;
    } finally {
      log.debug("Clearing authentication for placement formula auto update job...");
      UserAuthenticationUtils.clearAuthentication();
      log.info(
          "Placement formula auto update job took {} ms to finish.",
          System.currentTimeMillis() - start);
    }
  }

  private boolean execAutoUpdateTask(PlacementFormulaUpdateService updateService) {
    PlacementFormulaAutoUpdateMetrics metrics = null;
    long start = System.currentTimeMillis();
    try {
      List<Long> pids = updateService.findAllToUpdate();
      metrics =
          new PlacementFormulaAutoUpdateMetrics(System.currentTimeMillis() - start, pids.size());

      for (Long pid : pids) {
        updateService.tryUpdate(pid, metrics);
      }

      return metrics.getErrors() == 0;
    } catch (Exception e) {
      log.error(
          "Placement formula auto update job failed for {} because: {}",
          updateService.getEntityType(),
          e.getMessage(),
          e);
      return false;
    } finally {
      if (metrics != null) {
        metrics.setTotalTime(System.currentTimeMillis() - start);
        logAutoUpdateMetrices(updateService.getEntityType(), metrics);
      }
    }
  }

  private void logAutoUpdateMetrices(String entityType, PlacementFormulaAutoUpdateMetrics metrics) {
    log.info("============ Summary of placement formula auto update job for {}", entityType);
    log.info(
        "Job took {} ms to find {} objects to update",
        metrics.getFindAllTime(),
        metrics.getTotalFound());
    if (metrics.getTotalFound() > 0) {
      log.info(
          "It took {} ms in total to load {} individual objects",
          metrics.getFindTime(),
          metrics.getLoaded());
      log.info(
          "It took {} ms in total to search for placements using formula for {} objects",
          metrics.getSearchTime(),
          metrics.getSearched());
      log.info(
          "A total of {} placements were found for the {} objects",
          metrics.getTotalFoundPlacements(),
          metrics.getSearched());
      log.info(
          "{} objects needed update while there were no changes to {} objects",
          metrics.getChanged(),
          metrics.getNotChanged());
      if (metrics.getChanged() > 0) {
        log.info(
            "It took {} ms in total to successfully update {} objects",
            metrics.getUpdateTime(),
            metrics.getUpdated());
      }
      log.info(
          "There were {} errors (see logs) and {} optimistic locking warnings",
          metrics.getErrors(),
          metrics.getWarnings());
    }
    log.info("Auto update task for {} took {} ms in total", entityType, metrics.getTotalTime());
  }
}
