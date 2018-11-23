#!/bin/bash

set -e
set -u
set -o pipefail

docker images -f dangling=true -q | xargs -I {} docker rmi {}

mkdir -p localstore

docker run -it \
           --rm  \
           --volume ${PWD}:/current-folder \
           --workdir /current-folder \
           ubuntu:18.04 /bin/bash
