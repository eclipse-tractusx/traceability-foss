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

	#Check if *CANCELLATION* of quality alerts is processed correctly which contains following checks:
	#* correct CANCELLATION on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
	@TRACEFOSS-1864 @TRACEFOSS-3128 @TRACEFOSS-2910 @TEST-1217 @TRACEFOSS-2715 @TEST-904 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1101 @INTEGRATION_TEST @[QUALITY_ALERTS]
	Scenario: [BE] Check correct processing of CANCELLATION of quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality alert
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		Then I check, if quality alert has proper values
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		  | "status"      | "CREATED" |
		When I cancel quality alert
		Then I check, if quality alert has proper values
		  | "status" | "CANCELED" |
		  
		When I am logged into TRACE_X_B application
		Then I check, if quality alert has not been received