package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.geneva.request.Request;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.RtbProfileLibrariesRequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.geneva.JsonField;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

public class RtbProfileLibrariesSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private RtbProfileLibrariesRequests requests;

  private JSONObject expectedLibrary;
  private String libraryPid;

  private static final String ANY_COMPANY_PID = "0000";
  private static final String ANY_RTB_LIBRARY_PID = "0000";

  @When("^the user searches all RTB Profile Libraries$")
  public void the_user_searches_all_RTB_Profile_Libraries() throws Throwable {
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.request =
        requests.getGetAllProfileLibrariesRequest(true).setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches all RTB Profile Libraries with parameter in the url$")
  public void the_user_searches_all_RTB_Profile_Libraries_with_parameter() throws Throwable {
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.request =
        requests
            .getGetAllProfileLibrariesRequestWithParam()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user searches all RTB Profile Libraries$")
  public void the_PSS_user_searches_all_RTB_Profile_Libraries() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests.getGetAllProfileLibrariesPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the PSS user searches all RTB Profile Libraries for a publisher without ref seller_admin parameter$")
  public void the_PSS_user_searches_all_RTB_Profile_Libraries_for_a_publisher() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests.getGetAllProfileLibrariesPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the PSS user searches all RTB Profile Libraries for a publisher WITH ref seller_admin parameter$")
  public void the_PSS_user_searches_all_RTB_Profile_Libraries_for_a_publisher_with_parameter()
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests
            .getGetAllProfileLibrariesPssRequestWithParameter()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches RTB Profile Library \"(.+?)\"$")
  public void the_user_searches_RTB_Profile_Library(String name) throws Throwable {
    libraryPid = getRtbLibraryPidByName(name);
    commonSteps.requestMap = new RequestParams().setRtbLibraryPid(libraryPid).getRequestParams();
    commonSteps.request =
        requests.getGetProfileLibraryRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user searches RTB Profile Library \"(.+?)\"$")
  public void the_PSS_user_searches_RTB_Profile_Library(String name) throws Throwable {
    libraryPid = getRtbLibraryPidByNamePss(name);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setRtbLibraryPid(libraryPid)
            .getRequestParams();
    commonSteps.request =
        requests.getGetProfileLibraryPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @And("^the user updates the RTB Profile Library from the json file \"(.+?)\"$")
  public void the_user_updates_the_RTB_Profile_Library_from_json_file(String filename)
      throws Throwable {
    expectedLibrary = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap = new RequestParams().setRtbLibraryPid(libraryPid).getRequestParams();
    commonSteps.request =
        requests
            .getUpdateProfileLibraryRequest(true)
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedLibrary);
    commonFunctions.executeRequest(commonSteps);
  }

  @And("^the PSS user updates the RTB Profile Library from the json file \"(.+?)\"$")
  public void the_PSS_user_updates_the_RTB_Profile_Library_from_json_file(String filename)
      throws Throwable {
    expectedLibrary = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setRtbLibraryPid(libraryPid)
            .getRequestParams();
    commonSteps.request =
        requests
            .getUpdateProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedLibrary);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates RTB Profile Library from the json file \"(.+?)\"$")
  public void the_user_creates_RTB_Profile_Library_from_json_file(String filename)
      throws Throwable {
    expectedLibrary = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request =
        requests.getCreateProfileLibraryRequest(true).setRequestPayload(expectedLibrary);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user creates RTB Profile Library from the json file \"(.+?)\"$")
  public void the_PSS_user_creates_RTB_Profile_Library_from_json_file(String filename)
      throws Throwable {
    expectedLibrary = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests
            .getCreateProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(expectedLibrary);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user clones RTB Profile Library from the json file \"(.+?)\"$")
  public void the_user_clones_RTB_Profile_Library_from_json_file(String filename) throws Throwable {
    JSONObject rtbProfileLibraryCloneData = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request =
        requests.getCloneProfileLibraryRequest().setRequestPayload(rtbProfileLibraryCloneData);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user clones RTB Profile Library from the json file \"(.+?)\"$")
  public void the_PSS_user_clones_RTB_Profile_Library_from_json_file(String filename)
      throws Throwable {
    JSONObject libraryCloneData = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests
            .getCloneProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(libraryCloneData);
    commonFunctions.executeRequest(commonSteps);
  }

  @And("^the user deletes the RTB Profile Library with follow redirect is \"(.+?)\"$")
  public void the_user_deletes_the_RTB_Profile_Library(String redirect) throws Throwable {
    commonSteps.requestMap = new RequestParams().setRtbLibraryPid(libraryPid).getRequestParams();
    commonSteps.request =
        requests
            .getDeleteProfileLibraryRequest(Boolean.valueOf(redirect))
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @And("^the PSS user deletes the RTB Profile Library$")
  public void the_PSS_user_deletes_the_RTB_Profile_Library() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setRtbLibraryPid(libraryPid)
            .getRequestParams();
    commonSteps.request =
        requests.getDeleteProfileLibraryPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user tries to create RTB Profile Library from the json file \"(.+?)\" with follow redirect is \"(.+?)\"$")
  public void the_user_tries_to_create_RTB_Profile_Library_from_json_file(
      String filename, String redirect) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request =
        requests
            .getCreateProfileLibraryRequest(Boolean.valueOf(redirect))
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to create a RTB Profile Library from the json file \"(.+?)\"$")
  public void the_PSS_user_tries_to_create_a_RTB_Profile_Library_from_json_file(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        requests
            .getCreateProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @And(
      "^the user tries to update the RTB Profile Library from the json file \"(.+?)\" with follow redirect is \"(.*?)\"$")
  public void the_user_tries_to_update_the_RTB_Profile_Library_from_json_file(
      String filename, String redirect) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap = new RequestParams().setRtbLibraryPid(libraryPid).getRequestParams();
    commonSteps.request =
        requests
            .getUpdateProfileLibraryRequest(Boolean.valueOf(redirect))
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @And("^the PSS user tries to update RTB Profile Library from the json file \"(.+?)\"$")
  public void the_PSS_user_tries_to_update_RTB_Profile_Library_from_json_file(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setRtbLibraryPid(libraryPid)
            .getRequestParams();
    commonSteps.request =
        requests
            .getUpdateProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to update any RTB Profile Library from the json file \"(.+?)\"$")
  public void the_PSS_user_tries_to_update_any_RTB_Profile_Library_from_json_file(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setRtbLibraryPid(ANY_RTB_LIBRARY_PID)
            .getRequestParams();
    commonSteps.request =
        requests
            .getUpdateProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to search any RTB Profile Library with follow redirect is \"(.+?)\"$")
  public void the_user_tries_to_search_any_RTB_Profile_Library(String redirect) throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setRtbLibraryPid(ANY_RTB_LIBRARY_PID).getRequestParams();
    commonSteps.request =
        requests
            .getGetProfileLibraryRequest()
            .setRequestParams(commonSteps.requestMap)
            .setFollowRedirects(Boolean.valueOf(redirect));
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to search any RTB Profile Library$")
  public void the_PSS_user_tries_to_search_any_RTB_Profile_Library() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setRtbLibraryPid(ANY_RTB_LIBRARY_PID)
            .getRequestParams();
    commonSteps.request =
        requests.getGetProfileLibraryPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to delete any RTB Profile Library with follow redirect is \"(.+?)\"$")
  public void the_user_tries_to_delete_a_non_existing_RTB_Profile_Library(String redirect)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setRtbLibraryPid(ANY_RTB_LIBRARY_PID).getRequestParams();
    commonSteps.request =
        requests
            .getDeleteProfileLibraryRequest(Boolean.valueOf(redirect))
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to delete any RTB Profile Library$")
  public void the_PSS_user_tries_to_delete_any_RTB_Profile_Library() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setRtbLibraryPid(ANY_RTB_LIBRARY_PID)
            .getRequestParams();
    commonSteps.request =
        requests.getDeleteProfileLibraryPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to update any RTB Profile Library with follow redirect is \"(.+?)\"$")
  public void the_user_tries_to_update_a_non_existing_RTB_Profile_Library(String redirect)
      throws Throwable {
    JSONObject anyPayload = new JSONObject("{}");
    commonSteps.requestMap =
        new RequestParams().setRtbLibraryPid(ANY_RTB_LIBRARY_PID).getRequestParams();
    commonSteps.request =
        requests
            .getUpdateProfileLibraryRequest(Boolean.valueOf(redirect))
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(anyPayload);

    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user tries to search all RTB Profile Libraries with follow redirect is \"(.+?)\"$")
  public void the_user_tries_to_search_all_RTB_Profile_Libraries(boolean redirect)
      throws Throwable {
    commonSteps.request = requests.getGetAllProfileLibrariesRequest(redirect);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to delete RTB Profile Library of any publisher$")
  public void the_PSS_user_tries_to_delete_RTB_Profile_Library_of_any_publisher() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(ANY_COMPANY_PID)
            .setRtbLibraryPid(ANY_RTB_LIBRARY_PID)
            .getRequestParams();
    commonSteps.request =
        requests.getDeleteProfileLibraryPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the PSS user tries to create RTB Profile Library of any publisher from the json file \"(.+?)\"$")
  public void the_PSS_user_tries_to_create_RTB_Profile_Library_of_any_publisher(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(ANY_COMPANY_PID).getRequestParams();
    commonSteps.request =
        requests
            .getCreateProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the PSS user tries to search RTB Profile Library of any publisher$")
  public void the_PSS_user_tries_to_search_RTB_Profile_Library_of_any_publisher() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(ANY_COMPANY_PID)
            .setRtbLibraryPid(ANY_RTB_LIBRARY_PID)
            .getRequestParams();
    commonSteps.request =
        requests.getGetProfileLibraryPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the PSS user tries to update RTB Profile Library of any publisher from the json file \"(.+?)\"$")
  public void the_PSS_user_tries_to_update_RTB_Profile_Library_of_any_publisher(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(ANY_COMPANY_PID)
            .setRtbLibraryPid(ANY_RTB_LIBRARY_PID)
            .getRequestParams();
    commonSteps.request =
        requests
            .getUpdateProfileLibraryPssRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @And("^the user cannot search deleted library$")
  public void the_user_cannot_search_deleted_library() throws Throwable {
    commonSteps.requestMap = new RequestParams().setRtbLibraryPid(libraryPid).getRequestParams();
    commonSteps.request =
        requests.getGetProfileLibraryRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
    assertNotNull("RTB Profile library can be searched out.", commonSteps.exceptionMessage);
  }

  @And("^the PSS user cannot search deleted library$")
  public void the_PSS_user_cannot_search_deleted_library() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setPublisherPid(commonSteps.companyPid)
            .setRtbLibraryPid(libraryPid)
            .getRequestParams();
    commonSteps.request =
        requests.getGetProfileLibraryPssRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
    assertNotNull("RTB Profile library can be searched out.", commonSteps.exceptionMessage);
  }

  private String getRtbLibraryPidByName(String name) throws Throwable {
    Request getAllLibraries = requests.getGetAllProfileLibrariesRequest(true);
    JSONArray allRtbProfileLibraries = getAllLibraries.execute().array();
    return getRtbProfileLibrary(allRtbProfileLibraries, name);
  }

  private String getRtbLibraryPidByNamePss(String name) throws Throwable {
    RequestParams requestParamsForGetAllLibraries =
        new RequestParams().setPublisherPid(commonSteps.companyPid);
    Request getAllLibrariesPss =
        requests
            .getGetAllProfileLibrariesPssRequest()
            .setRequestParams(requestParamsForGetAllLibraries.getRequestParams());
    JSONArray allRtbProfileLibraries = getAllLibrariesPss.execute().array();
    return getRtbProfileLibrary(allRtbProfileLibraries, name);
  }

  private String getRtbProfileLibrary(JSONArray allRtbProfileLibraries, String name)
      throws Throwable {
    for (int i = 0; i < allRtbProfileLibraries.length(); i++) {
      JSONObject rtbProfileLibrary = allRtbProfileLibraries.getJSONObject(i);
      if (rtbProfileLibrary.getString(JsonField.NAME).equals(name)) {
        return rtbProfileLibrary.getString(JsonField.PID);
      }
    }
    return null;
  }
}
