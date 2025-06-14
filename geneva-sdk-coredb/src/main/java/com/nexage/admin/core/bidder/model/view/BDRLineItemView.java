package com.nexage.admin.core.bidder.model.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

@Immutable
@Entity
@Table(name = "bdr_lineitem")
public class BDRLineItemView {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "name", length = 100)
  @NotNull
  private String name;

  @ManyToOne
  @JoinColumn(name = "insertionorder_pid")
  private BDRInsertionOrderView insertionOrder;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "start")
  @NotNull
  private Date startDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "stop")
  private Date stopDate;

  @Column(name = "spend")
  private BigDecimal spendGoal;

  @Column(name = "impressions")
  private Long impressionGoal;

  @Column(name = "status")
  private Integer status = 0;

  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(mappedBy = "lineItem")
  private List<BDRTargetGroupView> targetGroups = new ArrayList<>();

  @Type(type = "com.nexage.admin.core.bidder.usertype.RevInfoUserType")
  @Formula(
      value =
          "(select concat(r.rev,',',r.user_name,',',r.revtstmp) from revinfo r where r.rev=(select max(i.rev) from bdr_lineitem_aud i where i.pid=pid))")
  private RevInfo revinfo;

  public BDRLineItemView() {}

  public Long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Date getStopDate() {
    return stopDate;
  }

  public Long getImpressionGoal() {
    return impressionGoal;
  }

  public BigDecimal getSpendGoal() {
    return spendGoal;
  }

  public int getStatus() {
    return status;
  }

  public RevInfo getRevinfo() {
    return revinfo;
  }

  public List<BDRTargetGroupView> getTargetGroups() {
    return targetGroups;
  }
}
