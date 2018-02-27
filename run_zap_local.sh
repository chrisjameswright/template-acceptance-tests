#!/usr/bin/env bash

zapProxyPort=11000
startZapDaemon=true

# check if there's already something listening on zapProxyPort
listen=`netstat -lnt | grep $zapProxyPort`
if [ ! -z "$listen" ] ; then
    # do not start the daemon if something is already listening on this port
    # n.b. here we are making the assumption that only zap will run on this port...
    startZapDaemon=false
fi

### Start ZAP daemon
if ($startZapDaemon); then
    echo "Starting ZAP Daemon on port $zapProxyPort..."
    zap.sh -daemon -config api.disablekey=true -port ${zapProxyPort} &

    function finish {
        echo "Killing Zap Daemon..."
        curl --silent http://localhost:$zapProxyPort/HTML/core/action/shutdown
    }
    # if we started the daemon then we need to kill it
    trap finish EXIT

    sleep 10
fi

### Run Test Suite with ZAP browser capabilities
sbt -Dbrowser=zap-chrome -Denvironment=local -DzapProxyPort=$zapProxyPort 'test-only uk.gov.hmrc.integration.cucumber.utils.RunnerZAP'

### Run ZAP Penetration Tests
sbt 'test-only uk.gov.hmrc.integration.cucumber.utils.ZapRunner'



