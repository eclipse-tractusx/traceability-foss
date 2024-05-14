@TRACEFOSS-1125
Feature: ⭐[BE] User select severity for Quality Investigation
	#*As a* User,
	#*I want* to be able to assign a severity status for the part(s) of a notification
	#*so that* I am able to inform the supplier (within the notification) about the criticality of my request for investigation.
	#h3. Outcome
	# * User can select the severity based on the list in the documentation
	# ** MINOR
	# ** MAJOR
	# ** CRITICAL
	# ** LIFE-THREATENING
	# * Severity is sent to the receiver of the notification in the corresponding field
	# * The severity of the parts is changed on sender and receiver side based on the information in the notification.
	#
	#h2. Hints
	# * Today severity is hard coded "minor" in Notification
	# * [Concept|https://confluence.catena-x.net/pages/viewpage.action?pageId=69429778]

	#Check if *severity* is processed correctly for created quality investigations which contains following checks:
	# * correct creation
	# * correct reception on receiver side
	#
	#h2. Sprint Planning 2
	# * Make sure to not have duplicate lines of gherkin language which link to the same technical methods
	# * Write test
	# * Validate github action against e2e environment
	# * Give Feedback to Alex and make suggestions of gherkin and technical steps
	# * Request two new technical users (see reference ticket here: https://jira.catena-x.net/browse/CPLP-2808)  with client id / secret for
	# ** ADMIN role
	# ** USER role
	# * Request system team ticket (see reference ticket here: [https://github.com/eclipse-tractusx/sig-infra/issues/66) |https://github.com/eclipse-tractusx/sig-infra/issues/66]
	# ** For the mapping of the secrets in github
	# * Add new secrets to the code
	#
	# 
  @TRACEFOSS-1220 @TRACEFOSS-3373 @TRACEFOSS-3128 @TRACEFOSS-2910 @TEST-1217 @TRACEFOSS-2715 @TEST-904 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1101 @TRACEFOSS-1139 @TRACEFOSS-1138 @INTEGRATION_TEST @[QualityInvestigation]
  Scenario Outline: [BE] Check correct processing of severity in quality investigation
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
    And I create quality notification
		  | "severity"    | <severity> |
		  | "description" | "Testing severity TRACEFOSS-1220" |
      | "type"        | "INVESTIGATION"                   |
    Then I check, if quality notification has proper values
		  | "severity"    | <severity> |
		  | "description" | "Testing severity TRACEFOSS-1220" |
		  | "status"      | "CREATED" |
    When I approve quality notification
    Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
    Then I check, if quality notification has been received
    Then I check, if quality notification has proper values
		  | "severity"    | <severity> |
		  | "description" | "Testing severity TRACEFOSS-1220" |
		  | "status"      | "RECEIVED" |
    When I acknowledge quality notification
    Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I am logged into TRACE_X_A application
    Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |

    Examples:
		|severity|
		|"MINOR"|
		|"MAJOR"|
		|"CRITICAL"|
		|"LIFE-THREATENING"|
