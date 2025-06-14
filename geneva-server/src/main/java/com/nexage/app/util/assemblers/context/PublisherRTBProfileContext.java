package com.nexage.app.util.assemblers.context;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.app.dto.publisher.PublisherRTBProfileDTO;

public class PublisherRTBProfileContext extends AssemblerContext {

  protected Site site;
  protected Tag tag;
  protected Company company;
  protected PublisherRTBProfileDTO.Builder profileBuilder;

  public final Site getSite() {
    return site;
  }

  public final Tag getTag() {
    return tag;
  }

  public void setTag(Tag tag) {
    this.tag = tag;
  }

  public Company getCompany() {
    return company;
  }

  public PublisherRTBProfileDTO.Builder getProfileBuilder() {
    return profileBuilder;
  }

  public void setProfileBuilder(PublisherRTBProfileDTO.Builder profileBuilder) {
    this.profileBuilder = profileBuilder;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder<C extends PublisherRTBProfileContext, B extends Builder> {

    protected C context;
    protected B builder;

    public Builder() {
      this.context = (C) new PublisherRTBProfileContext();
      this.builder = (B) this;
    }

    public B withSite(Site site) {
      context.site = site;
      context.company = site.getCompany();
      return builder;
    }

    public B withTag(Tag tag) {
      context.tag = tag;
      return builder;
    }

    public B withCompany(Company company) {
      context.company = company;
      return builder;
    }

    public B withProfileBuilder(PublisherRTBProfileDTO.Builder profileBuilder) {
      context.profileBuilder = profileBuilder;
      return builder;
    }

    public PublisherRTBProfileContext build() {
      return context;
    }
  }
}
