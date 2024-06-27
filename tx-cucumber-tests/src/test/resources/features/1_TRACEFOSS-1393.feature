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

	#Check if *edit* of quality notification is processed correctly which contains following checks:
	# * create notification 
	# * edit notification in status created
	# * validate the values have been updated
	@TRACEFOSS-3354 @TRACEFOSS-3373 @TRACEFOSS-3128 @TRACEFOSS-1920 @TRACEFOSS-2910 @TRACEFOSS-1101 @TRACEFOSS-1673 @TEST-904 @TRACEFOSS-2715 @TEST-1217 @INTEGRATION_TEST
	Scenario: [BE] Check edit notification of quality notification
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		  | "type" | "ALERT" |
		  | "title" | "Initial title" |
		  | "receiverBpn" | "BPNL00000003CNKC"                  |
		Then I check, if quality notification has proper values
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		  | "status"      | "CREATED" |
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa03'  
		When I edit quality notification
		  | "severity"    | "MINOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864 edited" |
		  | "title" | "New Title" |
		  
		Then I check, if quality notification has proper values
		  | "status" | "CREATED" |
		  | "severity"    | "MINOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864 edited" |
		  | "title" | "New Title" |
		  | "affectedPartId" | "urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa03" |	

	#Check if *CANCELLATION* of quality alerts is processed correctly which contains following checks:
	#* correct CANCELLATION on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
	@TRACEFOSS-1864 @TRACEFOSS-3373 @TRACEFOSS-3128 @TRACEFOSS-2910 @TEST-1217 @TRACEFOSS-2715 @TEST-904 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1101 @INTEGRATION_TEST @[QUALITY_ALERTS]
	Scenario: [BE] Check correct processing of CANCELLATION of quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		  | "type" | "ALERT" |
		  | "receiverBpn" | "BPNL00000003CNKC"                  |
		Then I check, if quality notification has proper values
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		  | "status"      | "CREATED" |
		When I cancel quality notification
		Then I check, if quality notification has proper values
		  | "status" | "CANCELED" |
		  
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has not been received