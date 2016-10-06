#!/bin/bash

# This script is not going to be run from the source code since Bamboo deployments don't contain the source code only artifacts.
# But it would be the reference script to be copy&paste into Bamboo.

#Ensure that we run in bash not sh
if [ "$(ps -p "$$" -o comm=)" != "bash" ]; then
  # Taken from http://unix-linux.questionfor.info/q_unix-linux-programming_85038.html
  bash "$0" "$@"
  exit "$?"
fi

set -e

function wait_for_deployment() {

  local deployment_id=$1
  local deployment_status_cmd="aws deploy get-deployment --deployment-id ${deployment_id} --query deploymentInfo.status"
  local deployment_status=$($deployment_status_cmd | tr -d '"')

  echo "Checking status of deployment \"$deployment_id\""

  while true; do
    echo "Current deployment status \"$deployment_status\""
    if [ "$deployment_status" == "Succeeded" ]; then
      echo "Deployment successful"
      return 0
    fi

    if [ "$deployment_status" == "Failed" ]; then
      echo "Deployment failed"
      return 1
    fi

    sleep 10
    deployment_status=$($deployment_status_cmd | tr -d '"')
  done
}

if [ -z "$AWS_ACCESS_KEY_ID" ] || [ -z "$AWS_SECRET_ACCESS_KEY" ] || [ -z "$AWS_DEFAULT_REGION" ]; then
  echo "No AWS credentials provided"
  exit 1
fi

if [ -z "$S3_BUCKET" ]; then
  echo "No S3 bucket provided"
  exit 1
fi

if [ -z "$DEPLOYMENT_GROUP" ] || [ -z "$CODE_DEPLOY_APP_NAME" ]; then
  echo "No deployment group or application name provided"
  exit 1
fi

S3_ARTIFACT_KEY=$(cat s3.artifact)

DEPLOYMENT_ID=$(aws deploy create-deployment \
  --application-name "${CODE_DEPLOY_APP_NAME}" \
  --s3-location "bucket=${S3_BUCKET},key=${S3_ARTIFACT_KEY},bundleType=tar" \
  --deployment-group-name "${DEPLOYMENT_GROUP}" --query deploymentId | tr -d '"')

echo "Deployment \"$DEPLOYMENT_ID\" created"

wait_for_deployment $DEPLOYMENT_ID
