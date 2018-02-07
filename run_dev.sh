#!/bin/bash
ENV="dev"
BROWSER="chrome"

# if firefox is greater than version 59 then automatically use chrome
if [ $BROWSER = "firefox" ]
then
 firefoxVersion=`firefox -v | grep -P '\s([0-9]*)' -o`
 if [ $firefoxVersion -gt 59 ]
 then
  BROWSER="chrome"
 fi
fi
# end firefox check section

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
    sbt -Dbrowser=firefox -Denvironment=$ENV 'test-only uk.gov.hmrc.integration.cucumber.utils.Runner' #|grep -v "error"
elif [ $BROWSER = "chrome" ]
then
    sbt -Dbrowser=chrome -Denvironment=$ENV 'test-only uk.gov.hmrc.integration.cucumber.utils.Runner' #| grep -v "error"
elif [ $BROWSER = "chrome-headless" ]
then
    sbt -Dbrowser=$BROWSER -Denvironment=$ENV 'test-only uk.gov.hmrc.integration.cucumber.utils.Runner' #| grep -v "error"
fi
