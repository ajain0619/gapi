package com.ssp.geneva.common.cache;

import javax.servlet.http.HttpServletRequest;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

/** Created by arjunmudan on 3/14/16. */
public class SinglePageCachingFilterNoQueryParams extends SimplePageCachingFilter {
  @Override
  protected String calculateKey(HttpServletRequest request) {
    var sb = new StringBuilder();
    sb.append(request.getMethod()).append(request.getRequestURI());
    return sb.toString();
  }
}
