package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nexage.admin.core.model.SellerAttributes;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "smart_exchange_attributes")
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SmartExchangeAttributes implements Serializable {

  private static final long serialVersionUID = -5183917592471285980L;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @Id
  @ToString.Include
  private Long pid;

  @Version @ToString.Include private Integer version;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_attributes_id", referencedColumnName = "seller_pid")
  @JsonBackReference
  private SellerAttributes sellerAttributes;

  @Column(name = "smart_margin_override")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Boolean smartMarginOverride = false;

  @Column(name = "created_on", updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date createdOn;

  @Column(name = "updated_on")
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private Date updatedOn;
}
