package com.nexage.app.dto;

import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import java.io.Serializable;
import java.util.List;
import lombok.NoArgsConstructor;

/**
 * A class to encapsulate all the data sent from client to server to support the RTB Profile Library
 * clone functionality
 */
@NoArgsConstructor
public class RTBProfileLibraryCloneDataDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private List<PublisherRTBProfileLibraryDTO> libraries;
  private String privilegeLevel;
  private int listType;
  private List<String> bidderPids;
  private List<String> categories;
  private List<String> adomains;
  private Long publisherPid;

  public String getName() {
    return name;
  }

  public List<PublisherRTBProfileLibraryDTO> getLibraries() {
    return libraries;
  }

  public String getPrivilegeLevel() {
    return privilegeLevel;
  }

  public int getListType() {
    return listType;
  }

  public List<String> getBidderPids() {
    return bidderPids;
  }

  public List<String> getCategories() {
    return categories;
  }

  public List<String> getAdomains() {
    return adomains;
  }

  public Long getPublisherPid() {
    return publisherPid;
  }

  public void setPublisherPid(long pid) {
    publisherPid = pid;
  }

  public void setPublisherOwned() {
    privilegeLevel = "Publisher";
  }
}
