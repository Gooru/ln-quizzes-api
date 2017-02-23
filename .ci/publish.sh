#!/bin/bash

set -e

source .ci/common.sh

GIT_BRANCH=$(echo ${bamboo_repository_branch_name} | sed 's/\//-/')
VERSION=$(cat gradle.properties | grep version | cut -d "=" -f2)
GIT_COMMIT_HASH="${bamboo_planRepository_revision}"

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

