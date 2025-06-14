package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.PlacementVideoSkippable;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.PlacementVideoVastVersion;
import com.nexage.admin.core.enums.VideoPlacementType;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.validator.CheckUniqueEntity;
import com.nexage.admin.core.validator.CheckUniqueGroup;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Range;

@Entity
@Audited
@Table(name = "placement_video")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@CheckUniqueEntity(
    errorCode = CoreDBErrorCodes.CORE_DB_DUPLICATE_POSITION,
    groups = CheckUniqueGroup.class,
    properties = {"position"})
public class PlacementVideo implements Serializable {

  private static final long serialVersionUID = 4920894180327728325L;

  @EqualsAndHashCode.Include @ToString.Include @Id private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pid", referencedColumnName = "pid")
  @NotNull
  @JsonIgnore
  @JsonBackReference
  @NotAudited
  @MapsId
  private PositionView position;

  @Column(name = "playback_method", nullable = true)
  private String playbackMethod;

  @Column(name = "linearity")
  @Type(type = "com.nexage.admin.core.custom.type.PlacementVideoLinearityEnumType")
  private PlacementVideoLinearity linearity;

  @Column(name = "start_delay", nullable = true)
  private Integer startDelay;

  @Column(name = "maxdur", nullable = true)
  private Integer maxdur;

  @Column(name = "skippable", nullable = true)
  @Enumerated(EnumType.ORDINAL)
  private PlacementVideoSkippable skippable;

  @Column(name = "skipthreshold", nullable = true)
  private Integer skipThreshold;

  @Column(name = "skipoffset", nullable = true)
  private Integer skipOffset;

  @Column(name = "vast_version", nullable = true)
  @Type(type = "com.nexage.admin.core.custom.type.PlacementVideoVastVersionEnumType")
  private PlacementVideoVastVersion vastVersion;

  @Column(name = "vpaid_support", nullable = true)
  private boolean vpaidSupport;

  @Column(name = "wrapper_support", nullable = true)
  private boolean wrapperSupport;

  @Column(name = "failover_support", nullable = true)
  private boolean failoverSupport;

  @Column(name = "file_formats", nullable = true)
  private String fileFormats;

  @OneToMany(mappedBy = "placementVideo", cascade = CascadeType.ALL)
  @LazyCollection(LazyCollectionOption.FALSE)
  @JsonIgnore
  private List<PlacementVideoCompanion> companions = new ArrayList<>();

  @Column(name = "player_required", nullable = false)
  @NotNull
  private boolean playerRequired;

  @Column(name = "player_height")
  @Range(min = 90, max = 9999)
  private Integer playerHeight;

  @Column(name = "player_width")
  @Range(min = 90, max = 9999)
  private Integer playerWidth;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on", updatable = false, insertable = false)
  @JsonIgnore
  private Date updatedOn;

  @Column(name = "longform", nullable = false)
  @ColumnDefault("false")
  @NotNull
  private boolean longform;

  @Column(name = "stream_type")
  @Enumerated(EnumType.ORDINAL)
  private PlacementVideoStreamType streamType;

  @Column(name = "player_brand")
  private String playerBrand;

  @Column(name = "ssai")
  @Enumerated(EnumType.ORDINAL)
  private PlacementVideoSsai ssai;

  @Column(name = "video_placement_type")
  @Enumerated(EnumType.STRING)
  private VideoPlacementType videoPlacementType;

  @Column(name = "dap_player_type")
  @Enumerated(EnumType.STRING)
  private DapPlayerType dapPlayerType;

  @Column(name = "player_id")
  private String playerId;

  @Column(name = "playlist_id")
  private String playListId;

  @Column(name = "multi_impression_bid", nullable = false)
  @ColumnDefault("false")
  @NotNull
  private boolean multiImpressionBid;

  @Column(name = "competitive_separation", nullable = false)
  @ColumnDefault("false")
  @NotNull
  private boolean competitiveSeparation;

  public void addCompanion(PlacementVideoCompanion companion) {
    companions.add(companion);
    companion.setPlacementVideo(this);
  }

  public void removeCompanion(PlacementVideoCompanion companion) {
    companions.remove(companion);
    companion.setPlacementVideo(null);
  }
}
