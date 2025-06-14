#!/usr/bin/env bash

SCRIPT=$(basename ${BASH_SOURCE[0]})

PATH_LOGO=/opt/creative/buyer_logo
PATH_BIDDER=/opt/creative/bidder
PATH_NAS_BUYER=/tmp/NAS/creative/buyer_logo
PATH_NAS_BIDDER=/tmp/NAS/creative/bidder
PATH_TMP_LOCALSTACK=/tmp/localstack

print() {
  printf "[${SCRIPT}] %s\n" "$*"
}

set_paths_requirements() {
  print "$FUNCNAME"
  if [ ! -d "$PATH_LOGO" ]; then
    echo "Directory $PATH_LOGO DOES NOT exists. Creating it..."
    sudo mkdir -p $PATH_LOGO
  fi

  if [ ! -d "$PATH_BIDDER" ]; then
    echo "Directory $PATH_BIDDER not found. Creating it..."
    sudo mkdir -p $PATH_BIDDER
  fi

  if [ ! -d "PATH_NAS_BUYER" ]; then
    echo "Directory $PATH_NAS_BUYER not found. Creating it..."
    mkdir -p $PATH_NAS_BUYER
  fi

  if [ ! -d "PATH_NAS_BIDDER" ]; then
    echo "Directory $PATH_NAS_BIDDER not found. Creating it..."
    mkdir -p $PATH_NAS_BIDDER
  fi

  if [ ! -d "PATH_TMP_LOCALSTACK" ]; then
    echo "Directory $PATH_TMP_LOCALSTACK not found. Creating it..."
    mkdir -p $PATH_TMP_LOCALSTACK
  fi
  print "done"
}

set_host_requirements() {
  print "$FUNCNAME"
  if ! grep localstack "/etc/hosts" > /dev/null; then
    echo "Localstack entry not found at /etc/hosts.. Creating it..."
    echo '127.0.0.1 localstack' | sudo tee -a /etc/hosts > /dev/null
  fi
  print "done"
}

set_docker_requirements() {
  print "$FUNCNAME"
  docker compose pull && docker compose up -d
  print "done"
}

verify_docker_dependencies() {
  print "$FUNCNAME"
  while ! nc -v -z localhost 5433; do
    echo "Waiting for dwdb"
    sleep 1
  done
  while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:4566/local-bucket)" != "200" ]]; do
    AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test aws s3api --endpoint-url=http://localhost:4566 create-bucket --bucket local-bucket --region us-east-1;
    echo "Waiting for localstack"
    sleep 5
  done
  print "done"
}

main() {
  set_paths_requirements
  set_host_requirements
  set_docker_requirements
  verify_docker_dependencies
}

main $@
