package com.ssp.geneva.sdk.dv360.seller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.sdk.dv360.seller.model.type.CreativeType;
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
public class CreativeConfig implements Serializable {
  @EqualsAndHashCode.Include private CreativeType creativeType;
  @EqualsAndHashCode.Include private DimensionCreativeConfig dimensionCreativeConfig;
  @EqualsAndHashCode.Include private DurationCreativeConfig durationCreativeConfig;
}
