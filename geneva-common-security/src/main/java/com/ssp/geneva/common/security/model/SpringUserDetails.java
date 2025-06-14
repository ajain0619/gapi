package com.ssp.geneva.common.security.model;

import static java.util.Collections.singleton;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SpringUserDetails implements UserDetails {

  private static final long serialVersionUID = 1L;

  private final long pid;
  private final String username;
  private final String password;
  private final Role role;
  private final CompanyType type;
  private final Set<Long> companyPids;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean enabled;
  private final Set<Long> publisherSelfServe;
  private final Long sellerSeatPid;

  @Getter private final boolean isGlobal;
  @Getter private final Set<Long> sellerSeatCompanies;
  @Getter private final boolean dealAdmin;
  @Getter private final Set<String> companyMdmIds;
  @Getter private final Set<String> sellerSeatMdmIds;
  @Getter @Setter private List<Entitlement> entitlements;

  public SpringUserDetails(UserAuth userAuth) {
    var user = userAuth.getUser();
    pid = user.getPid();
    username = user.getUserName();
    password = user.getPassword();
    role = user.getRole();
    type = user.getCompanyType();
    if (!CollectionUtils.isEmpty(user.getCompanies())) {
      publisherSelfServe =
          user.getCompanies().stream()
              .filter(Company::isSelfServeAllowed)
              .mapToLong(Company::getPid)
              .collect(TreeSet::new, Set::add, Set::addAll);
      companyPids = Collections.unmodifiableSet(extractCompanyPids(user));
    } else {
      // to maintain compatibility until old 1-M relationship is dropped

      if (user.getCompany() != null && user.getCompany().isSelfServeAllowed()) {
        publisherSelfServe = ImmutableSet.of(user.getCompany().getPid());
      } else {
        publisherSelfServe = Collections.emptySet();
      }
      companyPids = singleton(user.getCompanyPid());
    }

    sellerSeatCompanies =
        Optional.ofNullable(user.getSellerSeat())
            .map(
                seat ->
                    Collections.unmodifiableSet(
                        seat.getSellers().stream()
                            .map(Company::getPid)
                            .collect(Collectors.toSet())))
            .orElse(Collections.emptySet());

    this.companyMdmIds = user.getCompanyMdmIds();
    this.sellerSeatMdmIds = user.getSellerSeatMdmIds();

    isGlobal = user.isGlobal();

    authorities = user.getAuthorities();
    enabled = user.isEnabled();
    sellerSeatPid = Objects.nonNull(user.getSellerSeat()) ? user.getSellerSeat().getPid() : null;
    dealAdmin = user.isDealAdmin();

    entitlements = userAuth.getEntitlements();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public Role getRole() {
    return role;
  }

  public CompanyType getType() {
    return type;
  }

  public boolean canAccess(Long companyPid) {
    return companyPids.contains(companyPid);
  }

  /**
   * @param sellerIds A set of seller Ids to check if the current user have access to them
   * @return boolean true or false.
   */
  public boolean canAccess(Set<Long> sellerIds) {
    return companyPids.containsAll(sellerIds);
  }

  /**
   * @param sellerIds A set of sellerIds to check if they belong to the current User's seller-seat
   * @return boolean true or false.
   */
  public boolean isAssociatedWithSellerSeat(Set<Long> sellerIds) {
    return sellerSeatCompanies.containsAll(sellerIds);
  }

  /**
   * Checks if the sellerSeatPid provided matches the current user and returns false if they do not.
   * Also returns false if the current user has a sellerSeatPid of null.
   *
   * @param sellerSeatPid sellerSeat to compare user to
   * @return returns boolean result of check
   */
  public boolean canAccessSellerSeat(Long sellerSeatPid) {
    return this.sellerSeatPid != null && this.sellerSeatPid.equals(sellerSeatPid);
  }

  public boolean canAccess(User user) {
    Set<Long> userCompanyPids = extractCompanyPids(user);
    return companyPids.containsAll(userCompanyPids) && userCompanyPids.size() <= companyPids.size();
  }

  public Set<Long> getCompanyPids() {
    return companyPids;
  }

  public boolean isPublisherSelfServeEnabled(Long companyPid) {
    return publisherSelfServe.contains(companyPid);
  }

  public long getPid() {
    return pid;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SpringUserDetails other = (SpringUserDetails) obj;
    if (username == null) {
      if (other.username != null) return false;
    } else if (!username.equals(other.username)) {
      return false;
    }
    if (password == null) {
      if (other.password != null) return false;
    } else if (!password.equals(other.password)) {
      return false;
    }
    if (authorities == null) {
      return other.authorities == null;
    } else return authorities.equals(other.authorities);
  }

  private Set<Long> extractCompanyPids(User user) {
    if (CollectionUtils.isEmpty(user.getCompanies())) {
      return singleton(user.getCompanyPid());
    }
    return user.getCompanies().stream()
        .mapToLong(Company::getPid)
        .collect(TreeSet::new, Set::add, Set::addAll);
  }
}
