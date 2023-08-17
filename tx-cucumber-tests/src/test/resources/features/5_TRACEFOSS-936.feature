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

	#Check if *CANCELLATION* of quality investigations is processed correctly which contains following checks:
	#* correct CANCELLATION on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
  @TRACEFOSS-1862 @TRACEFOSS-1920 @TEST-904 @TRACEFOSS-1101 @TRACEFOSS-1673 @TEST-1217 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of CANCELLATION of quality investigation
    When I am logged into TRACE_X_A application
    And I create quality investigation
      | "severity"    | "MAJOR"                             |
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1862" |
    Then I check, if quality investigation has proper values
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1862" |
      | "status"      | "CREATED"                           |
    When I cancel quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "CANCELED" |

    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has not been received

	#Check if *CLOSURE* of quality investigations is processed correctly which contains following checks:
	#* correct CLOSE on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
  @TRACEFOSS-1861 @TRACEFOSS-1920 @TEST-904 @TRACEFOSS-1101 @TRACEFOSS-1673 @TEST-1217 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of CLOSURE of quality investigation
    When I am logged into TRACE_X_A application
    And I create quality investigation
      | "severity"    | "MAJOR"                             |
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1861" |
    Then I check, if quality investigation has proper values
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1861" |
      | "status"      | "CREATED"                           |
    When I approve quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has been received
    And I check, if quality investigation has proper values
      | "status" | "RECEIVED" |
    When I acknowledge quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "ACKNOWLEDGED" |

    When I am logged into TRACE_X_A application
    Then I check, if quality investigation has proper values
      | "status" | "ACKNOWLEDGED" |
    When I close quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "CLOSED" |

    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has proper values
      | "status" | "CLOSED" |

	#Check if *DECLINATION* of quality investigations is processed correctly which contains following checks:
	#* correct DECLINATION status on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
  @TRACEFOSS-1223 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1139 @TEST-904 @TRACEFOSS-1138 @TRACEFOSS-1101 @TEST-1217 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of DECLINATION of quality investigation
    When I am logged into TRACE_X_A application
    And I create quality investigation
      | "severity"    | "MAJOR"                              |
      | "description" | "Testing DECLINATION TRACEFOSS-1223" |
    Then I check, if quality investigation has proper values
      | "severity"    | "MAJOR"                              |
      | "description" | "Testing DECLINATION TRACEFOSS-1223" |
      | "status"      | "CREATED"                            |
    When I approve quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has been received
    Then I check, if quality investigation has proper values
      | "severity"    | "MAJOR"                              |
      | "description" | "Testing DECLINATION TRACEFOSS-1223" |
      | "status"      | "RECEIVED"                           |
    When I acknowledge quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "ACKNOWLEDGED" |
    When I decline quality investigation
      | "description" | "declined in TRACEFOSS-1223" |
    Then I check, if quality investigation has proper values
      | "status" | "DECLINED" |
    When I am logged into TRACE_X_A application
    Then I check, if quality investigation has proper values
      | "status"      | "DECLINED"                   |
      | "description" | "declined in TRACEFOSS-1223" |

	#Check if *ACCEPTANCE* of quality investigations is processed correctly which contains following checks:
	#* correct ACCEPTANCE on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
  @TRACEFOSS-1222 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1139 @TEST-904 @TRACEFOSS-1138 @TRACEFOSS-1101 @TEST-1217 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of ACCEPTANCE of quality investigation
    When I am logged into TRACE_X_A application
    And I create quality investigation
      | "severity"    | "MAJOR"                             |
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1222" |
    Then I check, if quality investigation has proper values
      | "severity"    | "MAJOR"                             |
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1222" |
      | "status"      | "CREATED"                           |
    When I approve quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has been received
    Then I check, if quality investigation has proper values
      | "severity"    | "MAJOR"                             |
      | "description" | "Testing ACCEPTANCE TRACEFOSS-1222" |
      | "status"      | "RECEIVED"                          |
    When I acknowledge quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "ACKNOWLEDGED" |
    When I accept quality investigation
      | "description" | "accepted in TRACEFOSS-1222" |
    Then I check, if quality investigation has proper values
      | "status" | "ACCEPTED" |
    When I am logged into TRACE_X_A application
    Then I check, if quality investigation has proper values
      | "status"      | "ACCEPTED"                   |
      | "description" | "accepted in TRACEFOSS-1222" |
