package com.nexage.app.dto;

import com.nexage.admin.dw.util.DateUtil;
import com.nexage.dw.geneva.util.ISO8601Util;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class BaseReportRequestDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  protected String start;

  protected String stop;

  protected FilterParam dim;

  protected Map<String, Object> filterParams = new HashMap<>();

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    try {
      Date d = ISO8601Util.parse(start);
      this.start = DateUtil.format(d);
    } catch (ParseException e) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  public String getStop() {
    return stop;
  }

  public final void setStop(String stop) {
    try {
      Date d = ISO8601Util.parse(stop);

      Calendar cal = new GregorianCalendar();
      cal.setTime(d);
      if (cal.get(Calendar.HOUR_OF_DAY) == 23 && cal.get(Calendar.MINUTE) == 59) {

        cal.add(Calendar.DAY_OF_MONTH, 1);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        d = cal.getTime();
      }

      this.stop = DateUtil.format(d);
    } catch (ParseException e) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  public FilterParam getDim() {
    return dim;
  }

  public void setDim(FilterParam dim) {
    this.dim = dim;
  }

  public Map<String, Object> getFilterParams() {
    return filterParams;
  }

  public void setFilterParams(Map<String, Object> filterParams) {
    this.filterParams = filterParams;
  }

  protected final void addToFilterParams(String key, Object value) {
    this.filterParams.put(key, value);
  }
}
