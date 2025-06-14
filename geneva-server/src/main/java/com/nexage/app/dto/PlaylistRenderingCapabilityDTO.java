package com.nexage.app.dto;

import com.nexage.admin.core.model.PlaylistRenderingCapability;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Data transfer representation of {@link PlaylistRenderingCapability} */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlaylistRenderingCapabilityDTO implements Serializable {

  private static final long serialVersionUID = -645453930773279417L;

  @EqualsAndHashCode.Include @ToString.Include private String value;

  @ToString.Include private String displayValue;
}
