package com.nexage.admin.core.bidder.model.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

@Immutable
@Entity
@Table(name = "bdr_targetgroup")
public class BDRTargetGroupView {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "name")
  private String name;

  @Column(name = "status")
  private Integer status = 0;

  @ManyToOne
  @JoinColumn(name = "lineitem_pid")
  private BDRLineItemView lineItem;

  @Column(name = "max_bid")
  private BigDecimal maxPrice;

  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany
  @JoinTable(
      name = "bdr_targetgroup_creative",
      joinColumns = {@JoinColumn(name = "targetgroup_pid")},
      inverseJoinColumns = {@JoinColumn(name = "creative_pid")})
  private List<BDRCreativeView> creatives = new ArrayList<>();

  @Type(type = "com.nexage.admin.core.bidder.usertype.RevInfoUserType")
  @Formula(
      value =
          "(select concat(r.rev,',',r.user_name,',',r.revtstmp) from revinfo r where r.rev=(select max(i.rev) from bdr_targetgroup_aud i where i.pid=pid))")
  private RevInfo revinfo;

  public BDRTargetGroupView() {}

  public Long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public List<BDRCreativeView> getCreatives() {
    return creatives;
  }

  public int getStatus() {
    return status;
  }

  public RevInfo getRevinfo() {
    return revinfo;
  }

  public BigDecimal getMaxPrice() {
    return maxPrice;
  }
}
