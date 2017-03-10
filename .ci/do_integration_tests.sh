#!/bin/bash

source .ci/common.sh

info "Login into Edify's docker registry..."
silent docker login -u $ARTIFACTORY_USERNAME -p $ARTIFACTORY_PASSWORD edify-dkr.jfrog.io

info "Installing virtualenv..."
silent pip install --user -U virtualenv

info "Creating a new virtualenv..."
silent ${HOME}/.local/bin/virtualenv ./.venv
source ./.venv/bin/activate

info "Installing docker compose..."
silent pip install -U docker-compose

info "Starting the application using docker compose..."
silent docker-compose up -d

CLEAN_WORKING_DIR=$(basename "$PWD" | sed 's/-//g' | sed 's/\///g' | tr '[:upper:]' '[:lower:]')

docker kill runner
docker rm runner
docker run -t \
  --net ${CLEAN_WORKING_DIR}_default \
  --rm \
  --name runner \
  --link ${CLEAN_WORKING_DIR}_web_1:quizzes \
  -v ${PWD}:/build \
  -e bamboo_buildNumber=${bamboo_buildNumber} \
  -e bamboo_repository_branch_name=${bamboo_repository_branch_name} \
  -w /build \
  node:6 .ci/integration_tests.sh

EXIT_CODE=$?

if [ "$EXIT_CODE" -eq 1 ]; then
  error "Integration tests failed"
  docker logs ${CLEAN_WORKING_DIR}_web_1
  exit 1
fi

info "Stoping the application..."
silent docker-compose stop

