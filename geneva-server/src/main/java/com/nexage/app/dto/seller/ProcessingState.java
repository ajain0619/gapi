package com.nexage.app.dto.seller;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** DTO for Seller Attribute Domain Verification status */
@Getter
@RequiredArgsConstructor
public class ProcessingState<T> {
  @NotNull private final T status;
}
