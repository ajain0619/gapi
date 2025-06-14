package com.nexage.app.security.test;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Profile("e2e-test")
@RequestMapping("/security-tests")
public class SecurityTestController {

  private final SecurityTestService securityTestService;

  @GetMapping("/self-serve/{sellerPid}")
  public ResponseEntity selfServeEnabled(@PathVariable Long sellerPid) {
    securityTestService.testSelfServe(sellerPid);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/nexage-manager-or-seller-user/{sellerPid}")
  public ResponseEntity nexageManagerOrSellerUser(@PathVariable long sellerPid) {
    securityTestService.testNexageManagerOrSellerUser(sellerPid);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/nexage-user-or-seller-user/{sellerPid}")
  public ResponseEntity nexageUserOrSellerUser(@PathVariable long sellerPid) {
    securityTestService.testNexageUserOrSellerUser(sellerPid);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/nexage-manager-or-seller-manager/{sellerPid}")
  public ResponseEntity nexageManagerOrSellerManager(@PathVariable long sellerPid) {
    securityTestService.testNexageManagerOrSellerManager(sellerPid);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/nexage-manager-or-seller-manager-2/{sellerPid}")
  public ResponseEntity nexageManagerOrSellerManagerAnotherMethod(@PathVariable long sellerPid) {
    securityTestService.testNexageManagerOrSellerManagerAnotherMethod(sellerPid);
    return ResponseEntity.ok().build();
  }
}
