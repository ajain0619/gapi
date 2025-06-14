package com.nexage.app.security.test;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Profile("e2e-test")
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller()")
public class SecurityTestService {

  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#sellerPid)")
  public void testSelfServe(Long sellerPid) {}

  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  public void testNexageManagerOrSellerUser(Long sellerPid) {}

  @PreAuthorize(
      "@loginUserContext.isOcManagerNexage() "
          + "or (@loginUserContext.doSameOrNexageAffiliation(#sellerPid) and @loginUserContext.isOcManagerSeller())")
  public void testNexageManagerOrSellerManager(long sellerPid) {}

  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() "
          + "or (@loginUserContext.doSameOrNexageAffiliation(#sellerPid) and @loginUserContext.isOcUserSeller())")
  public void testNexageUserOrSellerUser(long sellerPid) {}

  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()) "
          + "or (@loginUserContext.doSameOrNexageAffiliation(#sellerPid) and (@loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()))")
  public void testNexageManagerOrSellerManagerAnotherMethod(Long sellerPid) {}
}
