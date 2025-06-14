package com.nexage.app.services;

import com.nexage.admin.dw.xstream.XmlReportDefinition;
import java.util.List;

/** Defines an interface to get reports from dwdb */
public interface ReportingService {

  List<XmlReportDefinition> getAllReportDefinitions();

  XmlReportDefinition getReportDefinition(String id);
}
