@TRACEFOSS-1090
Feature: 🪓⭐[BE] Add information to notification inbox
	#As a *User*
	#
	#I want to have added the information of severity, send to (BPN / name), and received from (BPN / name) as additional columns to the notifications inbox
	#
	#so that I am able to see on the first sign who/whom the notification was sent to as well as the severity.
	#h3. Outcome
	# * Add an additional field for bpn name (sender + receiver)
	# * Add an additional field for severity
	#
	#
	#h3. Hint
	#* BPN name and severity is published over the API that it could be requested by the frontend API .
	#* Mapping between BPN number and company name is part of the job response of IRS.
	#
	#h3. Sprint Planning 2
	#* Add BPN and severity to the investigation response.

	#Check if *bpn names* of *sender and receiver* are processed correctly for created quality investigations which contains following checks:
	#* correct creation on sender side
	#* correct reception on receiver side
  @TRACEFOSS-1344 @TRACEFOSS-1920 @TRACEFOSS-1101 @TRACEFOSS-1138 @TRACEFOSS-1673 @TEST-1217 @TRACEFOSS-1139 @TEST-904 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of bpn names in quality investigation
    When I am logged into TRACE_X_A application
    And I create quality investigation
      | "description" | "Testing BPNs TRACEFOSS-1344" |
      | "severity"    | "MINOR"                       |
    Then I check, if quality investigation has proper values
      | "description" | "Testing BPNs TRACEFOSS-1344" |
      | "createdBy"   | "BPNL00000003CML1"            |
      | "sendTo"      | "BPNL00000003CNKC"            |
      | "status"      | "CREATED"                     |
    When I approve quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has been received
    Then I check, if quality investigation has proper values
      | "description" | "Testing BPNs TRACEFOSS-1344" |
      | "createdBy"   | "BPNL00000003CML1"            |
      | "sendTo"      | "BPNL00000003CNKC"            |
      | "status"      | "RECEIVED"                    |
