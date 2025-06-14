package com.nexage.admin.core.audit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@JsonInclude(Include.NON_NULL)
public class RevisionInfo {

  private final Number revision;
  @JsonIgnore private final Date date;
  private final String userName;

  private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

  public RevisionInfo(Number revision, String userName, Date date) {
    this.date = new Date(date.getTime());
    this.revision = revision;
    this.userName = userName;
  }

  public Number getRevision() {
    return revision;
  }

  public Date getDate() {
    return (Date) date.clone();
  }

  public String getRevisionDate() {
    String revisionDate = null;
    if (date != null) {
      revisionDate =
          format.format(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }
    return revisionDate;
  }

  public String getUserName() {
    return userName;
  }
}
