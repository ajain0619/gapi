package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.ProfileLibraryGroupRequest;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class ProfileLibraryGroupSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private ProfileLibraryGroupRequest libraryGroupRequest;
  @Autowired private DatabaseUtils databaseUtils;

  private JSONObject expectedLibrary;

  @When("^the user gets a group pid \"([^\"]*)\" in seller \"([^\"]*)\"$")
  public void the_user_gets_a_group_from_library(String groupPid, String sellerPid)
      throws Throwable {

    commonSteps.requestMap =
        new RequestParams().setSellerPid(sellerPid).setGroupPid(groupPid).getRequestParams();
    commonSteps.request =
        libraryGroupRequest.getGetLibraryTagRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user creates RTB Library Group Connections for seller \"([^\"]*)\" from the json file \"(.+?)\"$")
  public void the_user_creates_Tag_Library_Group_Connections_from_json_file(
      String sellerPid, String filename) throws Throwable {
    expectedLibrary = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap = new RequestParams().setSellerPid(sellerPid).getRequestParams();
    commonSteps.request =
        libraryGroupRequest
            .getPostLibraryTagRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedLibrary);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then(
      "^there are now \"(.+?)\" rtb_profile_library_association records with library pid \"(.+?)\"$")
  public void there_are_now_rtb_library_associatioon_records_with_pid(String count, String pid)
      throws Throwable {
    int expectedRecordsNumber = Integer.valueOf(count);
    int actualCount =
        databaseUtils.countCoreRecordsByFieldNameAndValue(
            "RTB_PROFILE_LIBRARY_ASSOCIATION_AUD", "LIBRARY_PID", pid);

    assertEquals(
        expectedRecordsNumber,
        actualCount,
        "Count of rtb_profile_library_association rows for RtbLibrary pid "
            + pid
            + "does not match expected value");
  }

  @Then(
      "^there are now \"(.+?)\" rtb_profile_library_association audit records with library pid \"(.+?)\"$")
  public void there_are_now_rtb_library_associatioon_audit_records_with_pid(
      String count, String pid) throws Throwable {
    int expectedRecordsNumber = Integer.valueOf(count);
    int actualCount =
        databaseUtils.countCoreRecordsByFieldNameAndValue(
            "RTB_PROFILE_LIBRARY_ASSOCIATION_AUD", "LIBRARY_PID", pid);

    assertEquals(
        expectedRecordsNumber,
        actualCount,
        "Count of rtb_profile_library_association rows for RtbLibrary pid "
            + pid
            + "does not match expected value");
  }

  @Then("^library pid \"(.+?)\" now associated with exchange tag pids$")
  public void library_associated_with_rtb_profiles(String libPid, List<String> eTagPids)
      throws Throwable {
    if (eTagPids == null
        || eTagPids.size() == 1 && eTagPids.get(0) == null
        || eTagPids.size() == 1 && eTagPids.get(0) != null && eTagPids.get(0).equals("")) {
      eTagPids = new ArrayList<>();
    }
    List<String> tags = databaseUtils.getExchageTagPidsAssociatedWithLibraryPid(libPid);
    assertEquals(
        eTagPids.size(),
        tags.size(),
        "Number of rtb profiles associated with the libraray is wrong");
    for (String eTag : eTagPids) {
      assertTrue(
          tags.contains(eTag),
          "Library pid " + libPid + " is not associated with exchange tag " + eTag);
    }
  }

  @Then("^library pid \"(.+?)\" now associated with exchange tag pid \"(.+?)\"$")
  public void library_associated_with_rtb_profile(String libPid, String eTagPid) throws Throwable {
    List<String> tags = databaseUtils.getExchageTagPidsAssociatedWithLibraryPid(libPid);
    assertTrue(
        tags.contains(eTagPid),
        "Library pid " + libPid + " is not associated with exchange tag " + eTagPid);
  }

  @Then("^library pid \"(.+?)\" now not associated with exchange tag pid \"(.+?)\"$")
  public void library_not_associated_with_rtb_profile(String libPid, String eTagPid)
      throws Throwable {
    List<String> tags = databaseUtils.getExchageTagPidsAssociatedWithLibraryPid(libPid);
    assertFalse(
        tags.contains(eTagPid),
        "Library pid " + libPid + " is not associated with exchange tag " + eTagPid);
  }
}
