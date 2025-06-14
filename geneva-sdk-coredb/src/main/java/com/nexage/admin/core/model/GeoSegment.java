package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "geo_segments_info")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class GeoSegment implements Serializable {

  private static final long serialVersionUID = 10L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  private Long pid;

  @Column(name = "segment_id", nullable = false)
  private Long segmentId;

  @Column(name = "woeid", nullable = true)
  private Long woeid;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "type", nullable = false)
  private Long type;

  @Column(name = "iso3_code", nullable = false)
  private String iso3Code;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_updated_on", nullable = false)
  private Date lastUpdateOn;
}
