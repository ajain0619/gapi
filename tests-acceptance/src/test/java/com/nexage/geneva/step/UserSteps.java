package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.Site;
import com.nexage.geneva.request.Request;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.UserRequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import com.nexage.geneva.util.UUIDGenerator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import us.monoid.json.JSONObject;

public class UserSteps {

  public static final String COMPANY_APP_USER_TABLE = "COMPANY_APP_USER";
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private UserRequests userRequests;
  @Autowired private AccountServiceSteps accountServiceSteps;
  @Autowired private DatabaseUtils databaseUtils;
  @Autowired private UserDTOSteps userDTOSteps;

  private Site site;
  private Map<String, Site> siteMap;

  @When("^the user creates a duplicate user from the json file \"(.+?)\"$")
  public void the_user_creates_a_duplicate_user_from_the_json_file(String filename)
      throws Throwable {
    JSONObject newUser = JsonHandler.getJsonObjectFromFile(filename);

    commonSteps.request = userRequests.getCreateUserRequest().setRequestPayload(newUser);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates a duplicate user in db from the json file \"(.+?)\"$")
  public void the_user_creates_a_duplicate_db_user_from_the_json_file(String filename)
      throws Throwable {
    JSONObject newUser = JsonHandler.getJsonObjectFromFile(filename);
    List<String[]> data = new ArrayList<>();
    final String company_pid = "105";
    final String user_pid;
    if (filename.contains("SellerAdmin")) {
      user_pid = "843";
      data.add(
          new String[] {
            "'" + new UUIDGenerator().generateUniqueId() + "'",
            "'" + user_pid + "'",
            "''",
            "'2016-03-11 13:15:19'",
            "'105_SELLER_ADMIN@nexage.com'",
            "1",
            "0", // is_password_temporary, can't delete it now, because of preparing sql approach
            "'2016-03-11 13:16:38'",
            "'105_SELLER_ADMIN'",
            "'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3'", // password - as
            // above
            "'ROLE_ADMIN'",
            "''",
            "'105_SELLER_ADMIN'",
            "'1'",
            "'" + company_pid + "'",
            "'105_SELLER_ADMIN'",
            null,
            null,
            null,
            "0",
            "0",
            "0"
          });
    } else if (filename.contains("SellerManager")) {
      user_pid = "854";
      data.add(
          new String[] {
            "'" + new UUIDGenerator().generateUniqueId() + "'",
            "'" + user_pid + "'",
            "''",
            "'2016-03-11 13:15:19'",
            "'105_SELLER_MANAGER@nexage.com'",
            "1",
            "0", // is_password_temporary, can't delete it now, because of preparing sql approach
            "'2016-03-11 13:16:38'",
            "'105_SELLER_MANAGER'",
            "'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3'", // password - as
            // above
            "'ROLE_MANAGER'",
            "''",
            "'105_SELLER_MANAGER'",
            "'1'",
            "'" + company_pid + "'",
            "'105_SELLER_MANAGER'",
            null,
            null,
            null,
            "0",
            "0",
            "0"
          });
    } else if (filename.contains("SellerUser")) {
      user_pid = "865";
      data.add(
          new String[] {
            "'" + new UUIDGenerator().generateUniqueId() + "'",
            "'" + user_pid + "'",
            "''",
            "'2016-03-11 13:15:19'",
            "'105_SELLER_USER@nexage.com'",
            "1",
            "0", // is_password_temporary, can't delete it now, because of preparing sql approach
            "'2016-03-11 13:16:38'",
            "'105_SELLER_USER'",
            "'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3'", // password - as
            // above
            "'ROLE_USER'",
            "''",
            "'105_SELLER_USER'",
            "'1'",
            "'" + company_pid + "'",
            "'105_SELLER_USER'",
            null,
            null,
            null,
            "0",
            "0",
            "0"
          });
    } else {
      throw new Exception("Unexpected file name");
    }

    try {
      databaseUtils.insertCoreDataToTable("APP_USER", data);
      List<String[]> ts = Collections.singletonList(new String[] {company_pid, user_pid});
      databaseUtils.insertCoreDataToTable(COMPANY_APP_USER_TABLE, ts);
    } catch (DuplicateKeyException e) {
      // can't have duplicate uses, ignoring
    }
  }

