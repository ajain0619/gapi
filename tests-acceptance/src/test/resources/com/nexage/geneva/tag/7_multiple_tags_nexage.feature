Feature: Seller - Sites - Tags: create multiple tags as Nexage Admin

  Background: log in as nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user specifies the date range from "2012-09-01T00:00:00-04:00" to "2017-09-23T00:00:00-04:00"
    When the user selects the "Seller" company "MedFTCompany"
    And the user selects the site "SY_HB_EnabledSite" and the position "SmartYieldPosition_SA"

  Scenario Outline: create multiple publisher tags: name not present, name is null - required field present, with site and position
    #(only buyer id has value - 1st tag)
    #(only buyer id and site has value - 2nd tag)
    #(only buyer id and position has value - 3rd tag)
    #PUT http://192.168.33.28:8080/geneva/pss/248/site/10000198/position/100320/multiTags
    Given the user creates multiple publisher "<type>" tags using multiTags call from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then request passed successfully
    And tag created with correct values

    Examples:
      | type         | file_payload                        |
      | exchange     | PssExchangeTagWithNoName_payload    |
      | exchange     | PssExchangeTagWithName_payload      |
      | non-exchange | PssNonExchangeTagWithNoName_payload |

  Scenario Outline: create multiple publisher tags with no name - buyer id not existing
    #(only buyer id has value - 1st tag)
    #(only buyer id and site has value - 2nd tag)
    #(only buyer id and position has value - 3rd tag)
    #PUT http://192.168.33.28:8080/geneva/pss/248/site/10000198/position/100320/multiTags
    Given the user creates multiple publisher "<type>" tags using multiTags call from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then "tag creation" failed with "500" response code

    Examples:
      | type         | file_payload                                       |
      | exchange     | PssExchangeTagWithNoNameBuyerIdNotExisting_payload |
      | non-exchange | PssExchangeTagWithNoNameBuyerIdNotExisting_payload |

  Scenario Outline: create multiple publisher tags with no name - buyer id not present and null
    #(only buyer id has value - 1st tag)
    #(only buyer id and site has value - 2nd tag)
    #(only buyer id and position has value - 3rd tag)
    #PUT http://192.168.33.28:8080/geneva/pss/248/site/10000198/position/100320/multiTags
    Given the user creates multiple publisher "<type>" tags using multiTags call from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then "tag creation" failed with "400" response code and error message "Invalid input. Please check your input parameters."

    Examples:
      | type         | file_payload                                           |
      | exchange     | PssExchangeTagWithNoNameBuyerIdNull_notPresent_payload |
      | non-exchange | PssExchangeTagWithNoNameBuyerIdNull_notPresent_payload |

  Scenario Outline: update multiple publisher tags
    #PUT http://192.168.33.28:8080/geneva/pss/248/site/10000198/position/100320/multiTags
    Given the user updates multiple publisher "<type>" tags using multiTags call from the json file "jsons/genevacrud/tag/pss/payload/update/<file_payload>.json"
    Then request passed successfully
    And tag created with correct values

    Examples:
      | type         | file_payload                                  |
      | exchange     | PssUpdateExchangeTagMultipleUpdate_payload    |
      | non-exchange | PssUpdateNonExchangeTagMultipleUpdate_payload |

  Scenario Outline: update multiple publisher tags - tag pid not existing in database
    #(only buyer id has value - 1st tag)
    #(only buyer id and site has value - 2nd tag)
    #(only buyer id and position has value - 3rd tag)
    #PUT http://192.168.33.28:8080/geneva/pss/248/site/10000198/position/100320/multiTags
    Given the user updates multiple publisher "<type>" tags using multiTags call from the json file "jsons/genevacrud/tag/pss/payload/update/<file_payload>.json"
    Then "tag update" failed with "404" response code and error message "Tag doesn't exist in database"

    # add check here that count is still 1 for the tag pid
    Examples:
      | type         | file_payload                                                 |
      | exchange     | PssUpdateExchangeTagMultipleUpdate_tagNotExisting_payload    |
      | non-exchange | PssUpdateNonExchangeTagMultipleUpdate_tagNotExisting_payload |
