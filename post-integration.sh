#!/usr/bin/env bash

SCRIPT=$(basename ${BASH_SOURCE[0]})

print() {
  printf "[${SCRIPT}] %s\n" "$*"
}

stop_docker_requirements() {
  print "$FUNCNAME"
  docker compose down
  print "done"
}

main() {
  stop_docker_requirements
}

main $@
