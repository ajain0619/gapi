package com.nexage.admin.core.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@Audited
@Table(name = "dooh_screen")
public class DoohScreen implements Serializable {

  private static final long serialVersionUID = 1L;

  @GeneratedValue @Id private Long pid;

  @Version @Column private Integer version;

  @Column(name = "created_on", updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdOn;

  @Column(name = "updated_on")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedOn;

  @Column(name = "ssp_screen_id")
  private String sspScreenId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_pid", referencedColumnName = "pid")
  private Company company;

  @Column(name = "seller_pid", insertable = false, updatable = false)
  private Long sellerPid;

  @Column(name = "seller_screen_id")
  private String sellerScreenId;

  @Column(name = "seller_screen_name")
  private String sellerScreenName;

  @Column private String network;

  @Column(name = "venue_type_id")
  private Integer venueTypeId;

  @Column(name = "location_type")
  private String locationType;

  @Column private Double latitude;
  @Column private Double longitude;
  @Column private String country;
  @Column private String state;
  @Column private String dma;
  @Column private String city;
  @Column private String zip;
  @Column private String address;
  @Column private String bearing;
  @Column private String link;

  @Column(name = "ad_types")
  private String adTypes;

  @Column(name = "min_ad_duration")
  private Integer minAdDuration;

  @Column(name = "max_ad_duration")
  private Integer maxAdDuration;

  @Column private String resolution;

  @Column(name = "accepted_ad_sizes")
  private String acceptedAdSizes;

  @Column(name = "aspect_ratio")
  private String aspectRatio;

  @Column(name = "avg_dwell_time")
  private Double avgDwellTime;

  @Column(name = "avg_impression_multiplier")
  private Double avgImpressionMultiplier;

  @Column(name = "avg_weekly_impressions")
  private Double avgWeeklyImpressions;

  @Column(name = "avg_daily_impressions")
  private Double avgDailyImpressions;

  @Column(name = "avg_monthly_impressions")
  private Double avgMonthlyImpressions;

  @Column(name = "avg_cpm")
  private Double avgCpm;

  @Column private String restrictions;

  @Column(name = "floor")
  private BigDecimal floorPrice;
}
