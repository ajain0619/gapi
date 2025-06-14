package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.Position;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/** @deprecated please use {@link DealPosition} */
@Entity
@Table(name = "deal_position")
@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Deprecated
public class LegacyDealPosition implements Serializable {

  private static final long serialVersionUID = 1870307231143560381L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_pid", referencedColumnName = "pid")
  private DirectDeal deal;

  @Version
  @Column(name = "version", nullable = false)
  private int version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "position_pid", referencedColumnName = "pid")
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  private Position position;

  @Column(name = "position_pid", insertable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long positionPid;

  public void setPosition(Position position) {
    this.position = position;
    if (position != null) {
      this.positionPid = position.getPid();
    }
  }
}
