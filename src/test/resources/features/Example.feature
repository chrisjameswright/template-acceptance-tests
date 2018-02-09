@Example
Feature: Running against a web browser

  Scenario: Opening Google.com
    Given A user wants to search the internet
    When the user opens Google
    Then they see a search bar