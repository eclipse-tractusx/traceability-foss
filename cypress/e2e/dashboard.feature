Feature: Dashboard Page feature
  Scenario: successful load dashboard page
    Given browser is opened to dashboard page
    Then url should contain dashboard
    And should be visible "Dashboard" header
    And should be visible "TOTAL OF PARTS" section
    And should be visible "TOTAL OF OTHER PARTS" section
    And should be visible "TOTAL INVESTIGATIONS" section
    And should be visible "Quality Investigations" section
    And should be visible "Number of parts per country" section
    And in "Quality Investigations" section should be able to click on "View all" button and go to integrations page