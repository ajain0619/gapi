package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.RtbProfileLibrariesIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RtbProfileLibrariesRequests {

  @Autowired private Request request;

  public Request getCreateProfileLibraryRequest(boolean redirect) {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/rtbprofilelibrary")
        .setExpectedObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.expectedObjectCreate)
        .setActualObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.actualObjectCreate)
        .setFollowRedirects(redirect);
  }

  public Request getGetAllProfileLibrariesRequest(boolean redirect) {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/rtbprofilelibrary")
        .setFollowRedirects(redirect);
  }

  public Request getGetAllProfileLibrariesRequestWithParam() {
    return request.clear().setGetStrategy().setUrlPattern("/rtbprofilelibrary?ref=seller_admin");
  }

  public Request getGetProfileLibraryRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/rtbprofilelibrary/" + RequestParams.PROFILE_LIBRARY_PID);
  }

  public Request getUpdateProfileLibraryRequest(boolean redirect) {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern("/rtbprofilelibrary/" + RequestParams.PROFILE_LIBRARY_PID)
        .setExpectedObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.expectedObjectUpdate)
        .setActualObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.actualObjectUpdate)
        .setFollowRedirects(redirect);
  }

  public Request getDeleteProfileLibraryRequest(boolean redirect) {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern("/rtbprofilelibrary/" + RequestParams.PROFILE_LIBRARY_PID)
        .setFollowRedirects(redirect);
  }

  public Request getCloneProfileLibraryRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/rtbprofilelibrary/clone")
        .setExpectedObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.expectedObjectClone)
        .setActualObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.actualObjectClone);
  }

  public Request getGetAllProfileLibrariesPssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/pss/" + RequestParams.PUBLISHER_PID + "/rtbprofilelibrary/");
  }

  public Request getGetAllProfileLibrariesPssRequestWithParameter() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/" + RequestParams.PUBLISHER_PID + "/rtbprofilelibrary?ref=seller_admin");
  }

  public Request getGetProfileLibraryPssRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/rtbprofilelibrary/"
                + RequestParams.PROFILE_LIBRARY_PID);
  }

  public Request getCreateProfileLibraryPssRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/pss/" + RequestParams.PUBLISHER_PID + "/rtbprofilelibrary/")
        .setExpectedObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.expectedObjectCreate)
        .setActualObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.actualObjectCreate);
  }

  public Request getUpdateProfileLibraryPssRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/rtbprofilelibrary/"
                + RequestParams.PROFILE_LIBRARY_PID)
        .setExpectedObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.expectedObjectUpdate)
        .setActualObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.actualObjectUpdate);
  }

  public Request getDeleteProfileLibraryPssRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(
            "/pss/"
                + RequestParams.PUBLISHER_PID
                + "/rtbprofilelibrary/"
                + RequestParams.PROFILE_LIBRARY_PID);
  }

  public Request getCloneProfileLibraryPssRequest() {
    return request
        .clear()
        .setPostStrategy()
        .setUrlPattern("/pss/" + RequestParams.PUBLISHER_PID + "/rtbprofilelibrary/clone")
        .setExpectedObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.expectedObjectClone)
        .setActualObjectIgnoredKeys(RtbProfileLibrariesIgnoredKeys.actualObjectClone);
  }

  public Request getGetBidderLibrariesLimit() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/pss/" + RequestParams.PUBLISHER_PID + "/checkLimit/bidder_libraries?");
  }

  public Request getGetBlockLibrariesLimit() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/pss/" + RequestParams.PUBLISHER_PID + "/checkLimit/block_libraries?");
  }
}
