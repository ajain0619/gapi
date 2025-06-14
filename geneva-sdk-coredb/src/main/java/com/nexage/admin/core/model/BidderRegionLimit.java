package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nexage.admin.core.bidder.support.validation.annotation.CountryLetterCodes;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

/**
 * @author Nick Ilkevich
 * @since 29.08.2014
 */
@Audited
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "bidder_region_limit")
public class BidderRegionLimit implements Serializable {

  /** */
  private static final long serialVersionUID = 6291749427516679278L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer version;

  /** Associated bidder config. */
  @ManyToOne
  @JoinColumn(name = "bidder_pid")
  @JsonBackReference
  private BidderConfig bidderConfig;

  /** A name of region limit */
  @Size(min = 1, max = 255)
  @Column(name = "name")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  /** A comma separated list of three letter country codes */
  @CountryLetterCodes
  @Size(min = 1, max = 1000)
  @Lob
  @Column(name = "filter_countries", length = 1000)
  @ToString.Include
  private String countriesFilter;

  /** Amount of request per second. */
  @Min(value = 1)
  @Max(value = 999_999)
  @Column(name = "filter_request_rate")
  @ToString.Include
  private Integer requestRate;
}
