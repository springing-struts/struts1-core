#!/usr/bin/env bash

SCRIPT_DIR=$(realpath $(dirname $BASH_SOURCE[0]))
MODULE_ROOT_DIR=$(realpath $SCRIPT_DIR/../)
PROJECT_ROOT_DIR=$(realpath $MODULE_ROOT_DIR/../)

main() {
  build
}

build() {
  (cd $MODULE_ROOT_DIR
    gradle publish
  )
}

main
