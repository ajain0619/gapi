Feature: Clone Positions

  Scenario Outline: clone non video position with different parameters
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site2"
    Given position with name "<positionname>" is selected
    When the PSS user clones position to target site "<targetsite>" from the json file "jsons/genevacrud/position/pss/payload/clone/<file_payload>_payload.json"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/pss/expected_results/clone/<file_ER>_ER.json"
    When the selected position can be searched out in database
    And the returned cloned position can be searched out in database
    Then the selected position and returned position data in databse is correct
    When the selected tags can be searched in database
    And the returned tags can be searched in database
    Then the selected and returned tags data in databse is correct for that position
    And the selected and returned rtb profile data in database is correct
    And the selected and returned deal terms are correct
    And the selected and returned revenue share and rtb fee values are the same
    And the selected and returned tag rules are equal
    And the selected and returned tag tiers are equal
    And the returned data doesn't contain the following fields "placementVideo"

    Examples:
      | positionname            | file_payload         | file_ER              | targetsite |
      | banner_placement_clone  | bannerPositionClone  | bannerPositionClone  | 10000176   |
      | medRect_placement_clone | medRectPositionClone | medRectPositionClone | 10000176   |
      | banner_placement_clone  | bannerPositionClone  | bannerPositionClone  | 10000175   |
      | medRect_placement_clone | medRectPositionClone | medRectPositionClone | 10000175   |
      | pss_banner_clone        | pssBannerClone       | pssBannerClone       | 10000176   |
      | pss_instream_clone      | pssInstreamClone     | pssInstreamClone     | 10000176   |
      | pss_medrect_clone       | pssMedRectClone      | pssMedRectClone      | 10000176   |

  Scenario: Test external user for cloning non video position
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user selects site pid "10000175" for the company with pid "10201"
    And position by pid "100269" is retrieved from database
    And  the PSS user gets data for selected position
    When the PSS user clones position to target site "10000176" from the json file "jsons/genevacrud/position/pss/payload/clone/bannerPositionClone_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.

  Scenario Outline: clone video position with different parameters
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site2"
    Given position with name "<positionname>" is selected
    When the PSS user clones position to target site "<targetsite>" from the json file "jsons/genevacrud/position/pss/payload/clone/<file_payload>_payload.json"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/pss/expected_results/clone/<file_ER>_ER.json"
    When the selected position can be searched out in database
    And the returned cloned position can be searched out in database
    Then the selected position and returned position data in databse is correct
    When the selected tags can be searched in database
    And the returned tags can be searched in database
    Then the selected and returned tags data in databse is correct for that position
    And the selected and returned rtb profile data in database is correct
    And the selected and returned deal terms are correct
    And the selected and returned revenue share and rtb fee values are the same
    And the selected and returned tag rules are equal
    And the selected and returned tag tiers are equal

    Examples:
      | positionname                 | file_payload                                          | file_ER                                               | targetsite |
      | interstitial_placement_clone | interstitialPositionClone                             | interstitialPositionClone                             | 10000176   |
      | interstitial_placement_clone | interstitialPositionCloneDuplicateTagNameDiffAdsource | interstitialPositionCloneDuplicateTagNameDiffAdsource | 10000176   |
      | inStreamVid_placement_clone  | inStreamVidPositionClone                              | inStreamVidPositionClone                              | 10000176   |
      | interstitial_placement_clone | interstitialPositionClone                             | interstitialPositionClone                             | 10000175   |
      | inStreamVid_placement_clone  | inStreamVidPositionClone                              | inStreamVidPositionClone                              | 10000175   |
      | pss_interstitial_clone       | pssInterstitialClone                                  | pssInterstitialClone                                  | 10000176   |
      | interstitial_placement_clone | interstitialPositionCloneWithPlacementVideo           | interstitialPositionCloneWithPlacementVideo           | 10000176   |
      | inStreamVid_placement_clone  | inStreamVidLongformPositionClone                      | inStreamVidLongformPositionClone                      | 10000175   |
      | inStreamVid_placement_clone  | inStreamVidWithoutPositionWithPlacementVideo          | inStreamVidWithoutPositionWithPlacementVideo          | 10000175   |

  Scenario: Validate mediation tag fields for cloned position including primary id
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site2"
    Given position with name "Banner_withMediationTag" is selected
    When the PSS user clones position to target site "10000183" from the json file "jsons/genevacrud/position/pss/payload/clone/bannerWithMediationTagPositionClone_payload.json"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/pss/expected_results/clone/bannerWithMediationTagPositionClone_ER.json"
    When the selected tags can be searched in database
    And the returned tags can be searched in database
    Then the selected and returned mediation tags data in databse is correct for that position

  @unstable
  Scenario Outline: clone position using invalid target site parameters will fail
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site2"
    When the PSS user clones position with id "<positionid>" to target site "<targetsite>" from the json file "jsons/genevacrud/position/pss/payload/clone/<filename>_payload.json"
    Then "position update" failed with "404" response code

    Examples:
      | positionid | targetsite  | filename                  |
      | 100270     | null        | interstitialPositionClone |
      | 100270     | xyz         | interstitialPositionClone |
      | 100270     | 12323453255 | interstitialPositionClone |

  @unstable
  Scenario Outline: clone position using invalid position id parameters will fail
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site2"
    When the PSS user clones position with id "<positionid>" to target site "<targetsite>" from the json file "jsons/genevacrud/position/pss/payload/clone/<filename>_payload.json"
    Then "position update" failed with "400" response code

    Examples:
      | positionid  | targetsite | filename                  |
      | null        | 10000176   | interstitialPositionClone |
      | xyz         | 10000176   | interstitialPositionClone |
      | 12345612345 | 10000176   | interstitialPositionClone |

  Scenario Outline: clone same position name to same site/other site will fail
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site2"
    And position with name "<positionname>" is selected
    When the PSS user clones position to target site "<targetsite>" from the json file "jsons/genevacrud/position/pss/payload/clone/<filename>_payload.json"
    Then "position update" failed with "400" response code

    Examples:
      | positionname                 | filename                  | targetsite |
      | interstitial_placement_clone | interstitialPositionClone | 10000176   |
      | interstitial_placement_clone | interstitialPositionClone | 10000175   |

  Scenario Outline: clone position using invalid data will fail
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site1"
    Given position with name "banner_placement" is selected
    When the PSS user clones position to target site "10000176" from the json file "jsons/genevacrud/position/payload/update/invalid/<filename>.json"
    Then "position update" failed with "400" response code

    Examples:
      | filename                                     |
      | UpdateInvalidJson                            |
      | UpdateVideoAndBannerToBannerInvalidLinearity |
      | UpdateVideoBannerToBannerInvalidWidth        |
      | UpdateVideoToVideoBannerInvalidHeight        |

  Scenario: the user with permissions ROLE_USER (other than admin/manager) is not authorized to clone positions
    Given the user logs out
    And the user "crudPositionUser" has logged in with role "UserSeller"
    And the user selects the site "CRUDPosition_Site2"
    Given position with name "interstitial_placement_clone" is selected
    When the PSS user clones position to target site "10000176" from the json file "jsons/genevacrud/position/pss/payload/clone/interstitialPositionClone_payload.json"
    Then "positions search" failed with "401" response code

  Scenario Outline: clone position with hb partner assignment
    Given the user "crudPositionAdmin" has logged in with role "AdminSeller"
    And the user selects the site "CRUDPosition_Site1"
    Given position with name "interstitial_placement" is selected
    When the PSS user clones position to target site "10000174" from the json file "jsons/genevacrud/position/pss/payload/clone/interstitialPositionWithHbPartnerAssignmentClone_payload.json"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/pss/expected_results/clone/interstitialPositionWithHbPartnerAssignmentClone_ER.json"

  @restartWiremockAfter
  Scenario:  restart wiremock server
    Then nothing else to be done
