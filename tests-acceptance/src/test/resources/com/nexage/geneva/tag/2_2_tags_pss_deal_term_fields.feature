Feature: PSS - Tags: create, update site with new fields from current deal term object - revenue settings

  Background: specify the date range
    Given the user specifies the date range from "2018-03-06T00:00:00-04:00" to "2019-03-06T23:59:59-04:00"
    And the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site" and the position "transparency_enabled_position"

  Scenario Outline: create a tag with new fields for revenue settings as an internal yield manager
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "created tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/<file_ER>.json"
    And deal term for a tag "<tag_name>" has "<rev_share>" correct revenue share
    And deal term for a tag "<tag_name>" has "<rtb_fee>" correct rtb fee

    Examples:
      | file_payload                      | file_ER                      | tag_name      | rev_share  | rtb_fee    |
      | CreatePssRtbTagRevenue_payload    | CreatePssRtbTagRevenue_ER    | rtbRevenue    | 0.33000000 | 0.66000000 |
      | CreatePssNonRtbTagRevenue_payload | CreatePssNonRtbTagRevenue_ER | nonRtbRevenue | 0.53000000 | 0.32000000 |

  Scenario Outline: copy a tag with revenue settings as an external admin
    Given the user "pssTransEnabledAdmin" has logged in with role "AdminSeller"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "<tag_name>"
    And the user selects the target site "10000202" and the target position "100318"
    And the user clones a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/<file_payload>.json"
    Then  "clone tag" failed with "403" response code and error message "Current deal term is not allowed"

    Examples:
      | tag_name      | file_payload                    |
      | rtbRevenue    | CopyPssRtbTagRevenue_payload    |
      | nonRtbRevenue | CopyPssNonRtbTagRevenue_payload |

  Scenario Outline: copy a tag with revenue settings to the same site as an internal yield manager
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "<tag_name_ori>"
    And the user selects the target site "10000202" and the target position "100318"
    And the user clones a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/<file_payload>.json"
    Then request passed successfully
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/clone/<file_ER>.json"
    And deal term for a tag "<tag_name_copied>" has "<rev_share>" correct revenue share
    And deal term for a tag "<tag_name_copied>" has "<rtb_fee>" correct rtb fee

    Examples:
      | tag_name_ori  | file_payload                    | file_ER                    | rev_share  | rtb_fee    | tag_name_copied |
      | rtbRevenue    | CopyPssRtbTagRevenue_payload    | CopyPssRtbTagRevenue_ER    | 0.13000000 | 0.70000000 | rtbRevenue4     |
      | nonRtbRevenue | CopyPssNonRtbTagRevenue_payload | CopyPssNonRtbTagRevenue_ER | 0.05000000 | 0.19000000 | nonRtbRevenue4  |

  Scenario: update nexage revenue share and rtb fee as an internal yield manager
    And the user selects the "Seller" company "transparency_enabled"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdatePssTagRevenueRevShareFee_payload.json"
    Then request passed successfully
    And returned "updated tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdatePssTagRevenueRevShareFee_ER.json"
    And deal term for a tag "Update1_tag_transparency_enabled" has "0.33000000" correct revenue share
    And deal term for a tag "Update1_tag_transparency_enabled" has "0.66000000" correct rtb fee

   #https://jira.vzbuilders.com/browse/MX-16631 flaky deal_term
  Scenario Outline: get a tag with new fields for revenue settings as an internal user
    Given the user "<user_login>" has logged in with role "<role>"
    And the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "<tag_name>"
    Then publisher tag is retrieved
    And request passed successfully
    And returned "tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/get/<file_ER>.json"

    Examples:
      | user_login           | role        | tag_name      | file_ER                           |
      | crudnexageuser       | UserNexage  | rtbRevenue    | GetPssRtbTagRevenue_ER            |
      | crudnexageuser       | UserNexage  | nonRtbRevenue | GetPssNonRtbTagRevenue_ER         |
      | pssTransEnabledAdmin | AdminSeller | rtbRevenue    | GetPssRtbTagRevenueExternal_ER    |
      | pssTransEnabledAdmin | AdminSeller | nonRtbRevenue | GetPssNonRtbTagRevenueExternal_ER |

  Scenario: update site with nexage revenue share and rtb fee and set these values to null as an internal yield manager
    And the user selects the "Seller" company "transparency_enabled"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdatePssTagWithoutRevenue_payload.json"
    Then request passed successfully
    And returned "updated tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdatePssTagWithoutRevenue_ER.json"
    And revenue share for a tag "Update1_tag_transparency_enabled" has null value
    And rtb fee for a tag "Update1_tag_transparency_enabled" has null value

  @restoreCrudCoreDatabaseBefore

  Scenario: update only nexage revenue share as an internal yield manager
    And the user selects the "Seller" company "transparency_enabled"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdatePssTagRevenueRevShare_payload.json"
    Then request passed successfully
    And returned "updated tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdatePssTagRevenueRevShare_ER.json"
    And deal term for a tag "Update1_tag_transparency_enabled" has "0.22000000" correct revenue share
    And rtb fee for a tag "Update1_tag_transparency_enabled" has null value

  @restoreCrudCoreDatabaseBefore

  Scenario: update only rtb fee as an internal yield manager
    And the user selects the "Seller" company "transparency_enabled"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdatePssTagRevenueRtbFee_payload.json"
    Then request passed successfully
    And returned "updated tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdatePssTagRevenueRtbFee_ER.json"
    And revenue share for a tag "Update1_tag_transparency_enabled" has null value
    And deal term for a tag "Update1_tag_transparency_enabled" has "0.38000000" correct rtb fee

  @restoreCrudCoreDatabaseBefore

  #Update a tag revenue share or rtb fee so that the sum of tag's revenue share and site's revenue share (or rtb fee) more than 100
  Scenario Outline: update a tag revenue share or rtb fee with incorrect data
    And the user selects the "Seller" company "transparency_enabled"
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<file_payload>.json"
    Then "update tag" failed with "400" response code and error message "Current deal term sum must be less than 100"

    Examples:
      | file_payload                          |
      | UpdatePssTagIncorrectRevShare_payload |
      | UpdatePssTagIncorrectRtbFee_payload   |

  Scenario: create a tag without revshare value as an internal yield manager
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/CreatePssTagRevenueWithoutRevShare_payload.json"
    Then request passed successfully with code "201"
    And returned "created tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/CreatePssTagRevenueWithoutRevShare_ER.json"
    And revenue share for a tag "revenueWithoutShare" has null value
    And deal term for a tag "revenueWithoutShare" has "0.18000000" correct rtb fee

  @restoreCrudCoreDatabaseBefore

  Scenario: create a tag without rtbfee value as an internal yield manager
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/CreatePssTagRevenueWithoutRtbFee_payload.json"
    Then request passed successfully with code "201"
    And returned "created tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/CreatePssTagRevenueWithoutRtbFee_ER.json"
    And deal term for a tag "revenueWithoutRtbFee" has "0.29000000" correct revenue share
    And rtb fee for a tag "revenueWithoutRtbFee" has null value

  Scenario Outline: create a tag without incorrect revenue share, rtb fee or its combinations
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then "create site" failed with "400" response code and error message "<error_message>"
    And the deal terms for specified tag "<tag_name>" equals to "0"

    Examples:
      | file_payload                             | error_message                                                      | tag_name             |
      | CreatePssTagIncorrectRevShare_payload    | Current deal term revenue share is less than 0 or greater than 100 | incorrectRevShare    |
      | CreatePssTagIncorrectRtbFee_payload      | Current deal term rtb fee is less than 0 or greater than 100       | incorrectRtbFee      |
      | CreatePssTagIncorrectSumFeeShare_payload | Current deal term sum must be less than 100                        | incorrectSumFeeShare |

  #External users
  Scenario Outline: update a tag with new revenue fields as an external admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tag_name>"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<file_payload>.json"
    Then "" failed with "401" response code and error message "You're not authorized to perform this operation"

    Examples:
      | tag_name                 | file_payload                              |
      | Nexage-Adserver - test8c | UpdatePssNonRtbTagRevenueExternal_payload |
      | Nexage Exchange - test8y | UpdatePssRtbTagRevenueExternal_payload    |

  Scenario Outline: create an rtb or non rtb tag as an external admin with revenue
    Given the user "pssTransEnabledAdmin" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then "" failed with "401" response code and error message "You're not authorized to perform this operation"

    Examples:
      | file_payload                      |
      | CreatePssRtbTagRevenue_payload    |
      | CreatePssNonRtbTagRevenue_payload |



