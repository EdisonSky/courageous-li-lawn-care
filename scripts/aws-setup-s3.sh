#!/usr/bin/env bash
set -euo pipefail

AWS_REGION="${AWS_REGION:-us-east-1}"
ACCOUNT_ID="$(aws sts get-caller-identity --query Account --output text)"
WEBSITE_BUCKET="${WEBSITE_BUCKET:-courageous-li-lawn-website-${ACCOUNT_ID}}"
UPLOAD_BUCKET="${UPLOAD_BUCKET:-courageous-li-lawn-uploads-${ACCOUNT_ID}}"

echo "Region: $AWS_REGION"
echo "Website bucket: $WEBSITE_BUCKET"
echo "Uploads bucket: $UPLOAD_BUCKET"

aws s3 mb "s3://${WEBSITE_BUCKET}" --region "$AWS_REGION" 2>/dev/null || true
aws s3 mb "s3://${UPLOAD_BUCKET}" --region "$AWS_REGION" 2>/dev/null || true

aws s3 website "s3://${WEBSITE_BUCKET}" \
  --index-document index.html \
  --error-document index.html

cat <<EOF

Created buckets. Save these names:

  WEBSITE_BUCKET=$WEBSITE_BUCKET
  UPLOAD_BUCKET=$UPLOAD_BUCKET

Website URL (after you upload dist/):
  http://${WEBSITE_BUCKET}.s3-website-${AWS_REGION}.amazonaws.com

Deploy UI:
  cd lawn-ui && npm run build
  aws s3 sync dist/ s3://${WEBSITE_BUCKET} --delete

EOF
