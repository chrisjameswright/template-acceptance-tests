#!/usr/bin/env bash

### Populate Stub with Data
./src/test/resources/stub_data/setup.sh "local" "???"

echo "Running browser tests..."
### Run Test Suite with ZAP browser capabilities
sbt -Dbrowser=zap -Denvironment=local 'testOnly runner.runnerClass'

sleep 5

echo "Running Zap Tests..."
### Run ZAP Penetration Tests
sbt 'testOnly runner.ZapRunner'