package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.RTBProfile;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Table(name = "rtb_profile_bidder")
public class RTBProfileBidder implements Serializable {

  private static final long serialVersionUID = 6915676240080953192L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid", nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer version;

  @ManyToOne
  @JoinColumn(name = "rtb_profile_pid")
  @JsonBackReference
  RTBProfile rtbprofile;

  @Column(name = "bidder_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  Long bidderPid;

  @ManyToOne
  @JoinColumn(
      name = "bidder_pid",
      referencedColumnName = "pid",
      insertable = false,
      updatable = false)
  @JsonIgnore
  @JsonBackReference
  @NotAudited
  BidderConfig bidder;

  @Column(name = "whitelist")
  @ToString.Include
  private String seatWhitelist;
}
