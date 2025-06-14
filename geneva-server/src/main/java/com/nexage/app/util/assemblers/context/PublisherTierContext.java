package com.nexage.app.util.assemblers.context;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;

public class PublisherTierContext extends AssemblerContext {

  private final Site site;
  private final Position position;

  private PublisherTierContext(Builder builder) {
    this.site = builder.site;
    this.position = builder.position;
  }

  public final Site getSite() {
    return site;
  }

  public final Position getPosition() {
    return position;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Site site;
    private Position position;

    public Builder withSite(Site site) {
      this.site = site;
      return this;
    }

    public Builder withPosition(Position position) {
      this.position = position;
      return this;
    }

    public PublisherTierContext build() {
      return new PublisherTierContext(this);
    }
  }
}
