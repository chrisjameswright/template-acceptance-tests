#!/bin/bash
ENV="local"
BROWSER="firefox"

sbt -Dbrowser=$BROWSER -Denvironment=$ENV  -Dwebdriver.gecko.driver=/drivers/geckodriver 'test-only uk.gov.hmrc.integration.cucumber.utils.Runner'