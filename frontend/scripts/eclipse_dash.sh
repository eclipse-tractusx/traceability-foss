#!/bin/bash

version=${1:-LATEST}


echo "Using version = ${version}..."

if [[ $2 == "--summary" ]]; then
    java -jar ./scripts/download/org.eclipse.dash.licenses-${version}.jar yarn.lock -project automotive.tractusx -summary ../DEPENDENCIES_FRONTEND
    grep -E '(restricted, #)|(restricted$)' ../DEPENDENCIES_FRONTEND | if test $(wc -l) -gt 0; then exit 1; fi
else
    java -jar ./scripts/download/org.eclipse.dash.licenses-${version}.jar yarn.lock -project automotive.tractusx
fi
