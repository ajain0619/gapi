package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.model.RTBProfile.ScreeningLevel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class RTBProfileDTO implements Serializable {
  @NotNull
  @Size(min = 1, max = 255)
  @Pattern(regexp = "^[\\x20-\\x7E]+$")
  @Pattern(regexp = "^[\\x20-\\x7E]+|\\r\\n")
  private String name;

  private Long pid;
  private Long numberOfEffectivePlacements;
  private boolean publisherDefault;
  private int auctionType;
  private String blockedAdTypes;

  @Positive
  @Max(1000)
  private BigDecimal pubNetLowReserve;

  @Positive
  @Max(1000)
  private BigDecimal pubNetReserve;

  @NotNull private ScreeningLevel screeningLevel;
  private Integer version;
  private AlterReserve alterReserve = AlterReserve.OFF;
  private BigDecimal defaultReserve;
  private boolean includeConsumerId;
  private boolean includeConsumerProfile;
  private boolean includeDomainReferences;
  private boolean includeGeoData = true;
  private BigDecimal lowReserve;
  private java.util.Date creationDate;
  private String description;
  private Date lastUpdate;
}
