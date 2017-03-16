#!/bin/bash

#AWS_DEFAULT_REGION=$(curl \
#  -s http://169.254.169.254/latest/meta-data/placement/availability-zone | sed 's/.$//')
#AWS_INSTANCE_ID=$(curl -s http://169.254.169.254/latest/meta-data/instance-id)

#ENVIRONMENT=$(aws ec2 describe-tags \
#  --filters \
#  "Name=resource-type,Values=instance" \
#  "Name=resource-id,Values=${AWS_INSTANCE_ID}" \
#  "Name=key,Values=Environment" \
#  --output=text --region ${AWS_DEFAULT_REGION} | cut -f5)

#if [ -z "${ENVIRONMENT}" ]; then
#  echo "No enviroment value found"
#  exit 1
#fi

#echo "Downloading config file for environment ${ENVIRONMENT}"

#aws s3 cp s3://quizzes-config/${ENVIRONMENT}.env /opt/quizzes-api/config.env

#echo "Chaging file permissions for /opt/quizzes-api"

#chown -R quizzes: /opt/quizzes-api
