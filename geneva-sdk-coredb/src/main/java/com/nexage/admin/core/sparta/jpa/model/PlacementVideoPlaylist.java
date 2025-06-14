package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.custom.MediaTypeConverter;
import com.nexage.admin.core.enums.MediaType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "placement_video_playlist")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlacementVideoPlaylist implements Serializable {

  private static final long serialVersionUID = 1L;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long pid;

  @Version private Integer version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "placement_video_pid", referencedColumnName = "pid")
  private PlacementVideo placementVideoPid;

  @Column(name = "fallback_url", nullable = false)
  private String fallbackURL;

  @Column(name = "media_type", nullable = false)
  @Convert(converter = MediaTypeConverter.class)
  private MediaType mediaType;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date updatedOn;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_on")
  private Date createdOn;

  @PrePersist
  public void prePersist() {
    Date nowDate = new Date();
    createdOn = nowDate;
    updatedOn = nowDate;
  }

  @PreUpdate
  public void preUpdate() {
    updatedOn = new Date();
  }
}
