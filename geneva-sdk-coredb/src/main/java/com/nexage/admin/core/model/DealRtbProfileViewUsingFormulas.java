package com.nexage.admin.core.model;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.sparta.jpa.model.DealTagRuleViewNoTagReference;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;

@SuppressWarnings("JpaQlInspection")
@Immutable
@Table(name = "exchange_site_tag")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealRtbProfileViewUsingFormulas implements Serializable {

  private static final long serialVersionUID = -1433303432515912127L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  private Long pid;

  @Column(name = "description")
  @EqualsAndHashCode.Include
  private String description;

  @Column(name = "site_alias")
  private Long siteAlias;

  @Column(name = "site_name_alias")
  private String siteNameAlias;

  @Column(name = "site_type")
  private char siteType;

  @Column(name = "pub_alias")
  private Long pubAlias;

  @Column(name = "tag_id")
  private String tagId;

  @Column(name = "pub_name_alias")
  private String pubNameAlias;

  @Column(name = "default_reserve")
  private BigDecimal defaultReserve;

  @Column(name = "low_reserve")
  private BigDecimal lowReserve;

  @Column(name = "auction_type")
  private int auctionType;

  @Column(name = "include_site_name")
  private Integer includeSiteName;

  @Column(name = "site_pid")
  private Long sitePid;

  @Column(name = "tag_pid")
  private Long exchangeTagPid;

  @Formula("(SELECT s.name FROM site s WHERE s.pid = site_pid)")
  private String siteName;

  @Formula("(SELECT s.platform FROM site s WHERE s.pid = site_pid)")
  private String platform;

  @Formula(
      "(SELECT c.name FROM site s inner join company c on c.pid = s.company_pid WHERE s.pid = site_pid)")
  private String pubName;

  @Formula(
      "(SELECT c.pid FROM site s inner join company c on c.pid = s.company_pid WHERE s.pid = site_pid)")
  private Long pubPid;

  @Formula("(SELECT group_concat(ic.category) FROM iab_cat ic WHERE ic.site_pid=site_pid)")
  private String categories;

  // need to limit 1, because due to data quality problems, there can be multiple tags whose
  // primary_id map to an rtb profile
  @Formula("(SELECT t.pid FROM tag t WHERE t.primary_id = tag_id LIMIT 1)")
  private Long tagPid;

  @Formula("(SELECT t.status FROM tag t WHERE t.primary_id = tag_id LIMIT 1)")
  private Integer status;

  @Formula("(SELECT t.name FROM tag t WHERE t.primary_id = tag_id LIMIT 1)")
  private String tagName;

  @Formula(
      "(SELECT coalesce(t.video_support,p.video_support) FROM tag t LEFT OUTER JOIN position p ON t.position_pid=p.pid WHERE t.primary_id=tag_id)")
  private Integer videoSupport;

  @Formula(
      "(SELECT coalesce(t.height,p.height) FROM tag t LEFT OUTER JOIN position p ON t.position_pid=p.pid WHERE t.primary_id=tag_id)")
  private Integer height;

  @Formula(
      "(SELECT coalesce(t.width,p.width) FROM tag t LEFT OUTER JOIN position p ON t.position_pid=p.pid WHERE t.primary_id=tag_id)")
  private Integer width;

  @Formula("(SELECT t.position_pid FROM tag t WHERE t.pid = tag_pid LIMIT 1)")
  private Long placementPid;

  @Formula(
      "(SELECT p.name FROM tag t LEFT OUTER JOIN position p ON t.position_pid=p.pid WHERE t.primary_id=tag_id)")
  private String placementName;

  @Formula(
      "(SELECT p.placement_type FROM tag t LEFT OUTER JOIN position p ON t.position_pid=p.pid WHERE t.primary_id=tag_id)")
  private Integer placementCategory;

  @Formula(
      "(SELECT tr.target_type FROM tag t LEFT OUTER JOIN tag_rule tr ON t.pid=tr.tag_pid WHERE tr.rule_type='Country' AND t.primary_id=tag_id LIMIT 1)")
  private String targetType;

  @Formula(
      "(SELECT tr.target FROM tag t LEFT OUTER JOIN tag_rule tr ON t.pid=tr.tag_pid WHERE tr.rule_type='Country' AND t.primary_id=tag_id LIMIT 1)")
  private String target;

  @Formula(
      "(SELECT tr.rule_type FROM tag t LEFT OUTER JOIN tag_rule tr ON t.pid=tr.tag_pid WHERE tr.rule_type='Country' AND t.primary_id=tag_id LIMIT 1)")
  private String ruleType;

  @Formula(
      "(SELECT tr.pid FROM tag t LEFT OUTER JOIN tag_rule tr ON t.pid=tr.tag_pid WHERE tr.rule_type='Country' AND t.primary_id=tag_id LIMIT 1)")
  private Long countryPid;

  @Column(name = "default_rtb_profile_owner_company_pid")
  private Long defaultRtbProfileOwnerCompanyPid;

  public Set<String> getCategories() {
    return categories == null ? new HashSet<>() : Sets.newHashSet(categories.split(","));
  }

  public Set<DealTagRuleViewNoTagReference> getCountries() {
    Set<DealTagRuleViewNoTagReference> countries = Sets.newHashSet();
    if (target != null) {
      DealTagRuleViewNoTagReference dtr = new DealTagRuleViewNoTagReference();
      dtr.setPid(countryPid);
      dtr.setRuleType(ruleType);
      dtr.setTarget(target);
      dtr.setTargetType(targetType);
      countries.add(dtr);
    }
    return countries;
  }

  public Platform getPlatform() {
    return Platform.valueOf(platform);
  }

  public VideoSupport getVideoSupport() {
    return null == videoSupport ? null : VideoSupport.fromInt(videoSupport);
  }

  public PlacementCategory getPlacementType() {
    return placementCategory == null ? null : PlacementCategory.fromInt(placementCategory);
  }

  public String getIsRealName() {
    return (includeSiteName == null || 1 == includeSiteName) ? "true" : "false";
  }
}
