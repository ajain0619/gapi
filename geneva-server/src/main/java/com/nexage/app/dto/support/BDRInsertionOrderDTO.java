package com.nexage.app.dto.support;

import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import java.util.HashSet;
import java.util.Set;

/**
 * Dto for BdrInsertionOrder {@link BdrInsertionOrder}
 *
 * @author Eugeny Yurko
 * @since 10.09.2014
 */
public class BDRInsertionOrderDTO extends BaseDTO {
  private Set<BDRLineItemDTO> lineItems = new HashSet<>();

  protected BDRInsertionOrderDTO(Builder builder) {
    super(builder);
    lineItems = builder.lineItems;
  }

  public Set<BDRLineItemDTO> getLineItems() {
    return lineItems;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder extends BaseDTO.Builder {
    private Set<BDRLineItemDTO> lineItems;

    protected Builder() {
      super();
    }

    public Builder withLineItems(Set<BDRLineItemDTO> lineItems) {
      this.lineItems = lineItems;
      return this;
    }

    @Override
    public BDRInsertionOrderDTO build() {
      return new BDRInsertionOrderDTO(this);
    }
  }
}
