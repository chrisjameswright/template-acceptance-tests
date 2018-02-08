#!/bin/bash
ENV="local"
if [ "$1" = "dev" ] || [ "$1" = "qa" ] || [ "$1" = "staging" ]
then
    ENV="$1"
fi
echo "Environment : $ENV"

echo "BrowserStackLocal instances:"
pidof BrowserStackLocal
if pidof BrowserStackLocal; then
  echo "BrowserStackLocal running already"
else
	if [ -f ./BrowserStackLocal ];
	then
	   echo "File BrowserStackLocal exists."
	else
	   	wget https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip
  	    unzip BrowserStackLocal-linux-x64.zip
	fi
  . ./src/test/resources/browserConfig.properties
  ./BrowserStackLocal $automatekey &
fi

# PHONES: declare -a setups=("BS_iOS_iPhone5S_v7")
# PADS: declare -a setups=("iPad_Mini_v7" "iPad_Air_v8_3")
# BROWSERS: declare -a setups=("BS_Win10_Chrome_v61" "BS_Win10_Firefox_v56" "BS_Win10_IE_v11" "BS_Win10_Edge_v15" "BS_Sierra_Chrome_v61" "BS_Win7_Chrome_v61" "BS_Win7_Firefox_v56" "BS_Win7_IE_v8" "BS_Win7_IE_v11" "BS_Win8_IE_v10" "BS_ElCapitan_Firefox_v56" "BS_Yosemite_Chrome_v61" "BS_iOS_iPhone5S_v7" "iPad_Mini_v7" "iPad_Air_v8_3")
declare -a setups=("iPad_Air_v8_3")
#declare -a setups=("BS_Win10_Chrome_v61" "BS_Win10_Firefox_v56" "BS_Win10_IE_v11" "BS_Win10_Edge_v15" "BS_Sierra_Chrome_v61" "BS_Win7_Chrome_v61" "BS_Win7_Firefox_v56" "BS_Win7_IE_v8" "BS_Win7_IE_v11" "BS_Win8_IE_v10" "BS_ElCapitan_Firefox_v56" "BS_Yosemite_Chrome_v61" "BS_iOS_iPhone5S_v7" "iPad_Mini_v7" "iPad_Air_v8_3")
for setup in "${setups[@]}"
do
    echo "******************** Loading config from $setup.json ********************"
    sbt -Dbrowser=browserstack -DtestDevice="$setup" -Denvironment=$ENV 'test-only uk.gov.hmrc.integration.cucumber.utils.RunnerBrowserStackTests'
    shift
done
