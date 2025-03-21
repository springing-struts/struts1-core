#!/usr/bin/env bash

set -eu

SCRIPT_DIR=$(realpath "$(dirname $BASH_SOURCE)")
PROJECT_DIR=$(realpath "$SCRIPT_DIR/../")

main() {
  format && build
}

build() {
  gradle --stacktrace build publish
}

format() {
  prettier \
    --write \
    --plugin "$(mise where npm:prettier-plugin-java)/lib/node_modules/prettier-plugin-java/dist/index.js" \
    --cache \
    --no-config \
    --no-editor-config \
    ./src/**/*.java
}

(cd "$PROJECT_DIR" \
  && eval "$(mise env)" \
  && main
)
