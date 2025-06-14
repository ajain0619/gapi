package com.nexage.admin.core.bidder.model.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

@Immutable
@Entity
@Table(name = "bdr_creative")
public class BDRCreativeView {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "name", length = 100)
  private String name;

  @Type(type = "com.nexage.admin.core.bidder.usertype.RevInfoUserType")
  @Formula(
      value =
          "(select concat(r.rev,',',r.user_name,',',r.revtstmp) from revinfo r where r.rev=(select max(i.rev) from bdr_creative_aud i where i.pid=pid))")
  private RevInfo revinfo;

  public BDRCreativeView() {}

  public Long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public RevInfo getRevinfo() {
    return revinfo;
  }
}
