package com.nexage.app.web.report;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.dw.xstream.XmlReportDefinition;
import com.nexage.app.services.ReportingService;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@RestController
@Profile({"default", "aws"})
public class VerticaReportController {

  private final ReportingService reportingService;

  public VerticaReportController(ReportingService reportingService) {
    this.reportingService = reportingService;
  }

  @Timed
  @ExceptionMetered
  @RequestMapping(value = "/reports/metadata", method = RequestMethod.GET)
  public List<XmlReportDefinition> getReportDefinitions() {
    return reportingService.getAllReportDefinitions();
  }

  @Timed
  @ExceptionMetered
  @RequestMapping(value = "/reports/metadata/{id}", method = RequestMethod.GET)
  public XmlReportDefinition getReportDefinitions(@PathVariable(value = "id") String id) {
    return reportingService.getReportDefinition(id);
  }
}
