package com.nexage.admin.core.bidder.model.view;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "bdr_insertionorder")
public class BDRInsertionOrderView {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "name", length = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "advertiser_pid", referencedColumnName = "pid")
  private BDRAdvertiserView advertiser;

  @Column(name = "type")
  private Integer type;

  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(mappedBy = "insertionOrder")
  private List<BDRLineItemView> lineItems = new ArrayList<>();

  @Type(type = "com.nexage.admin.core.bidder.usertype.RevInfoUserType")
  @Formula(
      value =
          "(select concat(r.rev,',',r.user_name,',',r.revtstmp) from revinfo r where r.rev=(select max(i.rev) from bdr_insertionorder_aud i where i.pid=pid))")
  private RevInfo revinfo;

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public String getName() {
    return name;
  }

  public BDRAdvertiserView getAdvertiser() {
    return advertiser;
  }

  public Integer getType() {
    return type;
  }

  public List<BDRLineItemView> getLineItems() {
    return lineItems;
  }

  public RevInfo getRevinfo() {
    return revinfo;
  }
}
