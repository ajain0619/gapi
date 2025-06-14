package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Range;

@Entity
@Audited
@Table(name = "placement_video_companion")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlacementVideoCompanion implements Serializable {

  private static final long serialVersionUID = 5591587709582725532L;

  @EqualsAndHashCode.Include @ToString.Include @Id @GeneratedValue private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "placement_video_pid", insertable = false, updatable = false, nullable = false)
  @NotAudited
  private Long placementVideoPid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "placement_video_pid", referencedColumnName = "pid")
  @NotNull
  @JsonIgnore
  @JsonBackReference
  private PlacementVideo placementVideo;

  @Column(name = "height", nullable = false)
  @Range(min = 1, max = 999)
  private Integer height;

  @Column(name = "width", nullable = false)
  @Range(min = 1, max = 999)
  private Integer width;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on", updatable = false, insertable = false)
  @JsonIgnore
  private Date updatedOn;
}
