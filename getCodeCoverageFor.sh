#!/usr/bin/env bash

set -e
set -u
set -o pipefail

SCRIPT_CURRENT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

challengeId=$1
JACOCO_TEST_REPORT_CSV_FILE="${SCRIPT_CURRENT_DIR}/build/reports/jacoco/test/jacocoTestReport.csv"

./gradlew clean test jacocoTestReport 1>&2 || true

# $2 - position where the package name containing the challenge id is located in the CSV file
# $9 - position where the line covered information for the challenge id is located in the CSV file
# Note: these two positions could change if a newer update of Jacoco changes the format of the CSV file
cat "${JACOCO_TEST_REPORT_CSV_FILE}" \
	              | awk -F, 'NR > 1 { print $2 "," $9 }' \
                  | grep "${challengeId}" \
                  | awk '{split($0, a, ","); print a[2]}'
