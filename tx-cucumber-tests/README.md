# Download feature files

 In order to download latest feature files from jira execute following command

``curl -s --show-error -w "%{http_code}" -u $JIRA_USERNAME:$JIRA_PASSWORD "https://jira.catena-x.net/rest/raven/1.0/export/test?filter=11711&fz=true" -o tx-cucumber-tests/features.zip``

replace username and password with Your onw jira credentials

after that extract features.zip to tx-cucumber-tests\src\test\resources\features

# Execute cucumber tests

``mvn --batch-mode clean install -pl tx-cucumber-tests -D"cucumber.filter.tags"="not @Ignore and @INTEGRATION_TEST"``


