#!/usr/bin/env bash
set -eu

PROJECTS=(LibAPR )

for PROJECT in ${PROJECTS[@]}; do
    if [[ ! -d $PROJECT ]]; then
        echo "Warning: Project \"$PROJECT\" not found"
    else
        echo "Installing \"$PROJECT\""
        mkdir -p "$PROJECT/build"
        pushd "$PROJECT/build"
        cmake -DCMAKE_BUILD_TYPE=Release -DAPR_INSTALL=OFF -DAPR_BUILD_SHARED_LIB=OFF -DAPR_BUILD_STATIC_LIB=ON -DAPR_BUILD_EXAMPLES=OFF -DAPR_TESTS=OFF -DAPR_PREFER_EXTERNAL_GTEST=OFF -DAPR_PREFER_EXTERNAL_BLOSC=OFF -DAPR_BUILD_JAVA_WRAPPERS=OFF -DAPR_USE_CUDA=OFF -DAPR_BENCHMARK=OFF -DAPR_USE_LIBTIFF=OFF ..
	make -j 4
        popd
    fi

done
