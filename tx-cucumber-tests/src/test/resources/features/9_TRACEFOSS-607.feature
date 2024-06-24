@TRACEFOSS-607
Feature: ⭐ [BE] Provisioning of Receiver update Quality Investigation over Backend API
	#As a Supervisor
	#I want to be able to update a quality investigation that I received
	#So that the corresponding partner knows in which status the notification is on my side.
	#
	#*Additional information*
	# * Update from RECEIVED to ACKNOWLEDGED without further input
	# * Update from ACKNOWLEDGED to ACCEPTED with text information
	# * Update from ACKNOWLEDGED to DECLINED with text information
	# * No Update to CLOSED possible on Receiver side (Once the Notification is closed it is read-only and could not be changed/modified any more )
	#
	#*Workflow Diagram*
	#
	#!NotificationStatusWorkflow.png|thumbnail!
	#h2. Backend Topics
	#
	# 
	#h2. Sprint Planning 2: 
	# * Backend: 5 SP
	# * Frontend: 5 SP
	#
	#h3. Backend
	# * Endpoint "Create Notification" [~jedrzej.serwa@partner.doubleslash.de]  Please update this 
	# * Endpoint "Status Update"
	# * Endpoint ""
	# * Real-time notification
	#
	# 

	#Check if *DECLINATION* of quality investigations is processed correctly which contains following checks:
	#* correct DECLINATION status on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
	@TRACEFOSS-1223 @TRACEFOSS-3373 @TRACEFOSS-3128 @TRACEFOSS-2910 @TEST-1217 @TRACEFOSS-2715 @TEST-904 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1101 @TRACEFOSS-1139 @TRACEFOSS-1138 @INTEGRATION_TEST @[QualityInvestigation]
	Scenario: [BE] Check correct processing of DECLINATION of quality notification
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		  | "severity"    | "MAJOR"                              |
		  | "description" | "Testing DECLINATION TRACEFOSS-1223" |
		  | "type"        | "INVESTIGATION"                      |
		  | "receiverBpn" | "BPNL00000003CNKC"                  |
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR"                              |
		  | "description" | "Testing DECLINATION TRACEFOSS-1223" |
		  | "status"      | "CREATED"                            |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR"                              |
		  | "description" | "Testing DECLINATION TRACEFOSS-1223" |
		  | "status"      | "RECEIVED"                           |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I decline quality notification
		  | "reason" | "declined in TRACEFOSS-1223" |
		Then I check, if quality notification has proper values
		  | "status" | "DECLINED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status"        | "DECLINED"                   |
		  | "declineReason" | "declined in TRACEFOSS-1223" |