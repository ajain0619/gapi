#!/usr/bin/env bash

SCRIPT=$(basename ${BASH_SOURCE[0]})
VERSION=$1
TEST_RUNNER=GlobalTestRunner
SPRING_PROFILE=global-runner,aws

print() {
  printf "[${SCRIPT}] %s\n" "$*"
}

run_tests() {
  print "$FUNCNAME"
  mvn --settings ../.mvn/settings.xml clean spotless:check verify -Drevision=$VERSION -Denvironment=$VERSION \
      -Dit.test=$TEST_RUNNER -Paws \
      -Dspring.profiles.active=$SPRING_PROFILE

  if [ $? -eq 0 ]
    then
      echo "Test run successfull"
      touch /tmp/success
  fi
  cp -rv tests-results /tmp
}

main() {
  run_tests
}

main $@
