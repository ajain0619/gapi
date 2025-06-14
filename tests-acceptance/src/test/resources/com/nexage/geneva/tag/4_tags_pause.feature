Feature: Ability to Pause a tag within a tier [PR-8826]

  Scenario: Get all tags summary - all active
    Given the user "pr8826admin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the tag summaries are retrieved
    Then request passed successfully
    And returned "tag summaries" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/summaries/TagSummaries_WithSitePR8826.json"

  Scenario Outline: Pause two tags
    Given the user "pr8826admin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "Site PR-8826" and the position "banner" and the tag "<tagName>"
    And the user updates the publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<filename>_payload.json"
    Then request passed successfully

    Examples:
      | type         | tagName                        | filename                                                 |
      | exchange     | Exchange PR-8826 ToBeInactive  | UpdateExchangeTag_ToBeInactive_Pause_SitePR8826          |
      | non-exchange | Mediation PR-8826 ToBeInactive | UpdateMediationAdSourceTag_ToBeInactive_Pause_SitePR8826 |

  Scenario: Get all tags summary - two inactive
    Given the user "pr8826admin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the tag summaries are retrieved
    Then request passed successfully
    And returned "tag summaries" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/summaries/TagSummaries_WithSitePR8826_WithTwoInactive.json"

  Scenario Outline: Re-active two tags
    Given the user "pr8826admin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "Site PR-8826" and the position "banner" and the tag "<tagName>"
    And the user updates the publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<filename>_payload.json"
    Then request passed successfully

    Examples:
      | type         | tagName                  | filename                                              |
      | exchange     | Exchange PR-8826 Paused  | UpdateExchangeTag_Paused_Reactive_SitePR8826          |
      | non-exchange | Mediation PR-8826 Paused | UpdateMediationAdSourceTag_Paused_Reactive_SitePR8826 |

# Negative tests
  Scenario Outline: Try to update tag with invalid data
    Given the user "pr8826admin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "Site PR-8826" and the position "banner" and the tag "<tagName>"
    And the user updates the publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/update/invalid/<filename>_payload.json"
    Then "tag update" failed with "400" response code

    Examples:
      | type         | tagName                  | filename                                                 |
      | exchange     | Exchange PR-8826 Active  | UpdateExchangeTag_Active_WrongStatus_SitePR8826          |
      | non-exchange | Mediation PR-8826 Active | UpdateMediationAdSourceTag_Active_WrongStatus_SitePR8826 |
