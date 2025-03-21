#!/usr/bin/env bash

set -eu

SCRIPT_DIR=$(realpath "$(dirname $BASH_SOURCE)")
PROJECT_DIR=$(realpath "$SCRIPT_DIR/../")

DEVELOPER_ID=
DEVELOPER_NAME=
DEVELOPER_EMAIL=
MAVEN_CENTRAL_USERNAME=
MAVEN_CENTRAL_PUBLIC_KEY=
MAVEN_CENTRAL_PASSWORD=

main() {
  local private_key
  local passphrase

  private_key=$(gpg --armor --export-secret-keys "$MAVEN_CENTRAL_PUBLIC_KEY")
  read -p 'Reenter PRIVATE_KEY_PASSPHRASE: ' -s passphrase && echo ''

  DEVELOPER_ID=$DEVELOPER_ID \
  DEVELOPER_NAME=$DEVELOPER_NAME \
  DEVELOPER_EMAIL=$DEVELOPER_EMAIL \
  MAVEN_CENTRAL_USERNAME=$MAVEN_CENTRAL_USERNAME \
  MAVEN_CENTRAL_PASSWORD=$MAVEN_CENTRAL_PASSWORD \
  MAVEN_CENTRAL_PRIVATE_KEY=$private_key \
  MAVEN_CENTRAL_PRIVATE_KEY_PASSPHRASE=$passphrase \
  gradle --stacktrace clean build generatePomFileForMavenPublication sonatypeCentralUpload
}

(cd "$PROJECT_DIR" \
  && eval "$(mise env)" \
  && main
)
