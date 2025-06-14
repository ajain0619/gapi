package com.nexage.admin.core.model;

import java.io.Serial;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

@Entity
@Immutable
@Audited
@Table(name = "seller_attributes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SellerAttributesView implements Serializable {

  @Serial private static final long serialVersionUID = 7963271261582122033L;

  @Id
  @Column(name = "seller_pid", nullable = false)
  private Long sellerPid;

  @Column(name = "revenue_group_pid")
  private Long revenueGroupPid;
}
