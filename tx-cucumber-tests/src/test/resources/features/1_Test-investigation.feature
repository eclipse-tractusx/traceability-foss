Feature: Investigation notification


  @TRACEFOSS-1516 @INTEGRATION_TEST
  Scenario: Send Investigation Flow Cucumber Test
    Given I am logged into TRACE_X_A application
    And I create investigation
    When I send investigation
    And I am logged into TRACE_X_B application
    And I wait for transfer
    Then I can see notification was received

