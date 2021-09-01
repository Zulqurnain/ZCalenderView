#!/usr/bin/env bash

# This script sends the generated apk to slack channel

APK_PATH="app/build/outputs/apk/release"

cd ${APK_PATH} || exit

APK_FILE=`find *.apk`
PUBLISHED_FILE=$(echo $APK_FILE | sed 's@.apk@'"-$BRANCH-$HASH.apk"'@')
mv ${APK_FILE} ${PUBLISHED_FILE}

# ENDPOINT info
SLACK_CHANNEL_ID=slack_channel_name
BOT_TOKEN=XXXXXXXXXXXXXXXXXXXXXXX
APP_AUTH_TOKEN=XXXXXXXXXXXXXXXXXXX

curl -X POST \
    https://slack.com/api/files.upload \
    -H "Authorization: Bearer ${APP_AUTH_TOKEN}" \
    -H 'Content-Type: multipart/form-data' \
    -F file=@"${PUBLISHED_FILE}" \
    -F token=${BOT_TOKEN} \
    -F channels=${SLACK_CHANNEL_ID}
