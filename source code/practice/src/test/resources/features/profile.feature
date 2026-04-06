Feature: Profile management
  As a user, i want to update my profile and password so that i can protect my account.

  Scenario: Change password with correct submitted password
    Given user exist with username "teri1712" and password "12345678"
    And user logins with username "teri1712" and password "12345678"
    When changing password to "123456789" with submitted password "12345678"
    And user logins with username "teri1712" and password "123456789"
    Then password is changed successfully to "123456789"
    And grant a new valid session
    And invalidate old session


  Scenario: Change profile partially
    Given user exist with username "teri1712" and password "12345678"
    And user logins with username "teri1712" and password "12345678"
    When the user update his name to "tetetetete" and his gender to 0.3
    Then his profile is reflected correctly with the name "tetetetete" and gender "Mental illness"

  Scenario: Change avatar successfully
    Given  user exist with username "teri1712" and password "12345678"
    And user logins with username "teri1712" and password "12345678"

    When user browse his file "ace-theme.jpg"
    And set as his avatar

    Then the file is saved
    And his profile avatar is reflected to the file "ace-theme.jpg"