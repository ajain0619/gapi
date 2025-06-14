Feature: Create, read, update and delete hb partners

  Scenario: Get HB partner
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets hb partner with pid "1"
    Then request passed successfully
    And returned "hbPartner" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/get_hb_partner_ER.json"

  Scenario: Get all HB partners as Nexage Admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user gets all hb partners
    Then request passed successfully
    And returned "hbPartner" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/get_all_hb_partners_ER.json"

  Scenario: Get all HB partners as Nexage User
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all hb partners
    Then request passed successfully
    And returned "hbPartner" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/get_all_hb_partners_ER.json"

  Scenario: Get HB partners with smaller detail as Nexage Manager
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    When the user finds hb partners for publisher with detail "false"
    Then request passed successfully
    And returned "hbPartner" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/get_all_hb_partners_summary_ER.json"

  Scenario: Get HB partners for publisher as Seller User
    Given the user "crudPositionUser" has logged in with role "UserSeller"
    When the user gets hb partners with publisher pid "10201" and detail "false"
    Then request passed successfully
    And returned "hbPartner" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/get_hb_partners_for_publisher_ER.json"

  Scenario: Get HB partners for site
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    When the user gets hb partners with site pid "10000174" and detail "false"
    Then request passed successfully
    And returned "hbPartner" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/get_hb_partners_for_site_ER.json"

  Scenario: Create HB partner
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates hb partner from the json file "jsons/genevacrud/hbpartner/payload/create_hb_partner_payload.json"
    Then request passed successfully
    And returned "hbPartner" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/create_hb_partner_ER.json"

  Scenario: Create HB partner fails with correct error message
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the user creates hb partner from the json file "jsons/genevacrud/hbpartner/payload/<file_payload>.json"
    Then "create HB partner" failed with "400" response code and error message "<fail_msg>"
    Examples:
      | file_payload                                                                   | fail_msg                                                           |
      | create_hb_partner_payload_incorrect_fill_max_duration                          | "To enable fill max duration multi impression bid must be enabled" |
      | create_hb_partner_payload_incorrect_max_ads_per_pod                            | "To set max ads per pod multi impression bid must be enabled"      |
      | create_hb_partner_payload_both_fill_max_duration_and_max_ads_per_pod_enabled   | "Both fill max duration and max ads per pod cannot be set together"|

  Scenario: Update HB partner
    Given the user "admin1c" has logged in with role "AdminNexage"
    Given the user updates hb partner with pid "1" from the json file "jsons/genevacrud/hbpartner/payload/update_hb_partner_payload.json"
    And request passed successfully
    Then returned "hbPartner" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/update_hb_partner_ER.json"

  Scenario: Update HB partner
    Given the user "admin1c" has logged in with role "AdminNexage"
    Given the user updates hb partner with pid "1" from the json file "jsons/genevacrud/hbpartner/payload/update_hb_partner_payload_incorrect_responseConfig.json"
    Then "update company" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"

  Scenario: Update HB partner
    Given the user "admin1c" has logged in with role "AdminNexage"
    Given the user updates hb partner with pid "1" from the json file "jsons/genevacrud/hbpartner/payload/update_hb_partner_payload_incorrect_formatted_defaults_flag.json"
    Then "update HB partner" failed with "400" response code and error message "Hb Partner formattedDefaultTypeEnabled cannot be changed during an update operation."

  Scenario: Update HB partner fails with correct error message
    Given the user "admin1c" has logged in with role "AdminNexage"
    Given the user updates hb partner with pid "1" from the json file "jsons/genevacrud/hbpartner/payload/<file_payload>.json"
    Then "update HB partner" failed with "403" response code and error message "<fail_msg>"
    Examples:
      | file_payload                                                                   | fail_msg                                         |
      | update_hb_partner_payload_multi_impression_bid_not_editable                    | "HB Partner multiImpressionBid is not editable"  |
      | update_hb_partner_payload_fill_max_duration_not_editable                       | "HB Partner fillMaxDuration is not editable"     |

  Scenario: Delete HB partner
    When the user deletes hb partner with pid "1"
    Given the user "admin1c" has logged in with role "AdminNexage"
    Then request passed successfully
    When the user gets hb partner with pid "1"
    Then request passed successfully
    And returned "hbPartner" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/deactivated_hb_partner_ER.json"

  Scenario: Delete HB partner with publisher integration
    When the user deletes hb partner with pid "2"
    Then "Delete Hb Partner" failed with "400" response code and error message "Unable to delete the S2S partner, please delete S2S publisher integration."

  Scenario: Get site data with hb-partner as nexage user
    Given the user "NexageUser1" has logged in with role "UserNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    When the user selects a site with pid "10000174"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/get_site_with_hb_partners_association_ER.json"
