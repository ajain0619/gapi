package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.UserDTO;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.UserDTORequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.SetUpWiremockStubs;
import com.nexage.geneva.util.TestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

public class UserDTOSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private UserDTORequests userDTORequests;
  @Autowired private DatabaseUtils databaseUtils;

  private Map<String, UserDTO> userMap;
  private UserDTO userDto;
  SetUpWiremockStubs setupwm = new SetUpWiremockStubs();

  @Given("^the user searches a company pid \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
  public void the_user_searches_a_company_pid(String queryField, String page, String companyPid)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setqf(queryField).setqt(companyPid).setPage(page).getRequestParams();
    commonSteps.request =
        userDTORequests
            .getPaginatedUsersForCompanyPidRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^the user searches a seller seat pid \"([^\"]*)\" \"([^\"]*)\"$")
  public void the_user_searches_a_seller_seat_pid(String queryField, String sellerSeatPid)
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setqf(queryField).setqt(sellerSeatPid).getRequestParams();
    commonSteps.request =
        userDTORequests
            .getPaginatedUsersForSellerSeatRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^the paginated list of users are retrieved$")
  public void the_paginated_list_of_users_are_retrieved() throws Throwable {
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.request =
        userDTORequests.getPaginatedUsersList().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^the paginated list of users are retrieved based on the search criteria$")
  public void the_paginated_list_of_users_are_retrieved_based_on_the_search_criteria()
      throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setqf("userName").setqt("crudPositionManager").getRequestParams();
    commonSteps.request =
        userDTORequests.getPaginatedUsersList().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Given("^the user searches a user pid \"([^\"]*)\"$")
  public void the_user_searches_a_user_pid(String userPid) throws Throwable {
    commonSteps.requestMap = new RequestParams().setUserPid(userPid).getRequestParams();
    commonSteps.request =
        userDTORequests.getUserForUserPidRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^users are retrieved$")
  public void users_are_retrieved() throws Throwable {
    commonSteps.request = userDTORequests.getGetUsersV1Request();
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      JSONObject jsonObject = commonSteps.serverResponse.object();
      JSONArray array = new JSONArray();

      if (jsonObject.get("content") instanceof JSONArray) {
        array = jsonObject.getJSONArray("content");
      }
      assertTrue(array.length() > 0, "Users are not found");

      userMap = new HashMap<>();
      for (int i = 0; i < array.length(); i++) {
        userDto = TestUtils.mapper.readValue(array.get(i).toString(), UserDTO.class);
        userMap.put(userDto.getUserName().toLowerCase(), userDto);
      }
    }
  }

  @When("^the user creates a userdto from the json file \"(.+?)\"$")
  public void the_user_creates_a_user_from_the_json_file(String filename) throws Throwable {
    String wmResponseBody = "";
    JSONObject newUser = JsonHandler.getJsonObjectFromFile(filename);
    String[] names = newUser.getString("name").split(" ");
    newUser.put("firstName", names[0]);
    newUser.put("lastName", names[1]);
    wmResponseBody =
        "{\"firstName\":\""
            + names[0]
            + "\",\"lastName\":\""
            + names[1]
            + "\",\"email\":\""
            + newUser.getString("email")
            + "\",\"username\":\""
            + newUser.getString("userName")
            + "\"}";

    String wmResponseBodyList =
        "{ \"list\": [{\"firstName\":\""
            + newUser.getString("firstName")
            + "\",\"lastName\":\""
            + newUser.getString("lastName")
            + "\",\"email\":\""
            + newUser.getString("email")
            + "\",\"username\":\""
            + newUser.getString("userName")
            + "\"}], \"totalCount\": 1}";

    setupwm.setUpWireMockFind1CUserByEmail(newUser.getString("email"), wmResponseBodyList);
    setupwm.setUpWireMockCreateUser(wmResponseBody);
    commonSteps.request = userDTORequests.getCreateUserRequest().setRequestPayload(newUser);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates a userdto \"(.+?)\" from the json file \"(.+?)\"$")
  public void the_user_updates_a_user_from_the_json_file(String username, String filename)
      throws Throwable {
    getUserByUsername(username.toLowerCase());
    JSONObject updateUser = JsonHandler.getJsonObjectFromFile(filename);
    updateUser.put("pid", userDto.getPid());

    commonSteps.requestMap = new RequestParams().setUserPid(userDto.getPid()).getRequestParams();
    String wmResponseBody =
        "{\"firstName\":\""
            + userDto.getFirstName()
            + "\",\"lastName\":\""
            + userDto.getLastName()
            + "\",\"email\":\""
            + updateUser.getString("email")
            + "\",\"username\":\""
            + userDto.getOneCentralUserName()
            + "\"}";
    setupwm.setUpWireMockUpdateUser(userDto.getOneCentralUserName(), wmResponseBody);
    setupwm.setupWireMockCreateRole();
    setupwm.setupWireMockGetRoles();
    setupwm.setupWireMockDeleteRoles();
    commonSteps.request =
        userDTORequests
            .getUpdateUserRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(updateUser);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^current user logged in details are retrieved$")
  public void current_user_logged_in_details_are_retrieved() throws Throwable {
    commonSteps.request = userDTORequests.getGetCurrentUserV1Request();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates a duplicate 1c userdto from the json file \"(.+?)\"$")
  public void the_user_creates_a_duplicate_1cuser_from_the_json_file(String filename)
      throws Throwable {
    JSONObject newUser = JsonHandler.getJsonObjectFromFile(filename);
    String[] names = newUser.getString("name").split(" ");
    if (names[0].equals("")) {
      newUser.put("firstName", "nexageTestFirstName");
    } else {
      newUser.put("firstName", names[0]);
    }
    if (names.length == 1) {
      newUser.put("lastName", "nexageTestLastName");
      newUser.put("name", newUser.get("firstName") + " " + newUser.get("lastName"));
    } else {
      newUser.put("lastName", names[1]);
    }

    String wmResponseBodyList =
        "{ \"list\": [{\"firstName\":\""
            + newUser.getString("firstName")
            + "\",\"lastName\":\""
            + newUser.getString("lastName")
            + "\",\"email\":\""
            + newUser.getString("email")
            + "\",\"username\":\""
            + newUser.getString("userName")
            + "\"}], \"totalCount\": 1}";

    setupwm.setUpWireMockFind1CUserByEmail(newUser.getString("email"), wmResponseBodyList);
    commonSteps.request = userDTORequests.getCreateUserRequest().setRequestPayload(newUser);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the current user details are retrieved based on the \"(.*?)\" and \"(.*?)\"$")
  public void theCurrentUserDetailsAreRetrievedBasedOnTheAnd(String qf, String qt)
      throws Throwable {
    RequestParams reqParams = new RequestParams();
    if (!StringUtils.isEmpty(qf)) {
      reqParams.setqf(qf);
    }
    if (!StringUtils.isEmpty(qt)) {
      reqParams.setqt(qt);
    }
    commonSteps.requestMap = reqParams.getRequestParams();
    commonSteps.request =
        userDTORequests.getPaginatedUsersList().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^user \"(.+?)\" is selected$")
  public void the_user_selects_user(String username) throws Throwable {
    getUserByUsername(username);
  }

  @When("^user data is retrieved$")
  public void user_data_is_retrieved() throws Throwable {
    commonSteps.requestMap = new RequestParams().setUserPid(userDto.getPid()).getRequestParams();
    commonSteps.request =
        userDTORequests.getUserForUserPidRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private void getUserByUsername(String username) {
    userDto = userMap.get(username);
    assertNotNull(userDto, String.format("User [%s] is not found", username));
  }

  public String getUserPidByUsername(String username) {
    return userMap.get(username).getPid();
  }

  public String getSelectedUserPid() {
    return userDto.getPid();
  }
}
