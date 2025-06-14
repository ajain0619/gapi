package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Version;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Defines the base class for all models defined in the system. This base class provides default
 * implementation of <code>equals</code>, <code>toString</code> and <code>hashCode</code> methods
 * using reflection based builders provided by Apache Commons Lang. The deriving class should
 * override these methods to provide model specific implementation.
 */
@SqlResultSetMapping(
    name = "pidNameReturn",
    columns = {@ColumnResult(name = "pid"), @ColumnResult(name = "name")})
@MappedSuperclass
public abstract class BaseModel implements Serializable {

  private static final long serialVersionUID = 8132600455944553898L;

  @Column(name = "id", nullable = false, length = 32)
  protected String id;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @Id
  protected Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  protected Integer version;

  /** @return the guid */
  public String getId() {
    return id;
  }

  /** @param guid the guid to set */
  public void setId(String guid) {
    this.id = guid;
  }

  /** @return the pid */
  public Long getPid() {
    return pid;
  }

  /** @param pid the pid to set */
  public void setPid(Long pid) {
    this.pid = pid;
  }

  /** @return the version */
  public Integer getVersion() {
    return version;
  }

  /** @param version the version to set */
  public void setVersion(Integer version) {
    this.version = version;
  }

  /** Generates the string representation of this model. */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  /**
   * Compares object equality. When using Hibernate, the primary key should not be a part of this
   * comparison.
   *
   * @param o object to compare to
   * @return true/false based on equality tests
   */
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  /**
   * When you override equals, you should override hashCode. See "Why are equals() and hashCode()
   * importation" for more information: http://www.hibernate.org/109.html
   *
   * @return hashCode
   */
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(17, 37, this);
  }
}
