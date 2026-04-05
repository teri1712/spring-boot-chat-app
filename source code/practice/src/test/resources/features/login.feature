# Created by decade at 4/2/26
Feature: Login
  As a user, I want to login and stay with my login session so that i can use the app and play with company documents

  Scenario: Login Successfully
    Given user exist with username "teri1712" and password "vcl123456"
    When user logins with username "teri1712" and password "vcl123456"
    Then the user should be granted access and their profile information

  Scenario: Login with wrong password
    Given user exist with username "teri1712" and password "vcl123456"
    When user logins with username "teri1712" and password "vcl1234567"
    Then the user should be denied access with "Wrong password" message


  Scenario: Login with username does not exist
    Given username "teri1712" does not exist
    When user logins with username "teri1713" and password "vcl1234567"
    Then the user should be denied access with "Username not found" message
