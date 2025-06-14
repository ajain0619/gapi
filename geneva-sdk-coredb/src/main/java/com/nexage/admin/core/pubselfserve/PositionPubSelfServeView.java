package com.nexage.admin.core.pubselfserve;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.TrafficType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

@Data
@Table(name = "position")
@Immutable
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PositionPubSelfServeView implements Serializable {

  @EqualsAndHashCode.Include @ToString.Include @Column @Id private long pid;

  @EqualsAndHashCode.Include @ToString.Include @Column private String name;

  @EqualsAndHashCode.Include @ToString.Include @Column String memo;

  @Formula(
      value =
          "(select case p.is_interstitial when 1 then 'Interstitial' else 'Banner' end from position p where p.pid=pid)")
  private String type;

  @Column(name = "site_pid")
  private long sitePid;

  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(mappedBy = "position")
  @Where(clause = "status >= 0")
  private List<TagPubSelfServeView> tags = new ArrayList<>();

  @Column(name = "is_interstitial")
  private Boolean interstitial;

  public PlacementCategory getPlacementCategory() {
    return placementCategory;
  }

  @Column(name = "placement_type")
  @Enumerated(EnumType.ORDINAL)
  private PlacementCategory placementCategory;

  @Column(name = "traffic_type")
  @Enumerated(EnumType.ORDINAL)
  private TrafficType trafficType;

  @Transient private boolean tagsPopulatedFromTiers = false;
}
