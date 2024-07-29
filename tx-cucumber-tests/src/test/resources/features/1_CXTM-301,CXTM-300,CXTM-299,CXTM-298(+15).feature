Feature:

	@TEST_CXTM-301 @trace-x-automated
	Scenario: TRACEFOSS-1216 [BE] Check correct processing of targetDate in quality investigation
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		  | "severity"    | "MINOR"                             |
		  | "description" | "Testing targetDate TRACEFOSS-1216" |
		  | "targetDate"  | "2099-03-11T22:44:06.333827Z"       |
		  | "type"        | "INVESTIGATION"                     |
		Then I check, if quality notification has proper values
		  | "description" | "Testing targetDate TRACEFOSS-1216" |
		  | "targetDate"  | "2099-03-11T22:44:06.333827Z"       |
		  | "status"      | "CREATED"                           |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "description" | "Testing targetDate TRACEFOSS-1216" |
		  | "targetDate"  | "2099-03-11T22:44:06.333827Z"       |
		  | "status"      | "RECEIVED"                          |

	@TEST_CXTM-300 @trace-x-automated
	Scenario Outline: TRACEFOSS-1220 [BE] Check correct processing of severity in quality investigation
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		  | "severity"    | <severity> |
		  | "description" | "Testing severity TRACEFOSS-1220" |
		  | "type"        | "INVESTIGATION"                   |
		Then I check, if quality notification has proper values
		  | "severity"    | <severity> |
		  | "description" | "Testing severity TRACEFOSS-1220" |
		  | "status"      | "CREATED" |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "severity"    | <severity> |
		  | "description" | "Testing severity TRACEFOSS-1220" |
		  | "status"      | "RECEIVED" |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |

		Examples:
		|severity|
		|"MINOR"|
		|"MAJOR"|
		|"CRITICAL"|
		|"LIFE-THREATENING"|

	@TEST_CXTM-299 @trace-x-automated
	Scenario Outline: TRACEFOSS-1221 [BE] Check correct filtering of owner in assets
		When I am logged into TRACE_X_A application
		And I request assets with <owner-filter>
		Then I check, if only assets with <owner-filter> are responded

		Examples:
		  | owner-filter |
		  | "SUPPLIER"   |
		  | "CUSTOMER"   |
		  | "OWN"        |

	@TEST_CXTM-298 @trace-x-automated
	Scenario: TRACEFOSS-1222 [BE] Check correct processing of ACCEPTANCE of quality investigation
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		  | "severity"    | "MAJOR"                             |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1222" |
		  | "type" | "INVESTIGATION" |
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

	@TEST_CXTM-297 @trace-x-automated
	Scenario: TRACEFOSS-1223 [BE] Check correct processing of DECLINATION of quality notification
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		  | "severity"    | "MAJOR"                              |
		  | "description" | "Testing DECLINATION TRACEFOSS-1223" |
		  | "type"        | "INVESTIGATION"                      |
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR"                              |
		  | "description" | "Testing DECLINATION TRACEFOSS-1223" |
		  | "status"      | "CREATED"                            |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR"                              |
		  | "description" | "Testing DECLINATION TRACEFOSS-1223" |
		  | "status"      | "RECEIVED"                           |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I decline quality notification
		  | "reason" | "declined in TRACEFOSS-1223" |
		Then I check, if quality notification has proper values
		  | "status" | "DECLINED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status"        | "DECLINED"                   |
		  | "declineReason" | "declined in TRACEFOSS-1223" |

	@TEST_CXTM-294 @trace-x-automated
	Scenario: TRACEFOSS-1247 [BE] Check correct processing of targetDate = null in quality investigation
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		  | "severity"    | "MINOR"                                     |
		  | "description" | "Testing without targetDate TRACEFOSS-1247" |
		  | "type"        | "INVESTIGATION"                     |
		Then I check, if quality notification has proper values
		  | "description" | "Testing without targetDate TRACEFOSS-1247" |
		  | "targetDate"  | ""                                          |
		  | "status"      | "CREATED"                                   |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "description" | "Testing without targetDate TRACEFOSS-1247" |
		  | "targetDate"  | ""                                          |
		  | "status"      | "RECEIVED"                                  |

	@TEST_CXTM-291 @trace-x-automated
	Scenario Outline: TRACEFOSS-1539 [BE] Check correct processing of severity in quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | <severity> |
		  | "description" | "Testing severity TRACEFOSS-1539" |
		  | "type" | "ALERT" |
		Then I check, if quality notification has proper values
		  | "severity"    | <severity> |
		  | "description" | "Testing severity TRACEFOSS-1539" |
		  | "status"      | "CREATED" |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "severity"    | <severity> |
		  | "description" | "Testing severity TRACEFOSS-1539" |
		  | "status"      | "RECEIVED" |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |

		Examples:
		|severity|
		|"MINOR"|
		|"MAJOR"|
		|"CRITICAL"|
		|"LIFE-THREATENING"|

	@TEST_CXTM-289 @trace-x-automated
	Scenario: TRACEFOSS-1543 [BE] Check correct processing of targetDate in quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MINOR" |
		  | "description" | "Testing targetDate TRACEFOSS-1543" |
		  | "targetDate"  | "2055-05-30T20:43:06.333827Z" |
		  | "type" | "ALERT" |
		Then I check, if quality notification has proper values
		  | "description" | "Testing targetDate TRACEFOSS-1543" |
		  | "targetDate"  | "2055-05-30T20:43:06.333827Z" |
		  | "status"      | "CREATED" |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "description" | "Testing targetDate TRACEFOSS-1543" |
		  | "targetDate"  | "2055-05-30T20:43:06.333827Z" |
		  | "status"      | "RECEIVED" |

	@TEST_CXTM-288 @trace-x-automated
	Scenario: TRACEFOSS-1544 [BE] Check correct processing of ACCEPTANCE of quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1544" |
		  | "type" | "ALERT" |
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1544" |
		  | "status"      | "CREATED" |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1544" |
		  | "status"      | "RECEIVED" |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I accept quality notification
		  | "reason" | "accepted in TRACEFOSS-1544" |
		Then I check, if quality notification has proper values
		  | "status" | "ACCEPTED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status" | "ACCEPTED" |
		And I check, if quality notification has proper values
		  | "acceptReason" | "accepted in TRACEFOSS-1544" |

	@TEST_CXTM-287 @trace-x-automated
	Scenario: TRACEFOSS-1545 [BE] Check correct processing of DECLINATION of quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing DECLINATION TRACEFOSS-1545" |
		  | "type" | "ALERT" |
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing DECLINATION TRACEFOSS-1545" |
		  | "status"      | "CREATED" |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing DECLINATION TRACEFOSS-1545" |
		  | "status"      | "RECEIVED" |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I decline quality notification
		  | "reason" | "declined in TRACEFOSS-1545" |
		Then I check, if quality notification has proper values
		  | "status" | "DECLINED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status" | "DECLINED" |
		And I check, if quality notification has proper values
		  | "declineReason" | "declined in TRACEFOSS-1545" |

	@TEST_CXTM-286 @trace-x-automated
	Scenario: TRACEFOSS-1546 [BE] Check correct processing of targetDate = null in quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MINOR" |
		  | "description" | "Testing without targetDate TRACEFOSS-1546" |
		  | "type" | "ALERT" |
		Then I check, if quality notification has proper values
		  | "description" | "Testing without targetDate TRACEFOSS-1546" |
		  | "targetDate"  | "" |
		  | "status"      | "CREATED" |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "description" | "Testing without targetDate TRACEFOSS-1546" |
		  | "targetDate"  | "" |
		  | "status"      | "RECEIVED" |

	@TEST_CXTM-284 @trace-x-automated
	Scenario: TRACEFOSS-1652 [BE] Check correct processing of several parts in quality investigation
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

	@TEST_CXTM-283 @trace-x-automated
	Scenario: TRACEFOSS-1670 [BE] Check correct processing of several parts in quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22,urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MINOR"                           |
		  | "description" | "Testing severity TRACEFOSS-1670" |
		  | "type" | "ALERT" |
		Then I check, if quality notification has proper values
		  | "description"  | "Testing severity TRACEFOSS-1670" |
		  | "status"       | "CREATED"                         |
		  | "assetIdCount" | "2"                               |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		Then I check, if quality notification has proper values
		  | "description"  | "Testing severity TRACEFOSS-1670" |
		  | "status"       | "RECEIVED"                        |
		  | "assetIdCount" | "2"                               |
		When I acknowledge quality notification
		Then I check, if quality notification has proper values
		  | "status" | "ACKNOWLEDGED" |
		When I am logged into TRACE_X_A application
		Then I check, if quality notification has proper values
		  | "status"       | "ACKNOWLEDGED" |
		  | "assetIdCount" | "2"            |

	@TEST_CXTM-282 @trace-x-automated
	Scenario: TRACEFOSS-1861 [BE] Check correct processing of CLOSURE of quality investigation
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
		And I create quality notification
		| "severity"    | "MAJOR"                             |
		| "description" | "Testing CLOSURE TRACEFOSS-1861" |
		| "type" | "INVESTIGATION" |
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

	@TEST_CXTM-281 @trace-x-automated
	Scenario: TRACEFOSS-1862 [BE] Check correct processing of CANCELLATION of quality notification
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

	@TEST_CXTM-280 @trace-x-automated
	Scenario: TRACEFOSS-1863 [BE] Check correct processing of CLOSURE of quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1863" |
		  | "type" | "ALERT" |
		Then I check, if quality notification has proper values
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1863" |
		  | "status"      | "CREATED" |
		When I approve quality notification
		Then I check, if quality notification has proper values
		  | "status" | "SENT" |
		When I am logged into TRACE_X_B application
		Then I check, if quality notification has been received
		And I check, if quality notification has proper values
		  | "status"      | "RECEIVED" |
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

	@TEST_CXTM-279 @trace-x-automated
	Scenario: TRACEFOSS-1864 [BE] Check correct processing of CANCELLATION of quality alerts
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		  | "type" | "ALERT" |
		Then I check, if quality notification has proper values
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		  | "status"      | "CREATED" |
		When I cancel quality notification
		Then I check, if quality notification has proper values
		  | "status" | "CANCELED" |

		When I am logged into TRACE_X_B application
		Then I check, if quality notification has not been received

	@TEST_CXTM-273 @trace-x-automated
	Scenario: TRACEFOSS-3354 [BE] Check edit notification of quality notification
		When I am logged into TRACE_X_A application
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02'
		And I create quality notification
		  | "severity"    | "MAJOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		  | "type" | "ALERT" |
		  | "title" | "Initial title" |
		Then I check, if quality notification has proper values
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864" |
		  | "status"      | "CREATED" |
		When I use assets with ids 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa03'
		When I edit quality notification
		  | "severity"    | "MINOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864 edited" |
		  | "title" | "New Title" |

		Then I check, if quality notification has proper values
		  | "status" | "CREATED" |
		  | "severity"    | "MINOR" |
		  | "description" | "Testing ACCEPTANCE TRACEFOSS-1864 edited" |
		  | "title" | "New Title" |
		  | "affectedPartId" | "urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa03" |


