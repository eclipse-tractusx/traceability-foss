curl --request POST \
          -u $JIRA_USERNAME:$JIRA_PASSWORD \
          --header 'Content-Type: application/json' \
          --data-binary '@cypress/reports/cucumber-report.json' \
          "https://jira.catena-x.net/rest/raven/1.0/import/execution/cucumber"
