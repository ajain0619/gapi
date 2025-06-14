package com.nexage.app.util.assemblers.context;

import com.nexage.admin.core.model.Company;

public class CompanyContext extends AssemblerContext {

  private final Company company;

  private CompanyContext(Builder builder) {
    this.company = builder.company;
  }

  public final Company getCompany() {
    return company;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Company company;

    public Builder withCompany(Company company) {
      this.company = company;
      return this;
    }

    public CompanyContext build() {
      return new CompanyContext(this);
    }
  }
}
