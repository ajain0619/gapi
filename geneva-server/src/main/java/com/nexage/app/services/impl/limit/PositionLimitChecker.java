package com.nexage.app.services.impl.limit;

import com.ssp.geneva.common.base.annotation.Legacy;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Fake class to be used for recursive comparison. Methods under this class should be captured by
 * {@link PositionLimitInterceptor} that encapsulates the real checker. Spring AOP does not allow
 * self-invoke and the operation was being called that way, this is a workaround to allow the code
 * to avoid circular dependencies due to bad code development.
 */
@Legacy
@Log4j2
@Service
public class PositionLimitChecker {

  /**
   * Check User Rate Limits for positions on sites to see if the limit has been reached.
   *
   * @param publisherPid publisherPid
   * @param sitePid sitePid
   */
  public void checkLimitsPositionsInSite(long publisherPid, long sitePid) {
    log.info("checkLimitsPositionsInSite, publisherPid={}, sitePid={}", publisherPid, sitePid);
  }
}
