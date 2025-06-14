package com.nexage.app.util.assemblers.context;

import com.nexage.admin.core.model.Site;

public class PublisherPositionContext extends AssemblerContext {

  private final Site site;
  private final boolean isCopy;
  private final boolean detail;

  private PublisherPositionContext(Builder builder) {
    this.site = builder.site;
    this.isCopy = builder.isCopy;
    this.detail = builder.detail;
  }

  public final Site getSite() {
    return site;
  }

  public final boolean isCopyOperation() {
    return isCopy;
  }

  public boolean isDetail() {
    return detail;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Site site;
    private boolean isCopy;
    private boolean detail;

    public Builder withSite(Site site) {
      this.site = site;
      return this;
    }

    public Builder withDetail(boolean detail) {
      this.detail = detail;
      return this;
    }

    public Builder withCopyOperation(boolean type) {
      this.isCopy = type;
      return this;
    }

    public PublisherPositionContext build() {
      return new PublisherPositionContext(this);
    }
  }
}
