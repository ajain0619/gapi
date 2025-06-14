package com.nexage.admin.core.dto;

import com.nexage.admin.core.enums.Status;
import java.io.Serializable;

public class SiteSearchSummaryDTO extends SearchSummaryDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private final Status status;
  private final String company;

  public SiteSearchSummaryDTO(long pid, String name, Type type, Status status, String company) {
    super(pid, name, type);
    this.status = status;
    this.company = company;
  }

  public Status getStatus() {
    return status;
  }

  public String getCompany() {
    return company;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + (int) (pid ^ (pid >>> 32));
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CompanySearchSummaryDTO other = (CompanySearchSummaryDTO) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (pid != other.pid) return false;
    if (type != other.type) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SiteSearchSummary [name=" + name + ", pid=" + pid + ", type=" + type + "]";
  }
}
