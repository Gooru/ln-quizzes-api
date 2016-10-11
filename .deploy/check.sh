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

retry /usr/bin/curl -S --fail -s http://localhost:8080/health
