Feature: Try to call tiers services for the user that has no privileges

  Background: log in as buyer admin
    Given the user "crudbuyeradmin" has logged in with role "AdminBuyer"

  Scenario: the user is not authorized to create publisher tier
    When the user calls forbidden create publisher tier service
    Then "user creation" failed with "401" response code

  Scenario: the user is not authorized to get publisher tier
    When the user calls forbidden get publisher tier service
    Then "user search" failed with "401" response code

  Scenario: the user is not authorized to get all publisher tiers
    When the user calls forbidden get all publisher tier service
    Then "user search all" failed with "401" response code
