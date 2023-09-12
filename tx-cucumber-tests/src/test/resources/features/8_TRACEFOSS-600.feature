@TRACEFOSS-600
Feature: ⭐ [BE][QUALITY_ALERTS] Enable Quality Alerts
	#h2. User Story
	#
	#*As a* user
	#*I want to* be able to use all quality alerts
	#*so that* can report quality problems in upstream visibility.
	#h2. Outcome
	# * (x) Quality Alerts are implementd (HAPPY PATH)
	#
	#h2. Consolidation for pbis
	# * TRACEFOSS-1388
	#
	#h2. Sprint Planning 2
	# * Part 1 -> Pull Request 1
	# ** Make sure to not duplicate code instead of base class or reuse existing logic (e.g. state transitions)
	# ** Implement a service
	# ** Publisher Service
	# ** Receiver Service
	# ** Implement a repository
	# * Part 2 -> Pull Request 2 ( Do not start with it before PR 1 is merged)
	# ** Make sure all condiitions which exclude quality alerts are removed
	# ** Make sure that the existing api which creates a contract will be able to create it for quality alerts
	# *** /api/edc/notification/contract
	# ** Make sure that the logic of receiver side does filter for quality alert assets (currently only for quality investigations)
	# ** Send alert from a to b
	# ** Send alert from b to a
	# * Part 3 -> Pull Request 3 -> Enable frontend -> [~martin.maul@doubleslash.de] 
	# ** [https://github.com/catenax-ng/tx-traceability-foss/pull/264/files#diff-de4cfce80ee64138f3cdf6ab0af7b29aed8687212738a0a2d18567e29e7b9472R31]
	# **  

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
