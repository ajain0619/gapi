package com.nexage.admin.core.model;

import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "rtb_profile_group")
@NoArgsConstructor
@Getter
@Setter
public class RtbProfileGroup implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long pid;

  @Column(name = "name", nullable = false, updatable = false, unique = true)
  private String name;

  @Version
  @Column(name = "VERSION", nullable = false)
  private Integer version;

  @Column(name = "privilege_level", nullable = false, length = 30)
  @NotNull
  @Enumerated(EnumType.STRING)
  RTBProfileLibraryPrivilegeLevel privilegeLevel;

  @Column(name = "data_type", nullable = false)
  @Type(type = "com.nexage.admin.core.usertype.RTBProfileItemType")
  ItemType itemType;

  @Column(name = "list_type", nullable = false)
  @Type(type = "com.nexage.admin.core.usertype.RTBProfileListType")
  @Enumerated(EnumType.ORDINAL)
  ListType listType;

  @Column(name = "publisher_pid", nullable = true)
  private Long publisherPid;

  @Column(nullable = false, length = 1000)
  String data;

  @Column(name = "ui_custom_group")
  boolean isUICustomGroup;

  @RequiredArgsConstructor
  public enum ItemType {
    CATEGORY(0),
    ADOMAIN(1),
    BIDDER(2);

    @Getter private final int value;
    private static final Map<Integer, ItemType> fromIntMap =
        EnumSet.allOf(ItemType.class).stream()
            .collect(Collectors.toMap(ItemType::getValue, item -> item));

    public static ItemType fromInt(int value) {
      return fromIntMap.computeIfAbsent(
          value,
          i -> {
            throw new IllegalArgumentException();
          });
    }
  }

  @RequiredArgsConstructor
  public enum ListType {
    BLOCKLIST(0),
    WHITELIST(1);

    @Getter private final int value;
    private static final Map<Integer, ListType> fromIntMap =
        EnumSet.allOf(ListType.class).stream()
            .collect(Collectors.toMap(ListType::getValue, item -> item));

    public static ListType fromInt(int value) {
      return fromIntMap.computeIfAbsent(
          value,
          i -> {
            throw new IllegalArgumentException();
          });
    }
  }
}
