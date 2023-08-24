# Download feature files

 In order to download latest feature files from jira execute following command

``curl -s --show-error -w "%{http_code}" -u $JIRA_USERNAME:$JIRA_PASSWORD "https://jira.catena-x.net/rest/raven/1.0/export/test?filter=11711&fz=true" -o tx-cucumber-tests/features.zip``

replace username and password with Your onw jira credentials

after that extract features.zip to tx-cucumber-tests\src\test\resources\features

# Execute cucumber tests with Maven

Be aware that you need to provide the following system variables before running it with Maven.

````
E2E_TXA_HOST=https://traceability-e2e-a.dev.demo.catena-x.net
E2E_TXB_HOST=https://traceability-e2e-b.dev.demo.catena-x.net
KEYCLOAK_HOST=https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/token
SUPERVISOR_CLIENT_ID={take this value from Insomnia collection}
SUPERVISOR_PASSWORD={take this value from Insomnia collection}
````

After providing these variables in you system, you can execute maven as follows.

``mvn --batch-mode clean install -pl tx-cucumber-tests -D"cucumber.filter.tags"="not @Ignore and @INTEGRATION_TEST"``

# Execute cucumber tests with IntelliJ

If you want to execute the cucumber tests within IntelliJ, you have to edit you Cucumber Java configuration Template.
To do this, head to your run configurations on the top right. Click "Edit configurations..." -> "Edit configuration Templates...".
Next, head to "Cucumber Java" and paste below variables into the "Environment Variables" Section. Add the correct values for the missing ones after.

````
E2E_TXA_HOST=https://traceability-e2e-a.dev.demo.catena-x.net
E2E_TXB_HOST=https://traceability-e2e-b.dev.demo.catena-x.net
KEYCLOAK_HOST=https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/token
SUPERVISOR_CLIENT_ID={take this value from Insomnia collection}
SUPERVISOR_PASSWORD={take this value from Insomnia collection}
````

Now you should be able to use IntelliJ to run Cucumber tests by just clicking run on the desired test.
