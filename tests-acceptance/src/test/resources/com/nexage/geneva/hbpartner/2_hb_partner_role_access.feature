Feature: Hb partner role based access tests

  Scenario Outline: create a hb partner with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user creates hb partner from the json file "<file_payload>"
    Then "hb partner creation" failed with "401" response code

    Examples:
      | user        | role       | file_payload                                                      |
      | NexageUser1 | UserNexage | jsons/genevacrud/hbpartner/payload/create_hb_partner_payload.json |
      | BuyerUser1  | UserNexage | jsons/genevacrud/hbpartner/payload/create_hb_partner_payload.json |

  Scenario Outline: Update a hb partner with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user updates hb partner with pid "1" from the json file "<file_payload>"
    Then "hb partner update" failed with "401" response code

    Examples:
      | user        | role       | file_payload                                                      |
      | NexageUser1 | UserNexage | jsons/genevacrud/hbpartner/payload/update_hb_partner_payload.json |
      | BuyerUser1  | UserNexage | jsons/genevacrud/hbpartner/payload/update_hb_partner_payload.json |

  Scenario Outline: get a hb partner with no permissions will fail
    Given the user "<user>" has logged in with role "<role>"
    When the user gets hb partner with pid "1"
    Then "hb partner get" failed with "401" response code

    Examples:
      | user        | role      |
      | SellerUser1 | ApiSeller |
      | SellerUser1 | ApiSeller |
