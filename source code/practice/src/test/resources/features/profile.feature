Feature: Profile management

  Scenario: Change password with correct submitted password
    Given user exist with username "teri1712" and password "12345678"
    And user logins with username "teri1712" and password "12345678"
    When changing password to "123456789" with submitted password "12345678"
    Then password is changed successfully to "123456789"
    And invalidate current session
    And grant new valid session


  Scenario: Change profile partially
    Given user exist with username "teri1712" and password "12345678"
    And user logins with username "teri1712" and password "12345678"
    When the user update his name to "tetetetete" and his gender to 0.3
    Then his profile is reflected correctly with the name "tetetetete" and gender "Mental illness"
