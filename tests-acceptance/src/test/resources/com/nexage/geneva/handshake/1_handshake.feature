Feature: get, create, update and delete handshake configs as nexage user

  Background: user logs in
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario: get all existing handshake data
    When the user does a call to retrieve all the existing handshake keys
    Then request passed successfully
    And returned "all existing handshake" data matches the following json file "jsons/genevacrud/handshake/expected_results/GetAllHandshakeKeys.json"

  Scenario: get existing handshake config by pid
    When the user does a call to retrieve a handshake config by pid "131"
    Then request passed successfully
    And returned "handshake config" data matches the following json file "jsons/genevacrud/handshake/expected_results/GetHandshakeKey.json"

  Scenario: delete existing handshake config by pid
    When the user does a call to delete a handshake config by pid "144"
    Then request passed with "204" response code
    And the user does a call to retrieve a handshake config by pid "144"
    And "delete hanshake config" failed with "404" response code

  Scenario: delete handshake config with default key is not allowed
    When the user does a call to delete a handshake config by pid "1"
    Then "delete hanshake config" failed with "400" response code

  Scenario: updated existing handshake config by editing key
    When the user does a call to update handshake config from the json file "jsons/genevacrud/handshake/payload/UpdateHandshakeKey.json" on pid "183"
    Then request passed successfully
    And returned "updated handshake config" data matches the following json file "jsons/genevacrud/handshake/expected_results/AfterUpdateHandshakeKey.json"

  Scenario Outline: create new handshake data with all possible key formats (HS create positive cases)
    Given the query parameters are "<queryString>"
    When the user does a call to create handshake from the json file "jsons/genevacrud/handshake/payload/<filename>.json"
    Then request passed successfully
    And returned "created handshake" data matches the following json file "jsons/genevacrud/handshake/expected_results/<compare filename>.json"

    Examples:
      | queryString                               | filename           | compare filename          |
      | appId=com.sriniapp&keyType=DCN&key=testdc | CreateHandshakeKey | AfterCreateHandshakeKey_1 |
      | appId=com.sriniapp&keyType=SDK&key=9.0.4  | CreateHandshakeKey | AfterCreateHandshakeKey_2 |
      | appId=com.sriniapp&keyType=DCN            | CreateHandshakeKey | AfterCreateHandshakeKey_3 |
      | appId=com.sriniapp&keyType=SDK            | CreateHandshakeKey | AfterCreateHandshakeKey_4 |

  Scenario Outline: create new handshake data with invalid key params (HS create negative cases)
    Given the query parameters are "<queryString>"
    When the user does a call to create handshake from the json file "jsons/genevacrud/handshake/payload/<filename>.json"
    Then "create hanshake config" failed with "400" response code

    Examples:
      | queryString                                | filename           |
      | appId=com.sriniapp&keyType=dcn&key=testdcn | CreateHandshakeKey |
      | appId=com.sriniapp&keyType=sdk&key=9.0.4   | CreateHandshakeKey |
      | appId=com.sriniapp&keyType=abc             | CreateHandshakeKey |
      | appId=com.sriniapp&key=9.0.4               | CreateHandshakeKey |
      | appId=com.sriniapp&key=tesdcn              | CreateHandshakeKey |
