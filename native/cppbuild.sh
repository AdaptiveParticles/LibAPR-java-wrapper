#!/usr/bin/env bash
# Scripts to build and install native C++ libraries
# Adapted from https://github.com/bytedeco/javacpp-presets
set -eu

echo "================= HELLO FROM CPPBUILD.SH ======================================"

which cmake3 &> /dev/null && CMAKE3="cmake3" || CMAKE3="cmake"
[[ -z ${CMAKE:-} ]] && CMAKE=$CMAKE3
[[ -z ${MAKEJ:-} ]] && MAKEJ=4
[[ -z ${OLDCC:-} ]] && OLDCC="gcc"
[[ -z ${OLDCXX:-} ]] && OLDCXX="g++"
[[ -z ${OLDFC:-} ]] && OLDFC="gfortran"

KERNEL=(`uname -s | tr [A-Z] [a-z]`)
ARCH=(`uname -m | tr [A-Z] [a-z]`)
case $KERNEL in
    darwin)
        OS=macosx
        ;;
    mingw32*)
        OS=windows
        KERNEL=windows
        ARCH=x86
        ;;
    mingw64*)
        OS=windows
        KERNEL=windows
        ARCH=x86_64
        ;;
    *)
        OS=$KERNEL
        ;;
esac
case $ARCH in
    arm*)
        ARCH=arm
        ;;
    i386|i486|i586|i686)
        ARCH=x86
        ;;
    amd64|x86-64)
        ARCH=x86_64
        ;;
esac
PLATFORM=$OS-$ARCH
EXTENSION=
echo "Detected platform \"$PLATFORM\""

while [[ $# > 0 ]]; do
    case "$1" in
        -platform=*)
            PLATFORM="${1#-platform=}"
            ;;
        -platform)
            shift
            PLATFORM="$1"
            ;;
        -extension=*)
            EXTENSION="${1#-extension=}"
            ;;
        -extension)
            shift
            EXTENSION="$1"
            ;;
        *)
            PROJECTS+=("$1")
            ;;
    esac
    shift
done

echo -n "Building for platform \"$PLATFORM\""
if [[ -n "$EXTENSION" ]]; then
    echo -n " with extension \"$EXTENSION\""
fi
echo

TOP_PATH=`pwd`

if [[ -z ${PROJECTS:-} ]]; then
    PROJECTS=(LibAPR )
fi

for PROJECT in ${PROJECTS[@]}; do
    if [[ ! -d $PROJECT ]]; then
        echo "Warning: Project \"$PROJECT\" not found"
    else
        echo "Installing \"$PROJECT\""
        mkdir -p "$PROJECT/build"
        pushd "$PROJECT/build"
        cmake -DCMAKE_BUILD_TYPE=Release -DAPR_INSTALL=OFF -DAPR_BUILD_SHARED_LIB=OFF -DAPR_BUILD_STATIC_LIB=ON -DAPR_BUILD_EXAMPLES=OFF -DAPR_TESTS=OFF -DAPR_PREFER_EXTERNAL_GTEST=OFF -DAPR_PREFER_EXTERNAL_BLOSC=OFF -DAPR_BUILD_JAVA_WRAPPERS=OFF -DAPR_USE_CUDA=OFF -DAPR_BENCHMARK=OFF ..
        make -j 4
        popd
    fi

done
