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
  @TRACEFOSS-1670 @TRACEFOSS-1920 @TRACEFOSS-1101 @TRACEFOSS-1673 @TEST-904 @TEST-1217 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of several parts in quality alerts
    When I am logged into TRACE_X_A application
    And I create quality alert with two parts
      | "severity"    | "MINOR"                           |
      | "description" | "Testing severity TRACEFOSS-1670" |
    Then I check, if quality alert has proper values
      | "description"  | "Testing severity TRACEFOSS-1670" |
      | "status"       | "CREATED"                         |
      | "assetIdCount" | "2"                               |
    When I approve quality alert
    Then I check, if quality alert has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality alert has been received
    Then I check, if quality alert has proper values
      | "description"  | "Testing severity TRACEFOSS-1670" |
      | "status"       | "RECEIVED"                        |
      | "assetIdCount" | "2"                               |
    When I acknowledge quality alert
    Then I check, if quality alert has proper values
      | "status" | "ACKNOWLEDGED" |
    When I am logged into TRACE_X_A application
    Then I check, if quality alert has proper values
      | "status"       | "ACKNOWLEDGED" |
      | "assetIdCount" | "2"            |
