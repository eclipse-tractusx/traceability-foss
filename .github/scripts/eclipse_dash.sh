./gradlew dependencies | grep -Poh "(?<=\s)[\w\.-]+:[\w\.-]+:[^:\s]+" | grep -v "^org\.eclipse" | sort | uniq > DEPENDENCIES
java -jar org.eclipse.dash.licenses-0.0.1-20221105.055038-599.jar DEPENDENCIES -project automotive.tractusx -summary DASH_SUMMARY
grep "restricted" DASH_SUMMARY | if test $(wc -l) -gt 0; then exit 1; fi
