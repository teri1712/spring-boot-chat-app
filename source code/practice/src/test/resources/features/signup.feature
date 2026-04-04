Feature: Sign Up

  As a user, I want to register an account, so that I can manage my company resources

  Scenario: Sign up successfully
    Given username "teri1712" does not exist
    When user sign up new account with username "teri1712" and password "12345678"
    Then user logins with username "teri1712" and password "12345678"
    And the user should be granted access and their profile information

  Scenario: Sign up failed
    Given user exist with username "teri1712" and password "12345678"
    When user sign up new account with username "teri1712" and password "12345678"
    Then fails with error "Username already exists"