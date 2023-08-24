@TRACEFOSS-2354
Feature: [BE] Implementation of Sorting, Filtering, Searching
	#h2. Business Value
	# # (y) [ ] {*}User-business value:{*}:
	# # (y) [ ] *Risk reduction:*
	# # (y) [ ] *Regulatory value:*
	# # (y) [ ] *Commercial value:*
	# # (y) [ ] *Market value:*
	# # (y) [ ] *Efficiency value:*
	# # (y) [ ] *Future value:*
	#
	#h2. User Story
	#
	#*As a user* of Trace-X
	#*I want* to be able to search, sort, filter for specific  data like "ID, Part Number, etc."
	#*so that* I only see relevant data and I'm able to handle big amount of data
	#
	#--> This User Story should cover all Backend tasks that are necessary to cover the funcionalities in the frontend
	#h2. Outcome
	# - Sorting, Filtering, Searching possible in table view of Trace-X
	#
	#h2. NFR
	#
	#*
	#h2. Hints / Details :
	#
	#*<write down here the developer notes, code snippets and Infos that are important
	#h2. Dependency
	# # (?) [ ] * Portal / Access Management
	# # (?) [ ] * Testdata Management
	# # (?) [ ] * Test Managgement & Release Management
	# # (?) [ ] * Semantic Model Teams
	# # (?) [ ] * EDC
	# # (?) [ ] * Decentral twin infrastructure (discovery finders * )
	# # (?) [ ] * To be extended ....
	#
	#h2. TODO:
	# * (-) Fill out description
	# * (-) Fill out Story Points
	# * (-) (Assign an Assignee - might be done during the Sprint)
	# * (-) define Acceptance Criteria
	# * (-) [DoR |https://confluence.catena-x.net/pages/viewpage.action?pageId=917505]

	#Check if *owner filter* in *assets* is working correctly including:
	#* filter without owner specification returns assets of all owner
	#* filter for owner specification only return desired assets
  @TRACEFOSS-1221 @TRACEFOSS-1101 @INTEGRATION_TEST
  Scenario Outline: [BE] Check correct filtering of owner in assets
    When I am logged into TRACE_X_A application
    And I request assets with <owner-filter>
    Then I check, if only assets with <owner-filter> are responded

    Examples:
      | owner-filter |
      | "SUPPLIER"   |
      | "CUSTOMER"   |
      | "OWN"        |
