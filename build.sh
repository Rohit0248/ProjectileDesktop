#!/bin/sh
set -eu
rm -rf build
mkdir -p build/classes
javac -d build/classes src/ProjectileLab.java
cat > build/manifest.txt <<'EOM'
Manifest-Version: 1.0
Main-Class: ProjectileLab
EOM
jar cfm build/ProjectileLab.jar build/manifest.txt -C build/classes .
