Feature: Run different API queries and assert the returned response

  @restoreGlobalCoreDatabasesBefore

  Scenario: get all ad net summaries
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "pr_buyer_test"
    When the user gets all ad net summaries
    Then request passed successfully
    And returned "ad nets" data matches the following json file "jsons/genevacrud/adnet/expected_results/AdNetAllSummaries_ER.json"

  Scenario: get all mediation ad source defaults
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2016-01-13T00:00:00-04:00" to "2016-01-19T23:59:59-04:00"
    And "MEDIATION" ad source type is selected
    When the user gets all ad source defaults
    Then request passed successfully
    And returned "mediation ad source defaults" data matches the following json file "jsons/genevacrud/adsourcedefaults/mediation/pss/expected_results/GetAllAdSourceDefaults_ER.json"

  Scenario Outline: get available ad sources
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "pr_buyer_test"
    When the user selects the "Seller" company "<publisherName>"
    And available ad sources are retrieved
    Then request passed successfully
    And returned "available ad sources" data matches the following json file "jsons/genevacrud/availableadsources/expected_results/<file_ER>.json"

    Examples:
      | publisherName               | file_ER                        |
      | adserverSellerTest8         | adserverSellerTest8_ER         |
      | Rovio                       | Rovio_ER                       |
      | auto-tag-generation-company | auto-tag-generation-company_ER |
      | CRUDPositionTest            | CRUDPositionTest_ER            |

  Scenario: get list of publishers
    Given the user "asdfadmin" has logged in with role "AdminBuyer"
    When the user gets list of publishers
    Then request passed successfully
    And returned "list of publishers" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetListOfPublishers_ER.json"

  Scenario: get list of publisher sites
    Given the user "asdfadmin" has logged in with role "AdminBuyer"
    When the user gets list of publisher sites
    Then request passed successfully
    And returned "list of publisher's sites" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetListOfPublishersSites_ER.json"

  Scenario: get list of publishers
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "asdf"
    When the user gets list of publishers
    Then request passed successfully
    And returned "list of publishers" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetListOfPublishers_ER.json"

  Scenario: get list of publisher sites
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "asdf"
    When the user gets list of publisher sites
    Then request passed successfully
    And returned "list of publisher's sites" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetListOfPublishersSites_ER.json"

  Scenario Outline: search Seller sites by prefix
    Given the user "<user>" has logged in with role "<role>"
    When the user searches "Seller" companies by prefix "<prefix>"
    Then request passed successfully
    And returned "Seller sites by prefix" data matches the following json file "jsons/genevacrud/company/expected_results/seller/<file_ER>.json"

    Examples:
      | user              | role          | prefix | file_ER                |
      | crudnexageadmin   | AdminNexage   | x      | SearchSitesPrefix_ER   |
      | crudnexagemanager | ManagerNexage | DJG    | SearchSellersPrefix_ER |


  Scenario: get all sites summaries
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "Athens1"
    When all site summaries are retrieved
    Then request passed successfully
    And returned "sites summaries" data matches the following json file "jsons/genevacrud/site/expected_results/GetAllSites_ER.json"

  Scenario: get all seller sites
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "Athens1"
    When the seller site summaries are retrieved
    Then request passed successfully
    And returned "seller sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetAllSellerSites_ER.json"

  Scenario: get sites and sellers by prefix
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "Athens1"
    When the user searches for sites and sellers by prefix "test"
    Then request passed successfully
    And returned "sites and sellers" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesSellersPrefix_ER.json"

  Scenario Outline: users with different roles search all users
    Given the user "<user>" has logged in with role "<role>"
    When users are retrieved
    Then request passed successfully
    And returned "users" data matches the following json file "jsons/genevacrud/user/expected_results/<filename>_ER.json"

    Examples:
      | user              | role          | filename                   |
      | crudnexageadmin   | AdminNexage   | NexageAdminAllUserSearch   |
      | crudnexagemanager | ManagerNexage | NexageManagerAllUserSearch |
      | crudnexageuser    | UserNexage    | NexageUserAllUserSearch    |
      | adminathens1      | AdminSeller   | SellerAdminAllUserSearch   |
      | adminbuyer1       | AdminBuyer    | BuyerAdminAllUserSearch    |
