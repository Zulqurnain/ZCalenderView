#!/usr/bin/env bash

# This script sends the generated APK to a Slack channel

APK_PATH="app/build/outputs/apk/release"

cd "${APK_PATH}" || exit

APK_FILE=$(find . -name "*.apk")
BRANCH=$(git rev-parse --abbrev-ref HEAD)
HASH=$(git rev-parse --short HEAD)
PUBLISHED_FILE="${APK_FILE/.apk/-${BRANCH}-${HASH}.apk}"
mv "${APK_FILE}" "${PUBLISHED_FILE}"

# Slack API endpoint information
SLACK_CHANNEL_ID="your_slack_channel_id"
BOT_TOKEN="your_bot_token"
APP_AUTH_TOKEN="your_app_auth_token"

curl -X POST \
    https://slack.com/api/files.upload \
    -H "Authorization: Bearer ${APP_AUTH_TOKEN}" \
    -F file=@"${PUBLISHED_FILE}" \
    -F channels="${SLACK_CHANNEL_ID}" \
    -F token="${BOT_TOKEN}"
