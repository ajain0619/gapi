package com.nexage.admin.core.model;

import com.ssp.geneva.common.model.inventory.CompanyType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.NONE)
@Entity
@Immutable
@Audited
@Table(name = "company")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CompanyView implements Serializable {

  private static final long serialVersionUID = -6609671388383939972L;

  public CompanyView(Long pid, String name) {
    this.pid = pid;
    this.name = name;
  }

  public CompanyView(Long pid, String name, CompanyType type, boolean selfServeAllowed) {
    this.pid = pid;
    this.name = name;
    this.type = type;
    this.selfServeAllowed = selfServeAllowed;
  }

  public CompanyView(
      Long pid,
      String name,
      CompanyType type,
      boolean selfServeAllowed,
      SellerAttributesView sellerAttributes) {
    this.pid = pid;
    this.name = name;
    this.type = type;
    this.selfServeAllowed = selfServeAllowed;
    this.sellerAttributes = sellerAttributes;
  }

  public CompanyView(
      Long pid, String name, CompanyType type, boolean selfServeAllowed, Long sellerSeatPid) {
    this.pid = pid;
    this.name = name;
    this.type = type;
    this.selfServeAllowed = selfServeAllowed;
    this.sellerSeatPid = sellerSeatPid;
  }

  @EqualsAndHashCode.Include @ToString.Include @Id @Column private Long pid;

  @ToString.Include @EqualsAndHashCode.Include @Column private String name;

  @Column
  @Enumerated(EnumType.STRING)
  private CompanyType type;

  @Column(name = "selfserve_allowed")
  private boolean selfServeAllowed;

  @Column(name = "seller_seat_id")
  private Long sellerSeatPid;

  @Column(name = "global_alias_name")
  private String globalAliasName;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pid", referencedColumnName = "seller_pid")
  private SellerAttributesView sellerAttributes;
}
