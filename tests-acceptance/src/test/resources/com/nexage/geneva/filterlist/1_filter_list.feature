Feature: search, create and delete filter list for buyer as buyer admin;

  Background: log in as nexage admin and select the buyer
    Given the user "asdfadmin" has logged in with role "ManagerNexage"

  Scenario: get filter list
    When the user gets filter list with id "1"
    Then request passed successfully
    And returned "filter list" data matches the following json file "jsons/genevacrud/filterlist/expected_results/get/GetFilterList_ER.json"

  Scenario: get all filter lists of a buyer
    When the user gets all filter lists with buyer id "300"
    Then request passed successfully
    And returned "filter list" data matches the following json file "jsons/genevacrud/filterlist/expected_results/get/GetAllFilterListsOfBuyer_ER.json"

  Scenario: create filter list
    When the user creates filter list from the json file "jsons/genevacrud/filterlist/payload/create/CreateFilterList_payload.json"
    Then request passed successfully with code "200"
    And returned "create filter list" data matches the following json file "jsons/genevacrud/filterlist/expected_results/create/CreateFilterList_ER.json"

  Scenario: delete filter list
    When the user deletes filter list with id "3"
    Then request passed without errors
    When the user gets filter list with id "3"
    Then "get filter list" failed with "404" response code and error message "FilterList cannot be found."

  Scenario: get domains of a filter list
    When the user gets domains of a filter list with id "1"
    Then request passed successfully
    And returned "filter list domains" data matches the following json file "jsons/genevacrud/filterlist/expected_results/get/GetDomainsOfFilterList_ER.json"

  Scenario: get domains of a filter list which does not exist
    When the user gets domains of a filter list with id "3"
    Then "get filter list domains" failed with "404" response code and error message "FilterList cannot be found."
