package com.nexage.app.dto.seller.nativeads;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(as = NativePlacementExtensionDTO.class)
public class NativePlacementExtensionDTO extends BaseNativePlacementExtensionDTO {}
