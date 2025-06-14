Feature: Very simple security annotations test around PublisherSelfServiceImpl

  Scenario Outline: nexage user can access any resource requiring self-serve.
    Given the user "<user>" has logged in with role "<role>"
    And request to a company with pid "<company pid>"
    When request to endpoint with role "self-serve"
    Then the request returned status "<http status>"
    Examples:
      | user                | company pid | http status  | role        |
      | nexageuser          | 1           | OK           | UserNexage |
      | crudPositionUser    | 10201       | OK           | UserSeller  |
      # company with self-serve disabled
      | selleruser2         | 79          | UNAUTHORIZED | UserSeller  |

  Scenario Outline: nexage manager can access same endpoints as any seller user
    Given the user "<user>" has logged in with role "<role>"
    And request to a company with pid "<company pid>"
    When request to endpoint with role "nexage-manager-or-seller-user"
    Then the request returned status "<http status>"
    Examples:
      | user             | company pid | http status  | role          |
      | admin1c          | 1           | OK           | AdminNexage   |
      | nexagemanager    | 1           | OK           | ManagerNexage |
      # user without self-serve enabled
      | selleruser2      | 79          | OK           | UserSeller    |
      | nexageuser       | 1           | UNAUTHORIZED | UserNexage    |
      | nexageuser       | 10201       | UNAUTHORIZED | UserNexage    |
      | crudPositionUser | 10201       | OK           | UserSeller    |

  Scenario Outline: nexage user can access same endpoints as any seller user
    Given the user "<user>" has logged in with role "<role>"
    And request to a company with pid "<company pid>"
    When request to endpoint with role "nexage-user-or-seller-user"
    Then the request returned status "<http status>"
    Examples:
      | user             | company pid | http status  | role          |
      | admin1c          | 1           | OK           | AdminNexage   |
      | nexagemanager    | 1           | OK           | ManagerNexage |
      | nexageuser       | 10201       | OK           | UserNexage    |
      | crudPositionUser | 10201       | OK           | UserSeller    |
      | crudPositionUser | 10202       | UNAUTHORIZED | UserSeller    |

  Scenario Outline: nexage manager can access any publisher or seller manager or external admin can access same endpoints as external seller
    Given the user "<user>" has logged in with role "<role>"
    And request to a company with pid "<company pid>"
    When request to endpoint with role "nexage-manager-or-seller-manager"
    Then the request returned status "<http status>"
    Examples:
      | user                | company pid | http status  | role          |
      | admin1c             | 10201       | OK           | AdminNexage   |
      | nexagemanager       | 10201       | OK           | ManagerNexage |
      | nexageuser          | 10201       | UNAUTHORIZED | UserNexage    |
      | crudPositionUser    | 10201       | UNAUTHORIZED | UserSeller    |
      | crudPositionManager | 10201       | OK           | ManagerSeller |

  Scenario Outline: nexage manager can access any publisher or seller manager or external admin can access same endpoints as external seller. Different variant of endpoint
    Given the user "<user>" has logged in with role "<role>"
    And request to a company with pid "<company pid>"
    When request to endpoint with role "nexage-manager-or-seller-manager-2"
    Then the request returned status "<http status>"
    Examples:
      | user                | company pid | http status  | role          |
      | admin1c             | 10201       | OK           | AdminNexage   |
      | nexagemanager       | 10201       | OK           | ManagerNexage |
      | nexageuser          | 10201       | UNAUTHORIZED | UserNexage    |
      | crudPositionUser    | 10201       | UNAUTHORIZED | UserSeller    |
      | crudPositionManager | 10201       | OK           | ManagerSeller |
