#!/bin/sh

curl -s https://api.github.com/repos/peh/bamboohr-slack-bot/releases/latest | \
    jq '.assets[] | select(.name = "current.war") |  .browser_download_url' --raw-output | \
    xargs curl -L# --output /app/current.jar

java -jar /app/current.jar -Dredis.host=redis
