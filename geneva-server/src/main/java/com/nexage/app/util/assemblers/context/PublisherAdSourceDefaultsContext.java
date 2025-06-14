package com.nexage.app.util.assemblers.context;

public class PublisherAdSourceDefaultsContext extends AssemblerContext {

  private PublisherAdSourceDefaultsContext(Builder builder) {}

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    public PublisherAdSourceDefaultsContext build() {
      return new PublisherAdSourceDefaultsContext(this);
    }
  }
}
