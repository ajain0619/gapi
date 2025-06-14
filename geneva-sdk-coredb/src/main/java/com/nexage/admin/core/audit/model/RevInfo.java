package com.nexage.admin.core.audit.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "REVINFO")
@RevisionEntity(NexageRevListener.class)
public class RevInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  @RevisionNumber
  @Column(name = "REV")
  private long id;

  @Column(name = "user_name")
  private String userName;

  @RevisionTimestamp
  @Column(name = "revtstmp")
  private Date timestamp;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUserName() {
    return this.userName;
  }

  public void setUserName(String name) {
    this.userName = name;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RevInfo)) return false;

    RevInfo that = (RevInfo) o;

    return (id == that.id && timestamp == that.timestamp && userName == that.userName);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;

    result = prime * result + ((userName == null) ? 0 : userName.hashCode());
    result =
        prime * result + ((timestamp == null) ? (new Date()).hashCode() : timestamp.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "RevInfo(id = "
        + id
        + ", userName = "
        + userName
        + ", revisionDate = "
        + DateFormat.getDateTimeInstance().format(timestamp)
        + ")";
  }
}
