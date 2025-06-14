Feature: get SDK capabilities

  Scenario: get playlist rendering capabilities - default paging params
    Given the user "crudselleruser" has logged in with role "UserSeller"
    When get playlist rendering capabilities
    Then request passed successfully
    And returned "SDK capabilities" data matches the following json file "jsons/genevacrud/playlist-rendering-capability/expected_results/get_default_paging_ER.json"

  Scenario: get playlist rendering capabilities - custom paging params
    Given the user "crudselleruser" has logged in with role "UserSeller"
    When get playlist rendering capabilities page "1" when page size is "5"
    Then request passed successfully
    And returned "SDK capabilities" data matches the following json file "jsons/genevacrud/playlist-rendering-capability/expected_results/get_custom_paging_ER.json"

  Scenario Outline: get playlist rendering capabilities - sufficient entitlements
    Given the user "crudselleruser" has logged in with role "<role>"
    When get playlist rendering capabilities
    Then request passed successfully

    Examples:
      | role           |
      | UserNexage     |
      | UserSeller     |
      | UserSellerSeat |

  Scenario Outline: get playlist rendering capabilities - insufficient entitlements
    Given the user "crudselleruser" has logged in with role "<role>"
    When get playlist rendering capabilities
    Then "request" failed with "401" response code

    Examples:
      | role             |
      | AdminBuyer       |
      | AdminSeatHolder  |
      | ApiSeller        |
      | ApiBuyer         |
      | ApiIIQ           |
      | DealManager      |
