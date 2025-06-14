#!/usr/bin/env bash

SCRIPT=$(basename ${BASH_SOURCE[0]})
PR=$1
VERSION=$2
REGION=$3
ARCHIVE_PATH=$4

print() {
  printf "[${SCRIPT}] %s\n" "$*"
}

do_upload() {
  print "$FUNCNAME"
  print "Uploading test results to S3"
  print "Version=$VERSION, region=$REGION"

  cd /tmp

  aws s3 cp \
    tests-results/ \
    s3://geneva-apps-dev-$REGION/geneva-api/$PR/$VERSION/tests/acceptance \
    --sse AES256 --recursive

  STATUS=$?
  if [ $STATUS -eq 0 ]
  then
    echo "Test results upload completed successfully"
  else
    echo "Test results upload failed. Status code: $STATUS"
  fi
}

do_archive() {
  print "$FUNCNAME"
  print "Archive test results"
  print "Version=$VERSION, PR=$PR"

  if [ -d tests-results/ ]; then
    mkdir -p $ARCHIVE_PATH/ || true
    cp -v -R tests-results/ $ARCHIVE_PATH/ || true
  fi
}

main() {
  do_upload
  do_archive
}

main $@
