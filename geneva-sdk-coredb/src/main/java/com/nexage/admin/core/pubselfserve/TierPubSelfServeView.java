package com.nexage.admin.core.pubselfserve;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Immutable
@Entity
@Table(name = "tier")
public class TierPubSelfServeView implements Serializable {

  @Id private Long pid;

  @Column(name = "position_pid")
  Long positionPid;

  @ManyToMany
  @LazyCollection(LazyCollectionOption.FALSE)
  @JoinTable(
      name = "tier_tag",
      joinColumns = @JoinColumn(name = "tier_pid"),
      inverseJoinColumns = @JoinColumn(name = "tag_pid"))
  private List<TagPubSelfServeView> tags = new ArrayList<>();

  public Long getPid() {
    return pid;
  }

  public Long getPositionPid() {
    return positionPid;
  }

  public List<TagPubSelfServeView> getTags() {
    return tags;
  }
}
