#!/bin/bash

source .ci/common.sh

info "Installing docker compose..."
silent pip install --user -U docker-compose

info "Starting the application using docker compose..."
${HOME}/.local/bin/docker-compose up -d

retry /usr/bin/curl -S --fail -s http://localhost:8080/health

CLEAN_WORKING_DIR=$(basename "$PWD" | sed 's/-//g' | sed 's/\///g' | tr '[:upper:]' '[:lower:]')

docker kill runner
docker rm runner
docker run -t \
  --network ${CLEAN_WORKING_DIR}_default \
  --rm \
  --name runner \
  --link ${CLEAN_WORKING_DIR}_web_1:quizzes \
  -v ${PWD}:/build \
  -e bamboo_buildNumber=${bamboo_buildNumber} \
  -e bamboo_repository_branch_name=${bamboo_repository_branch_name} \
  -w /build \
  node:4.6 .ci/integration_tests.sh

EXIT_CODE=$?

if [ "$EXIT_CODE" -eq 1 ]; then
  error "Integration tests failed"
  exit 1
fi

info "Stoping the application..."
${HOME}/.local/bin/docker-compose stop

