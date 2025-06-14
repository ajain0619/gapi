package com.ssp.geneva.server.report.report.velocity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.log4j.Log4j2;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

@Log4j2
public class CachedStringResourceRepository implements StringResourceRepository {

  private static final String DEFAULT_SPEC = "maximumSize=1000";
  private static final String PROPERTY_KEY = "velocity.template.stringcache.spec";

  private String encoding = RuntimeConstants.ENCODING_DEFAULT;
  private Cache<String, StringResource> cache;

  public CachedStringResourceRepository() {
    String initSpec = System.getProperty(PROPERTY_KEY, DEFAULT_SPEC);
    log.debug("{}: {}", PROPERTY_KEY, initSpec);
    cache = CacheBuilder.from(initSpec).build();
  }

  /** @see StringResourceRepository#getStringResource(java.lang.String) */
  public StringResource getStringResource(final String name) {
    return cache.getIfPresent(name);
  }

  /** @see StringResourceRepository#putStringResource(java.lang.String, java.lang.String) */
  public void putStringResource(final String name, final String body) {
    cache.put(name, new StringResource(body, getEncoding()));
  }

  /**
   * @see StringResourceRepository#putStringResource(java.lang.String, java.lang.String,
   *     java.lang.String)
   * @since 1.6
   */
  public void putStringResource(final String name, final String body, final String encoding) {
    cache.put(name, new StringResource(body, encoding));
  }

  /** @see StringResourceRepository#removeStringResource(java.lang.String) */
  public void removeStringResource(final String name) {
    cache.invalidate(name);
  }

  /** @see org.apache.velocity.runtime.resource.util.StringResourceRepository#getEncoding() */
  public String getEncoding() {
    return encoding;
  }

  /**
   * @see
   *     org.apache.velocity.runtime.resource.util.StringResourceRepository#setEncoding(java.lang.String)
   */
  public void setEncoding(final String encoding) {
    this.encoding = encoding;
  }
}
