#!/bin/bash

function fail {
  echo $1 >&2
  #Print journald logs
  journalctl -xe -u quizzes-api | cat
  exit 1
}

function retry {
  local n=1
  local max=10
  local delay=10
  while true; do
    "$@" && break || {
    if [[ $n -lt $max ]]; then
      ((n++))
      echo "Command failed. Attempt $n/$max:"
      sleep $delay;
    else
      fail "The command has failed after $n attempts."
    fi
  }
done
}

PORT=8080
ACTUAL_PORT=$(cat /opt/quizzes-api/config.env | grep SERVER_PORT | cut -d'=' -f2 | tr -d '\n')

if [ ! -z "$ACTUAL_PORT" ]; then
  PORT=${ACTUAL_PORT}
fi

retry /usr/bin/curl -S --fail -s http://localhost:${PORT}/health
