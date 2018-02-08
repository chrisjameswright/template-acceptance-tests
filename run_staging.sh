#!/bin/bash
sbt -Dbrowser=chrome -Denvironment=staging 'test-only uk.gov.hmrc.integration.cucumber.utils.Runner'

