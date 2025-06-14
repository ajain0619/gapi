package com.nexage.admin.core.sparta.jpa.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Deprecated
@Immutable
@Entity
@Data
@Table(name = "bidder_config")
public class DealBuyerView implements Serializable {

  private static final long serialVersionUID = -7648823444973287719L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long bidderPid;

  @Column(name = "company_id")
  private Long companyPid;

  private String name;
}
