Feature: Try to call tags services for the user that has no privileges

  Background: log in as buyer admin
    Given the user "crudbuyeradmin" has logged in with role "AdminBuyer"

  Scenario: create a publisher tag will fail
    When the user calls forbidden create publisher tag service
    Then "publisher tag creation" failed with "401" response code

  Scenario: get all tag summaries will fail
    When the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    And the user calls forbidden get tag summaries service
    Then "publisher tag search all" failed with "401" response code

  Scenario: get a publisher tag will fail
    When the user calls forbidden get publisher tag service
    Then "publisher tag search" failed with "401" response code
