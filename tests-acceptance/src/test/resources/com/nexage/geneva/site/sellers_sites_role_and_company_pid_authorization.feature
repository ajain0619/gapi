Feature: Test account and role, and company pid based authorization for the GET seller's sites endpoint

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  # Account data used:
  # | One Central Username | Company Pid | Account Type | Role Type |
  # | NexageAdmin1         | 1           | Nexage       | Admin     |
  # | NexageManager1       | 1           | Nexage       | Manager   |
  # | NexageUser1          | 1           | Nexage       | User      |
  # | adminathens1         | 339         | Seller       | Admin     |
  # | athens1manager1      | 339         | Seller       | Manager   |
  # | athens1user1         | 339         | Seller       | User      |
  # | pssSellerAdmin       | 105         | Seller       | Admin     |
  # | buyerAdmin1          | 274         | Buyer        | Admin     |
  # | buyermanager1        | 274         | Buyer        | Manager   |
  # | buyeruser1           | 274         | Buyer        | User      |

  ######################
  # Without Query Term #
  ######################

  Scenario Outline:
  Happy path for all roles of the NEXAGE account type. Since the NEXAGE account type cannot have sites, the company pid
  of a SELLER account type needs to be provided. Test at least 2 different seller companies.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    And make a request to read sites for the company with company pid "<company_pid>"
    Then request passed successfully

    Examples:
      | one_central_username | role          | company_pid |
      | NexageAdmin1         | AdminNexage   | 339         |
      | NexageManager1       | ManagerNexage | 339         |
      | NexageUser1          | UserNexage    | 339         |
      | NexageAdmin1         | AdminNexage   | 105         |
      | NexageManager1       | ManagerNexage | 105         |
      | NexageUser1          | UserNexage    | 105         |

  Scenario Outline:
  Happy path for all roles of the SELLER account type.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    And make a request to read this company's sites
    Then request passed successfully

    Examples:
      | one_central_username | role          |
      | adminathens1         | AdminSeller   |
      | athens1manager1      | ManagerSeller |
      | athens1user1         | UserSeller    |

  Scenario Outline:
  All roles of the SELLER account type are not authorized to read another seller's sites.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    And make a request to read sites for the company with company pid "<other_company_pid>"
    Then "GET - seller's sites" failed with "<expected_error_code>" response code and error message "<expected_error_message>"

    Examples:
      | one_central_username | role          | other_company_pid | expected_error_code | expected_error_message                           |
      | adminathens1         | AdminSeller   | 105               | 401                 | You're not authorized to perform this operation. |
      | athens1manager1      | ManagerSeller | 105               | 401                 | You're not authorized to perform this operation. |
      | athens1user1         | UserSeller    | 105               | 401                 | You're not authorized to perform this operation. |

  Scenario Outline:
  All roles of the BUYER account type are not authorized to read any seller's sites. Since the BUYER account type cannot
  have sites, the company pid of a SELLER account type needs to be provided.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And "Buyer" companies are retrieved
    And make a request to read sites for the company with company pid "<other_company_pid>"
    Then "GET - seller's sites" failed with "<expected_error_code>" response code and error message "<expected_error_message>"

    Examples:
      | one_central_username | role         | other_company_pid | expected_error_code | expected_error_message                          |
      | buyerAdmin1          | AdminBuyer   | 339               | 401                 | You're not authorized to perform this operation |
      | buyermanager1        | ManagerBuyer | 339               | 401                 | You're not authorized to perform this operation |
      | buyeruser1           | UserBuyer    | 339               | 401                 | You're not authorized to perform this operation |

  ###################
  # With Query Term #
  ###################

  Scenario Outline:
  Happy path for all roles of the NEXAGE account type. Since the NEXAGE account type cannot have sites, the company pid
  of a SELLER account type needs to be provided. Test at least 2 different seller companies.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    And make a request to read sites for the company with company pid "<company_pid>" and the query term "<query_term>"
    Then request passed successfully
    And returned "GET - seller's sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSellersSitesPaginatedCompanyPid339QTAthens1Site1_ER.json"

    Examples:
      | one_central_username | role          | company_pid | query_term   |
      | NexageAdmin1         | AdminNexage   | 339         | athens1site1 |
      | NexageManager1       | ManagerNexage | 339         | athens1site1 |
      | NexageUser1          | UserNexage    | 339         | athens1site1 |

  Scenario Outline:
  Happy path for all roles of the NEXAGE account type. Since the NEXAGE account type cannot have sites, the company pid
  of a SELLER account type needs to be provided. Test at least 2 different seller companies.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    And make a request to read sites for the company with company pid "<company_pid>" and the query term "<query_term>"
    Then request passed successfully
    And returned "GET - seller's sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesQTPaginated_ER.json"

    Examples:
      | one_central_username | role          | company_pid | query_term |
      | NexageAdmin1         | AdminNexage   | 105         | AS8A       |
      | NexageManager1       | ManagerNexage | 105         | AS8A       |
      | NexageUser1          | UserNexage    | 105         | AS8A       |

  Scenario Outline:
  Happy path for all roles of the SELLER account type.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    And make a request to read this company's sites with the query term "<query_term>"
    Then request passed successfully
    And returned "GET - seller's sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSellersSitesPaginatedCompanyPid339QTAthens1Site1_ER.json"

    Examples:
      | one_central_username | role          | query_term   |
      | adminathens1         | AdminSeller   | athens1site1 |
      | athens1manager1      | ManagerSeller | athens1site1 |
      | athens1user1         | UserSeller    | athens1site1 |

  Scenario Outline:
  All roles of the SELLER account type are not authorized to read another seller's sites.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And "Seller" companies are retrieved
    And make a request to read sites for the company with company pid "<other_company_pid>" and the query term "<query_term>"
    Then "GET - seller sites" failed with "<expected_error_code>" response code and error message "<expected_error_message>"

    Examples:
      | one_central_username | role          | other_company_pid | query_term | expected_error_code | expected_error_message                           |
      | adminathens1         | AdminSeller   | 105               | AS8A       | 401                 | You're not authorized to perform this operation. |
      | athens1manager1      | ManagerSeller | 105               | AS8A       | 401                 | You're not authorized to perform this operation. |
      | athens1user1         | UserSeller    | 105               | AS8A       | 401                 | You're not authorized to perform this operation. |

  Scenario Outline:
  All roles of the Buyer account type are not authorized to read any seller's sites. Since the BUYER account type cannot
  have sites, the company pid of a SELLER account type needs to be provided.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And "Buyer" companies are retrieved
    And make a request to read sites for the company with company pid "<other_company_pid>" and the query term "<query_term>"
    Then "seller's sites" failed with "<expected_error_code>" response code and error message "<expected_error_message>"

    Examples:
      | one_central_username | role         | other_company_pid | query_term   | expected_error_code | expected_error_message                          |
      | buyerAdmin1          | AdminBuyer   | 339               | athens1site1 | 401                 | You're not authorized to perform this operation |
      | buyermanager1        | ManagerBuyer | 339               | athens1site1 | 401                 | You're not authorized to perform this operation |
      | buyeruser1           | UserBuyer    | 339               | athens1site1 | 401                 | You're not authorized to perform this operation |

  Scenario: NexageManager should have access via B2B to access sites for its seller
    Given the user "NexageManager1" logs in via B2B with role "ManagerNexage"
    And make a request to read sites for the company with company pid "105"
    Then request passed successfully
