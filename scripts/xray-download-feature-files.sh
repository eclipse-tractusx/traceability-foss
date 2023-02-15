# https://docs.getxray.app/display/XRAY/Exporting+Cucumber+Tests+-+REST
curl -u $JIRA_USERNAME:$JIRA_PASSWORD "https://jira.catena-x.net/rest/raven/1.0/export/test?filter=${JIRA_FILTER_ID:-11645}&fz=true" -o features.zip
unzip -o features.zip -d cypress/e2e
rm -f features.zip
