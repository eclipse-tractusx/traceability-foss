@TRACEFOSS-1282
Feature: ❓Trace-X Cucumber Test Story

	
	@TRACEFOSS-1283 @INTEGRATION_TEST
	Scenario: CLONE - Test Cucumber Test Story
		Given I have entered 1 into the calculator
		And I have entered 2 into the calculator
		When I press add
		Then the result should be 3 on the screen