Feature: Seller Sites: get, create, update as Nexage Admin

  Background: log in as nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "Athens1"

  Scenario: update site random fields
    Given the user selects the "Seller" company "cpi_seller"
    And the user selects the site "cpi_site"
    When the user updates site with data from the json file "jsons/genevacrud/site/payload/UpdateSite_payload.json"
    And the user updates site second call with data from the json file "jsons/genevacrud/site/payload/UpdateSite_2_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/expected_results/UpdateSite_2_ER.json"

  Scenario: update site adding all position types - verify value for advanced mraid tracking - Verify trafficType for mediation/smart_yield
    Given the user selects the "Seller" company "debseller_for_mraid_tracking_test"
    And the user selects the site "debmraidsitemobileweb"
    When the user updates site with data from the json file "jsons/genevacrud/site/payload/UpdateSiteAllPositionTypes_payload.json"
    And the user updates site second call with data from the json file "jsons/genevacrud/site/payload/UpdateSiteAllPositionTypes_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/expected_results/UpdateSiteAllPositionTypes_ER.json"

  Scenario: update site by adding a new banner position, then update again adding all other position types - verify advanced mraid tracking value for the existing banner doesn't change
    Given the user selects the "Seller" company "debseller_for_mraid_tracking_test"
    And the user selects the site "debmraidsiteapplication"
    When the user updates site with data from the json file "jsons/genevacrud/site/payload/UpdateSiteAddBanner_payload.json"
    And the user updates site second call with data from the json file "jsons/genevacrud/site/payload/UpdateSiteAddBanner_payload.json"
    Then request passed successfully
    When the user updates site with data from the json file "jsons/genevacrud/site/payload/UpdateSiteAddPositionTypes_payload.json"
    And the user updates site second call with data from the json file "jsons/genevacrud/site/payload/UpdateSiteAddPositionTypes_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/expected_results/UpdateSiteAddPositionTypes_ER.json"

  Scenario: update site platform field
    Given the user selects the "Seller" company "SellerToTestRtbReport1"
    And the user selects the site "SellerToTestRtbReport1Site1"
    When the user updates site with data from the json file "jsons/genevacrud/site/payload/UpdateSitePform_payload.json"
    And the user updates site second call with data from the json file "jsons/genevacrud/site/payload/UpdateSitePform_2_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/expected_results/UpdateSitePform_2_ER.json"

  Scenario: get the seller site
    When the user selects the site "athens1site2"
    And the site data is retrieved
    Then returned "seller site" data matches the following json file "jsons/genevacrud/site/expected_results/GetSite_ER.json"

  Scenario: delete site
    When the user deletes site "athens1site2"
    Then request passed successfully
    And the user searches for deleted site
    And "site search" failed with "404" response code

  # scenario below fails due to environment issue
  # Currently if a user attempts to remove the site or seller alias name it should make the field in the db null,
  # but instead it is an empty string
  # https://jira.nexage.com/browse/KS-5332
  @unstable
  Scenario: delete site alias name and check if it`s null
    Given the user selects the "Seller" company "AliasSiteTestSeller"
    And the user selects the site "AliasTestSite"
    When the user updates site with data from the json file "jsons/genevacrud/site/payload/DeleteAlias_payload.json"
    Then request passed successfully
    And the site "AliasTestSite" alias name was removed

  Scenario: get the site deal terms and update them to default (seller level) will fail with 401 response code
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user gets all the site deal terms
    Then returned "deal terms" data matches the following json file "jsons/genevacrud/site/expected_results/GetSiteDealTerms.json"
    When the user updates the sites with default deal term from the json file "jsons/genevacrud/site/payload/UpdateSiteDealTerms.json"
    Then "update deals term" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario Outline: update deal terms using invalid data will fail with 401 response code
    And the user selects the "Seller" company "<company>"
    And the user gets all the site deal terms
    When the user updates the sites with default deal term from the json file "jsons/genevacrud/site/payload/<filename>.json"
    Then "deals term update" failed with "<code>" response code and error message "<message>"

    Examples:
      | company             | filename                                    | code | message                                         |
      | 111 DJG Seller      | UpdateSiteDealTerms_NoDefaultSellerRevShare | 401  | You're not authorized to perform this operation |
      | adserverSellerTest8 | UpdateSiteDealTerms_NonExistingSitePids     | 401  | You're not authorized to perform this operation |

  Scenario: get deal terms using invalid data will fail
    Given the seller pid is empty
    When the user gets all the site deal terms
    Then "get deals term" failed with "400" response code and error message "Empty seller pid on request"

  Scenario Outline: get sites using query field for nexage user
    Given the user "<user>" has logged in with role "<role>"
    And the user is searching for sites with query field "<queryField>" and field operator "<operator>"
    When sites were retrieved using query field criteria
    Then request passed successfully
    And returned sites data matches the following json file "jsons/genevacrud/site/expected_results/<file_name_ER>.json"

    Examples:
      |user            | role        | queryField                           | file_name_ER                                      | operator |
      | admin1c        | UserNexage  | {name=test,globalAliasName=test}     | GetSitesMultiSearchByNameAndGlobalAliasName       | AND      |
      | crudselleruser | UserBuyer   | {pid=10000108, name=site}            | GetSitesMultiSearchByPidAndName                   | AND      |
      | crudselleruser | UserBuyer   | {companyName=test_rtb,name=site}     | GetSitesMultiSearchByNameAndCompanyName           | AND      |
      | crudselleruser | UserBuyer   | {name=test}                          | GetSitesMultiSearchNoResult                       | AND      |
      | crudselleruser | UserBuyer   | {companyPid=352}                     | GetSitesMultiSearchNoResult                       | AND      |

  Scenario Outline: get sites using an unauthorized user
    Given the user "admin1c" has logged in with role "UserSeller"
    And the user is searching for sites with query field "{name=test}" and field operator "AND"
    When sites were retrieved using query field criteria
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.
