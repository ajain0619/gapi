package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Table(name = "as_config")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class AdserverConfiguration implements Serializable {

  private static final long serialVersionUID = -4687163027121487584L;

  @AllArgsConstructor
  @Getter
  public enum AdserverConfigurationProperty {
    THROTTLE_VALUE_PROPERTY("adserver.system.throttle"),
    FREQUENCY_CAPPING_ENABLED_PROPERTY("adserver.system.frequency.capping.enabled"),
    DISPLAY_TTL_PROPERTY("adserver.system.display.ttl"),
    CLICK_TTL_PROPERTY("adserver.system.click.ttl"),
    IMPRESSION_COUNT_MODE_PROPERTY("adserver.system.impression.count.mode"),
    ADSERVER_HOST_PROPERTIES("adserver.host"),
    CREATIVE_HOST_PROPERTIES("creative.host"),
    CREATIVE_BANNER_ALT_PROPERTIES("creative.banner.alt"),
    VALID_ADSERVER_PIDS("valid.adserver.pids");

    private final String propertyName;
  }

  @Id
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  private Long pid;

  @Column(length = 100)
  @EqualsAndHashCode.Include
  private String property;

  @Column(length = 100)
  @EqualsAndHashCode.Include
  private String value;

  private String description;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date lastUpdate;
}
