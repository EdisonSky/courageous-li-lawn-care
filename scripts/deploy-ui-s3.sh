#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${WEBSITE_BUCKET:-}" ]]; then
  echo "Set WEBSITE_BUCKET first, e.g.:"
  echo "  export WEBSITE_BUCKET=courageous-li-lawn-website-123456789"
  exit 1
fi

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/lawn-ui"

if [[ ! -f .env.production ]]; then
  echo "Create lawn-ui/.env.production from .env.production.example"
  exit 1
fi

npm install
npm run build
aws s3 sync dist/ "s3://${WEBSITE_BUCKET}" --delete

echo "Done. Open your S3 static website URL from the AWS Console (bucket → Properties → Static website hosting)."
