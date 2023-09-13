@TRACEFOSS-608
Feature: ⭐[TEST] [BE] Set and show notification target date
	#*As a* User
	#*I want to* be able to set a target date for my notification while creating it
	#*so that* I am able to monitor if a reply was given in time.
	#
	#h2. Hints
	#[Concept | https://confluence.catena-x.net/pages/viewpage.action?pageId=69429778]

	#Check if *targetDate = null* is processed correctly for created quality investigations which contains following checks:
	#* correct sending of _targetDate_ = *null*
	#* correct reception on receiver side
  @TRACEFOSS-1247 @TRACEFOSS-1920 @TEST-1217 @TRACEFOSS-1139 @TEST-904 @TRACEFOSS-1673 @TRACEFOSS-1138 @TRACEFOSS-1101 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of targetDate = null in quality investigation
    When I am logged into TRACE_X_A application
    And I create quality investigation
      | "severity"    | "MINOR"                                     |
      | "description" | "Testing without targetDate TRACEFOSS-1247" |
    Then I check, if quality investigation has proper values
      | "description" | "Testing without targetDate TRACEFOSS-1247" |
      | "targetDate"  | ""                                          |
      | "status"      | "CREATED"                                   |
    When I approve quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has been received
    Then I check, if quality investigation has proper values
      | "description" | "Testing without targetDate TRACEFOSS-1247" |
      | "targetDate"  | ""                                          |
      | "status"      | "RECEIVED"                                  |

	#Check if *targetDate* is processed correctly for created quality investigations which contains following checks:
	#* correct sending of _targetDate_
	#* correct reception on receiver side
  @TRACEFOSS-1216 @TRACEFOSS-1920 @TEST-1217 @TRACEFOSS-1139 @TRACEFOSS-1673 @TRACEFOSS-1138 @TRACEFOSS-1101 @TEST-904 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of targetDate in quality investigation
    When I am logged into TRACE_X_A application
    And I create quality investigation
      | "severity"    | "MINOR"                             |
      | "description" | "Testing targetDate TRACEFOSS-1216" |
      | "targetDate"  | "2099-03-11T22:44:06.333827Z"       |
    Then I check, if quality investigation has proper values
      | "description" | "Testing targetDate TRACEFOSS-1216" |
      | "targetDate"  | "2099-03-11T22:44:06.333827Z"       |
      | "status"      | "CREATED"                           |
    When I approve quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has been received
    Then I check, if quality investigation has proper values
      | "description" | "Testing targetDate TRACEFOSS-1216" |
      | "targetDate"  | "2099-03-11T22:44:06.333827Z"       |
      | "status"      | "RECEIVED"                          |
