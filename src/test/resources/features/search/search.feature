Feature: Searching other people and messages
  As a user, I want to find another people so that i can contact them
  As a user, I want to look up my message history so that i know what did i send

  Scenario: Find "meomeo"
    Given user "meo meo" exists
    And user "teriteri" alr logins
    When "teriteri" search user for word "meo"
    Then the user "meo meo" must be returned

  Scenario: Find "meomeo" not exist
    Given user "meomeo" not exists
    And user "teriteri" alr logins
    When "teriteri" search user for word "meo"
    Then the user "meomeo" must not be returned

  Scenario: Find message "hello"
    Given there alr is a conversation from "decade" to  "anotherdecade"
    And user alr send "hello"
    When user find "hello" in the conversation
    Then message "hello" must be returned

  Scenario: message "hello" not found
    Given there alr is a conversation from "decade" to  "anotherdecade"
    And no "hello" messages in the conversation
    When user find "hello" in the conversation
    Then no matching messages returned

  Scenario: teri can not read conversation between decade and anotherdecade
    Given there alr is a conversation from "decade" to  "anotherdecade"
    When user "teriteri" search message of that conversation
    Then server reject that
