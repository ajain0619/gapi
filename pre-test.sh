#!/usr/bin/env bash

SCRIPT=$(basename ${BASH_SOURCE[0]})
ENVIRONMENT_FILE=${PWD}/.env

print() {
  printf "[${SCRIPT}] %s\n" "$*"
}

# extract property value from file
get_property() {
  PROPERTY_KEY=$1
  PROPERTY_VALUE=`grep $PROPERTY_KEY $ENVIRONMENT_FILE | cut -d'=' -f2`
  echo $PROPERTY_VALUE
}

fetch_data_dependencies() {
  print "$FUNCNAME"
  DWDB_VERSION=$(get_property "DWDB_VERSION")
  echo "dw db version: $DWDB_VERSION"
  rm -rf ${PWD}/tmp/docker-dwdb/ || true
  git clone git@git.ouryahoo.com:SSP/docker-dwdb.git --depth 1 --branch v$DWDB_VERSION --single-branch ${PWD}/tmp/docker-dwdb/
  GOLDEN_DATA_VERSION=$(cat ${PWD}/tmp/docker-dwdb/data.version)
  print "Current golden-data version=${GOLDEN_DATA_VERSION}"
  if [ ! -d "golden-data" ]; then
    print "Dependency golden-data version=${GOLDEN_DATA_VERSION} does not exist. Creating it..."
    rm -rf golden-data || true && git clone git@git.ouryahoo.com:SSP/golden-data.git --depth 1 --branch  ${GOLDEN_DATA_VERSION} --single-branch || true
  else
    print "Dependency golden-data exists. Skipping clone..."
    cd golden-data && git fetch --tags && git checkout tags/${GOLDEN_DATA_VERSION}
  fi
  rm -rf ${PWD}/tmp/ || true
}

main() {
  fetch_data_dependencies
}

main $@
