#!/usr/bin/env bash

set -e
set -u
set -o pipefail

challengeId=$1
JACOCO_TEST_REPORT_CSV_FILE="./build/reports/jacoco/test/jacocoTestReport.csv"

gradle clean test jacocoTestReport &>2 || true

# $2 - position where the package name containing the challenge id is located in the CSV file
# $9 - position where the line covered information for the challenge id is located in the CSV file
# Note: these two positions could change if a newer update of Jacoco changes the format of the CSV file
cat "${JACOCO_TEST_REPORT_CSV_FILE}" \
	              | awk -F, 'NR > 1 { print $2 "," $9 }' \
                  | grep "${challengeId}" \
                  | awk '{split($0, a, ","); print a[2]}'
