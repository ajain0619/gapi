package com.nexage.app.util.assemblers.context;

import com.nexage.admin.core.model.Site;

public class PublisherTagContext extends AssemblerContext {

  private final Site site;
  private final boolean isCopy;
  private final boolean isCopyAcrossSite;
  private final boolean forDefaultRTBProfile;

  private PublisherTagContext(Builder builder) {
    this.site = builder.site;
    this.isCopy = builder.isCopy;
    this.isCopyAcrossSite = builder.isCopyAcrossSite;
    this.forDefaultRTBProfile = builder.forDefaultRTBProfile;
  }

  public final Site getSite() {
    return site;
  }

  public final boolean isCopyOperation() {
    return isCopy;
  }

  public final boolean isCopyOperationAcrossSite() {
    return isCopyAcrossSite;
  }

  public final boolean forDefaultRTBProfile() {
    return forDefaultRTBProfile;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Site site;
    private boolean isCopy;
    private boolean isCopyAcrossSite;
    private boolean forDefaultRTBProfile;

    public Builder withSite(Site site) {
      this.site = site;
      return this;
    }

    public Builder withCopyOperation(boolean type) {
      this.isCopy = type;
      return this;
    }

    public Builder forRTBProfile(boolean forRTBProfile) {
      this.forDefaultRTBProfile = forRTBProfile;
      return this;
    }

    public Builder withCopyAcrossSite(boolean flag) {
      this.isCopyAcrossSite = flag;
      return this;
    }

    public PublisherTagContext build() {
      return new PublisherTagContext(this);
    }
  }
}
