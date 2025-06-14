Feature: Test account and role, and company pid based authorization for the read placements endpoints:

  # Account data used:
  # | One Central Username         | Company Pid | Account Type | Role Type |
  # | NexageAdmin1                 | 1           | Nexage       | Admin     |
  # | NexageManager1               | 1           | Nexage       | Manager   |
  # | NexageUser1                  | 1           | Nexage       | User      |
  # | crudPositionAdmin            | 10201       | Seller       | Admin     |
  # | crudPositionManager          | 10201       | Seller       | Manager   |
  # | crudPositionUser             | 10201       | Seller       | User      |
  # | SellerRtbAdmin@teamaol.com   | 10217       | Seller       | Admin     |
  # | SellerRtbManager@teamaol.com | 10217       | Seller       | Manager   |
  # | SellerRtbUser@teamaol.com    | 10217       | Seller       | User      |
  # | buyerAdmin1                  | 274         | Buyer        | Admin     |
  # | buyermanager1                | 274         | Buyer        | Manager   |
  # | buyeruser1                   | 274         | Buyer        | User      |


  #######################################################
  # ../v1/sellers/{sellerId}/sites/{siteId}/placements #
  #######################################################

  Scenario Outline:
  Happy path for all roles of the Nexage account type. Since the Nexage account type cannot have sites, the company pid
  of a seller account type needs to be provided. Test at least 2 different seller companies.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And the user selects the site with the site name "<site_name>" and the company with the company pid "<company_pid>"
    And The site pid is passed in to grab "<number_of_placements>" placements for the company with the company pid "<company_pid>"
    Then request passed successfully
    And returned "placements" data matches the following json file "<expected_response>"

    Examples:
      | one_central_username | role          | site_name          | company_pid | number_of_placements | expected_response                                                                                  |
      | NexageAdmin1         | AdminNexage   | CRUDPosition_Site1 | 10201       | 1                    | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201SitePid10000174Size1.json |
      | NexageManager1       | ManagerNexage | CRUDPosition_Site1 | 10201       | 1                    | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201SitePid10000174Size1.json |
      | NexageUser1          | UserNexage    | CRUDPosition_Site1 | 10201       | 1                    | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201SitePid10000174Size1.json |
      | NexageAdmin1         | AdminNexage   | SellerRtbSite1     | 10217       | 1                    | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10217SitePid10000193Size1.json |
      | NexageManager1       | ManagerNexage | SellerRtbSite1     | 10217       | 1                    | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10217SitePid10000193Size1.json |
      | NexageUser1          | UserNexage    | SellerRtbSite1     | 10217       | 1                    | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10217SitePid10000193Size1.json |

  Scenario Outline:
  Happy path for all roles of the seller account type.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And the user selects the site with the site name "<site_name>" and the company with the company pid "<company_pid>"
    And The site pid is passed in to grab "<number_of_placements>" placements for the company with the company pid "<company_pid>"
    Then request passed successfully
    And returned "placements" data matches the following json file "<expected_response>"

    Examples:
      | one_central_username | role          | site_name          | company_pid | number_of_placements | expected_response                                                                                  |
      | crudPositionAdmin    | AdminSeller   | CRUDPosition_Site1 | 10201       | 1                    | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201SitePid10000174Size1.json |
      | crudPositionManager  | ManagerSeller | CRUDPosition_Site1 | 10201       | 1                    | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201SitePid10000174Size1.json |
      | crudPositionUser     | UserSeller    | CRUDPosition_Site1 | 10201       | 1                    | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201SitePid10000174Size1.json |

  Scenario Outline:
  All roles of the seller account type are not authorized to read another seller's placements.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And the user reads the placements for the site with the site pid "<site_pid>" for the company with the company pid "<company_pid>"
    Then "read placements" failed with "<expected_error_code>" response code and error message "<expected_error_message>"

    Examples:
      | one_central_username | role          | site_pid | company_pid | expected_error_code | expected_error_message                          |
      | crudPositionAdmin    | AdminSeller   | 10000193 | 10217       | 401                 | You're not authorized to perform this operation |
      | crudPositionManager  | ManagerSeller | 10000193 | 10217       | 401                 | You're not authorized to perform this operation |
      | crudPositionUser     | UserSeller    | 10000193 | 10217       | 401                 | You're not authorized to perform this operation |

  Scenario Outline:
  All roles of the buyer account type are not authorized to read any seller's placements.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And the user reads the placements for the site with the site pid "<site_pid>" for the company with the company pid "<company_pid>"
    Then "read placements" failed with "<expected_error_code>" response code and error message "<expected_error_message>"

    Examples:
      | one_central_username | role         | site_pid | company_pid | expected_error_code | expected_error_message                          |
      | buyerAdmin1          | AdminBuyer   | 10000174 | 10201       | 401                 | You're not authorized to perform this operation |
      | buyermanager1        | ManagerBuyer | 10000174 | 10201       | 401                 | You're not authorized to perform this operation |
      | buyeruser1           | UserBuyer    | 10000174 | 10201       | 401                 | You're not authorized to perform this operation |

  ########################################
  # ../v1/sellers/{sellerId}/placements #
  ########################################

  Scenario Outline:
  Happy path for all roles of the Nexage account type. Since the Nexage account type cannot have sites, the company pid
  of a seller account type needs to be provided. Test at least 2 different seller companies.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And the company pid "<company_pid>" is passed in to grab "<number_of_placements>" and the query term "<query_term>"
    Then request passed successfully
    And returned "placements" data matches the following json file "<expected_results_file>"

    Examples:
      | one_central_username | role          | company_pid | number_of_placements | query_term                           | expected_results_file                                                                                         |
      | NexageAdmin1         | AdminNexage   | 10201       | 1                    | action=search,name=banner            | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201QueryTermBannerSize1.json            |
      | NexageManager1       | ManagerNexage | 10201       | 1                    | action=search,name=banner            | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201QueryTermBannerSize1.json            |
      | NexageUser1          | UserNexage    | 10201       | 1                    | action=search,name=banner            | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201QueryTermBannerSize1.json            |
      | NexageAdmin1         | AdminNexage   | 10217       | 1                    | action=search,name=SellerRTBPosition | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10217QueryTermSellerRTBPositionSize1.json |
      | NexageManager1       | ManagerNexage | 10217       | 1                    | action=search,name=SellerRTBPosition | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10217QueryTermSellerRTBPositionSize1.json |
      | NexageUser1          | UserNexage    | 10217       | 1                    | action=search,name=SellerRTBPosition | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10217QueryTermSellerRTBPositionSize1.json |


  Scenario Outline:
  Happy path for all roles of the seller account type.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And the company pid "<company_pid>" is passed in to grab "<number_of_placements>" and the query term "<query_term>"
    Then request passed successfully
    And returned "placements" data matches the following json file "<expected_results_file>"

    Examples:
      | one_central_username | role          | company_pid | number_of_placements | query_term                | expected_results_file                                                                              |
      | crudPositionAdmin    | AdminSeller   | 10201       | 1                    | action=search,name=banner | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201QueryTermBannerSize1.json |
      | crudPositionManager  | ManagerSeller | 10201       | 1                    | action=search,name=banner | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201QueryTermBannerSize1.json |
      | crudPositionUser     | UserSeller    | 10201       | 1                    | action=search,name=banner | jsons/genevacrud/placements/expected_results/GetPlacementsCompanyPid10201QueryTermBannerSize1.json |


  Scenario Outline:
  All roles of the seller account type are not authorized to read another seller's placements.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And the company pid "<company_pid>" is passed in to grab "<number_of_placements>" and the query term "<query_term>"
    Then "read placements with query term" failed with "<expected_error_code>" response code and error message "<expected_error_message>"

    Examples:
      | one_central_username | role          | company_pid | number_of_placements | query_term                | expected_error_code | expected_error_message                          |
      | crudPositionAdmin    | AdminSeller   | 10217       | 1                    | action=search,name=banner | 401                 | You're not authorized to perform this operation |
      | crudPositionManager  | ManagerSeller | 10217       | 1                    | action=search,name=banner | 401                 | You're not authorized to perform this operation |
      | crudPositionUser     | UserSeller    | 10217       | 1                    | action=search,name=banner | 401                 | You're not authorized to perform this operation |

  Scenario Outline:
  All roles of the buyer account type are not authorized to read any seller's placements.

    Given the user "<one_central_username>" has logged in with role "<role>"
    And the company pid "<company_pid>" is passed in to grab "<number_of_placements>" and the query term "<query_term>"
    Then "read placements with query term" failed with "<expected_error_code>" response code and error message "<expected_error_message>"

    Examples:
      | one_central_username | role         | company_pid | number_of_placements | query_term                | expected_error_code | expected_error_message                          |
      | buyerAdmin1          | AdminBuyer   | 10217       | 1                    | action=search,name=banner | 401                 | You're not authorized to perform this operation |
      | buyermanager1        | ManagerBuyer | 10217       | 1                    | action=search,name=banner | 401                 | You're not authorized to perform this operation |
      | buyeruser1           | UserBuyer    | 10217       | 1                    | action=search,name=banner | 401                 | You're not authorized to perform this operation |

  Scenario: External API user logged in via B2B should have access to read placements
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user reads the placements for the site with the site pid "10000199" for the company with the company pid "807"
    Then request passed successfully

  @restartWiremockAfter
  Scenario: restart wiremock server
    Then nothing else to be done
