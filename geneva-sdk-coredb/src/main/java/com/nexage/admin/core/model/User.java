package com.nexage.admin.core.model;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.admin.core.validator.CheckUnique;
import com.nexage.admin.core.validator.CheckUniqueGroup;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Defines a model that represents a user in the system. */
@Table(name = "app_user")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Audited
public class User extends BaseModel implements UserDetails {

  private static final long serialVersionUID = 7989358660450213907L;

  public static final String AUTH_SEPERATOR = "_";
  public static final String SELLER_SEAT_POSTFIX = AUTH_SEPERATOR + "SELLER_SEAT";

  public enum Role {
    ROLE_ADMIN,
    ROLE_MANAGER,
    ROLE_USER,
    ROLE_API,
    ROLE_MANAGER_YIELD,
    ROLE_API_IIQ,
    ROLE_MANAGER_SMARTEX,
    ROLE_AD_REVIEWER,
    ROLE_DUMMY;
  }

  public User() {
    companies = new HashSet<>();
  }

  @Column(name = "user_name", nullable = false, unique = true, length = 100)
  @NotNull
  @CheckUnique(
      entity = User.class,
      fieldName = "userName",
      errorCode = CoreDBErrorCodes.CORE_DB_DUPLICATE_USER_NAME,
      groups = CheckUniqueGroup.class)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String userName;

  @Column(name = "onecentral_username", length = 100)
  private String oneCentralUserName;

