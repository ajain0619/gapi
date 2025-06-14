package com.nexage.admin.core.model;

import com.nexage.admin.core.util.GlobalConfigUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "global_config")
public class GlobalConfig implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "pid", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  protected Long pid;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date updatedOn;

  @Column(length = 100)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String property;

  @Column(length = 100)
  private String value;

  @Column(length = 255)
  private String description;

  /**
   * Gets the value as a {@code String}.
   *
   * @return the configuration value.
   */
  public String getStringValue() {
    return getValue();
  }

  /**
   * Decodes the value as a {@code Long}. If there is a {@code NumberFormatException} on parsing the
   * value, {@code null} is returned.
   *
   * @return the decoded {@code Long} value.
   */
  public Long getLongValue() {
    return GlobalConfigUtil.getLongValue(this);
  }

  /**
   * Decodes the value as an {@code Integer}. If there is a {@code NumberFormatException} on parsing
   * the value, {@code null} is returned.
   *
   * @return the decoded {@code Integer} value.
   */
  public Integer getIntegerValue() {
    return GlobalConfigUtil.getIntegerValue(this);
  }

  /**
   * Decodes the value as a {@code Boolean}. This method will return {@code true} if the value is a
   * string equal to {@code "true"} ignoring case. Otherwise, {@code false} is returned.
   *
   * @return the decoded {@code Boolean} value.
   */
  public Boolean getBooleanValue() {
    return GlobalConfigUtil.getBooleanValue(this);
  }

  /**
   * Decodes the value as a comma separated list of numbers ({@code Long}s). Note: This method is
   * completely safe to call. It will never throw an exception. It will return an empty list if
   * there is a failure.
   *
   * @return the decoded list of {@code Long}s.
   */
  public List<Long> getLongListValue() {
    return GlobalConfigUtil.getCsvValueAsLongList(this);
  }
}
