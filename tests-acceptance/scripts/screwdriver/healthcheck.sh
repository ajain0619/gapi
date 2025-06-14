#!/usr/bin/env bash

SCRIPT=$(basename ${BASH_SOURCE[0]})
VERSION=$1
HOSTNAME=$VERSION.geneva-api.one-mobile-dev.aws.oath.cloud

print() {
  printf "[${SCRIPT}] %s\n" "$*"
}

healthy() {
  HC_RESPONSE=$(curl --fail --insecure -s -X GET https://${HOSTNAME}/geneva/healthCheck)
  if [[ $? == 0 ]]; then
    CORE_ALIVE=$(echo ${HC_RESPONSE} | jq '.[0]|.alive')
    DW_ALIVE=$(echo ${HC_RESPONSE} | jq '.[1]|.alive')
    if [[ ${CORE_ALIVE} == true ]] && [[ ${DW_ALIVE} == true ]]; then
      return 0
    else
      return 1
    fi
  else
    return 1
  fi
}

check_health_status() {
  until $(healthy);
  do
    print "Waiting on $HOSTNAME"
    sleep 5
  done
  print "$HOSTNAME Ready"
}

main() {
  check_health_status
}

main $@
