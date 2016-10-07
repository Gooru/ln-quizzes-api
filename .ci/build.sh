#!/bin/bash

set -e

source .ci/common.sh

info "Running build..."
./gradlew clean build -PskipJooq
info "Done building."

