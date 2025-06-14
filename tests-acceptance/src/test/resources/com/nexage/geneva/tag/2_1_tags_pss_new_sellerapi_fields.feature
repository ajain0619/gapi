Feature: Seller - Tags: get, create, update as nexage user or PSS user with new fields migrated from seller API

  Background: specify the date range
    Given the user specifies the date range from "2018-03-06T00:00:00-04:00" to "2019-03-06T23:59:59-04:00"

  Scenario Outline: create non RTB tag with all fields or without new fields as nexage yield manager
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "created tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/<file_ER>.json"

    Examples:
      | file_payload                                 | file_ER                                 |
      | CreatePssNonRtbTagAllNewFieldsNA_payload     | CreatePssNonRtbTagAllNewFieldsNA_ER     |
      | CreatePssNonRtbTagWithoutNewFieldsNA_payload | CreatePssNonRtbTagWithoutNewFieldsNA_ER |

  Scenario Outline: create an RTB tag with all fields or without new fields as nexage manager
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "created tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/<file_ER>.json"

    Examples:
      | file_payload                              | file_ER                              |
      | CreatePssRtbTagAllNewFieldsNM_payload     | CreatePssRtbTagAllNewFieldsNM_ER     |
      | CreatePssRtbTagWithoutNewFieldsNM_payload | CreatePssRtbTagWithoutNewFieldsNM_ER |
      | CreatePssRtbTagWithPublisherOwner_payload | CreatePssRtbTagWithPublisherOwner_ER |
      | CreatePssRtbTagWithNexageOwner_payload    | CreatePssRtbTagWithNexageOwner_ER    |

  Scenario Outline: create an RTB tag with all fields or without new fields as nexage manager
    Given the user "pssTransEnabledAdmin" has logged in with role "AdminSeller"
    And the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "created tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/<file_ER>.json"
    And rtb profile owner company pid for tag "<tag_name>" is null

    Examples:
      | file_payload                                       | file_ER                                       | tag_name    |
      | CreatePssRtbTagWithPublisherOwnerForSeller_payload | CreatePssRtbTagWithPublisherOwnerForSeller_ER | JohnnyColon |
      | CreatePssRtbTagWithNexageOwnerForSeller_payload    | CreatePssRtbTagWithNexageOwnerForSeller_ER    | Merecumbe   |

  Scenario: create a tag with all fields as nexage user
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/CreatePssNonRtbTagAllNewFieldsNA_payload.json"
    Then "create site" failed with "401" response code and error message "Unauthorized"

  Scenario Outline: get an RTB tag or non RTB tag as nexage admin, manager or user
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "<tag_name>"
    Then publisher tag is retrieved
    And request passed successfully
    And returned "publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/get/<file_ER>.json"

    Examples:
      | file_ER                        | user              | role          | tag_name   |
      | GetPssNonRtbTagAllNewFields_ER | admin1c           | AdminNexage   | campanella |
      | GetPssRtbTagAllNewFields_ER    | crudnexagemanager | ManagerNexage | NoMellores |
      | GetPssNonRtbTagAllNewFields_ER | crudnexageuser    | UserNexage    | campanella |
      | GetPssRtbTagAllNewFields_ER    | crudnexageuser    | UserNexage    | NoMellores |

  Scenario: update an RTB tag
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    And the user selects the "Seller" company "transparency_enabled"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdatePssRtbTagAllNewFieldsNA_payload.json"
    Then request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdatePssRtbTagAllNewFieldsNA_ER.json"
    And rtb profile owner company pid for tag "Update1_tag_transparency_enabled" is null

  Scenario: update a non RTB tag
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "MedFTCompany"
    When the user selects the site "SY_HB_EnabledSite" and the position "SmartYieldPosition_SA" and the tag "SY_Exchange_1 - 7051_Tag1"
    And the user updates the publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdatePssNonRtbTagAllNewFieldsNM_payload.json"
    Then request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdatePssNonRtbTagAllNewFieldsNM_ER.json"

  Scenario: update an RTB tag owner flag as nexage admin
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    And the user selects the "Seller" company "transparency_enabled"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdatePssRtbTagOwnerFlag_payload.json"
    Then request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdatePssRtbTagOwnerFlag_ER.json"

  Scenario: update a non RTB tag owner flag as nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "MedFTCompany"
    When the user selects the site "SY_HB_EnabledSite" and the position "SmartYieldPosition_SA" and the tag "SY_Exchange_1 - 7051_Tag1"
    And the user updates the publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdatePssNonRtbTagOwnerFlag_payload.json"
    Then request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdatePssNonRtbTagOwnerFlag_ER.json"

  Scenario: update an RTB tag as nexage user
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Seller" company "transparency_enabled"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdatePssRtbTagAllNewFieldsNA_payload.json"
    Then "update site" failed with "401" response code and error message "Unauthorized"

  Scenario Outline: update a non RTB tag or RTB tag as PSS admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tag_name>"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/<file_ER>.json"

    Examples:
      | tag_name                 | file_payload                             | file_ER                             |
      | Nexage-Adserver - test8c | UpdatePssNonRtbTagAllNewFieldsPA_payload | UpdatePssNonRtbTagAllNewFieldsPA_ER |
      | Nexage Exchange - test8y | UpdatePssRtbTagAllNewFieldsPA_payload    | UpdatePssRtbTagAllNewFieldsPA_ER    |

  Scenario Outline: update a tag without returnRawResponse or clickThroughDisable fields
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<file_payload>.json"
    Then "update site" failed with "500" response code

    Examples:
      | file_payload                                 |
      | UpdatePssTagWithoutRawResponseField_payload  |
      | UpdatePssTagWithoutClickDisableField_payload |
