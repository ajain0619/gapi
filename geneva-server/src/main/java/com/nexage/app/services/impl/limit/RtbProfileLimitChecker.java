package com.nexage.app.services.impl.limit;

import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.ssp.geneva.common.base.annotation.Legacy;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Fake class to be used for recursive comparison. Methods under this class should be captured by
 * {@link RtbProfileLimitChecker} that encapsulates the real checker. Spring AOP does not allow
 * self-invoke and the operation was being called that way, this is a workaround to allow the code
 * to avoid circular dependencies due to bad code development.
 */
@Legacy
@Log4j2
@Service
public class RtbProfileLimitChecker {

  /**
   * Check User Rate Limits for {@link PublisherRTBProfileGroupDTO} to see if the limit has been
   * reached.
   *
   * @param publisherPid sellerPid
   * @param group {@link PublisherRTBProfileGroupDTO}
   */
  public void checkLimitsGroup(Long publisherPid, PublisherRTBProfileGroupDTO group) {
    log.info("checkLimitsGroup, publisherPid={}, group={}", publisherPid, group);
  }

  /**
   * Check User Rate Limits for {@link PublisherRTBProfileLibraryDTO} to see if the limit has been
   * reached.
   *
   * @param publisherPid sellerPid
   * @param library {@link PublisherRTBProfileLibraryDTO}
   */
  public void checkLimitsLibrary(Long publisherPid, PublisherRTBProfileLibraryDTO library) {
    log.info("checkLimitsLibrary, publisherPid={}, library={}", publisherPid, library);
  }
}
