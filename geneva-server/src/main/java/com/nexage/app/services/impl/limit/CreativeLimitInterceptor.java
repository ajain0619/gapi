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
public class CreativeLimitInterceptor {
  private final SellerLimitService sellerLimitService;

  @Autowired
  public CreativeLimitInterceptor(SellerLimitService sellerLimitService) {
    this.sellerLimitService = sellerLimitService;
  }

  /**
   * This interceptor will act before the execution method is reached under the criteria.
   *
   * @param joinPoint {@link JoinPoint}
   * @param sellerPid sellerPid
   * @param campaignPid campaignPid
   */
  @Before(
      value =
          "execution(* com.nexage.app.services.impl.limit.CreativeLimitChecker.checkLimitsCreativeInCampaign(..)) && args(sellerPid,campaignPid)",
      argNames = "joinPoint,sellerPid,campaignPid")
  public void canCreateCreativesInCampaign(JoinPoint joinPoint, Long sellerPid, Long campaignPid) {
    log.debug("joinPoint={}", joinPoint);
    if (sellerLimitService.isLimitEnabled(sellerPid)
        && !sellerLimitService.canCreateCreativesInCampaign(sellerPid, campaignPid)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_CREATIVES_PER_CAMPAIGN_LIMIT_REACHED);
    }
  }
}
