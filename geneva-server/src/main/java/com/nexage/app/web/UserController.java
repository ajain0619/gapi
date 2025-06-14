package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.model.User;
import com.nexage.app.services.UserService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/users")
@RestController
@RequestMapping(value = "/users")
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @Timed
  @ExceptionMetered
  @PostMapping(value = "/{userPID}/changePasswd")
  public void changePassword(
      @PathVariable(value = "userPID") long userPid,
      @RequestParam(value = "oldPass") String oldPasswd,
      @RequestParam(value = "newPass") String newPasswd) {

    service.changePassword(userPid, oldPasswd, newPasswd);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(params = {"companyPID"})
  public List<User> getAllUsersByCompanyPid(
      @RequestParam(value = "companyPID", required = true) long companyPid) {
    return service.getAllUsersByCompanyPid(companyPid);
  }

  @Timed
  @ExceptionMetered
  @PostMapping(value = "/{userPID}/restrictAccessToSite")
  public void restrictUserAccessToSites(
      @PathVariable(value = "userPID") long userPid,
      @RequestParam(value = "sitePID") List<Long> sitePids) {

    service.restrictUserAccessToSites(userPid, sitePids);
  }

  @Timed
  @ExceptionMetered
  @PostMapping(value = "/{userPID}/allowAccessToSite")
  public void allowUserAccessToSites(
      @PathVariable(value = "userPID") long userPid,
      @RequestParam(value = "sitePID") List<Long> sitePids) {

    service.allowUserAccessToSites(userPid, sitePids);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/{userPID}")
  public ResponseEntity deleteUser(@PathVariable(value = "userPID") long userPid) {
    service.deleteUser(userPid);
    return ResponseEntity.noContent().build();
  }
}
