Feature: Investigation notification


  @TRACEFOSS-1516 @INTEGRATION_TEST
  Scenario: Send Investigation Flow Cucumber Test
#    Given I am logged into TRACE_X_A application
#    And I create investigation
#    When I send investigation
#    And I am logged into TRACE_X_B application
#    And I wait for transfer
#    Then I can see notification was received
#
    When I am logged into TRACE_X_A application
    And I create quality investigation
      | severity    | <severity>                  |
      | description | Hello                       |
      | targetDate  | 2099-03-11T22:44:06.333827Z |
    Then I check, if quality investigation has proper values
      | severity    | <severity>                  |
      | description | Hello                       |
      | targetDate  | 2099-03-11T22:44:06.333827Z |
      | status      | CREATED                     |
    When I approve quality investigation
    Then I check, if quality investigation has proper values
      | status | SENT |
    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has been received
    Then I check, if quality investigation has proper values
      | severity    | <severity>                  |
      | description | Hello                       |
      | targetDate  | 2099-03-11T22:44:06.333827Z |
      | status      | RECEIVED                    |
    When I acknowledge quality investigation
    Then I check, if quality investigation has proper values
      | status | ACKNOWLEDGED |
    When I am logged into TRACE_X_A application
    Then I check, if quality investigation has proper values
      | status | ACKNOWLEDGED |

    Examples:
      | severity |
      | MINOR    |
