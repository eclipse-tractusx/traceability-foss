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
	@TRACEFOSS-1862 @TRACEFOSS-3373 @TRACEFOSS-3128 @TRACEFOSS-2910 @TEST-1217 @TRACEFOSS-2715 @TEST-904 @TRACEFOSS-1920 @TRACEFOSS-1673 @TRACEFOSS-1101 @INTEGRATION_TEST @[QualityInvestigation]
	Scenario: [BE] Check correct processing of CANCELLATION of quality notification
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		  | "severity"    | "MAJOR"                             |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1862" |
		  | "type" | "INVESTIGATION" |  
		Then I check, if quality notification has proper values
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1862" |
		  | "status"      | "CREATED"                           |
		When I cancel quality notification
		Then I check, if quality notification has proper values
		  | "status" | "CANCELED" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has not been received