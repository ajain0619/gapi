package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.base.Splitter;
import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.database.RestoreDatabaseUtils;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SellingRuleRequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

/** <code>SellingRuleSteps</code> */
@Log4j2
public class SellingRuleSteps {

  @Autowired private CommonFunctions commonFunctions;
  @Autowired private DatabaseUtils databaseUtils;
  @Autowired private CommonSteps commonSteps;
  @Autowired private SellingRuleRequests sellingRuleRequests;
  @Autowired private RestoreDatabaseUtils restoreDatabaseUtils;
  private Map<String, String> requestMap;
  private String queryField;
  private String queryFieldOperator;

  @When("^the user creates selling rule using v1 api from the json file \"(.+?)\"$")
  public void the_user_creates_selling_rule_using_v1_api_from_the_json_file(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request = sellingRuleRequests.getV1CreateRuleRequest();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all rules with \"(.+?)\" type$")
  public void the_user_gets_rules(String ruleType) throws Throwable {
    requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setRuleType(ruleType)
            .getRequestParams();
    commonSteps.request = sellingRuleRequests.getGetAllRulesRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^one rule is retrieved for UI user$")
  public void one_rule_is_retrieved_ui() throws Throwable {
    requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setRulePid(commonSteps.rulePid)
            .getRequestParams();
    commonSteps.request =
        sellingRuleRequests.getGetOneRuleForSellerRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets rule with pid \"([0-9]+)\"$")
  public void get_rule_by_pid(String pid) throws Throwable {
    requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setRulePid(pid)
            .getRequestParams();
    commonSteps.request =
        sellingRuleRequests.getGetOneRuleForSellerRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all \"(.+?)\" rules$")
  public void get_rules_by_type(String type) throws Throwable {
    requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setRuleType(type)
            .getRequestParams();
    commonSteps.request =
        sellingRuleRequests.getGetRulesForSellerByTypeRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^rules were retrieved using query field criteria$")
  public void rules_were_retrieved() throws Throwable {
    requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setqf(queryField)
            .setqo(queryFieldOperator)
            .getRequestParams();
    commonSteps.request =
        sellingRuleRequests.getGetRulesForSellerRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^rules were retrieved without query field parameter in query string$")
  public void rules_were_retrieved_without_search_criteria() throws Throwable {
    requestMap = new RequestParams().setCompanyPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request =
        sellingRuleRequests
            .getGetRulesWithoutCriteriaForSellerRequest()
            .setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user is searching for rules with query field \"([^\"]+)\" and field operator \"(.+?)\"$")
  public void the_user_is_searching_for_rules_with_query_field_and_operator(
      String queryField, String queryFieldOperator) {
    assertNotNull(queryField, "Query field is null");
    assertNotNull(queryFieldOperator, "Query field operator is null");
    assertTrue(
        queryFieldOperator.matches("(?i:and|or)"),
        "Query field operator must match 'and' or 'or' ignoring case");
    this.queryField = queryField;
    this.queryFieldOperator = queryFieldOperator;
  }

  @When("^the rule status \"(.+?)\" is correct for rule \"(.+?)\"$")
  public void the_rule_status_correct_after_delete(String expectedStatus, String ruleName)
      throws Throwable {
    String actualStatus = databaseUtils.getRuleStatusByName(ruleName);
    assertEquals(
        expectedStatus, actualStatus, "Status of the rule " + ruleName + " is not correct");
  }

  @When("^rule count for \"(.+?)\" is (\\d+?)$")
  public void count_rules_by_name(String ruleName, Integer expectedRuleCount) {
    Integer actualRuleCount = databaseUtils.countRulesByName(ruleName);
    assertEquals(expectedRuleCount, actualRuleCount, "Incorrect rule count for " + ruleName);
  }

  @When("^rule intended actions count for \"(.+?)\" is (\\d+?)$")
  public void count_intended_action_by_rule(String ruleName, Integer expectedActionCount)
      throws Throwable {
    String actualRulePid = databaseUtils.getRulePidByName(ruleName);
    assertNotNull(actualRulePid, "Rule pid is null");
    Integer actualIntendedCount = databaseUtils.countIntendedActionsByRule(actualRulePid);
    assertEquals(
        expectedActionCount,
        actualIntendedCount,
        "Incorrect intented actions count for " + ruleName);
  }

  @When("^rule target count for \"(.+?)\" is (\\d+?)$")
  public void count_targets_by_rule(String ruleName, Integer expectedTargetCount) throws Throwable {
    String actualRulePid = databaseUtils.getRulePidByName(ruleName);
    assertNotNull(actualRulePid, "Rule pid is null");
    Integer actualTargetCount = databaseUtils.countTargetsByRule(actualRulePid);
    assertEquals(expectedTargetCount, actualTargetCount, "Incorrect target count for " + ruleName);
  }

  @When(
      "^rule with name \"(.+?)\" has correct action type value \"(.+?)\" in the db and action data value \"(.+?)\"$")
  public void validate_action_type_data_in_database(
      String ruleName, String expectedActionType, String expectedActionData) throws Throwable {
    String rulePid = databaseUtils.getRulePidByName(ruleName);
    assertEquals(
        expectedActionType,
        databaseUtils.getActionTypeByRulePid(rulePid),
        "Incorrect action type for rule with name " + ruleName);
    assertEquals(
        expectedActionData,
        databaseUtils.getActionDataByRulePid(rulePid),
        "Incorrect action data for rule with name " + ruleName);
  }

  @When("^rule is deleted$")
  public void rule_is_deleted() throws Throwable {
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setRulePid(commonSteps.rulePid)
            .getRequestParams();
    commonSteps.request = sellingRuleRequests.getDeleteRuleForSellerRequest();
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @And("^the user updates the rule using v1 api from the json file \"([^\"]*)\"$")
  public void theUserUpdatesTheRuleUsingV1ApiFromTheJsonFile(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = sellingRuleRequests.getV1UpdateRuleRequest();
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setRulePid(commonSteps.rulePid)
            .getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user updates the rule with pid \"([0-9]+)\" using v1 api from the json file \"([^\"]*)\"$")
  public void theUserUpdatesTheRuleWithPidUsingV1ApiFromTheJsonFile(String pid, String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = sellingRuleRequests.getV1UpdateRuleRequest();
    commonSteps.requestMap =
        new RequestParams()
            .setCompanyPid(commonSteps.companyPid)
            .setRulePid(pid)
            .getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  // without publisher ID
  @When("^the user creates selling rule without a publisher from the json file \"(.+?)\"$")
  public void the_user_creates_selling_rule_without_a_publisher_from_the_json_file(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = sellingRuleRequests.getCreateRuleWithoutPublisher();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all selling rules without a publisher$")
  public void the_user_gets_all_selling_rules_without_a_publisher() throws Throwable {
    commonSteps.request = sellingRuleRequests.getGetAllRulesWithoutPublisher();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all rules associated with deal\"(.+?)\"$")
  public void the_user_gets_rules_associated_with_deal(String dealPid) throws Throwable {
    requestMap = new RequestParams().setDealPid(dealPid).getRequestParams();
    commonSteps.request =
        sellingRuleRequests.getRulesAssociatedWithDealRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets one rule without a publisher$")
  public void the_user_gets_certain_rule_without_a_publisher() throws Throwable {
    requestMap = new RequestParams().setRulePid(commonSteps.rulePid).getRequestParams();
    commonSteps.request =
        sellingRuleRequests.getGetOneRuleWithoutPublisherRequest().setRequestParams(requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates one rule without a publisher from the json file \"(.+?)\"$")
  public void the_user_updates_one_rule_without_a_publisher(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = sellingRuleRequests.getUpdateRuleWithoutPublisherRequest();
    commonSteps.requestMap = new RequestParams().setRulePid(commonSteps.rulePid).getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When(
      "^the user updates one rule without a publisher with pid \"(.+?)\" from the json file \"(.+?)\"$")
  public void the_user_updates_one_rule_without_a_publisher(String pid, String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = sellingRuleRequests.getUpdateRuleWithoutPublisherRequest();
    commonSteps.requestMap = new RequestParams().setRulePid(pid).getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes one rule without a publisher$")
  public void the_user_deletes_rule_without_a_publisher() throws Throwable {
    commonSteps.requestMap = new RequestParams().setRulePid(commonSteps.rulePid).getRequestParams();
    commonSteps.request = sellingRuleRequests.getDeleteRuleWithoutPublisherRequest();
    commonSteps.request.setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets placement formula grid from the json file \"(.+?)\"$")
  public void the_user_gets_placement_formula_grid(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = sellingRuleRequests.getPlacementFormulaGrid();
    commonSteps.requestMap =
        new RequestParams().setCompanyPid(commonSteps.companyPid).getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets placement formula grid for deals from the json file \"(.+?)\"$")
  public void the_user_gets_placement_formula_grid_for_deals(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = sellingRuleRequests.getPlacementFormulaGridForDeals();
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.request.setRequestParams(commonSteps.requestMap).setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  // Placement formula section

  @When("^rule formulas count for rule \"(.+?)\" is (\\d+)$")
  public void count_formulas_by_rule_name(String ruleName, Integer expectedFormalaCount)
      throws Throwable {
    String actualRulePid = databaseUtils.getRulePidByName(ruleName);
    Integer actualFormulaCount = databaseUtils.countFormulasByRule(actualRulePid);
    assertEquals(
        expectedFormalaCount, actualFormulaCount, "Incorrect formula count for " + ruleName);
  }

  @Given(
      "^rules created for company named \"(.+?)\" with rule pids \"(.+?)\", rule names \"(.+?)\", rule types \"(.+?)\"$")
  public void populate_db_with_rules(
      String companyName, String ruleStrPids, String ruleStrNames, String ruleStrTypes)
      throws Throwable {
    String companyPid = databaseUtils.getCompanyPidByName(companyName);
    assertNotNull(companyPid, "Company has to exist in the database");

    List<Long> rulePids = splitAndReturnLongs(ruleStrPids);
    List<String> ruleNames = splitAndReturnStrings(ruleStrNames);
    List<Integer> ruleTypes = splitAndReturnInts(ruleStrTypes);

    int commonSize = Math.max(rulePids.size(), Math.max(ruleNames.size(), ruleTypes.size()));

    assertEquals(rulePids.size(), commonSize, "Size of rule pids should be same as others");
    assertEquals(ruleNames.size(), commonSize, "Size of rule names should be same as others");
    assertEquals(ruleTypes.size(), commonSize, "Size of rule types should be same as others");

    int rulesInserted = databaseUtils.insertRules(companyPid, rulePids, ruleNames, ruleTypes);
    assertEquals(rulePids.size(), rulesInserted);
  }

  private List<Integer> splitAndReturnInts(String comaSeparatedValues) {
    Iterable<String> iterable =
        Splitter.on(",").trimResults().omitEmptyStrings().split(comaSeparatedValues);

    return StreamSupport.stream(iterable.spliterator(), false)
        .map(Integer::valueOf)
        .collect(Collectors.toUnmodifiableList());
  }

  private List<String> splitAndReturnStrings(String comaSeparatedValues) {
    Iterable<String> iterable =
        Splitter.on(",").trimResults().omitEmptyStrings().split(comaSeparatedValues);
    return StreamSupport.stream(iterable.spliterator(), false)
        .collect(Collectors.toUnmodifiableList());
  }

  private List<Long> splitAndReturnLongs(String comaSeparatedValues) {
    Iterable<String> iterable =
        Splitter.on(",").trimResults().omitEmptyStrings().split(comaSeparatedValues);
    return StreamSupport.stream(iterable.spliterator(), false)
        .map(Long::valueOf)
        .collect(Collectors.toUnmodifiableList());
  }

  @Given("^sites created for company named \"(.+?)\" with site pids \"(.+?)\"$")
  public void populate_db_with_sites(String companyName, String siteStrPids) throws Throwable {
    String companyPid = databaseUtils.getCompanyPidByName(companyName);
    assertNotNull(companyPid, "Company has to exist in the database");

    List<Long> sitePids = splitAndReturnLongs(siteStrPids);

    int inserted = databaseUtils.insertSites(companyPid, sitePids);
    assertEquals(sitePids.size(), inserted);
  }

  @Given("^positions created with pids \"(.+?)\" for sites \"(.+?)\" correspondingly$")
  public void populate_db_with_positions(String positionStrPids, String siteStrPids) {
    List<Long> positionPids = splitAndReturnLongs(positionStrPids);
    List<Long> sitePids = splitAndReturnLongs(siteStrPids);

    int commonSize = Math.max(positionPids.size(), sitePids.size());
    assertEquals(positionPids.size(), commonSize, "Size of position pids must be same as others");
    assertEquals(sitePids.size(), commonSize, "Size of site pids must be same as others");

    int inserted = databaseUtils.insertPositions(positionPids, sitePids);
    assertEquals(positionPids.size(), inserted);
  }

  private List<Object[]> extractRelationPairs(String ruleRelations) {
    String input = ruleRelations.replaceAll("\\s", "");
    Pattern pattern = Pattern.compile("\\((\\d+?),(\\d+?)\\)");
    Matcher matcher = pattern.matcher(input);
    return matcher
        .results()
        .map(r -> new Object[] {Long.valueOf(r.group(1)), Long.valueOf(r.group(2))})
        .collect(Collectors.toList());
  }

  @Given("^site_rule relations \"(.+?)\" defined as \\(site_pid, rule_pid\\) pairs$")
  public void create_site_rule_relations(String siteRuleRelations) {
    List<Object[]> relations = extractRelationPairs(siteRuleRelations);
    assertFalse(relations.isEmpty(), "There are no relations. Check input data");
    databaseUtils.insertSiteRuleRelations(relations);
  }

  @Given("^position_rule relations \"(.+?)\" defined as \\(position_pid, rule_pid\\) pairs$")
  public void create_position_rule_relations(String positionRuleRelations) {
    List<Object[]> relations = extractRelationPairs(positionRuleRelations);
    assertFalse(relations.isEmpty(), "There are no relations. Check input data");
    databaseUtils.insertPositionRuleRelations(relations);
  }

  @Given("^rule pids \"(.+?)\" assigned to a seller company named \"(.+?)\"$")
  public void create_company_rule_relations(String ruleStrPids, String companyName)
      throws Throwable {
    String companyPid = databaseUtils.getCompanyPidByName(companyName);
    assertNotNull(companyPid, "Company has to exist in the database");
    List<Long> rulePids = splitAndReturnLongs(ruleStrPids);
    assertFalse(rulePids.isEmpty());

    List<Object[]> relations = new ArrayList<>();
    rulePids.forEach(pid -> relations.add(new Object[] {pid, companyPid}));

    assertFalse(relations.isEmpty(), "There are no relations. Check input data");
    databaseUtils.insertCompanyRuleRelations(relations);
  }

  @When("^the user creates experiment rule from the json file \"(.+?)\"$")
  public void the_user_creates_experiment_rule_from_the_json_file(String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request =
        sellingRuleRequests.getExperimentRuleCreateRequest().setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates experiment rule with pid \"(\\d+)\" from the json file \"(.+?)\"$")
  public void the_user_updates_experiment_rule_from_the_json_file(String pid, String filename)
      throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.requestMap = new RequestParams().setRulePid(pid).getRequestParams();
    commonSteps.request =
        sellingRuleRequests
            .getExperimentRuleUpdateRequest()
            .setRequestPayload(payload)
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user reads rule associated with experiment pid \"(\\d+)\"$")
  public void the_user_reads_rule_associated_with_experiment_pid(String experimentPid)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setExperimentPid(experimentPid).getRequestParams();
    commonSteps.request =
        sellingRuleRequests.getExperimentRuleReadRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @Before("@insertExperimentRuleTestData")
  public void insert_experiment_rule_test_data() {
    log.info("Running @Before on @insertExperimentRuleTestData tag");
    databaseUtils.insertExperimentRuleTestData();
  }

  @After("@insertExperimentRuleTestData")
  public void restore_data() {
    log.info("Running @After on @insertExperimentRuleTestData tag");
    restoreDatabaseUtils.restoreCrudCoreDatabase();
  }
}
