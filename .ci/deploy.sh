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

RED="\e[31m"
GREEN="\e[32m"
NORMAL="\e[0m"

function error() {
  echo -e "\n$RED-------> $1 $NORMAL"
}

function info() {
  echo -e "\n$GREEN-------> $1 $NORMAL"
}

function wait_for_deployment() {

  local deployment_id=$1
  local deployment_status_cmd="aws deploy get-deployment --deployment-id ${deployment_id} --query deploymentInfo.status"
  local deployment_status=$($deployment_status_cmd | tr -d '"')
  local n=1
  local max=10
  local delay=10

  info "Checking status of deployment \"$deployment_id\""

  while true; do
    if [[ $n -lt $max ]]; then
      ((n++))
      info "Current deployment status \"$deployment_status\""
      if [ "$deployment_status" == "Succeeded" ]; then
        info "Deployment successful"
        return 0
      fi

      if [ "$deployment_status" == "Failed" ]; then
        error "Deployment failed"
        echo "---------------------"
        error "For more info check: https://console.aws.amazon.com/cloudwatch/home?region=${AWS_DEFAULT_REGION}#logStream:group=quizzes-api-qa-codedeploy-deployments-log"
        echo "---------------------"
        return 1
      fi

      sleep 10
    else
      error "Fail to assert deployment status after $n attempts."
      return 1
    fi
    deployment_status=$($deployment_status_cmd | tr -d '"')
  done
}

GIT_BRANCH=$(echo ${bamboo_repository_branch_name} | sed 's/\//-/')
VERSION=$(cat gradle.properties | grep version | cut -d "=" -f2)
GIT_COMMIT_HASH="${bamboo_planRepository_revision}"

FULL_ARTIFACT_VERSION="${VERSION}-${GIT_BRANCH}-${GIT_COMMIT_HASH}"

ARTIFACT_S3_KEY="quizzes-api/${VERSION}/${GIT_BRANCH}/quizzes-api-${FULL_ARTIFACT_VERSION}.tar.gz"

if [ -z "$AWS_ACCESS_KEY_ID" ] || [ -z "$AWS_SECRET_ACCESS_KEY" ] || [ -z "$AWS_DEFAULT_REGION" ]; then
  error "No AWS credentials provided"
  exit 1
fi

if [ -z "$S3_BUCKET" ]; then
  error "No S3 bucket provided"
  exit 1
fi

if [ -z "$DEPLOYMENT_GROUP" ] || [ -z "$CODE_DEPLOY_APP_NAME" ]; then
  error "No deployment group or application name provided"
  exit 1
fi

S3_ARTIFACT_KEY=$(cat s3.artifact)

DEPLOYMENT_ID=$(aws deploy create-deployment \
  --application-name "${CODE_DEPLOY_APP_NAME}" \
  --s3-location "bucket=${S3_BUCKET},key=${ARTIFACT_S3_KEY},bundleType=tar" \
  --deployment-group-name "${DEPLOYMENT_GROUP}" --query deploymentId | tr -d '"')

info "Deployment \"$DEPLOYMENT_ID\" created"

wait_for_deployment $DEPLOYMENT_ID
