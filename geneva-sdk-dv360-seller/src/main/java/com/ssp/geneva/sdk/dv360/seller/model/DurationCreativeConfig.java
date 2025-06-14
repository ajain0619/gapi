package com.ssp.geneva.sdk.dv360.seller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.sdk.dv360.seller.model.type.DurationMatchType;
import com.ssp.geneva.sdk.dv360.seller.model.type.SkippableMatchType;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class DurationCreativeConfig implements Serializable {
  @EqualsAndHashCode.Include private String duration;
  @EqualsAndHashCode.Include private DurationMatchType durationMatchType;
  @EqualsAndHashCode.Include private SkippableMatchType skippableMatchType;
}
