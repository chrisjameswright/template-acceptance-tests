#!/bin/bash
ENV="qa"
BROWSER="chrome"

if [ "$1" = "dev" ] || [ "$1" = "qa" ] || [ "$1" = "staging" ]
then
    ENV="$1"
fi
if [ "$2" = "firefox" ] || [ "$2" = "chrome" ]
then
    BROWSER="$2"
fi

if [ $BROWSER = "firefox" ]
then
    sbt -Dbrowser=firefox -Denvironment=$ENV 'test-only uk.gov.hmrc.integration.cucumber.utils.RunnerQA' #|grep -v "error"
elif [ $BROWSER = "chrome" ]
then
    sbt -Dbrowser=chrome -Denvironment=$ENV 'test-only uk.gov.hmrc.integration.cucumber.utils.RunnerQA' #| grep -v "error"
elif [ $BROWSER = "chrome-headless" ]
then
    sbt -Dbrowser=$BROWSER -Denvironment=$ENV 'test-only uk.gov.hmrc.integration.cucumber.utils.RunnerQA' #| grep -v "error"
fi
