@TRACEFOSS-382
Feature: Create Request for quality investigation / store in queue
	#h2. Remarks
	#* (-) Test the solution
	#* (-) Provide test data
	#* (-) TRACEFOSS-392
	#* (-) TRACEFOSS-384
	#* (-)
	#
	#h2. User story
	#As a user
	#
	#I want to be able to create a quality investigation for selected parts with a description of the defect
	#
	#to notify the corresponding supplier(s) of the parts about a possible quality issue that needs to be investigated. The requests should be stored in a queue with status pending and not be directly send to the supplier(s).
	#
	# 
	#
	#*Detailed description*
	#
	#The user can create a quality investigation for one or more parts. The description added in the pop up will be added to every part. Once the user creates a quality investigation he will get an information that there is one or more investigations pending for distribution.
	#The user can click on the information and will directy be redirected to pending Queue in his Quality investigation inbox.
	#
	# 
	#
	#*Additional info*
	# * - Description: free text, max length: 1000 
	# * - Hint: Pending state and store in Queue because *later* we might set up a rights an role concept and only specific role (Supervisor) is able to send Notifications to partners.

	#Check if *several parts* in one investigation are processed correctly which contains following checks:
	#* correct sending of several parts in *one* investigation
	#* correct reception of several parts in *one* investigation on receiver side
	#* correct update of *one* investigation with several parts
  @TRACEFOSS-1652 @TRACEFOSS-3373 @TRACEFOSS-3128 @TRACEFOSS-2910 @TRACEFOSS-2715 @TRACEFOSS-1101 @INTEGRATION_TEST @[QualityInvestigation]
  Scenario: [BE] Check correct processing of several parts in quality investigation
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd,urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a'
		And I create quality notification
		  | "severity"    | "MINOR"                           |
		  | "description" | "Testing severity TRACEFOSS-1652" |
		  | "type" | "INVESTIGATION" |
		Then I check, if quality notification has proper values
		  | "description"  | "Testing severity TRACEFOSS-1652" |
		  | "status"       | "CREATED"                         |
		  | "assetIdCount" | "2"                               |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "description"  | "Testing severity TRACEFOSS-1652" |
		  | "status"       | "RECEIVED"                        |
		  | "assetIdCount" | "2"                               |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status"       | "ACKNOWLEDGED" |
		  | "assetIdCount" | "2"            |
