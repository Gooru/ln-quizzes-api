#!/usr/bin/env bash
for file in ./spec/quizzes-api/*_spec.js
do
    echo
    echo '===== Executing Test Suite:' $file ' ====='
    echo
    jasmine-node $file
    rc=$?; if [[ $rc != 0 ]]; then exit $rc; fi
done