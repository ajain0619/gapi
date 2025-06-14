Feature: Tag Validation for adnet

  Background: log in as nexage admin & create adnet tags
    Given the user "admin1c" has logged in with role "AdminNexage"
    And  the user creates ad net from the json file "jsons/genevacrud/tag/payload/adnet/Adnet1.json"
    And the adnet Id "8111" and name "adnet1"
    And  the user creates ad net from the json file "jsons/genevacrud/tag/payload/adnet/Adnet2.json"
    And the adnet Id "8112" and name "adnet2"
    And  the user creates ad net from the json file "jsons/genevacrud/tag/payload/adnet/Adnet3.json"
    And the adnet Id "8113" and name "adnet3"
    And  the user creates ad net from the json file "jsons/genevacrud/tag/payload/adnet/Adnet4.json"
    And the adnet Id "8114" and name "adnet4"
    And  the user creates ad net from the json file "jsons/genevacrud/tag/payload/adnet/Adnet5.json"
    And the adnet Id "8115" and name "adnet5"
    And  the user creates ad net from the json file "jsons/genevacrud/tag/payload/adnet/Adnet6.json"
    And the adnet Id "8116" and name "adnet6"
    And the adnet Id "8117" and name "adnet7"
    # adding a nother tag to check the existence.
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/adnet/pid_existed_tag_adnet2.json"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/adnet/pname_exists_tag_adnet2.json"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/adnet/sid_exists_tag_adnet2.json"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/adnet/sname_exists_tag_adnet2.json"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/adnet/pid_pname_exists_tag_adnet2.json"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/adnet/sid_sname_exists_tag_adnet2.json"
    And the user selects the "Seller" company "adserverSellerTest8"
    And the user selects the site "AS8A"
    And the user creates the "non-exchange" tag from the json file "jsons/genevacrud/tag/payload/adnet/pid_pname_sid_sname_exists_tag_adnet2.json"

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: validating against adnets required fields
    Given the user choses "<adnet>"
    And he sends "<pid>" as value for primaryId
    And "<pname>" as value for primaryName
    And "<sid>" as value for secondaryId
    And "<sname>" as value for secondaryName
    Then sends the request to validate
    And he expects to get "<responseCode>" as response code

#1-16 cases
    Examples:
      | adnet  | pid  | pname  | sid  | sname  | responseCode |
      | adnet1 | 123  | P name | 123  | S name | 200          |
      | adnet1 | 123  | P name |      |        | 400          |
      | adnet1 | 123  |        | 123  | S name | 400          |
      | adnet1 | 123  |        | 123  | S name | 400          |
      | adnet2 | 123  |        |      |        | 200          |
      | adnet2 | 123  | P name |      |        | 200          |
      | adnet2 | 123  |        | 123  |        | 200          |
      | adnet2 | 123  |        |      | S name | 200          |
      | adnet3 | 123  |        |      |        | 200          |
      | adnet3 | 123  | P name | 123  | S name | 200          |
      | adnet4 | 123  | P name |      |        | 200          |
      | adnet4 | 123  |        | 123  | S name | 400          |
      | adnet5 | 123  |        | 123  |        | 200          |
      | adnet5 | 123  | P name |      | S name | 400          |
      | adnet6 | 123  |        |      | S name | 200          |
      | adnet6 | 123  | P name | 123  |        | 400          |
      | adnet2 | 1234 |        |      |        | 400          |
      | adnet2 | 123  | pname  |      |        | 400          |
      | adnet2 | 123  |        | 1234 |        | 400          |
      | adnet2 | 123  |        |      | sname  | 400          |
      | adnet2 | 1234 | pname  |      |        | 400          |
      | adnet2 | 123  |        | 1234 | sname  | 400          |
      | adnet2 | 1234 | pname  | 1234 | sname  | 400          |
      | adnet3 | 1234 | pname  |      |        | 200          |
      | adnet2 |      |        |      |        | 400          |
      | adnet7 | 123  | name   | 123  | name   | 400          |