  @When("^the user deletes user \"(.+?)\"")
  public void the_user_deletes_user(String username) throws Throwable {
    String userPid = userDTOSteps.getUserPidByUsername(username.toLowerCase());

    commonSteps.requestMap = new RequestParams().setUserPid(userPid).getRequestParams();
    commonSteps.request =
        userRequests.getDeleteUserRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user \"(.+?)\" deletes himself$")
  public void the_user_deletes_himself(String oneCentralUsername) throws Throwable {
    String currentUserPid = databaseUtils.getUserPidForOneCentralUser(oneCentralUsername);
    commonSteps.requestMap = new RequestParams().setUserPid(currentUserPid).getRequestParams();
    commonSteps.request =
        userRequests.getDeleteUserRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes 1c user \"(.+?)\"")
  public void the_user_deletes_1cuser(String username) throws Throwable {
    String pid = userDTOSteps.getUserPidByUsername(username.toLowerCase());

    commonSteps.requestMap = new RequestParams().setUserPid(pid).getRequestParams();
    commonSteps.request =
        userRequests.getDeleteUserRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user resets password for the user \"(.+?)\"$")
  public void the_user_resets_password_for_the_user(String username) throws Throwable {
    String userPid = userDTOSteps.getUserPidByUsername(username.toLowerCase());

    commonSteps.requestMap = new RequestParams().setUserPid(userPid).getRequestParams();
    commonSteps.request =
        userRequests.getResetUserPasswordRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      accountServiceSteps.password = getPasswordAfterReset();
    }
  }

  @When("^the user retrieves allowed sites for selected user$")
  public void the_user_retrieves_allowed_sites_for_selected_user() throws Throwable {
    String userPid = userDTOSteps.getSelectedUserPid();
    commonSteps.requestMap = new RequestParams().setUserPid(userPid).getRequestParams();
    commonSteps.request =
        userRequests.getGetAllowedSitesForUserRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      siteMap = commonFunctions.getSites(commonSteps.serverResponse);
    }
  }

  @When("^the user searches for users by selected company$")
  public void the_user_searches_for_users_by_selected_company() throws Throwable {
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.getCompany().getPid()).getRequestParams();
    commonSteps.request =
        userRequests.getGetUsersByCompanyRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^count of companies with user \"(.+?)\" as contact (\\d+?)$")
  public void count_companies_with_contact_for_user(
      String userName, Integer expectedCompanyContactCount) {
    Integer actualRuleCount = databaseUtils.countCompaniesWithContactByUsername(userName);
    assertEquals(
        expectedCompanyContactCount,
        actualRuleCount,
        "Incorrect company contact count for " + userName);
  }

  @When("^the user \"(restricts|allows)\" access to site \"(.+?)\" for selected user$")
  public void the_user_access_to_site_for_selected_user(String action, String siteName)
      throws Throwable {
    String userPid = userDTOSteps.getSelectedUserPid();
    getSiteByName(siteName);

    Request currentRequest;
    if ("restricts".equals(action)) {
      currentRequest = userRequests.getRestrictAccessToSiteRequest();
    } else {
      currentRequest = userRequests.getAllowAccessToSiteRequest();
    }

    commonSteps.requestMap =
        new RequestParams().setUserPid(userPid).setSitePid(site.getPid()).getRequestParams();
    commonSteps.request = currentRequest.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private void getSiteByName(String siteName) {
    assertNotNull(siteMap, "No sites are returned");

    site = siteMap.get(siteName);
    assertNotNull(site, String.format("Site [%s] is not found", siteName));
  }

  private String getPasswordAfterReset() throws IOException {
    InputStream serverResponseContent =
        (InputStream) commonSteps.serverResponse.getUrlConnection().getContent();
    return IOUtils.toString(serverResponseContent).replace("\"", "");
  }

  @Given("^the user \"([^\"]*)\" is attached to the company \"([^\"]*)\"$")
  public void theUserIsAssignedToTheCompany(String oneCentralUsername, String companyName)
      throws Throwable {
    String userPid = databaseUtils.getUserPidForOneCentralUser(oneCentralUsername);
    assertNotNull(userPid, "User has to exist in database");
    String companyPid = databaseUtils.getCompanyPidByName(companyName);
    assertNotNull(companyPid, "Company has to exist in the database");
    List<String[]> params = Collections.singletonList(new String[] {companyPid, userPid});
    databaseUtils.insertCoreDataToTable(COMPANY_APP_USER_TABLE, params);
  }

  @Given("^the user \"([^\"]*)\" is detached from his companies$")
  public void theUserIsDetachedFromHisCompanies(String oneCentralUsername) {
    String userPid = databaseUtils.getUserPidForOneCentralUser(oneCentralUsername);
    assertNotNull(userPid, "User has to exist in database");
    databaseUtils.deleteCoreRecordsByFieldNameAndValue(COMPANY_APP_USER_TABLE, "user_id", userPid);
  }
}
