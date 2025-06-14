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

/** @deprecated please use {@link DealSite} */
@Entity
@Table(name = "deal_site")
@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Deprecated
public class LegacyDealSite implements Serializable {

  private static final long serialVersionUID = -2189460783442800372L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_pid", referencedColumnName = "pid")
  private DirectDeal deal;

  @Column(name = "site_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long sitePid;

  @Version
  @Column(name = "VERSION", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private int version;
}
