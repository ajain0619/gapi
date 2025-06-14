package com.ssp.geneva.server.report.report;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import java.util.List;
import org.springframework.dao.DataAccessException;

public abstract class Report<R extends ReportRequest, T extends ReportResponse> {

  protected abstract List<T> getReportData(R request) throws ReportException, DataAccessException;

  /**
   * This method is used to create a map key based on the reports dim parameter. If not set, a key
   * of "dim=default" is generated.
   *
   * @param dim ReportDimension implementation
   * @return map key to get query.
   */
  protected String getReportKeyByDimension(ReportDimension dim) {
    if (dim != null) {
      return ReportKeys.DIMENSION + "=" + dim.getName();
    } else {
      return ReportKeys.DEFAULT;
    }
  }
}
