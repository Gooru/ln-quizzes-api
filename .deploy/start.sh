#!/bin/bash

systemctl daemon-reload
systemctl enable quizzes-api.service
systemctl restart quizzes-api.service

