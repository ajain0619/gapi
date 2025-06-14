package com.nexage.app.util.assemblers.context;

public final class NullableContext extends AssemblerContext {

  public static final NullableContext nullableContext = NullableContext.newBuilder().build();

  private NullableContext() {}

  private NullableContext(Builder builder) {}

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    public NullableContext build() {
      return new NullableContext(this);
    }
  }
}
