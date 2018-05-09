#!/usr/bin/env bash

set -e
set -u
set -o pipefail

SCRIPT_CURRENT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

CHALLENGE_ID=$1
JACOCO_TEST_REPORT_CSV_FILE="${SCRIPT_CURRENT_DIR}/build/reports/jacoco/test/jacocoTestReport.xml"

./gradlew -q clean test jacocoTestReport || true 1>&2

xmllint --xpath '//package[@name="befaster/solutions/'${CHALLENGE_ID}'"]/counter[@type="INSTRUCTION"]' ${JACOCO_TEST_REPORT_CSV_FILE} 1>&2