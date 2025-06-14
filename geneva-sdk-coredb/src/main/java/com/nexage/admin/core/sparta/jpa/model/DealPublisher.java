package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.CompanyView;
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
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "deal_publisher")
@Data
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class DealPublisher implements Serializable {

  private static final long serialVersionUID = 2410858458425501059L;

  public DealPublisher(Long pid, CompanyView companyView) {
    this.pubPid = pid;
    this.companyView = companyView;
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
  @JoinColumn(name = "pub_pid", referencedColumnName = "pid", insertable = false, updatable = false)
  private CompanyView companyView;

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
