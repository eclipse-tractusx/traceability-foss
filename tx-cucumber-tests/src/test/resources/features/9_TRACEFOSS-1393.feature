@TRACEFOSS-1393
Feature: ⭐ [BE][QUALITY_ALERTS] Create (POST) quality alerts (Rest API)
	#h2. User Story
	#
	#*As a* user
	#*I want to* be able to see all quality alerts / notifications based on their status and type with the date created in a separate Quality Alerts inbox
	#*so that* I can have an overview and perform actions like view details on the notifications.
	#
	#h2. Outcome
	#
	#- (-) New table is added which shows "Quality Alerts"

	#Check if *DECLINATION* of quality alerts is processed correctly which contains following checks:
	#* correct DECLINATION status on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
  @TRACEFOSS-1545 @TRACEFOSS-1101 @TRACEFOSS-1920 @TRACEFOSS-1673 @TEST-904 @TEST-1217 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of DECLINATION of quality alerts
    When I am logged into TRACE_X_A application
    And I create quality alert
      | "severity"    | "MAJOR"                              |
      | "description" | "Testing DECLINATION TRACEFOSS-1545" |
    Then I check, if quality alert has proper values
      | "severity"    | "MAJOR"                              |
      | "description" | "Testing DECLINATION TRACEFOSS-1545" |
      | "status"      | "CREATED"                            |
    When I approve quality alert
    Then I check, if quality alert has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality alert has been received
    Then I check, if quality alert has proper values
      | "severity"    | "MAJOR"                              |
      | "description" | "Testing DECLINATION TRACEFOSS-1545" |
      | "status"      | "RECEIVED"                           |
    When I acknowledge quality alert
    Then I check, if quality alert has proper values
      | "status" | "ACKNOWLEDGED" |
    When I decline quality alert
      | "reason" | "declined in TRACEFOSS-1545" |
    Then I check, if quality alert has proper values
      | "status" | "DECLINED" |
    When I am logged into TRACE_X_A application
    Then I check, if quality alert has proper values
      | "status" | "DECLINED" |
    And I check, if quality alert has proper values
      | "declineReason" | "declined in TRACEFOSS-1545" |

	#Check if *ACCEPTANCE* of quality alerts is processed correctly which contains following checks:
	#* correct ACCEPTANCE on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
  @TRACEFOSS-1544 @TRACEFOSS-1101 @TRACEFOSS-1920 @TEST-904 @TRACEFOSS-1673 @TEST-1217 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of ACCEPTANCE of quality alerts
    When I am logged into TRACE_X_A application
    And I create quality alert
      | "severity"    | "MAJOR"                             |
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1544" |
    Then I check, if quality alert has proper values
      | "severity"    | "MAJOR"                             |
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1544" |
      | "status"      | "CREATED"                           |
    When I approve quality alert
    Then I check, if quality alert has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality alert has been received
    Then I check, if quality alert has proper values
      | "severity"    | "MAJOR"                             |
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1544" |
      | "status"      | "RECEIVED"                          |
    When I acknowledge quality alert
    Then I check, if quality alert has proper values
      | "status" | "ACKNOWLEDGED" |
    When I accept quality alert
      | "reason" | "accepted in TRACEFOSS-1544" |
    Then I check, if quality alert has proper values
      | "status" | "ACCEPTED" |
    When I am logged into TRACE_X_A application
    Then I check, if quality alert has proper values
      | "status" | "ACCEPTED" |
    And I check, if quality alert has proper values
      | "acceptReason" | "accepted in TRACEFOSS-1544" |

	#Check if *targetDate* is processed correctly for created quality alerts which contains following checks:
	#* correct sending of _targetDate_
	#* correct reception on receiver side
  @TRACEFOSS-1543 @TRACEFOSS-1920 @TEST-904 @TRACEFOSS-1673 @TRACEFOSS-1101 @TEST-1217 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of targetDate in quality alerts
    When I am logged into TRACE_X_A application
    And I create quality alert
      | "severity"    | "MINOR"                             |
      | "description" | "Testing targetDate TRACEFOSS-1543" |
      | "targetDate"  | "2055-05-30T20:43:06.333827Z"       |
    Then I check, if quality alert has proper values
      | "description" | "Testing targetDate TRACEFOSS-1543" |
      | "targetDate"  | "2055-05-30T20:43:06.333827Z"       |
      | "status"      | "CREATED"                           |
    When I approve quality alert
    Then I check, if quality alert has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality alert has been received
    Then I check, if quality alert has proper values
      | "description" | "Testing targetDate TRACEFOSS-1543" |
      | "targetDate"  | "2055-05-30T20:43:06.333827Z"       |
      | "status"      | "RECEIVED"                          |

	#Check if *severity* is processed correctly for created quality alerts which contains following checks:
	#* correct creation
	#* correct reception on receiver side
  @TRACEFOSS-1539 @TRACEFOSS-1920 @TRACEFOSS-1101 @TRACEFOSS-1673 @TEST-904 @TEST-1217 @INTEGRATION_TEST
  Scenario Outline: [BE] Check correct processing of severity in quality alerts
    When I am logged into TRACE_X_A application
    And I create quality alert
      | "severity"    | <severity>                        |
      | "description" | "Testing severity TRACEFOSS-1539" |
    Then I check, if quality alert has proper values
      | "severity"    | <severity>                        |
      | "description" | "Testing severity TRACEFOSS-1539" |
      | "status"      | "CREATED"                         |
    When I approve quality alert
    Then I check, if quality alert has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality alert has been received
    Then I check, if quality alert has proper values
      | "severity"    | <severity>                        |
      | "description" | "Testing severity TRACEFOSS-1539" |
      | "status"      | "RECEIVED"                        |
    When I acknowledge quality alert
    Then I check, if quality alert has proper values
      | "status" | "ACKNOWLEDGED" |
    When I am logged into TRACE_X_A application
    Then I check, if quality alert has proper values
      | "status" | "ACKNOWLEDGED" |

    Examples:
      | severity           |
      | "MINOR"            |
      | "MAJOR"            |
      | "CRITICAL"         |
      | "LIFE-THREATENING" |
