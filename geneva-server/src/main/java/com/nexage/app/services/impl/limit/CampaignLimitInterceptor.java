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
public class CampaignLimitInterceptor {
  private final SellerLimitService sellerLimitService;

  @Autowired
  public CampaignLimitInterceptor(SellerLimitService sellerLimitService) {
    this.sellerLimitService = sellerLimitService;
  }

  /**
   * This interceptor will act before the execution method is reached under the criteria.
   *
   * @param joinPoint {@link JoinPoint}
   * @param sellerPid sellerPid
   */
  @Before(
      value =
          "execution(* com.nexage.app.services.impl.limit.CampaignLimitChecker.checkLimitsCampaign(..)) && args(sellerPid)",
      argNames = "joinPoint,sellerPid")
  public void canCreateCampaigns(JoinPoint joinPoint, Long sellerPid) {
    log.debug("joinPoint={}", joinPoint);
    if (sellerLimitService.isLimitEnabled(sellerPid)
        && !sellerLimitService.canCreateCampaigns(sellerPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_LIMIT_REACHED);
    }
  }
}
