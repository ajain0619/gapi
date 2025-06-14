package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.DirectDeal;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(name = "deal_bidder")
@Audited
@Setter
@Getter
@NoArgsConstructor
public class DealBidder implements Serializable {

  private static final long serialVersionUID = -6981833390096394058L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  private int version;

  @ManyToOne
  @JoinColumn(name = "deal_pid", referencedColumnName = "pid")
  private DirectDeal deal;

  @ManyToOne
  @JoinColumn(name = "bidder_config_pid", referencedColumnName = "pid")
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  private BidderConfig bidderConfig;

  @Column(name = "filter_seats")
  private String filterSeats;

  @Column(name = "filter_adomains")
  private String filterAdomains;
}
