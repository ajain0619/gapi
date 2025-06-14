package com.nexage.app.dto.bdr;

import com.nexage.admin.core.bidder.type.BDRStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BdrExchangeDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Include private Long pid;
  @EqualsAndHashCode.Include @ToString.Include private String externalId;
  @EqualsAndHashCode.Include @ToString.Include private String name;
  // NOTE using a property of handler conflicts with javassist bytecode rewriting, hence the
  // dropping of "a"
  @EqualsAndHashCode.Include @ToString.Include private String hndler;
  @EqualsAndHashCode.Include @ToString.Include private BDRStatus status;
  @ToString.Include private Integer version;
  private Date updatedOn;
  @EqualsAndHashCode.Include @ToString.Include private Long nexageId;
  private BigDecimal exchangeBiddingFee;
  private Map<Long, BigDecimal> companyBiddingFees = new HashMap<>();
  private Integer bidderSite;
  private Integer bookingEvent;
  private Integer tmax;
  private Integer tmargin;
}
