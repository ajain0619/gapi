package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.DirectDeal;
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

/** @deprecated please use {@link DealPublisher} */
@Entity
@Table(name = "deal_publisher")
@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Deprecated
public class LegacyDealPublisher implements Serializable {

  private static final long serialVersionUID = -6692184076984406171L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_pid", referencedColumnName = "pid")
  private DirectDeal deal;

  @Version
  @Column(name = "version", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private int version;

  @Column(name = "pub_pid", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pubPid;
}
