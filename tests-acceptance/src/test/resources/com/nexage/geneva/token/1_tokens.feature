Feature: get tokens

  Scenario: Get Access Token for current user with Query Param with Session
    When the user "admin1c" has logged in with role "AdminNexage"
    Then the user grabs all tokens matching qt current with qf status with follow redirect is "true"
    And the request returned status "OK"
    And returned "access_token" data matches the following json file "jsons/genevacrud/token/expected_results/get_all_tokens_QT_QF_ER.json"

  Scenario: Get Access Token for current user with Query Param with no Session
    Given the user logs out
    Then the user grabs all tokens matching qt current with qf status with follow redirect is "false"
    And redirect to authenticate page


