./gradlew dependencies | grep -Poh "(?<=\s)[\w\.-]+:[\w\.-]+:[^:\s]+" | grep -v "^org\.eclipse" | sort | uniq > DASH_SUMMARY
java -jar org.eclipse.dash.licenses-0.0.1-20221105.055038-599.jar DASH_SUMMARY -project automotive.tractusx -summary DEPENDENCIES
grep "restricted" DEPENDENCIES | if test $(wc -l) -gt 0; then exit 1; fi
