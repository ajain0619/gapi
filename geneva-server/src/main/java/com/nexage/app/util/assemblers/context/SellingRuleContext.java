package com.nexage.app.util.assemblers.context;

import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.model.RuleDeployedSite;
import java.util.Set;

public class SellingRuleContext extends AssemblerContext {

  private Set<RuleDeployedSite> sites;
  private Set<RuleDeployedPosition> positions;
  private Set<RuleDeployedCompany> publishers;

  private SellingRuleContext(Builder builder) {
    this.sites = builder.sites;
    this.positions = builder.positions;
    this.publishers = builder.publishers;
  }

  public final Set<RuleDeployedSite> getSites() {
    return sites;
  }

  public final Set<RuleDeployedPosition> getPositions() {
    return positions;
  }

  public final Set<RuleDeployedCompany> getPublishers() {
    return publishers;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Set<RuleDeployedSite> sites;
    private Set<RuleDeployedPosition> positions;
    private Set<RuleDeployedCompany> publishers;

    public Builder withSitesForSellingRule(Set<RuleDeployedSite> sites) {
      this.sites = sites;
      return this;
    }

    public Builder withPositionsForSellingRule(Set<RuleDeployedPosition> positions) {
      this.positions = positions;
      return this;
    }

    public Builder withPublishersForSellingRule(Set<RuleDeployedCompany> publishers) {
      this.publishers = publishers;
      return this;
    }

    public SellingRuleContext build() {
      return new SellingRuleContext(this);
    }
  }
}
