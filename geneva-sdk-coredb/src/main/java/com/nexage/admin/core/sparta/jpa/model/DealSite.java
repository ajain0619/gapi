package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.SiteView;
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
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "deal_site")
@Data
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class DealSite implements Serializable {

  private static final long serialVersionUID = 2373988142815875060L;

  public DealSite(Long sitePid, SiteView siteView) {
    this.sitePid = sitePid;
    this.siteView = siteView;
  }

  public DealSite(Long sitePid, DirectDeal deal) {
    this.sitePid = sitePid;
    this.deal = deal;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_pid", referencedColumnName = "pid")
  private DirectDeal deal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "site_pid",
      referencedColumnName = "pid",
      insertable = false,
      updatable = false)
  private SiteView siteView;

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
