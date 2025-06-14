Feature: Seller - Sites - Positions: validate traffic type, tier type, decision maker tags for SA and waterfall placements

  Background: user logs in
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user specifies the date range as "2" last days
    And the user selects the "Seller" company "traffic_type"
    And set site "traffic_site"

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  # This scenario includes the whole cycle of creating position with super auction (when decision maker tag is equal to chosen tag
  # or is not equal, choose several tags) and then check, that decision maker tag is not present in the dashboard summary. Also check
  # that trafficType and tierType are present for 'position' and 'tag' objects correspondingly
  Scenario Outline: create position SA with one tag or several tags and validate company dashboardsummary
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/<pos_payload>.json"
    And returned "position create" data matches the following json file "jsons/genevacrud/position/pss/expected_results/create/<file_ER>.json"
    And position "<pos_name>" is retrieved from database
    Then create new sy_decision_maker tier request from the json file "jsons/genevacrud/tag/pss/payload/create/<dmaker_payload>.json"
    And the returned data doesn't contain the following fields "primaryName,secondaryId,secondaryName"
    And returned "decision tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/<dmaker_ER>.json"
    Then the user creates multiple "non-exchange" tags from the json file "jsons/genevacrud/tag/pss/payload/create/<multi_payload>.json"
    And the returned array data doesn't contain the following fields "primaryName,secondaryId,secondaryName"
    And returned "multitags" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/<multi_ER>.json"
    Then the user creates publisher tier from the json file "jsons/genevacrud/tier/pss/payload/<tier_payload>.json"
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/<tier_ER>.json"
    When the user retrieves publisher complete metrics
    Then request passed successfully
    And returned "publisher complete metrics" data matches the following json file "jsons/genevacrud/kpiboxesandcharting/<metrics_ER>.json"

    Examples:
      | pos_payload     | file_ER    | pos_name | dmaker_payload    | dmaker_ER    | multi_payload      | multi_ER      | tier_payload        | tier_ER        | metrics_ER            |
      | CrSAOne_payload | CrSAOne_ER | 15012018 | DMakerOne_payload | DMakerOne_ER | PssMulOne_payload  | PssMulOne_ER  | CrSATierOne_payload | CrSATierOne_ER | PubSAPositionOneTag   |
      | CrSATwo_payload | CrSATwo_ER | 16012018 | DMakerTwo_payload | DMakerTwo_ER | PssMulMany_payload | PssMulMany_ER | CrSATierTwo_payload | CrSATierTwo_ER | PubSAPositionManyTags |

  # This scenario includes the whole cycle of creating position with Waterfall and check that trafficType and tierType are present
  # for 'position' and 'tag' objects correspondingly. Also validate, that 'tierType' for unassigned tag equals 'null', for assigned
  # - 'WATERFALL'
  Scenario: create position WF with tags and validate company dashboardsummary
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/CreateWF_payload.json"
    And returned "position create" data matches the following json file "jsons/genevacrud/position/pss/expected_results/create/CreateWF_ER.json"
    And position "17012018" is retrieved from database
    Then the user selects the site "traffic_site" and the position "17012018"
    When the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/ExchangeTagFirst_payload.json"
    Then request passed successfully with code "201"
    And the user retrieves publisher complete metrics
    And returned "publisher complete metrics" data matches the following json file "jsons/genevacrud/kpiboxesandcharting/PublisherSAMedPositionUnassignedTag.json"
    Then the user creates publisher tier from the json file "jsons/genevacrud/tier/pss/payload/CreateMedTier_payload.json"
    And returned "created tier" data matches the following json file "jsons/genevacrud/tier/pss/expected_results/CreateMedTier_ER.json"
    Then the user retrieves publisher complete metrics
    And returned "publisher complete metrics" data matches the following json file "jsons/genevacrud/kpiboxesandcharting/PublisherSAMedPositionAssignedTag.json"

  # gets library group information for specified company and validate, that position and tags have correct trafficType
  # and tierType values correspondingly
  Scenario: get library group hierarchy for specified company
    When the user gets a group pid "1" in seller "10228"
    Then request passed successfully
    And returned "publisher hierarchy" data matches the following json file "jsons/genevacrud/profilelibrarygroup/expected_results/LibraryGroupTagsSaCompany.json"

  @restoreTagArchiveVericaBefore

  Scenario: validate SA position with archived tag
    And the user selects the site "traffic_site" and the position "15012018" and the tag "becido_SY_Exchange_1"
    And the PSS user gets performance metrics for selected tag
    And the_PSS_user_archives_the_selected_tag
    Then request passed successfully
    And status of the tag "becido_SY_Exchange_1" equals to "-1"
    When position with name "15012018" is selected
    And the PSS user gets performance metrics for selected position
    Then request passed successfully
    And returned "position metrics" data matches the following json file "jsons/genevacrud/position/pss/expected_results/archive/ArchiveSAPosition_ER.json"
