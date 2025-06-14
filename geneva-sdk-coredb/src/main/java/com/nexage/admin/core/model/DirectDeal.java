package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.sparta.jpa.model.DealBidder;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealProfile;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "deal")
@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class DirectDeal implements Serializable {

  private static final long serialVersionUID = 7876178041802678569L;

  public static final int DEAL_ID_MAX_LENGTH = 255;

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "deal")
  private List<DealSite> sites = new ArrayList<>();

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "deal")
  private List<DealProfile> profiles = new ArrayList<>();

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "deal")
  private List<DealPublisher> publishers = new ArrayList<>();

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "deal")
  private List<DealPosition> positions = new ArrayList<>();

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "deal")
  private List<DealBidder> bidders = new ArrayList<>();

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "deal")
  private List<DealRule> rules = new ArrayList<>();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long pid;

  @Column(name = "id", nullable = false, unique = true)
  @Size(max = DEAL_ID_MAX_LENGTH)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String dealId;

  @Version
  @Column(name = "VERSION", nullable = false)
  private Integer version;

  @Column(name = "description")
  private String description;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "start")
  private Date start;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "stop")
  private Date stop;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "status")
  @Type(type = "com.nexage.admin.core.usertype.DealStatusUserType")
  private DealStatus status;

  @Column(name = "floor")
  private BigDecimal floor;

  @Column(name = "auction_type")
  private Integer auctionType;

  @Column(name = "deal_category", nullable = false)
  private Integer dealCategory;

  @Column(name = "visibility", nullable = false)
  private boolean visibility;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "priority_type", nullable = false)
  @Type(type = "com.nexage.admin.core.usertype.DealPriorityUserType")
  private DealPriorityType priorityType;

  @Column(name = "guaranteed_impression_goal")
  private Long guaranteedImpressionGoal;

  @Column(name = "daily_impression_cap")
  private Long dailyImpressionCap;

  @Column(name = "pacing_enabled", nullable = true)
  private Boolean pacingEnabled;

  @Column(name = "pacing_strategy", nullable = true)
  private Integer pacingStrategy;

  @ColumnDefault("false")
  @Column(name = "external", nullable = false)
  private boolean external;

  @Lob
  @Column(name = "placement_formula")
  private String placementFormula;

  @Column(name = "auto_update")
  private Boolean autoUpdate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false, updatable = false)
  @NotNull
  private java.util.Date creationDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on", nullable = false, updatable = true)
  @NotNull
  private java.util.Date updatedOn;

  @ColumnDefault("false")
  @Column(name = "all_sellers", nullable = false)
  @NotNull
  private boolean allSellers;

  @ColumnDefault("false")
  @Column(name = "all_bidders", nullable = false)
  @NotNull
  private boolean allBidders;

  @Deprecated
  @Column(name = "viewability", nullable = true)
  private Float viewability;

  @Column(name = "placement_formula_status")
  @Enumerated(EnumType.STRING)
  private PlacementFormulaStatus placementFormulaStatus;

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "deal")
  private Set<DealTarget> dealTargets = new HashSet<>();

  @PrePersist
  private void setCreateAndUpdate() {
    creationDate = new java.util.Date();
    updatedOn = new java.util.Date();
  }

  @PreUpdate
  private void setUpdate() {
    updatedOn = new java.util.Date();
  }

  @AllArgsConstructor
  public enum DealStatus {
    Inactive(0),
    Active(1);

    private static final HashMap<Integer, DealStatus> fromIntMap = new HashMap<>();

    static {
      for (DealStatus status : DealStatus.values()) {
        fromIntMap.put(status.externalValue, status);
      }
    }

    private final int externalValue;

    public static DealStatus fromInt(int value) {
      return fromIntMap.get(value);
    }

    public int asInt() {
      return externalValue;
    }
  }
}
