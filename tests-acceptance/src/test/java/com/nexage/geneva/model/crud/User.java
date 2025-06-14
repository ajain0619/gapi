package com.nexage.geneva.model.crud;

public class User {
  private String pid;
  private String userName;
  private String name;
  private String email;
  private String contactNumber;
  private String title;
  private String role;
  private Boolean enabled;
  private Boolean primaryContact;
  private String companyPid;
  private String id;
  private Integer version;
  private String oneCentralUserName;
  private String firstName;
  private String lastName;
  private boolean global;
  private boolean dealAdmin;

  public boolean isDealAdmin() {
    return dealAdmin;
  }

  public void setDealAdmin(boolean dealAdmin) {
    this.dealAdmin = dealAdmin;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Boolean getPrimaryContact() {
    return primaryContact;
  }

  public void setPrimaryContact(Boolean primaryContact) {
    this.primaryContact = primaryContact;
  }

  public String getCompanyPid() {
    return companyPid;
  }

  public void setCompanyPid(String companyPid) {
    this.companyPid = companyPid;
  }

  public void setCompanyId(String company_id) {
    this.companyPid = company_id;
  }

  public String getOneCentralUserName() {
    return oneCentralUserName;
  }

  public void setOneCentralUserName(String oneCentralUserName) {
    this.oneCentralUserName = oneCentralUserName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public boolean isGlobal() {
    return global;
  }

  public void setGlobal(boolean global) {
    this.global = global;
  }

  @Override
  public int hashCode() {
    int result = pid != null ? pid.hashCode() : 0;
    result = 31 * result + (userName != null ? userName.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (email != null ? email.hashCode() : 0);
    result = 31 * result + (contactNumber != null ? contactNumber.hashCode() : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (role != null ? role.hashCode() : 0);
    result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
    result = 31 * result + (primaryContact != null ? primaryContact.hashCode() : 0);
    result = 31 * result + (companyPid != null ? companyPid.hashCode() : 0);
    result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
    result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
    result = 31 * result + (oneCentralUserName != null ? oneCentralUserName.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    } else {
      User c = (User) obj;
      return this.getPid().equals(c.getPid())
          && this.getName().equals(c.getName())
          && this.getUserName().equals(c.getUserName())
          && this.getEmail().equals(c.getEmail())
          && this.getContactNumber().equals(c.getContactNumber())
          && this.getTitle().equals(c.getTitle())
          && this.getRole().equals(c.getRole())
          && this.getEnabled().equals(c.getEnabled())
          && this.getPrimaryContact().equals(c.getPrimaryContact())
          && this.getCompanyPid().equals(c.getCompanyPid())
          && this.getFirstName().equals(c.getFirstName())
          && this.getLastName().equals(c.getLastName())
          && this.getOneCentralUserName().equals(c.getOneCentralUserName());
    }
  }
}
