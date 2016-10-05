#!/bin/bash

set -e

source .ci/common.sh

info "Running build..."
./gradlew clean build
info "Done building."

