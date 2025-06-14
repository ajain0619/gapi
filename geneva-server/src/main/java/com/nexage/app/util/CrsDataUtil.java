package com.nexage.app.util;

import com.nexage.app.services.CrsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component
public class CrsDataUtil {
  private final CrsService crsService;
}
