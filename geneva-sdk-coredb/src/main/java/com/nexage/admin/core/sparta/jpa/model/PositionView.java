package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.SiteView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "position")
@Immutable
@Data
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionView implements Serializable {

  private static final long serialVersionUID = 9170266198311541678L;

  @EqualsAndHashCode.Include @ToString.Include @Id private Long pid;

  @EqualsAndHashCode.Include @ToString.Include @Column private String name;

  @EqualsAndHashCode.Include @ToString.Include @Column private String memo;

  @EqualsAndHashCode.Include @ToString.Include @Column private Integer version;

  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  @EqualsAndHashCode.Include
  @ToString.Include
  @Column
  private Status status;

  @Column(name = "screen_location")
  @Type(type = "com.nexage.admin.core.usertype.ScreenLocationUserType")
  @EqualsAndHashCode.Include
  @ToString.Include
  private ScreenLocation screenLocation;

  @Column(name = "position_alias_name")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String positionAliasName;

  @Column(name = "ad_size_type")
  @EqualsAndHashCode.Include
  @ToString.Include
  @Enumerated(EnumType.ORDINAL)
  private AdSizeType adSizeType;

  @Column(name = "site_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long sitePid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "site_pid",
      referencedColumnName = "pid",
      insertable = false,
      updatable = false)
  @NotNull
  @ToString.Exclude
  @JsonIgnore
  @JsonBackReference
  private SiteView siteView;

  public PositionView(Long pid, String name, Status status) {
    this.pid = pid;
    this.name = name;
    this.status = status;
  }

  public PositionView(Long pid, String name, String memo, Integer version, Long sitePid) {
    this.pid = pid;
    this.name = name;
    this.memo = memo;
    this.version = version;
    this.sitePid = sitePid;
  }

  public PositionView(
      Long pid,
      String name,
      Status status,
      Integer version,
      ScreenLocation screenLocation,
      String positionAliasName,
      AdSizeType adSizeType) {
    this.pid = pid;
    this.name = name;
    this.status = status;
    this.version = version;
    this.screenLocation = screenLocation;
    this.positionAliasName = positionAliasName;
    this.adSizeType = adSizeType;
  }
}
