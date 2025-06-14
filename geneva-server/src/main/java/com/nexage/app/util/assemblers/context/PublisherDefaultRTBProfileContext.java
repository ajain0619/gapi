package com.nexage.app.util.assemblers.context;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PublisherDefaultRTBProfileContext extends PublisherRTBProfileContext {

  private List<Site> sites;
  private List<Position> positions;
  private Boolean isPublisherDefault;

  public final List<Site> getRTBProfileSites() {
    return sites;
  }

  public final List<Position> getRTBProfilePositions() {
    return positions;
  }

  public Boolean getIsPublisherDefault() {
    return isPublisherDefault;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder
      extends PublisherRTBProfileContext.Builder<PublisherDefaultRTBProfileContext, Builder> {

    public Builder() {
      this.context = new PublisherDefaultRTBProfileContext();
      this.builder = this;
    }

    public Builder withSitesForRTBProfile(List<Site> sites) {
      context.sites = sites;
      return builder;
    }

    public Builder withPositionsForRTBProfile(List<Position> positions) {
      context.positions = positions;
      return builder;
    }

    public Builder withIsPublisherDefault(Boolean isPublisherDefault) {
      context.isPublisherDefault = isPublisherDefault;
      return builder;
    }

    public PublisherDefaultRTBProfileContext build() {
      return context;
    }
  }
}
