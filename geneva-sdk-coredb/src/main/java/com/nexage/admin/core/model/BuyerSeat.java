package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(
    name = "buyer_seat",
    uniqueConstraints =
        @UniqueConstraint(
            name = "BuyerSeatBusinessId",
            columnNames = {"company_pid", "seat"}))
public class BuyerSeat implements Serializable {

  private static final long serialVersionUid = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @ToString.Include
  private Long pid;

  @Column(length = 255, nullable = false)
  @ToString.Include
  private String name;

  @Column(length = 255, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String seat;

  @Column(nullable = false)
  private boolean enabled;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_group_pid")
  private BuyerGroup buyerGroup;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid")
  @EqualsAndHashCode.Include
  private Company company;

  @Version private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = true)
  private Date creationDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_updated_date", nullable = true)
  private Date lastUpdate;

  @Column(name = "buyer_transparency_data_feed_pid")
  private Long buyerTransparencyDataFeedPid;

  @Column(name = "buyer_transparency_feed_enabled")
  private Boolean buyerTransparencyFeedEnabled = false;

  public BuyerSeat(String seat, String name, boolean enabled) {
    this.seat = seat;
    this.name = name;
    this.enabled = enabled;
  }

  public Date getCreationDate() {
    return creationDate != null ? new Date(creationDate.getTime()) : null;
  }

  @PrePersist
  private void prePersist() {
    Date nowDate = new Date();
    creationDate = nowDate;
    lastUpdate = nowDate;
  }

  public Date getLastUpdate() {
    return lastUpdate != null ? new Date(lastUpdate.getTime()) : null;
  }

  @PreUpdate
  private void preUpdate() {
    lastUpdate = new Date();
  }
}