  @Column(length = 100)
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "first_name", nullable = true, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = true, length = 100)
  private String lastName;

  @Column(nullable = false, unique = true, length = 100)
  @NotNull
  @CheckUnique(
      entity = User.class,
      fieldName = "email",
      errorCode = CoreDBErrorCodes.CORE_DB_DUPLICATE_EMAIL,
      groups = CheckUniqueGroup.class)
  @Email
  @EqualsAndHashCode.Include
  private String email;

  @Column(name = "contact_number", length = 20)
  private String contactNumber;

  /**
   * 1. Default fetch type is Eager and setting it to Lazy will eliminate unnecessary queries even
   * for associations not being used. 2. CascadeType is not set to ALL b'se a. company should not be
   * removed if a user is deleted. b. company should not be detached if a user is detached from
   * persistence Context c. company already exist when user is created
   *
   * @deprecated Please use Set<Company> companies instead.
   */
  @ManyToOne(
      fetch = FetchType.LAZY,
      optional = false,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "company_id", referencedColumnName = "pid")
  @JsonIgnore
  @Deprecated
  @Setter(AccessLevel.NONE)
  private Company company;

  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "company_app_user",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "pid"),
      inverseJoinColumns = @JoinColumn(name = "company_id", referencedColumnName = "pid"))
  @AuditJoinTable(name = "company_app_user_aud")
  @Setter(AccessLevel.NONE)
  @NotEmpty
  @JsonIgnore
  private Set<Company> companies;

  @Column(length = 100)
  private String title;

  @Column(nullable = false)
  @NotNull
  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(nullable = false)
  @NotNull
  private boolean enabled;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false)
  @NotNull
  @JsonIgnore
  private java.util.Date creationDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update")
  @JsonIgnore
  private java.util.Date lastUpdate;

  @Transient private boolean primaryContact;

  /** @deprecated Please use Set<Company> companies instead. */
  @Deprecated
  @Setter(AccessLevel.NONE)
  @Column(name = "company_id", insertable = false, updatable = false)
  private Long companyPid;

  @Column(name = "is_global", columnDefinition = "BIT default 0")
  @NotNull
  private boolean global;

  @Column(name = "is_deal_admin", columnDefinition = "BIT default 0")
  @NotNull
  private boolean dealAdmin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_seat_id", referencedColumnName = "pid")
  @JsonIgnore // temp fix for MX-11118
  private SellerSeat sellerSeat;

  /** @deprecated Please use Set<Company> companies instead. */
  @Deprecated(since = "SSP-25219", forRemoval = true)
  @Transient
  private String companyName;

  /** @deprecated Please use Set<Company> companies instead. */
  @Deprecated(since = "SSP-25219", forRemoval = true)
  @Transient
  private CompanyType companyType;

  @Transient
  @JsonProperty(access = Access.WRITE_ONLY)
  private String contactName;

  @Transient
  @JsonProperty(access = Access.WRITE_ONLY)
  private String contactEmail;

  @Column(name = "is_migrated_onecentral", columnDefinition = "BIT default 0")
  @NotNull
  private boolean migratedOneCentral;

  public Long getSellerSeatPid() {
    return sellerSeat != null ? sellerSeat.getPid() : null;
  }

  @JsonProperty
  public String getCompanyName() {
    if (isNull(companyName)) {
      Company company = getCompany();
      if (nonNull(company)) {
        companyName = company.getName();
      }
    }
    return companyName;
  }

  @JsonProperty
  public CompanyType getCompanyType() {
    if (isNull(companyType)) {
      if (nonNull(sellerSeat)) {
        companyType = CompanyType.SELLER;
      } else {
        companyType =
            companies.stream()
                .reduce(
                    (a, b) -> {
                      throw new IllegalStateException();
                    })
                .orElseThrow(IllegalStateException::new)
                .getType();
      }
    }
    return companyType;
  }

  @JsonIgnore
  public Company getCompany() {
    if (isNull(sellerSeat) && nonNull(companies)) {
      return companies.iterator().next();
    }
    return null;
  }

  public void addCompany(Company company) {
    if (nonNull(company)) {
      companies.add(company);
    }
  }

  public void removeCompany(Company company) {
    companies.remove(company);
  }

  /** @return the userName */
  public String getUserName() {
    return userName;
  }

  public void determinePrimaryContact() {
    Company company = getCompany();
    primaryContact =
        nonNull(company) && nonNull(company.getContact()) && (company.getContact().equals(this));
  }

  public Long getCompanyPid() {
    if (isNull(companyPid)) {
      Company company = getCompany();
      if (nonNull(company)) {
        companyPid = company.getPid();
      }
    }
    return companyPid;
  }

  @JsonIgnore
  public boolean isBelongingToCompany(long companyPid) {
    return companies != null && companies.stream().anyMatch(c -> c.getPid().equals(companyPid));
  }

  // --------------------------------------------------------------------------
  // Methods required by Spring Security - UserDetails interface
  // --------------------------------------------------------------------------

  @JsonIgnore
  public Collection<GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> auth = new HashSet<>();
    auth.add(new SimpleGrantedAuthority(this.role + AUTH_SEPERATOR + this.getCompanyType()));
    if (nonNull(this.getSellerSeat())) {
      auth.add(new SimpleGrantedAuthority(this.role + SELLER_SEAT_POSTFIX));
    }
    return auth;
  }

  @JsonIgnore
  @Override
  public String getPassword() {
    return null;
  }

  @JsonIgnore
  @SuppressWarnings("squid:S1845")
  public String getUsername() {
    return this.userName;
  }

  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @JsonIgnore
  public Set<String> getCompanyMdmIds() {
    if (CollectionUtils.isNotEmpty(companies)) {
      return extractCompanyMdmIds(getCompanies().stream());
    }

    // to maintain compatibility until old 1-M relationship is dropped
    return extractCompanyMdmIds(Stream.ofNullable(company));
  }

  @JsonIgnore
  public Set<String> getSellerSeatMdmIds() {
    if (sellerSeat == null) {
      return Collections.emptySet();
    }

    return getSellerSeat().getMdmIds().stream()
        .map(MdmId::getId)
        .collect(Collectors.toUnmodifiableSet());
  }

  @PreUpdate
  private void preUpdate() {
    lastUpdate = new Date();
  }

  @PrePersist
  private void prePersist() {
    id = new UUIDGenerator().generateUniqueId();
    creationDate = new Date();
  }

  private Set<String> extractCompanyMdmIds(Stream<Company> companyStream) {
    return companyStream
        .map(Company::getMdmIds)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(MdmId::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
  }
}
