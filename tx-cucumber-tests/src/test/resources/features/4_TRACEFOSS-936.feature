@TRACEFOSS-936
Feature: ⭐[BE] Include reason for receiver and sender investigations
	#*As a* Dev
	#
	#*I want to* update contract of:
	#
	#/investigations/created (sender side of notifications)
	#/investigations/received (receiver side of notifications)
	#
	#with following object:
	#{code:java}
	#{
	#  "reason" : {
	#    "close" : $string,
	#    "accept": $string, | nullable
	#    "decline": $string | nullable
	#  }
	#} {code}
	#Where for accept and decline reason, it's either one or another. Reasons are passed from receiver to sender and from sender to receiver via EDC and stored in the database.
	#
	#*so that* the information provided with the update is also stored and provided to sender and receiver sides.

	#Check if *CLOSURE* of quality investigations is processed correctly which contains following checks:
	#* correct CLOSE on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
	@TRACEFOSS-1861 @TRACEFOSS-3373 @TRACEFOSS-3128 @TRACEFOSS-2910 @TEST-1217 @TRACEFOSS-2715 @TEST-904 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1101 @INTEGRATION_TEST @[QualityInvestigation]
	Scenario: [BE] Check correct processing of CLOSURE of quality investigation
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		| "severity"    | "MAJOR"                             |
		| "description" | "Testing CLOSURE TRACEFOSS-1861" |
		| "type" | "INVESTIGATION" |
    | "receiverBpn" | "BPNL00000003CNKC" |
    Then I check, if quality notification has proper values
		| "description" | "Testing CLOSURE TRACEFOSS-1861" |
		| "status"      | "CREATED"                           |
		When I approve quality notification
		Then I check, if quality notification has proper values
		| "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		And I check, if quality notification has proper values
		| "status" | "RECEIVED" |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		| "status" | "ACKNOWLEDGED" |

		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		| "status" | "ACKNOWLEDGED" |
		When I close quality notification
		Then I check, if quality notification has proper values
		| "status" | "CLOSED" |

		When I am logged into TRACE_X_B application
		Then I check, if quality notification has proper values
		| "status" | "CLOSED" |

	#Check if *ACCEPTANCE* of quality investigations is processed correctly which contains following checks:
	#* correct ACCEPTANCE on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
	@TRACEFOSS-1222 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1139 @TRACEFOSS-1138 @TRACEFOSS-1101 @TRACEFOSS-2910 @TRACEFOSS-2715 @TEST-1217 @TEST-904 @TRACEFOSS-3128 @TRACEFOSS-3373 @INTEGRATION_TEST @[QualityInvestigation]
	Scenario: [BE] Check correct processing of ACCEPTANCE of quality investigation
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		  | "severity"    | "MAJOR"                             |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1222" |
		  | "type" | "INVESTIGATION" |
		  | "receiverBpn" | "BPNL00000003CNKC"                  |
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR"                             |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1222" |
		  | "status"      | "CREATED"                           |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR"                             |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1222" |
		  | "status"      | "RECEIVED"                          |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I accept quality notification
		  | "reason" | "accepted in TRACEFOSS-1222" |
		Then I check, if quality notification has proper values
		  | "status" | "ACCEPTED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status"       | "ACCEPTED"                   |
		  | "acceptReason" | "accepted in TRACEFOSS-1222" |
