Feature: Test the endpoint to read a Seller's site limit (.../geneva/pss/{publisherId}/checkLimit/sites).

    # Account data used:
    # | One Central Username | Account Type | Role Type |
    # | crudnexageadmin      | Internal     | Admin     |
    # | crudnexagemanager    | Internal     | Manager   |
    # | crudnexageuser       | Internal     | User      |
    # | crudPositionAdmin    | Seller       | Admin     |
    # | crudPositionManager  | Seller       | Manager   |
    # | crudPositionUser     | Seller       | User      |
    # | asc4                 | Seller       | Admin     |
    # | buyerAdmin1          | Buyer        | Admin     |
    # | buyermanager1        | Buyer        | Manager   |
    # | buyeruser1           | Buyer        | User      |

    # Site data:
    # | Site name          | Site Pid | Company Pid |
    # | CRUDPosition_Site1 | 10000174 | 10201       |

  Scenario: Set the global config site limit to 10 sites
    Given the user updates limit of property "seller.sites.limit" global config to "10"

  # Happy path for all roles of internal accounts
  Scenario Outline: Log in to an internal account and check a seller's site limit
    Given the user "<username>" has logged in with role "<role>"
    And the user selects the "<company_type>" company "<company_name>"
    And the user selects the site "<site_name>"
    Then the PSS user checks site limit and remaining item is "<number_of_remaining_sites>"

    Examples:
      | username          | role          | company_type | company_name     | site_name          | number_of_remaining_sites |
      | crudnexageadmin   | AdminNexage   | Seller       | CRUDPositionTest | CRUDPosition_Site1 | 4                         |
      | crudnexagemanager | ManagerNexage | Seller       | CRUDPositionTest | CRUDPosition_Site1 | 4                         |
      | crudnexageuser    | UserNexage    | Seller       | CRUDPositionTest | CRUDPosition_Site1 | 4                         |

  # Happy path for all roles of seller accounts
  Scenario Outline: Log in to a seller account and check its company's site limit
    Given the user "<username>" has logged in with role "<role>"
    And the PSS user selects the site "<site_name>"
    Then the PSS user checks site limit and remaining item is "<number_of_remaining_sites>"

    Examples:
      | username            | role          | site_name          | number_of_remaining_sites |
      | crudPositionAdmin   | AdminSeller   | CRUDPosition_Site1 | 4                         |
      | crudPositionManager | ManagerSeller | CRUDPosition_Site1 | 4                         |
      | crudPositionUser    | UserSeller    | CRUDPosition_Site1 | 4                         |

  # A seller for a particular company cannot read another company's site limt
  Scenario: Log in to a seller account and attempt to read another company's site limit
    Given the user "asc4" has logged in with role "ManagerSeller"
    And the user reads data for the site named "CRUDPosition_Site1" with the site pid "10000174"
    Then "the endpoint to read a seller's site limit" failed with "401" response code and error message "You're not authorized to perform this operation"

  # Buyers should not have access to read any seller company's site limit
  Scenario Outline: Log in to a buyer account and attempt to read a seller company's site limit
    Given the user "<username>" has logged in with role "<role>"
    And read the site limit for the company with company pid "<company_pid>" using the site with the site pid "<site_pid>"
    Then "<endpoint>" failed with "<response_code>" response code and error message "<error_message>"

    Examples:
      | username      | role         | company_pid | site_pid | endpoint                                   | response_code | error_message                                   |
      | buyerAdmin1   | AdminBuyer   | 10201       | 10000174 | the endpoint to read a seller's site limit | 401           | You're not authorized to perform this operation |
      | buyermanager1 | ManagerBuyer | 10201       | 10000174 | the endpoint to read a seller's site limit | 401           | You're not authorized to perform this operation |
      | buyeruser1    | UserBuyer    | 10201       | 10000174 | the endpoint to read a seller's site limit | 401           | You're not authorized to perform this operation |
