#!/usr/bin/env bash

SCRIPT=$(basename ${BASH_SOURCE[0]})
VERSION=$1
REGION=$2

print() {
  printf "[${SCRIPT}] %s\n" "$*"
}

get_parameters() {
  aws ssm get-parameter --name $1 --query "Parameter.Value" --with-decryption --output text --region $REGION | base64 --decode
}

grab_env_variables(){
  get_parameters geneva-api-$VERSION > $1.properties
  chmod 755 $1.properties
  sed -e 's/:[^:\/\/]/="/g;s/$/"/g;s/ *=/=/g' $1.properties > $1.sh
}

create_config() {
  print "$FUNCNAME"
  print "VERSION=$VERSION - REGION=$REGION"
  print "Setting up configuration for VERSION=$VERSION"

  grab_env_variables $VERSION
  . ./$VERSION.sh

cat << EOF > /tmp/$VERSION.properties
  ############## wiremock configuration
  crud.wm.host = 127.0.0.1
  crud.wm.port = 8090
  ############## geneva crud configuration
  crud.schema=https
  crud.host=$VERSION.geneva-api.one-mobile-dev.aws.oath.cloud
  crud.port=443
  db.core.username=$geneva_server_core_db_username
  db.core.password=$geneva_server_core_db_password
  db.core.url=jdbc:mysql://127.0.0.1:3306/core?createDatabaseIfNotExist=true&zeroDateTimeBehavior=convertToNull&serverTimezone=America/New_York
  crud.dw.db.username=$geneva_server_dw_db_username
  crud.dw.db.password=$geneva_server_dw_db_password
  crud.dw.db.url=jdbc:vertica://127.0.0.1:5433/vertica_osmium?searchpath=cucumber_crud_dw
  ############## configuration for OIDC token retrieval #########################
  oidc.redirectUri=https://$VERSION.geneva-api.one-mobile-dev.aws.oath.cloud/geneva/dooh
EOF

  rm -f src/test/resources/properties/$VERSION.properties || true
  cp /tmp/$VERSION.properties src/test/resources/properties/$VERSION.properties
}

main() {
  create_config
}

main $@


