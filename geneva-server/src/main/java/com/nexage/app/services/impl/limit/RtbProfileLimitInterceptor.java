package com.nexage.app.services.impl.limit;

import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO.ItemType;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.RtbProfileLibrarySellerLimitService;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Legacy
@Log4j2
@Aspect
@Service
public class RtbProfileLimitInterceptor {
  private final RtbProfileLibrarySellerLimitService rtbProfileLibrarySellerLimitService;

  @Autowired
  public RtbProfileLimitInterceptor(
      RtbProfileLibrarySellerLimitService rtbProfileLibrarySellerLimitService) {
    this.rtbProfileLibrarySellerLimitService = rtbProfileLibrarySellerLimitService;
  }

  /**
   * This interceptor will act before the execution method is reached under the criteria.
   *
   * @param joinPoint {@link JoinPoint}
   * @param publisherPid sellerPid
   * @param group {@link PublisherRTBProfileGroupDTO}
   */
  @Before(
      value =
          "execution(* com.nexage.app.services.impl.limit.RtbProfileLimitChecker.checkLimitsGroup(..)) && args(publisherPid,group)",
      argNames = "joinPoint,publisherPid,group")
  public void checkLimitsGroup(
      JoinPoint joinPoint, Long publisherPid, PublisherRTBProfileGroupDTO group) {
    log.info("{}.{}.{}", joinPoint, publisherPid, group);
    checkLimits(publisherPid, group);
  }

  /**
   * This interceptor will act before the execution method is reached under the criteria.
   *
   * @param joinPoint {@link JoinPoint}
   * @param publisherPid sellerPid
   * @param library {@link PublisherRTBProfileGroupDTO}
   */
  @Before(
      value =
          "execution(* com.nexage.app.services.impl.limit.RtbProfileLimitChecker.checkLimitsLibrary(..)) && args(publisherPid,library)",
      argNames = "joinPoint,publisherPid,library")
  public void checkLimitsLibrary(
      JoinPoint joinPoint, Long publisherPid, PublisherRTBProfileLibraryDTO library) {
    log.info("{}.{}.{}", joinPoint, publisherPid, library);
    checkLimits(publisherPid, library);
  }

  private void checkLimits(Long publisher, PublisherRTBProfileGroupDTO group) {
    if (group.getItemType() == ItemType.BIDDER) {
      if (!rtbProfileLibrarySellerLimitService.canCreateBidderGroups(publisher)) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_LIMIT_REACHED);
      }
    } else if (group.getItemType() == ItemType.CATEGORY
        || group.getItemType() == ItemType.ADOMAIN) {
      if (!rtbProfileLibrarySellerLimitService.canCreateBlockGroups(publisher)) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_LIMIT_REACHED);
      }
    }
  }

  private void checkLimits(Long publisherPid, PublisherRTBProfileLibraryDTO library) {
    library.getGroups().stream().findFirst().ifPresent(group -> checkLimits(publisherPid, group));
  }
}
