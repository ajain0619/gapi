package com.nexage.app.dto.deal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.sparta.jpa.model.DealTagRuleViewNoTagReference;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RTBProfileDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long pid;
  private Long rtbProfilePid;
  private String description;
  private Integer auctionType;
  private BigDecimal defaultReserve;
  private BigDecimal lowFloor;

  @JsonSerialize(using = ToStringSerializer.class)
  private Long pubAlias;

  @JsonSerialize(using = ToStringSerializer.class)
  private Long siteAlias;

  private String pubNameAlias;
  private String siteNameAlias;
  private char siteType;
  private String isRealName;
  // site fields
  private Long siteId;

  private String siteName;
  private Set<String> categories;
  private Set<DealTagRuleViewNoTagReference> countries;
  private Platform platform;

  // pub fields
  private Long pubId;
  private String pubName;

  // tag field
  private Long tagPid;
  private String tagName;

  // First not null of tag or position, null otherwise
  private Integer height;
  private Integer width;
  private VideoSupport videoSupport;

  // Position field
  private PlacementCategory placementType;
  private String placementName;
  private Long placementPid;
}
