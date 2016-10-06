#!/bin/bash

sudo systemctl daemon-reload
sudo systemctl stop quizzes-api.service
sudo systemctl enable quizzes-api.service
sudo systemctl start quizzes-api.service

