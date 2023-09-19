@TRACEFOSS-938
Feature: ⭐[TEST] Update Quality Investigation (over EDC)
	#h2. User Story
	#
	#*As* Supervisor
	#*I want* to be able to update a quality investigation that I received
	#*so that* the corresponding partner knows in which status the notification is on my side.
	#h2. Hints / Details / . Hints & NFR (Technical, Design & Content))* : 
	#
	#1. BPN B receives a notification
	#2. BPN B updates notification
	#3. BPN A receives notification update with corresponding status
	# * The right notification asset needs to be looked up in the Catalog offer of the counter side
	# * Lookup based on asset:props
	# * Keep in mind that some fields are optional and might be empty
	# ** Handle based on documentation / agreement like for send/receive
	# ** E.g. "information" will be empty for Update from REC to ACK
	# * Utilize EDC Update functionality in order to send investigation update over EDC from BNP B to BPN A.
	#
	#Docs: [Notification Update Docu|https://confluence.catena-x.net/pages/viewpage.action?pageId=69429778#id-(TRS)[Release3]%F0%9F%93%9CTraceabilityApp(ImplementationSpecification)-HttpPOSTendpointtoupdateanotification]
	#
	#!screenshot-1.png|thumbnail!
	#h2. TODO:
	# * (-) Fill out description
	# * (-) Fill out Story Points
	# * (-) Assign an Assignee
	# * (-) define Acceptance Criteria
	# * (-) [DoR |https://confluence.catena-x.net/pages/viewpage.action?pageId=917505] 
	#
	#h2. LOP
	# * (/) [~thomas.braun3@zf.com] Update AC and describe error handling, retry and rollback.
	# * (/) Add pbi for "Close notification on sender side". --> TRACEFOSS-961

	#Check if *CANCELLATION* of quality investigations is processed correctly which contains following checks:
	#* correct CANCELLATION on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
  @TRACEFOSS-1862 @TRACEFOSS-1920 @TEST-1217 @TEST-904 @TRACEFOSS-1101 @TRACEFOSS-1673 @INTEGRATION_TEST
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
  @TRACEFOSS-1861 @TRACEFOSS-1920 @TEST-1217 @TRACEFOSS-1101 @TEST-904 @TRACEFOSS-1673 @INTEGRATION_TEST
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
  @TRACEFOSS-1223 @TRACEFOSS-1920 @TRACEFOSS-1673 @TEST-1217 @TRACEFOSS-1139 @TEST-904 @TRACEFOSS-1138 @TRACEFOSS-1101 @INTEGRATION_TEST
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
      | "reason" | "declined in TRACEFOSS-1223" |
    Then I check, if quality investigation has proper values
      | "status" | "DECLINED" |
    When I am logged into TRACE_X_A application
    Then I check, if quality investigation has proper values
      | "status"        | "DECLINED"                   |
      | "declineReason" | "declined in TRACEFOSS-1223" |

	#Check if *ACCEPTANCE* of quality investigations is processed correctly which contains following checks:
	#* correct ACCEPTANCE on receiver side
	#* correct reception of status update on sender side
	#* correct reason on receiver and sender side
  @TRACEFOSS-1222 @TRACEFOSS-1920 @TRACEFOSS-1673 @TEST-1217 @TRACEFOSS-1139 @TRACEFOSS-1138 @TRACEFOSS-1101 @TEST-904 @INTEGRATION_TEST
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
      | "reason" | "accepted in TRACEFOSS-1222" |
    Then I check, if quality investigation has proper values
      | "status" | "ACCEPTED" |
    When I am logged into TRACE_X_A application
    Then I check, if quality investigation has proper values
      | "status"       | "ACCEPTED"                   |
      | "acceptReason" | "accepted in TRACEFOSS-1222" |
