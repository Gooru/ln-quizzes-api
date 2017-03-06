#!/bin/bash

source .ci/common.sh

retry /usr/bin/curl -S --fail -s http://quizzes:8080/health

if [ $UID -eq 0 ]; then
  info "Running as root creating builder user and dropping privileges"
  groupadd -r -g 501 builder && useradd -m -r -g builder -u 500 builder
  curl -sL https://github.com/ncopa/su-exec/archive/v0.2.tar.gz | tar xz -C /tmp
  CURDIR=$PWD
  cd /tmp/su-exec-0.2
  make
  cd $CURDIR
  /tmp/su-exec-0.2/su-exec builder $0
  exit $?
fi

info "Installing global npm dependencies..."
npm config set prefix '/tmp/node_modules'
PATH=/tmp/node_modules/bin:$PATH
silent npm -q install -g npm@latest
silent npm -q install -g \
  jasmine-node@1.14.5

info "Installing frisby"
silent npm install --save-dev frisby@0.8.5

info "Running integration tests"
silent jasmine-node \
  --config QUIZZES_SERVER_URL http://quizzes:8080 integration/spec/quizzes-api \
  --junitreport
