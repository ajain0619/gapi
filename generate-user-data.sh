#!/usr/bin/env bash

SCRIPT=$(basename ${BASH_SOURCE[0]})
VERSION=$1

if [ -z $VERSION ]; then
  VERSION="latest"
fi

print() {
  printf "[${SCRIPT}] %s\n" "$*"
}

PROPERTY_FILE=.env
# extract property value from file
function getProperty() {
  PROP_KEY=$1
  PROP_VALUE=`cat $PROPERTY_FILE | grep "$PROP_KEY" | cut -d'=' -f2`
  echo $PROP_VALUE
}

main() {
  DBCORE_VERSION=$(getProperty "DBCORE_VERSION")
  DWDB_VERSION=$(getProperty "DWDB_VERSION")
  SSO_MOCK_VERSION=$(getProperty "SSO_MOCK_VERSION")

  # create usere data script
  OUTPUT_SCRIPT=/tmp/user-data.sh
  cat << EOF > $OUTPUT_SCRIPT
#!/bin/bash
echo $DBCORE_VERSION > /etc/db-core.version
echo $DWDB_VERSION > /etc/dwdb.version
echo $SSO_MOCK_VERSION > /etc/sso-mock.version
echo $VERSION > /etc/pull-geneva-acceptance-test.version
EOF

  # encode as base64
  base64 $OUTPUT_SCRIPT | tr -d '\n\r'
}

main "$@"
