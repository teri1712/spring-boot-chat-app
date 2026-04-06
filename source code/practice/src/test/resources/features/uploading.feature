Feature: Uploading
  As a user i want to upload the company documentation so that I can query related information later

  Scenario: Upload a file successfully
    Given user exist with username "teri1712" and password "12345678"
    And user logins with username "teri1712" and password "12345678"
    When user browse his file "ace-theme.jpg"
    Then the file is saved