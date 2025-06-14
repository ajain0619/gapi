Feature: buyer group

  Background: log in as nexage admin and select the buyer company
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "MedFTBuyer"

  Scenario Outline: create buyer group
    When the user creates buyer group from the json file "jsons/genevacrud/buyergroup/payload/<file_payload>.json"
    Then request passed successfully
    And returned "create buyer group" data matches the following json file "jsons/genevacrud/buyergroup/expected_results/<file_ER>.json"

    Examples:
      | file_payload                                  | file_ER                                   |
      | 1_CreateBuyerGroup_payload                    | 1_CreateBuyerGroup_ER                     |
      | 1_CreateBuyerGroup_PidVersionExcluded_payload | 1_CreateBuyerGroup_PidVersionGenerated_ER |

  Scenario Outline: update buyer group
    When the user updates buyer group named "BuyerGroupCucumber" using the json file "jsons/genevacrud/buyergroup/payload/<file_payload>.json"
    Then request passed successfully
    And returned "buyer group update" data matches the following json file "jsons/genevacrud/buyergroup/expected_results/<file_ER>.json"

    Examples:
      | file_payload               | file_ER               |
      | 2_UpdateBuyerGroup_payload | 2_UpdateBuyerGroup_ER |

  Scenario: get all buyer groups
    When the user gets all buyer groups
    Then request passed successfully
    And returned "buyer group" data matches the following json file "jsons/genevacrud/buyergroup/expected_results/3_GetAllBuyerGroups_ER.json"

  Scenario: create buyer group - json containing invalid data field(s)
    When the user creates buyer group from the json file "jsons/genevacrud/buyergroup/payload/CreateBuyerGroup_InvalidFields_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "{"name":"must not be null"}"

  Scenario: update buyer group - inadvertently (or deliberately) assigning another company Pid in URL
    When the user updates buyer group named "BuyerGroupCucumber2" using companyPid "300" and the json file "jsons/genevacrud/buyergroup/payload/UpdateBuyerGroup_ChangedCompanyPidInUrl_payload.json"
    Then response failed with "400" response code, error message "It is not allowed to change company" and without field errors.

  Scenario Outline: create buyer group using v1 api
    When the user creates buyer group using V1 api from the json file "jsons/genevacrud/buyergroup/payload/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "create buyer group" data matches the following json file "jsons/genevacrud/buyergroup/expected_results/<file_ER>.json"

    Examples:
      | file_payload                                  | file_ER                                   |
      | 1_CreateBuyerGroup_payload                    | 1_CreateBuyerGroup_ER                     |

  Scenario: get one buyer group using V1 api
    When the user gets one buyer group using V1 api with pid "1"
    Then request passed successfully
    And returned "buyer group" data matches the following json file "jsons/genevacrud/buyergroup/expected_results/1_GetOne_BuyerGroup_ER.json"

  Scenario: update buyer group V1 api
    When the user updates buyer group with pid "1" using V1 api from the json file "jsons/genevacrud/buyergroup/payload/1_UpdateBuyerGroup_payload.json"
    Then request passed successfully
    And returned "buyer group update" data matches the following json file "jsons/genevacrud/buyergroup/expected_results/1_UpdateBuyerGroup_ER.json"

