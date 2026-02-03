#!/bin/bash
set -e

echo "Creating public S3 bucket..."

awslocal s3 mb s3://decade-bucket

awslocal s3api put-bucket-policy \
  --bucket decade-bucket \
  --policy '{
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": "*",
        "Action": "s3:GetObject",
        "Resource": "arn:aws:s3:::decade-bucket/*"
      }
    ]
  }'

awslocal s3api put-bucket-cors \
  --bucket decade-bucket \
  --cors-configuration '{
    "CORSRules": [
      {
        "AllowedOrigins": ["*"],
        "AllowedMethods": ["GET","PUT","POST","DELETE","HEAD"],
        "AllowedHeaders": ["*"],
        "ExposeHeaders": ["ETag"]
      }
    ]
  }'

echo "S3 bucket initialized"
