package com.nexage.app.util.assemblers.context;

public class PublisherBuyerContext extends AssemblerContext {

  private final boolean enabledForPublisher;

  private PublisherBuyerContext(Builder builder) {
    enabledForPublisher = builder.enabledForPublisher;
  }

  public boolean isEnabledForPublisher() {
    return enabledForPublisher;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private boolean enabledForPublisher = false;

    public Builder enabledForPublisher(Boolean enabledForPublisher) {
      this.enabledForPublisher = enabledForPublisher;
      return this;
    }

    public PublisherBuyerContext build() {
      return new PublisherBuyerContext(this);
    }
  }
}
