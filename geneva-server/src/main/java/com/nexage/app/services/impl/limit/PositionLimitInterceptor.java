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
public class PositionLimitInterceptor {
  private final SellerLimitService sellerLimitService;

  @Autowired
  public PositionLimitInterceptor(SellerLimitService sellerLimitService) {
    this.sellerLimitService = sellerLimitService;
  }

  /**
   * This interceptor will act before the execution method is reached under the criteria.
   *
   * @param joinPoint {@link JoinPoint}
   * @param publisherPid sellerPid
   * @param sitePid sitePid
   */
  @Before(
      value =
          "execution(* com.nexage.app.services.impl.limit.PositionLimitChecker.checkLimitsPositionsInSite(..)) && args(publisherPid,sitePid)",
      argNames = "joinPoint,publisherPid,sitePid")
  public void canCreatePositionsInSite(JoinPoint joinPoint, long publisherPid, long sitePid) {
    log.debug("joinPoint={}", joinPoint);
    if (!sellerLimitService.canCreatePositionsInSite(publisherPid, sitePid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_LIMIT_REACHED);
    }
  }
}
