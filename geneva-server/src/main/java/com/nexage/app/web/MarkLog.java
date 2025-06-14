package com.nexage.app.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class MarkLog {

  @RequestMapping("/marklog")
  @ResponseStatus(value = HttpStatus.OK)
  void handler(@RequestParam final String m) {
    log.info("MARK : {}", m);
  }
}
