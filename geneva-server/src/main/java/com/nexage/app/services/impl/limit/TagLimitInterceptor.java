package com.nexage.app.services.impl.limit;

import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerLimitService;
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
public class TagLimitInterceptor {
  private final SellerLimitService sellerLimitService;

  @Autowired
  public TagLimitInterceptor(SellerLimitService sellerLimitService) {
    this.sellerLimitService = sellerLimitService;
  }

  /**
   * This interceptor will act before the execution method is reached under the criteria.
   *
   * @param joinPoint {@link JoinPoint}
   * @param publisherPid sellerPid
   * @param sitePid sitePid
   * @param positionPid positionPid
   */
  @Before(
      value =
          "execution(* com.nexage.app.services.impl.limit.TagLimitChecker.checkLimitsTagsInPosition(..)) && args(publisherPid,sitePid,positionPid)",
      argNames = "joinPoint,publisherPid,sitePid,positionPid")
  public void canCreateTagsInPosition(
      JoinPoint joinPoint, long publisherPid, long sitePid, long positionPid) {
    log.debug("joinPoint={}", joinPoint);
    if (!sellerLimitService.canCreateTagsInPosition(publisherPid, sitePid, positionPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAGS_PER_POSITION_LIMIT_REACHED);
    }
  }
}
