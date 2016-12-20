#!/usr/bin/env bash
for file in ./spec/quizzes-api/*spec.js
do
echo $file
jasmine-node $file
done