package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryMdmIdDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Include @EqualsAndHashCode.Include private Long sellerPid;

  @ToString.Include @EqualsAndHashCode.Include private Set<String> companyMdmIds;

  @ToString.Include @EqualsAndHashCode.Include private Set<String> sellerSeatMdmIds;
}
