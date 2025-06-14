Feature: Only see active adnets for a company associated with you

  Scenario: unable to get access to a list of ad nets not assoicated to you as a pss admin
    Given the user "crudPositionAdmin" has logged in
    And the PSS user tries to get list of available adnets thats not their own
    Then "ad net" failed with "401" response code

  Scenario: unable to get access to a list of ad nets not assoicated to you as a pss user
    Given the user "crudPositionUser" has logged in
    And the PSS user tries to get list of available adnets thats not their own
    Then "ad net" failed with "401" response code

  Scenario: unable to get access to a list of ad nets not assoicated to you as a pss manager
    Given the user "tagarchivemanager" has logged in
    And the PSS user tries to get list of available adnets thats not their own
    Then "ad net" failed with "401" response code
