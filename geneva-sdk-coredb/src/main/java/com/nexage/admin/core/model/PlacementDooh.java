package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "placement_dooh")
@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlacementDooh implements Serializable {

  private static final long serialVersionUID = -7369984246703112665L;

  @Id
  @Column(name = "placement_pid", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "placement_pid", referencedColumnName = "pid")
  @JsonIgnore
  private Position position;

  @Column(name = "default_imp_multiplier", nullable = false)
  @ToString.Include
  private BigDecimal defaultImpressionMultiplier;

  @Version
  @Column(name = "version", nullable = false)
  @ToString.Include
  private Integer version;
}
