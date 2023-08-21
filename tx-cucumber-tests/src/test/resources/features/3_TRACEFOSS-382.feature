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
  @TRACEFOSS-1652 @TRACEFOSS-1101 @INTEGRATION_TEST
  Scenario: [BE] Check correct processing of several parts in quality investigation
    When I am logged into TRACE_X_A application
    And I create quality investigation with "two" parts
      | "severity"    | "MINOR"                           |
      | "description" | "Testing severity TRACEFOSS-1652" |
    Then I check, if quality investigation has proper values
      | "description" | "Testing severity TRACEFOSS-1652" |
      | "status"      | "CREATED"                         |
      | "assetIds"    | "two"                             |
    When I approve quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "SENT" |
    When I am logged into TRACE_X_B application
    Then I check, if quality investigation has been received
    Then I check, if quality investigation has proper values
      | "description" | "Testing severity TRACEFOSS-1652" |
      | "status"      | "RECEIVED"                        |
      | "assetIds"    | "two"                             |
    When I acknowledge quality investigation
    Then I check, if quality investigation has proper values
      | "status" | "ACKNOWLEDGED" |
    When I am logged into TRACE_X_A application
    Then I check, if quality investigation has proper values
      | "status"   | "ACKNOWLEDGED" |
      | "assetIds" | "two"          |
