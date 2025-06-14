package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.BrandProtectionRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.response.ResponseCode;
import com.nexage.geneva.response.ResponseHandler;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class BrandProtectionSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private BrandProtectionRequests brandProtectionRequests;
  @Autowired public DatabaseUtils databaseUtils;

  private Map<String, String> requestMap;

  /* Brand Protection Tag */

  @When("^the user creates a bp tag named \"(.+?)\" from the json file \"(.+?)\"$")
  public void tag_is_created_from_json_file(String tagName, String filename) throws Throwable {
    JSONObject tag = JsonHandler.getJsonObjectFromFile(filename);
    tag.put("name", tagName);
    commonSteps.request =
        brandProtectionRequests.getCreateBrandProtectionTagRequest().setRequestPayload(tag);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then(
      "^the \"(.+?)\" response matches by name \"(.+?)\" with the expected value from json file \"(.+?)\"$")
  public void response_matches_with_expected_value_from_json_file(
      String methodName, String tagName, String filename) throws Throwable {
    JSONObject jsonResponse = JsonHandler.getJsonObjectFromFile(filename);
    jsonResponse.put("name", tagName);
    ResponseHandler.matchResponseWithExpectedResult(
        commonSteps.request,
        jsonResponse.toString(),
        commonSteps.serverResponse.object(),
        methodName);
  }

  @When("^the user updates a bp tag with name \"(.+?)\" from the json file \"(.+?)\"$")
  public void tag_is_updated_from_json_file(String tagName, String filename) throws Throwable {
    Long tagPid = databaseUtils.getBpTagByName(tagName);
    JSONObject tag = JsonHandler.getJsonObjectFromFile(filename);
    tag.put("pid", tagPid);
    tag.put("name", tagName);
    commonSteps.request =
        brandProtectionRequests.getUpdateBrandProtectionTagRequest().setRequestPayload(tag);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user fetches a bp tag with name \"(.+?)\"$")
  public void user_fetches_tag_with_pid(String tagName) throws Throwable {
    Long tagPid = databaseUtils.getBpTagByName(tagName);
    requestMap = new RequestParams().setTagPid(tagPid.toString()).getRequestParams();
    commonSteps.request =
        brandProtectionRequests.getBrandProtectionTagRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user fetches all bp tags$")
  public void user_fetches_all_bp_tags() throws Throwable {
    commonSteps.request = brandProtectionRequests.getAllBrandProtectionTagsRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the bp tag with name \"(.+?)\"$")
  public void user_deletes_tag_with_name(String tagName) throws Throwable {
    Long tagPid = databaseUtils.getBpTagByName(tagName);
    requestMap = new RequestParams().setTagPid(tagPid.toString()).getRequestParams();
    commonSteps.request =
        brandProtectionRequests.getDeleteBrandProtectionTagRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the bp tag with name \"(.+?)\" should be deleted from the database$")
  public void tag_with_pid_should_be_deleted_from_db(String tagName) throws Throwable {
    ResponseHandler.verifySuccessfulResponseCode(
        commonSteps.request, commonSteps.serverResponse, ResponseCode.NO_CONTENT);
    assertEquals(0, databaseUtils.countBpTagByName(tagName));
  }

  @Then("^bp tag audit record with revtype \"([^\"]*)\" can be found in the database$")
  public void tag_audit_with_revtype_can_be_found_in_db(String expectedRevType) throws Throwable {
    String pid = commonSteps.serverResponse.object().getString("pid");
    assertTrue(databaseUtils.containsBrandProtectionTagAuditRevType(expectedRevType, pid));
  }

  /* Brand Protection Tag Values */

  @When("^the user creates a bp tag values with name \"(.+?)\" from the json file \"(.+?)\"$")
  public void tag_values_is_created_from_json_file(String tagName, String filename)
      throws Throwable {
    JSONObject tagValues = JsonHandler.getJsonObjectFromFile(filename);
    tagValues.put("name", tagName);
    commonSteps.request =
        brandProtectionRequests
            .getCreateBrandProtectionTagValuesRequest()
            .setRequestPayload(tagValues);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates a bp tag values with name \"(.+?)\" from the json file \"(.+?)\"$")
  public void tag_values_is_updated_from_json_file(String tagValuesName, String filename)
      throws Throwable {
    Long tagValuesPid = databaseUtils.getBpTagValuesByName(tagValuesName);
    JSONObject tagValues = JsonHandler.getJsonObjectFromFile(filename);
    tagValues.put("name", tagValuesName);
    tagValues.put("pid", tagValuesPid);
    commonSteps.request =
        brandProtectionRequests
            .getUpdateBrandProtectionTagValuesRequest()
            .setRequestPayload(tagValues);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user fetches a bp tag values with name \"(.+?)\"$")
  public void user_fetches_tag_values_with_pid(String tagValuesName) throws Throwable {
    Long tagValuesPid = databaseUtils.getBpTagValuesByName(tagValuesName);
    requestMap = new RequestParams().setTagValuesPid(tagValuesPid.toString()).getRequestParams();
    commonSteps.request =
        brandProtectionRequests.getBrandProtectionTagValuesRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the bp tag values with name \"(.+?)\"$")
  public void user_deletes_tag_values_with_name(String tagValuesName) throws Throwable {
    Long tagValuesPid = databaseUtils.getBpTagValuesByName(tagValuesName);
    requestMap = new RequestParams().setTagValuesPid(tagValuesPid.toString()).getRequestParams();
    commonSteps.request =
        brandProtectionRequests
            .getDeleteBrandProtectionTagValuesRequest()
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the bp tag values with name \"(.+?)\" should be deleted from the database$")
  public void tag_values_with_pid_should_be_deleted_from_db(String tagValuesName) throws Throwable {
    ResponseHandler.verifySuccessfulResponseCode(
        commonSteps.request, commonSteps.serverResponse, ResponseCode.NO_CONTENT);
    assertEquals(0, databaseUtils.countBpTagValuesByName(tagValuesName));
  }

  @When("^the user fetches all bp tag values$")
  public void user_fetches_all_bp_tag_values() throws Throwable {
    commonSteps.request = brandProtectionRequests.getAllBrandProtectionTagValuesRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^bp tag values audit record with revtype \"([^\"]*)\" can be found in the database$")
  public void tag_values_audit_with_revtype_can_be_found_in_db(String expectedRevType)
      throws Throwable {
    String pid = commonSteps.serverResponse.object().getString("pid");
    assertTrue(databaseUtils.containsBrandProtectionTagValuesAuditRevType(expectedRevType, pid));
  }

  /* Brand Protection Category */

  @When("^the user creates a bp category named \"(.+?)\" from the json file \"(.+?)\"$")
  public void category_is_created_from_json_file(String name, String filename) throws Throwable {
    JSONObject category = JsonHandler.getJsonObjectFromFile(filename);
    category.put("name", name);
    commonSteps.request =
        brandProtectionRequests
            .getCreateBrandProtectionCategoryRequest()
            .setRequestPayload(category);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates a bp category with name \"(.+?)\" from the json file \"(.+?)\"$")
  public void category_is_updated_from_json_file(String categoryName, String filename)
      throws Throwable {
    Long categoryPid = databaseUtils.getBpCategoryByName(categoryName);
    JSONObject category = JsonHandler.getJsonObjectFromFile(filename);
    category.put("name", categoryName);
    category.put("pid", categoryPid);
    commonSteps.request =
        brandProtectionRequests
            .getUpdateBrandProtectionCategoryRequest()
            .setRequestPayload(category);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user fetches a bp category with name \"(.+?)\"$")
  public void user_fetches_category_with_pid(String categoryName) throws Throwable {
    Long categoryPid = databaseUtils.getBpCategoryByName(categoryName);
    requestMap = new RequestParams().setCategoryPid(categoryPid.toString()).getRequestParams();
    commonSteps.request =
        brandProtectionRequests.getBrandProtectionCategoryRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the bp category with name \"(.+?)\"$")
  public void user_deletes_category_with_name(String categoryName) throws Throwable {
    Long categoryPid = databaseUtils.getBpCategoryByName(categoryName);
    requestMap = new RequestParams().setCategoryPid(categoryPid.toString()).getRequestParams();
    commonSteps.request =
        brandProtectionRequests
            .getDeleteBrandProtectionCategoryRequest()
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then("^the bp category with name \"(.+?)\" should be deleted from the database$")
  public void category_with_pid_should_be_deleted_from_db(String categoryName) throws Throwable {
    ResponseHandler.verifySuccessfulResponseCode(
        commonSteps.request, commonSteps.serverResponse, ResponseCode.NO_CONTENT);
    assertEquals(0, databaseUtils.countBpCategoryByName(categoryName));
  }

  @Then("^bp category audit record with revtype \"([^\"]*)\" can be found in the database$")
  public void category_audit_with_revtype_can_be_found_in_db(String expectedRevType)
      throws Throwable {
    String pid = commonSteps.serverResponse.object().getString("pid");
    assertTrue(databaseUtils.containsBrandProtectionCategoryAuditRevType(expectedRevType, pid));
  }

  /* Crs Tag Mappings */

  @When(
      "^the user creates a crs tag mappings with bpTagId \"(.+?)\" and crsTagId \"(.+?)\" from the json file \"(.+?)\"$")
  public void tag_mappings_is_created_from_json_file(
      String bpTagId, String crsTagId, String filename) throws Throwable {
    JSONObject tagMappings = JsonHandler.getJsonObjectFromFile(filename);
    tagMappings.put("brandProtectionTagPid", bpTagId);
    tagMappings.put("crsTagId", crsTagId);
    commonSteps.request =
        brandProtectionRequests.getCreateCrsTagMappingsRequest().setRequestPayload(tagMappings);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then(
      "^the \"(.+?)\" response matches by bpTagId \"(.+?)\" and crsTagId \"(.+?)\" with the expected value from json file \"(.+?)\"$")
  public void tag_mappings_created_response_matches_with_expected_value_from_json_file(
      String methodName, int bpTagId, int crsTagId, String filename) throws Throwable {
    JSONObject tagMappingsCreatedResponse = JsonHandler.getJsonObjectFromFile(filename);
    tagMappingsCreatedResponse.put("brandProtectionTagPid", bpTagId);
    tagMappingsCreatedResponse.put("crsTagId", crsTagId);
    ResponseHandler.matchResponseWithExpectedResult(
        commonSteps.request,
        tagMappingsCreatedResponse.toString(),
        commonSteps.serverResponse.object(),
        methodName);
  }

  @When(
      "^the user updates a crs tag mappings with bpTagId \"(.+?)\" and crsTagId \"(.+?)\" from the json file \"(.+?)\"$")
  public void tag_mappings_is_updated_from_json_file(
      String bpTagId, String crsTagId, String filename) throws Throwable {
    Long tagMappingsPid = databaseUtils.getCrsTagMappingsByBpAndCrs(bpTagId, crsTagId);
    JSONObject tagMappings = JsonHandler.getJsonObjectFromFile(filename);
    tagMappings.put("pid", tagMappingsPid);
    tagMappings.put("brandProtectionTagPid", bpTagId);
    tagMappings.put("crsTagId", crsTagId);
    commonSteps.request =
        brandProtectionRequests.getUpdateCrsTagMappingsRequest().setRequestPayload(tagMappings);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user fetches a crs tag mappings with bpTagId \"(.+?)\" and crsTagId \"(.+?)\"$")
  public void user_fetches_tag_mappings_with_pid(String bpTagId, String crsTagId) throws Throwable {
    Long tagMappingsPid = databaseUtils.getCrsTagMappingsByBpAndCrs(bpTagId, crsTagId);
    requestMap =
        new RequestParams().setTagMappingsPid(tagMappingsPid.toString()).getRequestParams();
    commonSteps.request =
        brandProtectionRequests.getCrsTagMappingsRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes the crs tag mappings with bpTagId \"(.+?)\" and crsTagId \"(.+?)\"$")
  public void user_deletes_tag_mappings_with_name(String bpTagId, String crsTagId)
      throws Throwable {
    Long tagMappingsPid = databaseUtils.getCrsTagMappingsByBpAndCrs(bpTagId, crsTagId);
    requestMap =
        new RequestParams().setTagMappingsPid(tagMappingsPid.toString()).getRequestParams();
    commonSteps.request =
        brandProtectionRequests.getDeleteCrsTagMappingsRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Then(
      "^the crs tag mappings with bpTagId \"(.+?)\" and crsTagId \"(.+?)\" should be deleted from the database$")
  public void tag_mappings_with_pid_should_be_deleted_from_db(String bpTagId, String crsTagId)
      throws Throwable {
    ResponseHandler.verifySuccessfulResponseCode(
        commonSteps.request, commonSteps.serverResponse, ResponseCode.NO_CONTENT);
    assertEquals(0, databaseUtils.countCrsTagMappingsByBpAndCrs(bpTagId, crsTagId));
  }

  @Then("^crs tag mappings audit record with revtype \"([^\"]*)\" can be found in the database$")
  public void tag_mappings_audit_with_revtype_can_be_found_in_db(String expectedRevType)
      throws Throwable {
    String pid = commonSteps.serverResponse.object().getString("pid");
    assertTrue(databaseUtils.containsCrsTagMappingAuditRevType(expectedRevType, pid));
  }
}
