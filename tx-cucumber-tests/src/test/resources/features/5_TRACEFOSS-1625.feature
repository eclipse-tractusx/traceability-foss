@TRACEFOSS-1625
Feature: [BE][FE]Handling of several parts in one quality alert
	#*As* a user
	#*I want* to be able to create and response quality alerts with several parts as one alert
	#*So that* quality alerts with several parts are able to be processed on either sender or receiver side correctly
	#
	#*Outcome*
	#* quality alerts are created on sender side as one alert even with several parts
	#* quality alerts with several parts are sent correctly as one alert to the receiver
	#* receiver creates one alert with several parts as sent
	#* receiver and sender can change status and update each other for alerts with several parts
	#
	#*Additional information*
	#Currently quality alerts with several parts are created correctly as one alert on the sender side but are created as own alert for each part on the receiver side.
	#(!) Quality-Investigations are processed correctly!

	#Check if *several parts* are processed correctly in *one* created quality alert which contains following checks:
	#* correct sending of several parts in *one* alert
	#* correct reception of several parts in *one* alert on receiver side
	#* correct update of *one* alert with several parts
	@TRACEFOSS-1670 @TRACEFOSS-3373 @TRACEFOSS-3128 @TRACEFOSS-2910 @TRACEFOSS-2715 @TEST-904 @TEST-1217 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1101 @INTEGRATION_TEST @[QUALITY_ALERTS]
	Scenario: [BE] Check correct processing of several parts in quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22,urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MINOR"                           |
		  | "description" | "Testing severity TRACEFOSS-1670" |
		  | "type" | "ALERT" |
		  | "receiverBpn" | "BPNL00000003CNKC"                  |
		Then I check, if quality notification has proper values
		  | "description"  | "Testing severity TRACEFOSS-1670" |
		  | "status"       | "CREATED"                         |
		  | "assetIdCount" | "2"                               |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "description"  | "Testing severity TRACEFOSS-1670" |
		  | "status"       | "RECEIVED"                        |
		  | "assetIdCount" | "2"                               |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status"       | "ACKNOWLEDGED" |
		  | "assetIdCount" | "2"            |