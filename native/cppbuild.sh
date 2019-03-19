#!/usr/bin/env bash
set -eu

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
    aarch64*)
        ARCH=arm64
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

PROJECTS=(LibAPR )

for PROJECT in ${PROJECTS[@]}; do
    if [[ ! -d $PROJECT ]]; then
        echo "Warning: Project \"$PROJECT\" not found"
    else
        echo "Installing \"$PROJECT\""
        mkdir -p "$PROJECT/build"
        pushd "$PROJECT/build"
	case $PLATFORM in
		linux-x86_64)
			export GENERATOR="Unix Makefiles"
		;;
		macosx-*)
			export GENERATOR="Unix Makefiles"
		;;
		windows-x86_64)
			export GENERATOR="Visual Studio 15 2017 Win64"
		;;
		*)
		echo "Platform $PLATFORM is not supported"
		return 0
		;;
	esac
        cmake -G "$GENERATOR" -DCMAKE_BUILD_TYPE=Release -DAPR_INSTALL=OFF -DAPR_BUILD_SHARED_LIB=OFF -DAPR_BUILD_STATIC_LIB=ON -DAPR_BUILD_EXAMPLES=OFF -DAPR_TESTS=OFF -DAPR_PREFER_EXTERNAL_GTEST=OFF -DAPR_PREFER_EXTERNAL_BLOSC=OFF -DAPR_BUILD_JAVA_WRAPPERS=OFF -DAPR_USE_CUDA=OFF -DAPR_BENCHMARK=OFF -DAPR_USE_LIBTIFF=OFF ..
	cmake --build .
        popd
    fi

done
