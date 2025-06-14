package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "position_buyer")
@Data
@NoArgsConstructor
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PositionBuyer implements Serializable {

  private static final long serialVersionUID = -5844021455424160030L;

  @Id @GeneratedValue private Long pid;

  @Column(name = "company_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long companyPid;

  @Column(name = "position_pid", insertable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long positionPid;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "position_pid", referencedColumnName = "pid")
  private Position position;

  @Column(name = "buyer_position_id")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String buyerPositionId;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "last_update")
  @UpdateTimestamp
  private Date lastUpdated;
}
