package com.nexage.admin.core.model.filter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nexage.admin.core.model.BidderConfig;
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
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "bidder_config_deny_allow_filter_list")
@Data
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BidderConfigDenyAllowFilterList implements Serializable {

  private static final long serialVersionUID = 7708140870498120147L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer pid;

  @ManyToOne
  @JoinColumn(name = "bidder_config_pid")
  @JsonBackReference
  private BidderConfig bidderConfig;

  @ManyToOne
  @JoinColumn(name = "black_white_filter_list_id", referencedColumnName = "pid")
  private FilterList filterListNonInclusive;

  @ManyToOne
  @JoinColumn(name = "deny_allow_filter_list_id", referencedColumnName = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private FilterList filterList;

  @Version private Integer version;
}
