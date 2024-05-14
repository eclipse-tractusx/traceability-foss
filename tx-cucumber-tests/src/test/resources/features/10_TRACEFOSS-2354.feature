@TRACEFOSS-2354
Feature: 👍[BE][TABLE_FEATURE] Implementation of Sorting in table views
	#h2. Clarification
	# * (/) [~steffen.duering@bmw.de] should cascading sorting be enabled ? --> [~martin.kanal@doubleslash.de]   If possible yes. Could also be done in a further development if not possible
	# * (/) [~steffen.duering@bmw.de] On which result set the sorting shall be implemented (current page vs / complete resultset? )
	#-->[~martin.kanal@doubleslash.de]  complete resultset would be great. Is it possible?
	#
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
	#*I want* to be able to sort within the table views
	#*so that* I only see relevant data and I'm able to better handle big amount of data
	#
	#--> This User Story should cover all *Backend* tasks that are necessary to cover the funcionalities in the frontend
	#h2. Outcome
	#
	#- (/) User can sort for any colums in UI (within all table views --> parts, other parts)
	#- (/) Multiple sort functionalities can be set for ascending and descending according to concept for different attributes on the same time
	#- (/) enable reset of sorting
	#- (/) Cascading multi-column sorting on multiple columns (Number is showing the sort order)
	#- (/) Sorting of columns is based on complete resultset 
	#- (/) Swagger API documentation is updated and available on DEV
	#- (/) Cover specific columns where cascading sorting might causes conflict (Takeing the semantic under account)
	#h2. Mockup
	#
	#!screenshot-1.png|thumbnail!
	#h2. NFR
	#
	#*
	#h2. Hints / Details / Design Sketch :
	#
	#*<this user story is part of frontend-optimizations and documented in confluenc: [https://confluence.catena-x.net/display/BDPQ/%5BTRACEFOSS-2338%5D+Concept+for+FE+Improvements%3A+Filter%2C+Search%2C+Sort]
	#
	# 
	#
	#!https://confluence.catena-x.net/download/attachments/98568902/image2023-8-17_12-14-30.png?version=1&modificationDate=1692267270895&api=v2!
	#
	#Also add a mouseover to the sorting funcionality where the funcionality with the 3 clicks is described:
	# * first click: "sort in descending order"
	# * second click: "sort in ascending order"
	# * third click: "reset sorting"
	#
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
  @TRACEFOSS-1221 @TRACEFOSS-3373 @TRACEFOSS-3128 @TRACEFOSS-2910 @TRACEFOSS-2715 @TRACEFOSS-1101 @INTEGRATION_TEST
	Scenario Outline: [BE] Check correct filtering of owner in assets
		When I am logged into TRACE_X_A application
		And I request assets with <owner-filter>
		Then I check, if only assets with <owner-filter> are responded

    Examples:
		  | owner-filter |
		  | "SUPPLIER"   |
		  | "CUSTOMER"   |
		  | "OWN"        |
