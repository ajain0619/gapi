package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Data transfer representation of {@link com.nexage.admin.core.model.BidderDeviceType}. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidderDeviceTypeDTO implements Serializable {

  private static final long serialVersionUID = -7567649634297276637L;

  @EqualsAndHashCode.Include @ToString.Include private Long pid;

  private Integer version;

  private Long bidderPid;

  @EqualsAndHashCode.Include @ToString.Include @NotNull private Integer deviceTypeId;

  private DeviceTypeDTO deviceType;
}
