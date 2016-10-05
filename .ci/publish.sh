#!/bin/bash

set -e

source .ci/common.sh

#if [ -z "$ARTIFACTORY_USERNAME" ] || [ -z "$ARTIFACTORY_PASSWORD" ]; then
#  error "No Artifactory credentials provided."
#  exit 1
#fi

VERSION=$(cat gradle.properties | grep version | cut -d "=" -f2)
GIT_COMMIT_HASH=$(git rev-parse HEAD)
GIT_BRANCH=$(git rev-parse --abbrev-ref HEAD | sed 's/\//-/')

#info "Publishing version $VERSION to artifactory..."

#STATUS_CODE=$(curl -o /dev/null -s -w "%{http_code}\\n" -u$ARTIFACTORY_USERNAME:$ARTIFACTORY_PASSWORD -T build/libs/quizzes-api-${VERSION}.jar "https://edify.jfrog.io/edify/gooruweb-releases-local/quizzes-api/quizzes-api-${VERSION}.jar")

#if [ "$STATUS_CODE" != "201" ]; then
#  error "Fail to publish to artifactory status code $STATUS_CODE."
#  exit 1
#fi

if [ -z "$S3_BUCKET" ]; then
  error "No S3 bucket specified."
  exit 1
fi

if [ -z "$AWS_ACCESS_KEY_ID" ] || [ -z "$AWS_SECRET_ACCESS_KEY" ] || [ -z "$AWS_DEFAULT_REGION" ]; then
  error "No AWS credentials provided."
  exit 1
fi

info "Creating CodeDeploy bundle..."

FULL_ARTIFACT_VERSION="${VERSION}-${GIT_BRANCH}-${GIT_COMMIT_HASH}"

cp build/libs/quizzes-api-${VERSION}.jar .deploy/quizzes-api.jar
$(cd .deploy && tar czf ../quizzes-api-${FULL_ARTIFACT_VERSION}.tar.gz .)

info "CodeDeploy bundle created."

info "Publishing version $FULL_ARTIFACT_VERSION to S3..."

aws s3 cp quizzes-api-${FULL_ARTIFACT_VERSION}.tar.gz s3://${S3_BUCKET}/quizzes-api/${VERSION}/${GIT_BRANCH}/quizzes-api-${FULL_ARTIFACT_VERSION}.tar.gz

info "Done publishing."

info "Creating S3 artifact for Bamboo deployments..."

echo "quizzes-api/${VERSION}/${GIT_BRANCH}/quizzes-api-${FULL_ARTIFACT_VERSION}.tar.gz" > s3.artifact

