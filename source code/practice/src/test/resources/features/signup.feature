Feature: Sign Up

  As a user, I want to register an account, so that I can manage my company resources

  Scenario: Sign up successfully
    Given username "teri1712" does not exist

    When user sign up new account with username "teri1712" and password "12345678"
    And user set his avatar "ace-theme.jpg"

    Then his profile is created successfully with the name "teri1712" and avatar "ace-theme.jpg"

  Scenario: Sign up failed
    Given user exist with username "teri1712" and password "12345678"
    When user sign up new account with username "teri1712" and password "12345678"
    Then fails with error "Username already exists"